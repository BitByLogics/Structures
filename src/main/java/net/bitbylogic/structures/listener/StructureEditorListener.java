package net.bitbylogic.structures.listener;

import lombok.NonNull;
import net.bitbylogic.packetblocks.event.PacketBlockBreakEvent;
import net.bitbylogic.packetblocks.lib.bitsutils.location.WorldPosition;
import net.bitbylogic.structures.manager.StructureManager;
import net.bitbylogic.structures.structure.Structure;
import net.bitbylogic.utils.Placeholder;
import net.bitbylogic.utils.message.config.MessageProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.UUID;

public class StructureEditorListener implements Listener {

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

        if(structureManager.getEditorModeLocations().get(player.getUniqueId()).contains(blockLocation)) {
            return;
        }

        structureManager.getEditorModeLocations().get(player.getUniqueId()).add(blockLocation);

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

        player.sendActionBar(messageProvider.getMessage("Editor.Action-Bar.Block-Removed",
                new Placeholder("%x%", targetBlockLoc.getBlockX()),
                new Placeholder("%y%", targetBlockLoc.getBlockY()),
                new Placeholder("%z%", targetBlockLoc.getBlockZ())
        ));

        structureManager.getPacketBlockGroup(id).ifPresent(group ->
                structureManager.getPacketBlockManager().removeBlockFromGroup(group, targetBlockLoc));
    }

}
