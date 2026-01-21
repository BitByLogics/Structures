package net.bitbylogic.structures.structure;

import lombok.*;
import net.bitbylogic.utils.trigger.Trigger;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Structure {

    private static final StructureConfigSerializer SERIALIZER = new StructureConfigSerializer();

    private final String id;
    private final Map<Location, BlockData> blocks;

    private final List<Trigger> showTriggers;
    private final List<Trigger> hideTriggers;

    private final List<Predicate<Player>> showConditions;
    private final List<Predicate<Player>> hideConditions;

    @Setter
    private @Nullable String appearAnimationId;

    @Setter
    private @Nullable String disappearAnimationId;

    private final boolean requireTrigger;

    public Structure(@NonNull String id) {
        this.id = id;
        this.blocks = new HashMap<>();

        this.showTriggers = new ArrayList<>();
        this.hideTriggers = new ArrayList<>();

        this.showConditions = new ArrayList<>();
        this.hideConditions = new ArrayList<>();

        this.appearAnimationId = null;
        this.disappearAnimationId = null;

        this.requireTrigger = false;
    }

    public static StructureConfigSerializer getSerializer() {
        return SERIALIZER;
    }

}
