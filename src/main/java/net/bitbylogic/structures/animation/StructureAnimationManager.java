package net.bitbylogic.structures.animation;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StructureAnimationManager {

    private final Map<String, StructureAnimation> registeredAnimations = new HashMap<>();

    /**
     * Registers a {@link StructureAnimation} into the manager if it has not already been registered.
     *
     * @param animation the {@link StructureAnimation} instance to register. Must not be null.
     * @return {@code true} if the animation was successfully registered, or {@code false} if an animation
     *         with the same ID is already registered.
     */
    public boolean registerAnimation(@NonNull StructureAnimation animation) {
        if(registeredAnimations.containsKey(animation.getId().toLowerCase())) {
            return false;
        }

        registeredAnimations.put(animation.getId().toLowerCase(), animation);
        return true;
    }

    /**
     * Retrieves the animation registered with the specified identifier.
     *
     * @param id The identifier of the animation to retrieve. Can be null, in which case an empty {@code Optional} is returned.
     * @return An {@code Optional} containing the {@code StructureAnimation} if the identifier is non-null
     *         and an animation is registered for it; otherwise, an empty {@code Optional}.
     */
    public Optional<StructureAnimation> getAnimation(@Nullable String id) {
        if(id == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(registeredAnimations.get(id.toLowerCase()));
    }

    /**
     * Retrieves an immutable view of all registered animations within the manager.
     *
     * @return a {@code Map} containing all registered animations, where the keys are the animation IDs
     *         and the values are the corresponding {@code StructureAnimation} instances.
     */
    public Map<String, StructureAnimation> getRegisteredAnimations() {
        return Map.copyOf(registeredAnimations);
    }

}
