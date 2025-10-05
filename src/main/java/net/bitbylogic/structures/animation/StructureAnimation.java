package net.bitbylogic.structures.animation;

import lombok.NonNull;
import net.bitbylogic.utils.context.Context;

public interface StructureAnimation {

    /**
     * Retrieves the unique identifier of this animation.
     *
     * @return a non-null {@link String} representing the identifier of this animation.
     */
    @NonNull String getId();

    /**
     * Activates the animation using the provided context. This method is intended to process
     * specific logic related to the animation type (e.g., SHOW, HIDE) and any additional data
     * relevant to the animation context.
     *
     * @param context the {@code Context} containing relevant information for the animation's activation.
     *                Must not be null.
     */
    void onActivate(@NonNull Context context);

    /**
     * Represents the {@link Type} of animation used to visually display or reveal a structure.
     */
    enum Type {

        /**
         * Represents the `SHOW` type for a {@link StructureAnimation}.
         * This type is used when a structure is revealed to a player.
         */
        SHOW,
        /**
         * Represents the `HIDE` type for a {@link StructureAnimation}.
         * This type is used when a structure is hidden from a player.
         */
        HIDE;

    }

}
