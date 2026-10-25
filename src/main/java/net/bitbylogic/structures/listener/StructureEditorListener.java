package net.bitbylogic.structures.listener;

import lombok.NonNull;
import net.bitbylogic.packetblocks.event.PacketBlockBreakEvent;
import net.bitbylogic.structures.manager.StructureManager;
import net.bitbylogic.structures.structure.Structure;
import net.bitbylogic.utils.Placeholder;
import net.bitbylogic.utils.message.config.MessageProvider;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.List;
import java.util.Set;


public class StructureEditorListener implements Listener {

    private static final Set<Material> TALL_PLANTS = Set.of(Material.SUNFLOWER, Material.LILAC, Material.ROSE_BUSH, Material.PEONY, Material.TALL_GRASS, Material.LARGE_FERN, Material.TALL_SEAGRASS, Material.PITCHER_PLANT);

    private final MessageProvider messageProvider;
    private final StructureManager structureManager;

    public StructureEditorListener(@NonNull MessageProvider messageProvider, @NonNull StructureManager structureManager) {
        this.messageProvider = messageProvider;
        this.structureManager = structureManager;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        String structureId = structureManager.getEditorMode().get(player.getUniqueId());

        if (structureId == null) {
            return;
        }

        Structure structure = structureManager.getStructures().get(structureId);

        if (structure == null) {
            return;
        }

        Location blockLocation = event.getBlock().getLocation();
        List<Location> editedLocations = structureManager.getEditorModeLocations().get(player.getUniqueId());

        if(editedLocations.contains(blockLocation)) {
            return;
        }

        editedLocations.add(blockLocation);

        Location partnerLocation = getPartnerLocation(event.getBlock());

        if(partnerLocation != null && !editedLocations.contains(partnerLocation)) {
            editedLocations.add(partnerLocation);
        }

        player.sendActionBar(messageProvider.getMessage("Editor.Action-Bar.Block-Added",
                new Placeholder("%x%", blockLocation.getBlockX()),
                new Placeholder("%y%", blockLocation.getBlockY()),
                new Placeholder("%z%", blockLocation.getBlockZ())
        ));
    }

    @EventHandler
    public void onPacketBlockBreak(PacketBlockBreakEvent event) {
        Player player = event.getPlayer();
        String id = structureManager.getEditorMode().get(player.getUniqueId());

        if (id == null) {
            return;
        }

        Structure structure = structureManager.getStructures().get(id);
        if (structure == null) {
            return;
        }

        Location targetBlockLoc = event.getLocation();

        if(!structure.getBlocks().containsKey(targetBlockLoc)) {
            return;
        }

        structure.getBlocks().remove(targetBlockLoc);

        player.sendActionBar(messageProvider.getMessage("Editor.Action-Bar.Block-Removed",
                new Placeholder("%x%", targetBlockLoc.getBlockX()),
                new Placeholder("%y%", targetBlockLoc.getBlockY()),
                new Placeholder("%z%", targetBlockLoc.getBlockZ())
        ));

        structureManager.getPacketBlockGroup(id).ifPresent(group -> structureManager.getPacketBlockManager().removeBlockFromGroup(group, targetBlockLoc));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        String id = structureManager.getEditorMode().get(player.getUniqueId());

        if (id == null) {
            return;
        }

        Structure structure = structureManager.getStructures().get(id);
        if (structure == null) {
            return;
        }

        Location targetBlockLoc = event.getBlock().getLocation();

        if(!structure.getBlocks().containsKey(targetBlockLoc) && !structureManager.getEditorModeLocations().get(player.getUniqueId()).contains(targetBlockLoc)) {
            return;
        }

        structure.getBlocks().remove(targetBlockLoc);
        structureManager.getEditorModeLocations().get(player.getUniqueId()).remove(targetBlockLoc);

        Location partnerLocation = getPartnerLocation(event.getBlock());

        if(partnerLocation != null) {
            structure.getBlocks().remove(partnerLocation);
            structureManager.getEditorModeLocations().get(player.getUniqueId()).remove(partnerLocation);

            structureManager.getPacketBlockGroup(id).ifPresent(group ->
                    structureManager.getPacketBlockManager().removeBlockFromGroup(group, partnerLocation));
        }

        player.sendActionBar(messageProvider.getMessage("Editor.Action-Bar.Block-Removed",
                new Placeholder("%x%", targetBlockLoc.getBlockX()),
                new Placeholder("%y%", targetBlockLoc.getBlockY()),
                new Placeholder("%z%", targetBlockLoc.getBlockZ())
        ));

        structureManager.getPacketBlockGroup(id).ifPresent(group ->
                structureManager.getPacketBlockManager().removeBlockFromGroup(group, targetBlockLoc));
    }

    private Location getPartnerLocation(Block block) {
        BlockData blockData = block.getBlockData();

        if(!(blockData instanceof Bisected bisected) || blockData instanceof Stairs || blockData instanceof TrapDoor) {
            return null;
        }

        if(!(blockData instanceof Door) && !isTallPlant(block)) {
            return null;
        }

        return block.getLocation().add(0, bisected.getHalf() == Bisected.Half.BOTTOM ? 1 : -1, 0);
    }

    private boolean isTallPlant(Block block) {
        Material type = block.getType();

        return TALL_PLANTS.contains(type);
    }

}
