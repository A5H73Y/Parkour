package io.github.a5h73y.parkour.type.kit;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

/**
 * ParkourKit.
 * A collection of Materials that perform an action on a Course.
 * Named after Kits as you can populate your inventory with the kit contents to
 * design and build the course.
 * A ParkourKit can be linked to one to many courses, and when updated it will affect the linked Courses.
 * A Material can only be used once, however there can be multiples of each action type.
 */
public class ParkourKit implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final Map<Material, ParkourKitAction> parkourActions;

    /**
     * Construct a ParkourKit from the details.
     * Each {@link Material} will have a corresponding {@link ParkourKitAction}.
     *
     * @param name parkour kit name
     * @param parkourActions the Materials and associated action
     */
    public ParkourKit(final String name, Map<Material, ParkourKitAction> parkourActions) {
        this.name = name;
        this.parkourActions = parkourActions;
    }

    /**
     * Get the corresponding {@link ParkourKitAction} for the {@link Material}.
     * @param material material
     * @return matching action
     */
    @Nullable
    public ParkourKitAction getAction(Material material) {
        return parkourActions.get(material);
    }

    /**
     * Get each {@link Material} defined in the ParkourKit.
     * @return each Material
     */
    public Set<Material> getMaterials() {
        return parkourActions.keySet();
    }

    /**
     * Get the ParkourKit name.
     * @return kit name
     */
    public String getName() {
        return name;
    }
}
