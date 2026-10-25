package net.bitbylogic.structures.listener;

import lombok.NonNull;
import net.bitbylogic.packetblocks.PacketBlocks;
import net.bitbylogic.packetblocks.block.PacketBlockManager;
import net.bitbylogic.structures.Structures;
import net.bitbylogic.structures.manager.StructureManager;
import net.bitbylogic.structures.structure.Structure;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class StructureListener implements Listener {

    private final Structures plugin;
    private final StructureManager structureManager;
    private final PacketBlockManager packetBlockManager;

    public StructureListener(@NonNull Structures plugin, @NonNull StructureManager structureManager) {
        this.plugin = plugin;
        this.structureManager = structureManager;
        this.packetBlockManager = PacketBlocks.getInstance().getBlockManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.getScheduler().runDelayed(plugin, task -> {
            for (Structure structure : structureManager.getStructures().values()) {
                if(structure.isRequireTrigger()) {
                    continue;
                }

                if(structure.getShowConditions().stream().anyMatch(condition -> !condition.test(player))) {
                    continue;
                }

                structureManager.addViewer(structure.getId(), player);
            }
        }, null, 5);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        structureManager.getStructureViewers().forEach((key, value) -> value.remove(player.getUniqueId()));
        packetBlockManager.getBlocksByMetadata(StructureManager.PACKET_BLOCK_METADATA_KEY).forEach(block -> block.removeViewer(player));
    }

}
