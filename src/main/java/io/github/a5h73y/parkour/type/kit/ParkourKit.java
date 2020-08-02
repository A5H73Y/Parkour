package io.github.a5h73y.parkour.type.kit;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;

public class ParkourKit implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final Map<Material, ParkourKitAction> parkourActions;

    public ParkourKit(final String name, Map<Material, ParkourKitAction> parkourActions) {
        this.name = name;
        this.parkourActions = parkourActions;
    }

    public Set<Material> getMaterials() {
        return parkourActions.keySet();
    }

    public String getName() {
        return name;
    }

    public ParkourKitAction getAction(Material material) {
        return parkourActions.get(material);
    }
}
