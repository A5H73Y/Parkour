package io.github.a5h73y.parkour.type.player.quiet;

import java.io.File;
import java.util.List;
import de.leonhard.storage.Yaml;

public class QuietModeConfig extends Yaml {

    public QuietModeConfig(File file) {
        super(file);
    }

    public List<String> getQuietPlayers() {
        return this.getStringList("Quiet");
    }

    public void setQuietPlayers(List<String> players) {
        this.set("Quiet", players);
    }
}
