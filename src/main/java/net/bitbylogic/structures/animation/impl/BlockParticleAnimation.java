package net.bitbylogic.structures.animation.impl;

import lombok.NonNull;
import net.bitbylogic.structures.animation.StructureAnimation;
import net.bitbylogic.structures.context.StructureContextKeys;
import net.bitbylogic.structures.structure.Structure;
import net.bitbylogic.utils.context.BukkitContextKeys;
import net.bitbylogic.utils.context.Context;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class BlockParticleAnimation implements StructureAnimation {

    @Override
    public @NonNull String getId() {
        return "block_particle";
    }

    @Override
    public void onActivate(@NonNull Context context) {
        Player player = context.get(BukkitContextKeys.PLAYER).orElse(null);
        Structure structure = context.get(StructureContextKeys.STRUCTURE).orElse(null);

        if(player == null || structure == null) {
            return;
        }

        structure.getBlocks().forEach((location, blockData) -> {
            player.spawnParticle(
                    Particle.BLOCK,
                    location.clone().add(0.5, 0.5, 0.5),
                    20,
                    0.5, 0.5, 0.5,
                    0.01,
                    blockData
            );
        });
    }

}
