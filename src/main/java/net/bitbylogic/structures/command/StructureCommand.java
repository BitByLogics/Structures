package net.bitbylogic.structures.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import lombok.RequiredArgsConstructor;
import net.bitbylogic.packetblocks.PacketBlocks;
import net.bitbylogic.packetblocks.lib.bitsutils.location.WorldPosition;
import net.bitbylogic.packetblocks.util.PacketBlockUtil;
import net.bitbylogic.structures.Structures;
import net.bitbylogic.structures.animation.StructureAnimation;
import net.bitbylogic.structures.animation.StructureAnimationManager;
import net.bitbylogic.structures.manager.StructureManager;
import net.bitbylogic.structures.structure.Structure;
import net.bitbylogic.structures.util.RotationUtil;
import net.bitbylogic.utils.Placeholder;
import net.bitbylogic.utils.message.config.MessageProvider;
import net.bitbylogic.utils.smallcaps.SmallCapsConverter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

@CommandAlias("structure")
@CommandPermission("structures.admin")
@RequiredArgsConstructor
public class StructureCommand extends BaseCommand {

    private static final BlockData AIR_BLOCK_DATA = Material.AIR.createBlockData();

    private final MessageProvider messageProvider;
    private final StructureAnimationManager animationManager;
    private final StructureManager structureManager;

    @Dependency
    private Structures plugin;

    @Default
    public void onDefault(CommandSender sender) {
        displayCommandHelp(sender);
    }

    @Subcommand("help")
    public void onHelp(CommandSender sender) {
        displayCommandHelp(sender);
    }

    @Subcommand("reload")
    public void onReload(CommandSender sender) {
        plugin.reloadConfig();
        sender.sendMessage(messageProvider.getMessage("Configuration-Reloaded"));
    }

    @Subcommand("info")
    public void onInfo(Player player) {
        RayTraceResult result = PacketBlockUtil.rayTrace(player, 10);

        if (result == null || result.getHitBlock() == null) {
            player.sendMessage(messageProvider.getMessage("Info.Invalid-Location"));
            return;
        }

        PacketBlocks.getInstance().getBlockManager().getBlock(result.getHitBlock().getLocation()).ifPresentOrElse(packetBlock -> {
            if(!packetBlock.hasMetadata(StructureManager.PACKET_BLOCK_METADATA_KEY)) {
                player.sendMessage(messageProvider.getMessage("Info.No-Structure"));
                return;
            }

            player.sendMessage(messageProvider.getMessage("Info.Structure-Info",
                    new Placeholder("%id%", packetBlock.getMetadata(StructureManager.PACKET_BLOCK_METADATA_KEY))));
        }, () -> player.sendMessage(messageProvider.getMessage("Info.No-Structure")));
    }

    @Subcommand("animation")
    @CommandCompletion("@structureIds * @animationIds")
    public void onAnimation(CommandSender sender, String structureId, StructureAnimation.Type animationType, String animationId) {
        Structure structure = structureManager.getStructures().get(structureId);

        if (structure == null) {
            sender.sendMessage(messageProvider.getMessage("Animation.Invalid-ID"));
            return;
        }

        if(animationId.equalsIgnoreCase("none")) {
            switch (animationType) {
                case SHOW -> {
                    structure.setAppearAnimationId(null);
                    sender.sendMessage(messageProvider.getMessage("Animation.Show-Animation-Set",
                            new Placeholder("%id%", structureId),
                            new Placeholder("%animationId%", "ɴᴏɴᴇ")));
                }
                case HIDE -> {
                    structure.setDisappearAnimationId(null);
                    sender.sendMessage(messageProvider.getMessage("Animation.Hide-Animation-Set",
                            new Placeholder("%id%", structureId),
                            new Placeholder("%animationId%", "ɴᴏɴᴇ")));
                }
            }

            structureManager.saveStructure(structureId);
            return;
        }

        animationManager.getAnimation(animationId).ifPresentOrElse(structureAnimation -> {
            switch (animationType) {
                case SHOW -> {
                    structure.setAppearAnimationId(structureAnimation.getId());
                    sender.sendMessage(messageProvider.getMessage("Animation.Show-Animation-Set",
                            new Placeholder("%id%", structureId),
                            new Placeholder("%animationId%", animationId)));
                }
                case HIDE -> {
                    structure.setDisappearAnimationId(structureAnimation.getId());
                    sender.sendMessage(messageProvider.getMessage("Animation.Hide-Animation-Set",
                            new Placeholder("%id%", structureId),
                            new Placeholder("%animationId%", animationId)));
                }
            }

            structureManager.saveStructure(structureId);
        }, () -> sender.sendMessage(messageProvider.getMessage("Animation.Invalid-Animation")));
    }

    @Subcommand("edit")
    @CommandCompletion("@structureIds")
    public void onEdit(Player player, @Optional String structureId) {
        String editingStructureId = structureManager.getEditorMode().get(player.getUniqueId());

        if (editingStructureId != null) {
            if (structureId != null && !editingStructureId.equals(structureId)) {
                messageProvider.getMessageList("Editor.Already-Editing", new Placeholder("%id%", editingStructureId))
                        .forEach(player::sendMessage);
                return;
            }

            Structure structure = structureManager.getStructures().get(structureId);
            Set<UUID> viewers = structureManager.getStructureViewers().get(structureId);

            if (viewers == null) {
                Bukkit.getLogger().log(
                        Level.SEVERE,
                        "Viewers entry for structure " + structureId + " does not exist");
                return;
            }

            viewers.add(player.getUniqueId());

            Map<Location, BlockData> editedLocations = new HashMap<>();
            structureManager.getEditorModeLocations().get(player.getUniqueId()).forEach(location -> editedLocations.put(location, location.getBlock().getBlockData().clone()));

            editedLocations.keySet().forEach(location -> {
                BlockData blockData = location.getBlock().getBlockData();

                structure.getBlocks().put(location, blockData);
                location.getBlock().setBlockData(AIR_BLOCK_DATA, false);
            });

            structureManager.getPacketBlockGroup(editingStructureId).ifPresentOrElse(group ->
                    structureManager.getPacketBlockManager().addBlocksToGroup(group, editedLocations),
                    () -> structureManager.createGroup(structure));

            boolean saved = structureManager.saveStructure(editingStructureId);
            structureManager.getEditorMode().remove(player.getUniqueId());
            structureManager.getEditorModeLocations().remove(player.getUniqueId());

            if (structure.getBlocks().isEmpty()) {
                player.sendMessage(messageProvider.getMessage("Editor.Deleted-Structure",
                        new Placeholder("%id%", editingStructureId)));
                return;
            }

            if (saved) {
                player.sendMessage(messageProvider.getMessage("Editor.Saved-Structure",
                        new Placeholder("%id%", editingStructureId)));
                return;
            }

            player.sendMessage(messageProvider.getMessage("Editor.Failed-To-Save"));
            return;
        }

        if (structureId == null) {
            player.sendMessage(messageProvider.getMessage("Editor.Missing-ID"));
            return;
        }

        structureManager.getEditorMode().put(player.getUniqueId(), structureId);
        structureManager.getEditorModeLocations().put(player.getUniqueId(), new ArrayList<>());

        Structure structure = structureManager.getStructures().get(structureId);
        if (structure == null) {
            structureManager.createStructure(structureId);
            structureManager.addViewer(structureId, player);

            player.sendMessage(messageProvider.getMessage("Editor.Started-Editing-New", new Placeholder("%id%", structureId)));
            return;
        }

        structureManager.addViewer(structureId, player);

        player.sendMessage(messageProvider.getMessage("Editor.Started-Editing", new Placeholder("%id%", structureId)));
    }

    @Subcommand("copy")
    @CommandCompletion("@structureIds")
    @Syntax("<sourceId> <newId>")
    public void onCopy(Player player, String sourceId, String newId) {
        Structure source = structureManager.getStructures().get(sourceId);

        if (source == null) {
            player.sendMessage(messageProvider.getMessage("Copy.Invalid-Structure", new Placeholder("%id%", sourceId)));
            return;
        }

        if (structureManager.getStructures().containsKey(newId)) {
            player.sendMessage(messageProvider.getMessage("Copy.Duplicate-ID", new Placeholder("%id%", newId)));
            return;
        }

        RayTraceResult result = PacketBlockUtil.rayTrace(player, 10);

        if (result == null || result.getHitBlock() == null) {
            player.sendMessage(messageProvider.getMessage("Copy.Invalid-Location"));
            return;
        }

        Location target = result.getHitBlock()
                .getLocation()
                .toBlockLocation()
                .add(result.getHitBlockFace().getDirection());

        Location center = getCopyCenter(player, source);

        Vector offset = target.toVector().subtract(center.toVector());
        int rotationSteps = Math.floorMod(Math.round(player.getLocation().getYaw() / 90f), 4);

        Structure newStructure = structureManager.createStructure(newId).orElse(null);

        if (newStructure == null) {
            player.sendMessage(messageProvider.getMessage("Copy.Failed-To-Copy"));
            return;
        }

        Set<UUID> viewers = structureManager.getStructureViewers().get(newStructure.getId());

        if (viewers == null) {
            Bukkit.getLogger().log(
                    Level.SEVERE,
                    "Viewers entry for structure " + newStructure + " does not exist");
            return;
        }

        viewers.add(player.getUniqueId());

        for (Map.Entry<Location, BlockData> entry : source.getBlocks().entrySet()) {
            Location oldLoc = entry.getKey();
            BlockData blockData = entry.getValue().clone();

            double relX = oldLoc.getX() - center.getX();
            double relY = oldLoc.getY() - center.getY();
            double relZ = oldLoc.getZ() - center.getZ();

            Vector rotated = switch (rotationSteps) {
                case 1 -> new Vector(-relZ, relY, relX);
                case 2 -> new Vector(-relX, relY, -relZ);
                case 3 -> new Vector(relZ, relY, -relX);
                default -> new Vector(relX, relY, relZ);
            };

            Location newLoc = center.clone().add(rotated).add(offset);
            blockData.rotate(RotationUtil.getOppositeRotation(player.getYaw()));

            newStructure.getBlocks().put(newLoc, blockData);
        }

        structureManager.createGroup(newStructure);
        structureManager.saveStructure(newId);

        player.sendMessage(messageProvider.getMessage("Copy.Successfully-Copied",
                new Placeholder("%sourceId%", sourceId),
                new Placeholder("%newId%", newId),
                new Placeholder("%direction%", SmallCapsConverter.convert(player.getFacing().name()))
        ));
    }

    private @NotNull Location getCopyCenter(Player player, Structure source) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

        for (Location loc : source.getBlocks().keySet()) {
            minX = Math.min(minX, loc.getBlockX());
            minY = Math.min(minY, loc.getBlockY());
            minZ = Math.min(minZ, loc.getBlockZ());
            maxX = Math.max(maxX, loc.getBlockX());
            maxY = Math.max(maxY, loc.getBlockY());
            maxZ = Math.max(maxZ, loc.getBlockZ());
        }

        return new Location(
                player.getWorld(),
                (minX + maxX) / 2.0,
                minY,
                (minZ + maxZ) / 2.0
        );
    }

    @Subcommand("delete")
    @CommandCompletion("@structureIds")
    public void onDelete(CommandSender sender, String structureId) {
        Structure structure = structureManager.getStructures().get(structureId);

        if (structure == null) {
            sender.sendMessage(messageProvider.getMessage("Delete.Invalid-ID"));
            return;
        }

        if (structureManager.deleteStructure(structureId)) {
            sender.sendMessage(messageProvider.getMessage("Delete.Successfully-Deleted",
                    new Placeholder("%id%", structureId)));
            return;
        }

        sender.sendMessage(messageProvider.getMessage("Delete.Failed-To-Delete",
                new Placeholder("%id%", structureId)));
    }

    @Subcommand("view")
    @CommandCompletion("@structureIds @players *")
    private void onDefault(CommandSender sender, String structureId, @Optional OnlinePlayer target) {
        Set<UUID> viewers = structureManager.getStructureViewers().get(structureId);

        if (viewers == null) {
            sender.sendMessage(messageProvider.getMessage("View.Invalid-ID"));
            return;
        }

        Player player = target == null ? (Player) sender : target.getPlayer();
        boolean toggle = !viewers.contains(player.getUniqueId());

        if (toggle) {
            structureManager.addViewer(structureId, player);

            if (target != null) {
                return;
            }

            sender.sendMessage(messageProvider.getMessage("View.Viewing-Enabled",
                    new Placeholder("%id%", structureId)));
            return;
        }

        structureManager.removeViewer(structureId, player);

        if (target != null) {
            return;
        }

        sender.sendMessage(messageProvider.getMessage("View.Viewing-Disabled",
                new Placeholder("%id%", structureId)));
    }

    private void displayCommandHelp(CommandSender sender) {
        messageProvider.getMessageList("Command-Help").forEach(sender::sendMessage);
    }

}
