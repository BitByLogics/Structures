package net.bitbylogic.structures.context;

import net.bitbylogic.structures.animation.StructureAnimation;
import net.bitbylogic.structures.structure.Structure;
import net.bitbylogic.utils.context.ContextKey;

public class StructureContextKeys {

    /**
     * A static context key representing a {@link Structure} object. This key is used to store and
     * retrieve structure-related data within a contextual data system. Along with the animation type,
     * this key is used for the StructureAnimation onActivate system.
     */
    public static ContextKey<Structure> STRUCTURE = new ContextKey<>("structure", Structure.class);

    /**
     * Represents a context key that specifies the type of animation associated with a structure.
     * This key is used to define the action of animating a structure, such as showing or hiding it.
     * The type of animation is represented by the {@link StructureAnimation.Type} enum, which includes
     * types such as {@code SHOW} and {@code HIDE}.
     */
    public static ContextKey<StructureAnimation.Type> ANIMATION_TYPE = new ContextKey<>("animation_type", StructureAnimation.Type.class,
            context -> StructureAnimation.Type.SHOW);

}
