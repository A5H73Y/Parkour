package io.github.a5h73y.parkour;

import static io.github.a5h73y.parkour.other.ParkourConstants.DEATHS_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.PLAYER_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.POSITION_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.TIME_PLACEHOLDER;

import io.github.a5h73y.parkour.database.TimeEntry;
import io.github.a5h73y.parkour.type.course.CourseConfig;
import io.github.a5h73y.parkour.type.player.PlayerConfig;
import io.github.a5h73y.parkour.type.player.session.ParkourSession;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.cache.GenericCache;
import io.github.a5h73y.parkour.utility.time.DateTimeUtils;
import java.util.List;
import java.util.function.Supplier;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Parkour's implementation of {@link PlaceholderExpansion}.
 * Various Parkour related information can be retrieved through placeholders.
 */
public class ParkourPlaceholders extends PlaceholderExpansion {

    private static final String UNKNOWN_COURSE = TranslationUtils.getTranslation("PlaceholderAPI.UnknownCourse", false);
    private static final String INVALID_SYNTAX = TranslationUtils.getTranslation("PlaceholderAPI.InvalidSyntax", false);
    private static final String NO_TIME_RECORDED = TranslationUtils.getTranslation("PlaceholderAPI.NoTimeRecorded", false);
    private static final String TOP_TEN_RESULT = TranslationUtils.getTranslation("PlaceholderAPI.TopTenResult", false);
    private static final String COURSE_ACTIVE = TranslationUtils.getTranslation("PlaceholderAPI.CourseActive", false);
    private static final String COURSE_INACTIVE = TranslationUtils.getTranslation("PlaceholderAPI.CourseInactive", false);
    private static final String NO_COOLDOWN_REMAINING = TranslationUtils.getTranslation("PlaceholderAPI.NoPrizeCooldown", false);

    private final Parkour parkour;
    private final GenericCache<String, String> cache;

    /**
     * Construct the Parkour Placeholders functionality.
     * A Cache is used for repeated expensive calls to the database.
     * @param parkour plugin instance
     */
    public ParkourPlaceholders(final Parkour parkour) {
        this.parkour = parkour;
        this.cache = new GenericCache<>(parkour.getParkourConfig().getLong("Plugin.PlaceholderAPI.CacheTime"));
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return Parkour.PLUGIN_NAME;
    }

    @NotNull
    @Override
    public String getAuthor() {
        return parkour.getDescription().getAuthors().toString();
    }

    @NotNull
    @Override
    public String getVersion() {
        return parkour.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(final OfflinePlayer offlinePlayer,
                            final @NotNull String placeholder) {
        return retrieveValue(offlinePlayer, placeholder.toLowerCase(), placeholder.toLowerCase().split("_"));
    }

    private String retrieveValue(OfflinePlayer offlinePlayer,
                                 @NotNull String placeholder,
                                 @NotNull String... arguments) {


        if (arguments.length < 2) {
            return INVALID_SYNTAX;
        }

        // parent command
        switch (arguments[0]) {
            case "gl":
            case "global":
                return getGlobalPlaceholderValue(placeholder);

            case "pl":
            case "player":
                return getPlayerPlaceholderValue(offlinePlayer, arguments);

            case "co":
            case "course":
                if (arguments.length < 3) {
                    return INVALID_SYNTAX;
                }
                return getCoursePlaceholderValue(arguments);

            case "cu":
            case "current":
                return getCurrentPlaceholderValue(offlinePlayer, arguments);

            case "lb":
            case "leaderboard":
                if (arguments.length != 4 || !ValidationUtils.isPositiveInteger(arguments[2])) {
                    return INVALID_SYNTAX;
                }

                return getCourseLeaderboardPlaceholderValue(arguments);

            case "tt":
            case "topten":
                if (arguments.length != 3 || !ValidationUtils.isPositiveInteger(arguments[2])) {
                    return INVALID_SYNTAX;
                }
                return getTopTenPlaceholderValue(arguments);

            default:
                return INVALID_SYNTAX;
        }
    }

    private String getGlobalPlaceholderValue(String placeholder) {
        switch (placeholder) {
            case "global_version":
                return getVersion();

            case "global_course_count":
                return String.valueOf(parkour.getCourseManager().getCourseNames().size());

            case "global_player_count":
                return String.valueOf(parkour.getParkourSessionManager().getNumberOfParkourPlayers());

            default:
                return INVALID_SYNTAX;
        }
    }

    private String getPlayerPlaceholderValue(OfflinePlayer offlinePlayer, String... arguments) {
        if (offlinePlayer == null) {
            return "";
        }
        PlayerConfig playerConfig = PlayerConfig.getConfig(offlinePlayer);

        switch (arguments[1]) {
            case "level":
                return String.valueOf(playerConfig.getParkourLevel());

            case "rank":
                return playerConfig.getParkourRank();

            case "parkoins":
                return String.valueOf(playerConfig.getParkoins());

            case "last":
                if (arguments.length < 3) {
                    return INVALID_SYNTAX;
                }
                switch (arguments[2]) {
                    case "completed":
                        return playerConfig.getLastCompletedCourse();

                    case "joined":
                        return playerConfig.getLastPlayedCourse();

                    default:
                        return INVALID_SYNTAX;
                }

            case "courses":
                if (arguments.length < 3) {
                    return INVALID_SYNTAX;
                }
                switch (arguments[2]) {
                    case "completed":
                        return String.valueOf(parkour.getConfigManager().getCourseCompletionsConfig()
                                .getNumberOfCompletedCourses(offlinePlayer));

                    case "uncompleted":
                        return String.valueOf(parkour.getPlayerManager().getNumberOfUncompletedCourses(offlinePlayer));

                    default:
                        return INVALID_SYNTAX;
                }

            case "course":
                if (arguments.length < 3) {
                    return INVALID_SYNTAX;
                }

                switch (arguments[2]) {
                    case "completed":
                        return getOrRetrieveCache(offlinePlayer.getName() + arguments[2] + arguments[3],
                                () -> getCompletedMessage(offlinePlayer, arguments[3]));

                    case "position":
                        return getOrRetrieveCache(offlinePlayer.getName() + arguments[2] + arguments[3],
                                () -> getLeaderboardPosition(offlinePlayer, arguments[3]));

                    default:
                        return INVALID_SYNTAX;
                }

            case "prize":
                if (arguments.length < 4) {
                    return INVALID_SYNTAX;
                }
                if ("delay".equals(arguments[2])) {
                    if (parkour.getConfigManager().getCourseConfig(arguments[3]).hasRewardDelay()) {
                        return DateTimeUtils.getDelayTimeRemaining(offlinePlayer, arguments[3]);
                    } else {
                        return NO_COOLDOWN_REMAINING;
                    }
                }
                return INVALID_SYNTAX;

            case "personal":
                if (arguments.length != 5 && !arguments[2].equals("best")) {
                    return INVALID_SYNTAX;
                }
                Player player = offlinePlayer.getPlayer();
                if (player == null) {
                    return "";
                }
                return getPersonalCourseRecord(player, arguments[3], arguments[4]);

            default:
                return INVALID_SYNTAX;
        }
    }

    private String getCoursePlaceholderValue(String... arguments) {
        if (!parkour.getCourseManager().doesCourseExist((arguments[2]))) {
            return UNKNOWN_COURSE;
        }

        CourseConfig courseConfig = parkour.getConfigManager().getCourseConfig(arguments[2]);

        switch (arguments[1]) {
            case "displayname":
                return courseConfig.getCourseDisplayName();

            case "record":
                if (arguments.length != 4) {
                    return INVALID_SYNTAX;
                }

                return getCourseRecord(arguments[2], arguments[3]);

            case "completions":
                return String.valueOf(courseConfig.getCompletions());

            case "views":
                return String.valueOf(courseConfig.getViews());

            case "creator":
                return String.valueOf(courseConfig.getCreator());

            case "minparkourlevel":
                return String.valueOf(courseConfig.getMinimumParkourLevel());

            case "rewardparkourlevel":
                return String.valueOf(courseConfig.getRewardParkourLevel());

            case "rewardparkourleveladd":
                return String.valueOf(courseConfig.getRewardParkourLevelIncrease());

            case "parkourmode":
                return courseConfig.getParkourModeName();

            case "maxdeaths":
                return String.valueOf(courseConfig.getMaximumDeaths());

            case "maxtime":
                return DateTimeUtils.convertSecondsToTime(courseConfig.getMaximumTime());

            case "checkpoints":
                return String.valueOf(courseConfig.getCheckpointAmount());

            case "joinfee":
                return String.valueOf(courseConfig.getEconomyJoiningFee());

            case "ecoreward":
                return String.valueOf(courseConfig.getEconomyFinishReward());

            case "players":
                return String.valueOf(parkour.getParkourSessionManager().getNumberOfPlayersOnCourse(arguments[2]));

            case "playerlist":
                return String.join(", ", parkour.getParkourSessionManager().getPlayerNamesOnCourse(arguments[2]));

            case "status":
                return courseConfig.getReadyStatus() ? COURSE_ACTIVE : COURSE_INACTIVE;

            default:
                return INVALID_SYNTAX;
        }
    }

    private String getCurrentPlaceholderValue(OfflinePlayer offlinePlayer, String... arguments) {
        Player player = offlinePlayer.getPlayer();
        if (player == null) {
            return "";
        }
        ParkourSession session = parkour.getParkourSessionManager().getParkourSession(player);

        if (session == null) {
            return "";
        }

        switch (arguments[1]) {
            case "course":
                if (arguments.length < 3) {
                    return INVALID_SYNTAX;
                }
                return getCurrentCoursePlaceholderValue(player, session, arguments);

            case "checkpoint":
                if (arguments.length < 3) { // deprecated
                    return String.valueOf(session.getCurrentCheckpoint());
                }
                return getCurrentCheckpointPlaceholderValue(player, session, arguments);

            default:
                return INVALID_SYNTAX;
        }
    }

    private String getCurrentCheckpointPlaceholderValue(Player player, ParkourSession session, String... arguments) {
        switch (arguments[2]) {
            case "number":
                return String.valueOf(session.getCurrentCheckpoint());

            case "hologram":
                if (arguments.length != 5 || !ValidationUtils.isInteger(arguments[4])) {
                    return INVALID_SYNTAX;
                }
                return getCheckpointHologramMessage(session, arguments[3], Integer.parseInt(arguments[4]));

            default:
                return INVALID_SYNTAX;
        }
    }

    private String getCurrentCoursePlaceholderValue(Player player, ParkourSession session, String... arguments) {
        switch (arguments[2]) {
            case "name":
                return session.getCourseName();

            case "displayname":
                return session.getCourse().getDisplayName();

            case "deaths":
                return String.valueOf(session.getDeaths());

            case "timer":
                return session.getDisplayTime();

            case "checkpoints":
                return String.valueOf(session.getCourse().getNumberOfCheckpoints());

            case "completed":
                return getOrRetrieveCache(player.getName() + arguments[2] + session.getCourseName(),
                        () -> getCompletedMessage(player, session.getCourseName()));

            case "record":
                if (arguments.length != 4) {
                    return INVALID_SYNTAX;
                }
                return getCourseRecord(session.getCourseName(), arguments[3]);

            case "personal":
                if (arguments.length != 5 && !arguments[3].equals("best")) {
                    return INVALID_SYNTAX;
                }
                return getPersonalCourseRecord(player, session.getCourseName(), arguments[4]);

            case "remaining":
                if (arguments.length != 4 && !arguments[3].equals("deaths")) {
                    return INVALID_SYNTAX;
                }
                return String.valueOf(session.getRemainingDeaths());

            default:
                return getCoursePlaceholderValue("", arguments[2], session.getCourseName());
        }
    }

    private String getCourseLeaderboardPlaceholderValue(String... arguments) {
        return getCourseRecord(arguments[1], arguments[3], Integer.parseInt(arguments[2]));
    }

    private String getTopTenPlaceholderValue(String... arguments) {
        int position = Integer.parseInt(arguments[2]);
        TimeEntry result = parkour.getDatabaseManager().getNthBestTime(arguments[1], position);

        if (result == null) {
            return NO_TIME_RECORDED;

        } else {
            return TOP_TEN_RESULT.replace(PLAYER_PLACEHOLDER, result.getPlayerName())
                    .replace(POSITION_PLACEHOLDER, String.valueOf(position))
                    .replace(TIME_PLACEHOLDER, DateTimeUtils.displayCurrentTime(result.getTime()))
                    .replace(DEATHS_PLACEHOLDER, String.valueOf(result.getDeaths()));
        }
    }

    private TimeEntry getTopPlayerResultForCourse(Player player, String courseName) {
        List<TimeEntry> time = parkour.getDatabaseManager().getTopPlayerCourseResults(player, courseName, 1);
        return time.isEmpty() ? null : time.get(0);
    }

    private String getCompletedMessage(OfflinePlayer player, String courseName) {
        boolean resultFound = parkour.getDatabaseManager().hasPlayerAchievedTime(player, courseName);
        String key = resultFound ? "PlaceholderAPI.CurrentCourseCompleted" : "PlaceholderAPI.CurrentCourseNotCompleted";
        return TranslationUtils.getTranslation(key, false);
    }

    private String getCourseRecord(String courseName, String key) {
        return getCourseRecord(courseName, key, 1);
    }

    private String getCourseRecord(String courseName, String key, Integer position) {
        return getOrRetrieveCache(courseName + key + position,
                () -> extractResultDetails(parkour.getDatabaseManager().getNthBestTime(courseName, position), key));
    }

    private String getPersonalCourseRecord(Player player, String courseName, String key) {
        return getOrRetrieveCache(player.getName() + courseName + key,
                () -> extractResultDetails(getTopPlayerResultForCourse(player, courseName), key));
    }

    private String getLeaderboardPosition(OfflinePlayer player, String courseName) {
        int result = parkour.getDatabaseManager().getPositionOnLeaderboard(player, courseName);
        return result < 0 ? NO_TIME_RECORDED : String.valueOf(result + 1);
    }

    private String extractResultDetails(TimeEntry result, String key) {
        if (result == null) {
            return NO_TIME_RECORDED;

        } else {
            switch (key) {
                case "time":
                    return DateTimeUtils.displayCurrentTime(result.getTime());

                case "milliseconds":
                    return String.valueOf(result.getTime());

                case "deaths":
                    return String.valueOf(result.getDeaths());

                case "player":
                    return result.getPlayerName();

                default:
                    return INVALID_SYNTAX;
            }
        }
    }

    private String getCheckpointHologramMessage(ParkourSession session, String course, int checkpoint) {
        if (session.getCurrentCheckpoint() + 1 == checkpoint
                && session.getCourseName().equals(course.toLowerCase())) {
            return TranslationUtils.getValueTranslation("PlaceholderAPI.CheckpointHologram",
                    Integer.toString(checkpoint), false);
        } else {
            return "";
        }
    }

    private String getOrRetrieveCache(String key, Supplier<String> callback) {
        // check if the key exists or its 'get' is about to expire.
        if (!cache.containsKey(key) || cache.get(key).isEmpty()) {
            cache.put(key, callback.get());
        }

        return cache.get(key).orElse(NO_TIME_RECORDED);
    }

    public void clearCache() {
        this.cache.clear();
    }
}
