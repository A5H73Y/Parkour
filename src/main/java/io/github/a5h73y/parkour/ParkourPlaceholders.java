package io.github.a5h73y.parkour;

import static io.github.a5h73y.parkour.other.ParkourConstants.DEATHS_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.PLAYER_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.TIME_PLACEHOLDER;

import io.github.a5h73y.parkour.database.TimeEntry;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.type.player.ParkourSession;
import io.github.a5h73y.parkour.type.player.PlayerInfo;
import io.github.a5h73y.parkour.utility.DateTimeUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.cache.GenericCache;
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

    private static final String INVALID_SYNTAX = TranslationUtils.getTranslation("PlaceholderAPI.InvalidSyntax", false);
    private static final String NO_TIME_RECORDED = TranslationUtils.getTranslation("PlaceholderAPI.NoTimeRecorded", false);
    private static final String TOP_TEN_RESULT = TranslationUtils.getTranslation("PlaceholderAPI.TopTenResult", false);

    private final Parkour parkour;
    private final GenericCache<String, String> cache;

    /**
     * Construct the Parkour Placeholders functionality.
     * A Cache is used for repeated expensive calls to the database.
     * @param parkour plugin instance
     */
    public ParkourPlaceholders(final Parkour parkour) {
        this.parkour = parkour;
        this.cache = new GenericCache<>(parkour.getConfig().getLong("Plugin.PlaceholderAPI.CacheTime"));
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
                return getCoursePlaceholderValue(offlinePlayer, arguments);

            case "cu":
            case "current":
                return getCurrentCoursePlaceholderValue(offlinePlayer, arguments);

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
                return String.valueOf(CourseInfo.getAllCourseNames().size());

            case "global_player_count":
                return String.valueOf(parkour.getPlayerManager().getNumberOfParkourPlayer());

            default:
                return INVALID_SYNTAX;
        }
    }

    private String getPlayerPlaceholderValue(OfflinePlayer offlinePlayer, String... arguments) {
        if (offlinePlayer == null) {
            return "";
        }

        switch (arguments[1]) {
            case "level":
                return String.valueOf(PlayerInfo.getParkourLevel(offlinePlayer));

            case "rank":
                return PlayerInfo.getParkourRank(offlinePlayer);

            case "parkoins":
                return String.valueOf(PlayerInfo.getParkoins(offlinePlayer));

            case "last":
                switch (arguments[2]) {
                    case "completed":
                        return PlayerInfo.getLastCompletedCourse(offlinePlayer);

                    case "joined":
                        return PlayerInfo.getLastPlayedCourse(offlinePlayer);

                    default:
                        return INVALID_SYNTAX;
                }

            case "courses":
                switch (arguments[2]) {
                    case "completed":
                        return String.valueOf(PlayerInfo.getNumberOfCompletedCourses(offlinePlayer));

                    case "uncompleted":
                        return String.valueOf(PlayerInfo.getNumberOfUncompletedCourses(offlinePlayer));

                    default:
                        return INVALID_SYNTAX;
                }

            case "course":
                if (arguments.length == 4 && arguments[3].equals("completed")) {
                    return getOrRetrieveCache(offlinePlayer.getName() + arguments[2] + arguments[3],
                            () -> getCompletedMessage(offlinePlayer, arguments[3]));
                } else {
                    return INVALID_SYNTAX;
                }

            case "prize":
                if ("delay".equals(arguments[2])) {
                    if (CourseInfo.hasRewardDelay(arguments[3])) {
                        return DateTimeUtils.getDelayTimeRemaining(offlinePlayer, arguments[3]);
                    } else {
                        return "0";
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

    private String getCoursePlaceholderValue(OfflinePlayer offlinePlayer, String... arguments) {
        switch (arguments[1]) {
            case "displayname":
                if (arguments.length != 3) {
                    return INVALID_SYNTAX;
                }

                return CourseInfo.getCourseDisplayName(arguments[2]);

            case "record":
                if (arguments.length != 4) {
                    return INVALID_SYNTAX;
                }

                return getCourseRecord(arguments[2], arguments[3]);

            case "completions":
                if (arguments.length != 3) {
                    return INVALID_SYNTAX;
                }

                return String.valueOf(CourseInfo.getCompletions(arguments[2]));

            case "views":
                if (arguments.length != 3) {
                    return INVALID_SYNTAX;
                }

                return String.valueOf(CourseInfo.getViews(arguments[2]));

            case "joinfee":
                if (arguments.length != 3) {
                    return INVALID_SYNTAX;
                }

                return String.valueOf(CourseInfo.getEconomyJoiningFee(arguments[2]));

            case "ecoreward":
                if (arguments.length != 3) {
                    return INVALID_SYNTAX;
                }

                return String.valueOf(CourseInfo.getEconomyFinishReward(arguments[2]));

            case "players":
                if (arguments.length != 3) {
                    return INVALID_SYNTAX;
                }

                return String.valueOf(parkour.getPlayerManager().getNumberOfPlayersOnCourse(arguments[2]));

            case "playerlist":
                if (arguments.length != 3) {
                    return INVALID_SYNTAX;
                }

                return String.join(", ", parkour.getPlayerManager().getPlayerNamesOnCourse(arguments[2]));

            default:
                return INVALID_SYNTAX;
        }
    }

    private String getCurrentCoursePlaceholderValue(OfflinePlayer offlinePlayer, String... arguments) {
        Player player = offlinePlayer.getPlayer();
        if (player == null) {
            return "";
        }
        ParkourSession session = parkour.getPlayerManager().getParkourSession(player.getPlayer());

        if (session == null) {
            return "";
        }

        switch (arguments[1]) {
            case "course":
                if (arguments.length < 3) {
                    return INVALID_SYNTAX;
                }

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
                        return INVALID_SYNTAX;
                }

            case "checkpoint":
                return String.valueOf(session.getCurrentCheckpoint());

            default:
                return INVALID_SYNTAX;
        }
    }

    private String getCourseLeaderboardPlaceholderValue(String... arguments) {
        return getCourseRecord(arguments[1], arguments[3], Integer.parseInt(arguments[2]));
    }

    private String getTopTenPlaceholderValue(String... arguments) {
        int position = Integer.parseInt(arguments[2]);
        TimeEntry result = parkour.getDatabase().getNthBestTime(arguments[1], position);

        if (result == null) {
            return NO_TIME_RECORDED;

        } else {
            return TOP_TEN_RESULT.replace(PLAYER_PLACEHOLDER, result.getPlayerName())
                    .replace("%POSITION%", String.valueOf(position))
                    .replace(TIME_PLACEHOLDER, DateTimeUtils.displayCurrentTime(result.getTime()))
                    .replace(DEATHS_PLACEHOLDER, String.valueOf(result.getDeaths()));
        }
    }

    private TimeEntry getTopPlayerResultForCourse(Player player, String courseName) {
        List<TimeEntry> time = parkour.getDatabase().getTopPlayerCourseResults(player, courseName, 1);
        return time.isEmpty() ? null : time.get(0);
    }

    private String getCompletedMessage(OfflinePlayer player, String courseName) {
        boolean resultFound = parkour.getDatabase().hasPlayerAchievedTime(player, courseName);
        String key = resultFound ? "PlaceholderAPI.CurrentCourseCompleted" : "PlaceholderAPI.CurrentCourseNotCompleted";
        return TranslationUtils.getTranslation(key, false);
    }

    private String getCourseRecord(String courseName, String key) {
        return getCourseRecord(courseName, key, 1);
    }

    private String getCourseRecord(String courseName, String key, Integer position) {
        return getOrRetrieveCache(courseName + key + position,
                () -> extractResultDetails(parkour.getDatabase().getNthBestTime(courseName, position), key));
    }

    private String getPersonalCourseRecord(Player player, String courseName, String key) {
        return getOrRetrieveCache(player.getName() + courseName + key,
                () -> extractResultDetails(getTopPlayerResultForCourse(player, courseName), key));
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

    private String getOrRetrieveCache(String key, Supplier<String> callback) {
        // check if the key exists or its 'get' is about to expire.
        if (!cache.containsKey(key) || !cache.get(key).isPresent()) {
            cache.put(key, callback.get());
        }

        return cache.get(key).orElse(NO_TIME_RECORDED);
    }
}
