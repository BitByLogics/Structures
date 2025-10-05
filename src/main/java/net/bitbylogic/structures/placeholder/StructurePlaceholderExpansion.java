package net.bitbylogic.structures.placeholder;

import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.bitbylogic.structures.manager.StructureManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
public class StructurePlaceholderExpansion extends PlaceholderExpansion {

    private final StructureManager structureManager;

    @Override
    public @NotNull String getIdentifier() {
        return "structures";
    }

    @Override
    public @NotNull String getAuthor() {
        return "BitByLogic";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        String[] args = params.split("_");

        if(args[0].equalsIgnoreCase("viewing")) {
            if(args.length == 1) {
                return "Missing Structure ID";
            }

            return structureManager.getViewers(args[1]).contains(player.getUniqueId()) ? "true" : "false";
        }

        return params;
    }

}
