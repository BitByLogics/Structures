package net.bitbylogic.structures.util;

import org.bukkit.block.structure.StructureRotation;

public class RotationUtil {

    /**
     * Determines the opposite structure rotation based on the given yaw value.
     *
     * @param yaw The yaw value in degrees, which represents the direction of rotation. This value is normalized
     *            to a range of 0 to 360 degrees before determining the opposite rotation.
     * @return The opposite {@link StructureRotation} based on the normalized yaw value.
     */
    public static StructureRotation getOppositeRotation(float yaw) {
        float normalizedYaw = ((yaw % 360) + 360) % 360;

        if (normalizedYaw >= 315 || normalizedYaw < 45) {
            return StructureRotation.CLOCKWISE_180;
        } else if (normalizedYaw >= 45 && normalizedYaw < 135) {
            return StructureRotation.COUNTERCLOCKWISE_90;
        } else if (normalizedYaw >= 135 && normalizedYaw < 225) {
            return StructureRotation.NONE;
        }

        return StructureRotation.CLOCKWISE_90;
    }

}
