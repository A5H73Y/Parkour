package io.github.a5h73y.parkour.enums;

import io.github.a5h73y.parkour.Parkour;
import java.util.function.BiConsumer;
import org.bukkit.command.CommandSender;

public enum QuestionType {

    DELETE_COURSE("You are about to delete Course &b%s&f...",
            "&7This will remove all information about the Course ever existing, which includes all leaderboard data, course statistics and everything else the plugin knows about it.",
            (sender, value) -> Parkour.getInstance().getCourseManager().deleteCourse(sender, value)),

    DELETE_CHECKPOINT("You are about to delete Checkpoint &b%d &ffor Course &b%s&f...",
            "&7Deleting a checkpoint will impact everybody that is currently playing on the Course. You should not set a Course to ready and then continue to make changes.",
            (sender, value) -> Parkour.getInstance().getCheckpointManager().deleteCheckpoint(sender, value)),

    DELETE_LOBBY("You are about to delete Lobby &b%s&f...",
            "&7Deleting a Lobby will remove all information about it from the server.",
            (sender, value) -> Parkour.getInstance().getLobbyManager().deleteLobby(sender, value)),

    DELETE_PARKOUR_KIT("You are about to delete ParkourKit &b%s&f...",
            "&7Deleting a ParkourKit will remove all information about it from the server.",
            (sender, value) -> Parkour.getInstance().getParkourKitManager().deleteParkourKit(sender, value)),

    DELETE_AUTOSTART("You are about to delete the AutoStart at this location...",
            "&7Deleting an autostart will remove all information about it from the server.",
            (sender, value) -> Parkour.getInstance().getCourseManager().deleteAutoStart(sender, value)),

    RESET_COURSE("You are about to reset Course &b%s&f...",
            "&7Resetting a Course will delete all the statistics stored, which includes leaderboards and various Parkour attributes. This will NOT affect the spawn or checkpoints.",
            (sender, value) -> Parkour.getInstance().getCourseManager().resetCourse(sender, value)),


    RESET_PLAYER("You are about to reset Player &b%s&f...",
            "&7Resetting a Player will delete all their times across all Courses and delete all various Parkour attributes.",
            (sender, value) -> Parkour.getInstance().getPlayerManager().resetPlayer(sender, value)),


    RESET_LEADERBOARD("You are about to reset Leaderboards for &b%s&f...",
            "&7Resetting the leaderboards will remove all times from the database for this course. This will NOT affect the course in any other way.",
            (sender, value) -> Parkour.getInstance().getCourseManager().resetCourseLeaderboards(sender, value)),

    RESET_PLAYER_LEADERBOARD("You are about to reset &b%s &fLeaderboards on Course &b%s&f...",
            "&7Resetting the player's leaderboards will remove all times they have from the database for this course. This will NOT affect the player or course in any other way.",
            (sender, value) -> {
                String[] args = value.split(";");
                Parkour.getInstance().getCourseManager().resetPlayerCourseLeaderboards(sender, args[0], args[1]);
            }),

    RESET_PRIZES("You are about to reset the prizes for &b%s&f...",
            "Resetting the prizes for this course will set the prize to the default prize found in the main configuration file.",
            (sender, value) -> Parkour.getInstance().getCourseManager().resetPrize(sender, value));

    private final String actionSummary;
    private final String description;
    private final BiConsumer<CommandSender, String> outcome;

    QuestionType(String actionSummary, String description, BiConsumer<CommandSender, String> outcome) {
        this.actionSummary = actionSummary;
        this.description = description;
        this.outcome = outcome;
    }

    public void confirm(CommandSender sender, String value) {
        this.outcome.accept(sender, value);
    }

    public String getActionSummary() {
        return actionSummary;
    }

    public String getDescription() {
        return description;
    }
}
