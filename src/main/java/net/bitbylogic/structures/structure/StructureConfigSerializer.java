package net.bitbylogic.structures.structure;

import lombok.NonNull;
import net.bitbylogic.utils.condition.ConditionParser;
import net.bitbylogic.utils.condition.parsed.ParsedCondition;
import net.bitbylogic.utils.config.ConfigSerializer;
import net.bitbylogic.utils.context.ContextBuilder;
import net.bitbylogic.utils.location.LocationUtil;
import net.bitbylogic.utils.trigger.Trigger;
import net.bitbylogic.utils.trigger.TriggerParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Predicate;

public class StructureConfigSerializer implements ConfigSerializer<Structure> {

    @Override
    public Optional<Structure> serializeFrom(@NonNull ConfigurationSection section) {
        String id = section.getName();

        Map<Location, BlockData> blocks = new HashMap<>();

        section.getStringList("Blocks").forEach(savedBlockData -> {
            String[] splitData = savedBlockData.split(";");
            Location location = LocationUtil.stringToLocation(splitData[0]);
            BlockData blockData = Bukkit.createBlockData(splitData[1]);

            blocks.put(location, blockData);
        });

        List<Trigger> showTriggers = TriggerParser.loadTriggers(section.getConfigurationSection("Show-Triggers"));
        List<Trigger> hideTriggers = TriggerParser.loadTriggers(section.getConfigurationSection("Hide-Triggers"));

        List<ParsedCondition> showConditions = ConditionParser.loadConditions(section.getConfigurationSection("Show-Conditions"));
        List<ParsedCondition> hideConditions = ConditionParser.loadConditions(section.getConfigurationSection("Hide-Conditions"));

        List<Predicate<Player>> showPredicates = new ArrayList<>();
        List<Predicate<Player>> hidePredicates = new ArrayList<>();

        showConditions.forEach(parsedCondition -> showPredicates.add(player -> parsedCondition.matches(ContextBuilder.create().with(player).build())));
        hideConditions.forEach(parsedCondition -> hidePredicates.add(player -> parsedCondition.matches(ContextBuilder.create().with(player).build())));

        String appearAnimationId = section.getString("Appear-Animation");
        String disappearAnimationId = section.getString("Disappear-Animation");

        boolean requireTrigger = section.getBoolean("Require-Trigger", false);

        return Optional.of(new Structure(id, blocks, showTriggers, hideTriggers, showPredicates, hidePredicates, appearAnimationId, disappearAnimationId, requireTrigger));
    }

    @Override
    public ConfigurationSection serializeTo(@NonNull ConfigurationSection section, @NonNull Structure structure) {
        ConfigurationSection structureSection = section.createSection(structure.getId());

        List<String> blocks = new ArrayList<>();
        structure.getBlocks().forEach((location, blockData) -> blocks.add(LocationUtil.locationToString(location) + ";" + blockData.getAsString()));

        structureSection.set("Blocks", blocks);

        structureSection.set("Show-Triggers", structure.getShowTriggers().stream().map(Trigger::getId).toList());
        structureSection.set("Hide-Triggers", structure.getHideTriggers().stream().map(Trigger::getId).toList());

        structureSection.set("Appear-Animation", structure.getAppearAnimationId());
        structureSection.set("Disappear-Animation", structure.getDisappearAnimationId());

        return structureSection;
    }

}
