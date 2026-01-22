package net.bitbylogic.structures.manager;

import lombok.Getter;
import lombok.NonNull;
import net.bitbylogic.packetblocks.PacketBlocks;
import net.bitbylogic.packetblocks.block.PacketBlockManager;
import net.bitbylogic.packetblocks.group.PacketBlockGroup;
import net.bitbylogic.structures.Structures;
import net.bitbylogic.structures.animation.StructureAnimation;
import net.bitbylogic.structures.animation.StructureAnimationManager;
import net.bitbylogic.structures.context.StructureContextKeys;
import net.bitbylogic.structures.structure.Structure;
import net.bitbylogic.utils.context.BukkitContextKeys;
import net.bitbylogic.utils.context.ContextBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;

@Getter
public class StructureManager {

    public static final String PACKET_BLOCK_METADATA_KEY = "structure";

    private final Map<String, Structure> structures = new HashMap<>();
    private final Map<String, Set<UUID>> structureViewers = new HashMap<>();

    private final Map<UUID, String> editorMode = new HashMap<>();
    private final Map<UUID, List<Location>> editorModeLocations = new HashMap<>();

    private final Structures plugin;
    private final StructureAnimationManager animationManager;
    private final PacketBlockManager packetBlockManager;

    public StructureManager(@NonNull Structures plugin, @NonNull StructureAnimationManager animationManager) {
        this.plugin = plugin;
        this.animationManager = animationManager;
        this.packetBlockManager = PacketBlocks.getInstance().getBlockManager();

        loadStructures();
    }

    public void loadStructures() {
        // IMPORTANT: If we don't do this, we *WILL* have memory leaks.
        structures.values().forEach(structure -> {
            getViewers(structure.getId()).forEach(viewer -> removeViewer(structure.getId(), Bukkit.getPlayer(viewer)));
            packetBlockManager.removeIf(pb -> pb.hasMetadata(createStructureIdMetadataKey(structure.getId())));

            structure.getShowTriggers().forEach(trigger -> trigger.setActive(false));
            structure.getHideTriggers().forEach(trigger -> trigger.setActive(false));
        });

        structures.clear();
        structureViewers.clear();

        editorMode.values().forEach(structureId -> structureViewers.put(structureId, new HashSet<>()));

        for (String structureId : plugin.getStructureConfig().getKeys(false)) {
            ConfigurationSection structureSection = plugin.getStructureConfig().getConfigurationSection(structureId);

            if (structureSection == null) {
                continue;
            }

            Structure.getSerializer().deserialize(structureSection).ifPresent(structure -> {
                structures.put(structureId, structure);
                structureViewers.put(structureId, new HashSet<>());

                loadShowTriggers(structure);
                loadHideTriggers(structure);

                createGroup(structure);

                plugin.getLogger().log(Level.INFO, String.format(
                        "Loaded structure %s (%d blocks)",
                        structure.getId(),
                        structure.getBlocks().size()));
            });
        }

        structures.values().forEach(structure -> {
            if(structure.isRequireTrigger()) {
                return;
            }

            Bukkit.getOnlinePlayers().forEach(player -> {
                if(structure.getShowConditions().stream().anyMatch(condition -> !condition.test(player))) {
                    return;
                }

                addViewer(structure.getId(), player);
            });
        });
    }

    protected String createStructureIdMetadataKey(String structureId) {
        return PACKET_BLOCK_METADATA_KEY + "_" + structureId;
    }

    public void createGroup(@NonNull Structure structure) {
        PacketBlockGroup group = PacketBlocks.getInstance().getBlockManager().createGroup(structure.getBlocks());

        group.addMetadata(PACKET_BLOCK_METADATA_KEY, structure.getId());
        group.addMetadata(createStructureIdMetadataKey(structure.getId()), true);
    }

    private void loadShowTriggers(Structure structure) {
        structure.getShowTriggers().forEach(trigger -> {
            trigger.setActive(true);
            trigger.setAction(context -> {
                Player player = context.get(BukkitContextKeys.PLAYER).orElse(null);

                if (player == null || structureViewers.getOrDefault(structure.getId(), new HashSet<>()).contains(player.getUniqueId())) {
                    return;
                }

                if (structure.getShowConditions().stream().anyMatch(condition -> !condition.test(player))) {
                    return;
                }

                addViewer(structure.getId(), player);
            });
        });
    }

    private void loadHideTriggers(Structure structure) {
        structure.getHideTriggers().forEach(trigger -> {
            trigger.setActive(true);
            trigger.setAction(context -> {
                Player player = context.get(BukkitContextKeys.PLAYER).orElse(null);

                if (player == null || !structureViewers.getOrDefault(structure.getId(), new HashSet<>()).contains(player.getUniqueId())) {
                    return;
                }

                if (structure.getHideConditions().stream().anyMatch(condition -> !condition.test(player))) {
                    return;
                }

                removeViewer(structure.getId(), player);
            });
        });
    }

    public Optional<PacketBlockGroup> getPacketBlockGroup(@NonNull String structureId) {
        return packetBlockManager.getBlocksByMetadata(createStructureIdMetadataKey(structureId)).stream().findFirst().map(block -> (PacketBlockGroup) block);
    }

    /**
     * Creates a new structure with the given structure ID if it does not already exist.
     * If a structure with the provided ID already exists, the method will return an empty {@code Optional}.
     * Otherwise, a new {@code Structure} is created, registered in the internal storage, and returned.
     *
     * @param structureId The unique identifier for the structure to be created.
     *                    Must not be null or empty.
     * @return An {@code Optional} containing the newly created {@link Structure} if successful,
     *         or an empty {@code Optional} if the structure ID is already in use.
     */
    public Optional<Structure> createStructure(String structureId) {
        if (structures.containsKey(structureId)) {
            return Optional.empty();
        }

        Structure structure = new Structure(structureId);

        structures.put(structureId, structure);
        structureViewers.put(structureId, new HashSet<>());

        return Optional.of(structure);
    }

    /**
     * Saves the structure associated with the given structure ID. If the structure exists
     * and contains blocks, it is serialized to the plugin's structure configuration and persisted.
     * If the structure contains no blocks, it is deleted.
     *
     * @param structureId The unique identifier of the structure to be saved.
     *                    Must not be null or empty.
     * @return A boolean indicating whether the save operation was successful:
     *         - Returns {@code true} if the structure was successfully saved or deleted.
     *         - Returns {@code false} if the structure with the given ID does not exist.
     */
    public boolean saveStructure(String structureId) {
        Structure structure = structures.get(structureId);

        if (structure == null) {
            return false;
        }

        if (!structure.getBlocks().isEmpty()) {
            Structure.getSerializer().serialize(plugin.getStructureConfig(), structure);
            plugin.saveStructureConfig();
            return true;
        }

        deleteStructure(structureId);
        return true;
    }

    /**
     * Deletes the structure associated with the specified structure ID. This method removes
     * the structure from the internal registry, updates the structure configuration file,
     * and removes associated packet blocks and viewers.
     *
     * @param structureId The unique identifier of the structure to be deleted. Must not be null.
     * @return A boolean indicating whether the delete operation was successful:
     *         - Returns {@code true} if the structure was successfully deleted.
     *         - Returns {@code false} if no structure with the given ID exists.
     */
    public boolean deleteStructure(String structureId) {
        Structure structure = structures.get(structureId);

        if (structure == null) {
            return false;
        }

        packetBlockManager.removeIf(pb -> pb.hasMetadata(createStructureIdMetadataKey(structureId)));

        plugin.getStructureConfig().set(structureId, null);
        plugin.saveStructureConfig();

        structureViewers.remove(structureId);
        structures.remove(structureId);

        return true;
    }

    /**
     * Retrieves the {@code Structure} associated with the specified structure ID.
     * The structure is fetched from the internal registry and returned as an {@code Optional}.
     *
     * @param structureId The unique identifier of the structure to retrieve. Must not be null.
     * @return An {@code Optional} containing the {@link Structure} associated with the given ID,
     *         or an empty {@code Optional} if no such structure exists.
     */
    public Optional<Structure> getStructure(@NonNull String structureId) {
        return Optional.ofNullable(structures.get(structureId));
    }

    /**
     * Adds a viewer to the specified structure. If the structure exists, the provided player
     * is added as a viewer. Additionally, the player will receive block changes related to
     * the structure and the appear animation (if defined) will be triggered.
     *
     * @param structureId The unique identifier of the structure to add the viewer to.
     *                    Must not be null.
     * @param player The player to add as a viewer. Must not be null.
     * @return {@code true} if the viewer was successfully added; {@code false} if the structure
     *         does not exist or its viewer set is unavailable.
     */
    public boolean addViewer(@NonNull String structureId, @NonNull Player player) {
        Set<UUID> viewers = structureViewers.get(structureId);

        if (viewers == null) {
            return false;
        }

        viewers.add(player.getUniqueId());

        packetBlockManager
                .getBlocksByMetadata(createStructureIdMetadataKey(structureId))
                .forEach(block -> {
                    if (!(block instanceof PacketBlockGroup group)) {
                        return;
                    }

                    group.addAndUpdateViewer(player);
                });

        getStructure(structureId).ifPresent(structure -> {
            animationManager.getAnimation(structure.getAppearAnimationId()).ifPresent(structureAnimation -> {
                structureAnimation.onActivate(
                        ContextBuilder.create()
                                .with(player)
                                .with(StructureContextKeys.STRUCTURE, structure)
                                .with(StructureContextKeys.ANIMATION_TYPE, StructureAnimation.Type.SHOW)
                                .build()
                );
            });
        });

        return true;
    }

    /**
     * Removes a viewer from the specified structure. If the player is specified,
     * they will be removed from the set of viewers associated with the structure.
     * Additionally, the viewer will be removed from all related packet blocks and
     * a "disappear" animation may be triggered for the structure.
     *
     * @param structureId The unique identifier of the structure from which the viewer will be removed.
     *                    Must not be null.
     * @param player The player to remove as a viewer. Can be null, in which case no operation is performed.
     * @return {@code true} if the operation was executed successfully; {@code false} if either
     *         the structure's viewer set does not exist or the player is null.
     */
    public boolean removeViewer(@NonNull String structureId, @Nullable Player player) {
        Set<UUID> viewers = structureViewers.get(structureId);

        if (viewers == null || player == null) {
            return false;
        }

        viewers.remove(player.getUniqueId());
        packetBlockManager
                .getBlocksByMetadata(createStructureIdMetadataKey(structureId))
                .forEach(block -> block.removeViewer(player));

        getStructure(structureId).ifPresent(structure -> {
            animationManager.getAnimation(structure.getDisappearAnimationId()).ifPresent(structureAnimation -> {
                structureAnimation.onActivate(
                        ContextBuilder.create()
                                .with(player)
                                .with(StructureContextKeys.STRUCTURE, structure)
                                .with(StructureContextKeys.ANIMATION_TYPE, StructureAnimation.Type.HIDE)
                                .build()
                );
            });
        });

        return true;
    }

    /**
     * Retrieves the viewers associated with the specified structure ID.
     * Viewers are represented as a set of UUIDs corresponding to the players
     * currently viewing the structure. If no viewers are associated with the
     * given structure ID, an empty set is returned.
     *
     * @param structureId The unique identifier of the structure whose viewers are to be retrieved.
     *                    Must not be null.
     * @return A set of UUIDs representing the viewers associated with the structure ID.
     *         Returns an empty set if the structure ID has no viewers.
     */
    public Set<UUID> getViewers(@NonNull String structureId) {
        return new HashSet<>(structureViewers.getOrDefault(structureId, Set.of()));
    }

}
