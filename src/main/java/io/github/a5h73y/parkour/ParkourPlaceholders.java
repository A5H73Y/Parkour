package io.github.a5h73y.parkour;

import static io.github.a5h73y.parkour.other.ParkourConstants.AMOUNT_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.DEATHS_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.PLAYER_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.POSITION_PLACEHOLDER;
import static io.github.a5h73y.parkour.other.ParkourConstants.TIME_PLACEHOLDER;

import io.github.a5h73y.parkour.database.FirstPlacesEntry;
import io.github.a5h73y.parkour.database.TimeEntry;
import io.github.a5h73y.parkour.type.course.CourseConfig;
import io.github.a5h73y.parkour.type.player.PlayerConfig;
import io.github.a5h73y.parkour.type.player.session.ParkourSession;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.ValidationUtils;
import io.github.a5h73y.parkour.utility.cache.GenericCache;
import io.github.a5h73y.parkour.utility.time.DateTimeUtils;
import java.util.Date;
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
    private static final String TOP_FIRST_PLACE_RESULT = TranslationUtils.getTranslation("PlaceholderAPI.TopFirstPlaceResult", false);
    private static final String COURSE_ACTIVE = TranslationUtils.getTranslation("PlaceholderAPI.CourseActive", false);
    private static final String COURSE_INACTIVE = TranslationUtils.getTranslation("PlaceholderAPI.CourseInactive", false);
    private static final String NO_COOLDOWN_REMAINING = TranslationUtils.getTranslation("PlaceholderAPI.NoPrizeCooldown", false);
    private static final String PLAYER_NOT_FOUND = TranslationUtils.getTranslation("PlaceholderAPI.PlayerNotFound", false);

    private static final String COMPLETED = "completed";
    private static final String POSITION = "position";
    private static final String PRIZE = "prize";
    private static final String DELAY = "delay";
    private static final String PERSONAL = "personal";
    private static final String BEST = "best";
    private static final String COURSE = "course";
    private static final String UNCOMPLETED = "uncompleted";
    private static final String COURSES = "courses";
    private static final String JOINED = "joined";
    private static final String LAST = "last";
    private static final String TOTAL = "total";
    private static final String LEADERBOARD = "leaderboard";
    private static final String DEATHS = "deaths";
    private static final String TIME = "time";
    private static final String TIMES = "times";
    private static final String PLAYING = "playing";
    private static final String CHECKPOINT = "checkpoint";
    private static final String MILLISECONDS = "milliseconds";
    private static final String PLAYER = "player";
    private static final String DELIMITER = "/";

    private static final String PLACEHOLDER_API_CHECKPOINT_HOLOGRAM = "PlaceholderAPI.CheckpointHologram";
    private static final String PLACEHOLDER_API_CURRENT_COURSE_COMPLETED = "PlaceholderAPI.CurrentCourseCompleted";
    private static final String PLACEHOLDER_API_CURRENT_COURSE_NOT_COMPLETED = "PlaceholderAPI.CurrentCourseNotCompleted";

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
    public synchronized String onRequest(final OfflinePlayer offlinePlayer,
                                         final @NotNull String placeholder) {
        return retrieveValue(offlinePlayer, placeholder.toLowerCase(), placeholder.toLowerCase().split("_"));
    }

    public void clearCache() {
        this.cache.clear();
    }

    public GenericCache<String, String> getCache() {
        return this.cache;
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
            case PLAYER:
                return getPlayerPlaceholderValue(offlinePlayer, arguments);

            case "co":
            case COURSE:
                if (arguments.length < 3) {
                    return INVALID_SYNTAX;
                }
                return getCoursePlaceholderValue(arguments);

            case "cu":
            case "current":
                return getCurrentPlaceholderValue(offlinePlayer, arguments);

            case "lb":
            case LEADERBOARD:
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

            case "tfp":
            case "topfirstplaces":
                if (arguments.length != 2 || !ValidationUtils.isPositiveInteger(arguments[1])) {
                    return INVALID_SYNTAX;
                }
                return getOrRetrieveCache(() -> getTopFirstPlacePlaceholderValue(arguments[1]),
                        arguments[0], arguments[1]);

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

        if (!PlayerConfig.hasPlayerConfig(offlinePlayer)) {
            return PLAYER_NOT_FOUND;
        }

        PlayerConfig playerConfig = PlayerConfig.getConfig(offlinePlayer);

        switch (arguments[1]) {
            case "level":
                return String.valueOf(playerConfig.getParkourLevel());

            case "rank":
                return playerConfig.getParkourRank();

            case "parkoins":
                return String.valueOf(playerConfig.getParkoins());

            case LAST:
                if (arguments.length < 3) {
                    return INVALID_SYNTAX;
                }
                switch (arguments[2]) {
                    case COMPLETED:
                        return playerConfig.getLastCompletedCourse();

                    case JOINED:
                        return playerConfig.getLastPlayedCourse();

                    default:
                        return INVALID_SYNTAX;
                }

            case COURSES:
                if (arguments.length < 3) {
                    return INVALID_SYNTAX;
                }
                switch (arguments[2]) {
                    case COMPLETED:
                        return String.valueOf(parkour.getConfigManager().getCourseCompletionsConfig()
                                .getNumberOfCompletedCourses(offlinePlayer));

                    case UNCOMPLETED:
                        return String.valueOf(parkour.getPlayerManager().getNumberOfUncompletedCourses(offlinePlayer));

                    default:
                        return INVALID_SYNTAX;
                }

            case COURSE:
                if (arguments.length < 4) {
                    return INVALID_SYNTAX;
                }

                switch (arguments[2]) {
                    case COMPLETED:
                        return getOrRetrieveCache(() -> getCompletedMessage(offlinePlayer, arguments[3]),
                                offlinePlayer.getName(), arguments[1], arguments[2], arguments[3]);

                    case POSITION:
                        return getOrRetrieveCache(() -> getLeaderboardPosition(offlinePlayer, arguments[3]),
                                offlinePlayer.getName(),arguments[1], arguments[2], arguments[3]);

                    default:
                        return INVALID_SYNTAX;
                }

            case PRIZE:
                if (arguments.length < 4) {
                    return INVALID_SYNTAX;
                }
                if (DELAY.equals(arguments[2])) {
                    return getOrRetrieveCache(() -> getDelayCooldown(offlinePlayer, arguments[3]),
                            offlinePlayer.getName(), arguments[1], arguments[2], arguments[3]);
                }
                return INVALID_SYNTAX;

            case PERSONAL:
                if (arguments.length != 5 || !arguments[2].equals(BEST)) {
                    return INVALID_SYNTAX;
                }
                Player player = offlinePlayer.getPlayer();
                if (player == null) {
                    return "";
                }
                return getPersonalCourseRecord(player, arguments[3], arguments[4]);

            case TOTAL:
                switch (arguments[2]) {
                    case LEADERBOARD:
                        if (arguments.length < 4) {
                            return INVALID_SYNTAX;
                        }

                        switch (arguments[3]) {
                            case DEATHS:
                                return getOrRetrieveCache(() -> String.valueOf(parkour.getDatabaseManager().getAccumulatedDeaths(offlinePlayer)),
                                        offlinePlayer.getName(), arguments[1], arguments[2], arguments[3]);

                            case TIME:
                                return getOrRetrieveCache(() -> DateTimeUtils.displayCurrentTime(parkour.getDatabaseManager().getAccumulatedTime(offlinePlayer)),
                                        offlinePlayer.getName(), arguments[1], arguments[2], arguments[3]);

                            case TIMES:
                                return getOrRetrieveCache(() -> String.valueOf(parkour.getDatabaseManager().getAccumulatedTimes(offlinePlayer)),
                                        offlinePlayer.getName(), arguments[1], arguments[2], arguments[3]);

                            default:
                                return INVALID_SYNTAX;
                        }

                    case PLAYING:
                        if (arguments.length < 4) {
                            return INVALID_SYNTAX;
                        }

                        switch (arguments[3]) {
                            case DEATHS:
                                return String.valueOf(playerConfig.getTotalDeaths());

                            case TIME:
                                return DateTimeUtils.displayCurrentTime(playerConfig.getTotalTime());

                            default:
                                return INVALID_SYNTAX;
                        }

                    case "firstplaces":
                        return getOrRetrieveCache(() -> String.valueOf(parkour.getDatabaseManager().getNumberOfFirstPlaces(offlinePlayer)),
                                offlinePlayer.getName(), arguments[1], arguments[2]);

                    default:
                        return INVALID_SYNTAX;
                }

            default:
                return INVALID_SYNTAX;
        }
    }

    private String getDelayCooldown(OfflinePlayer offlinePlayer, String courseName) {
        if (parkour.getConfigManager().getCourseConfig(courseName).hasRewardDelay()) {
            String remaining = DateTimeUtils.getDelayTimeRemaining(offlinePlayer, courseName);
            return "0".equals(remaining) ? NO_COOLDOWN_REMAINING : remaining;
        } else {
            return NO_COOLDOWN_REMAINING;
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
            case COURSE:
                if (arguments.length < 3) {
                    return INVALID_SYNTAX;
                }
                return getCurrentCoursePlaceholderValue(player, session, arguments);

            case CHECKPOINT:
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

            case "next":
                return session.getCurrentCheckpoint() < session.getCourse().getNumberOfCheckpoints()
                        ? String.valueOf(session.getCurrentCheckpoint() + 1) : "";

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

            case DEATHS:
                return String.valueOf(session.getDeaths());

            case "timer":
                return session.getDisplayTime();

            case "checkpoints":
                return String.valueOf(session.getCourse().getNumberOfCheckpoints());

            case COMPLETED:
                return getOrRetrieveCache(() -> getCompletedMessage(player, session.getCourseName()),
                        player.getName(), arguments[1], arguments[2], session.getCourseName());

            case "record":
                if (arguments.length != 4) {
                    return INVALID_SYNTAX;
                }
                return getCourseRecord(session.getCourseName(), arguments[3]);

            case PERSONAL:
                if (arguments.length != 5 || !arguments[3].equals(BEST)) {
                    return INVALID_SYNTAX;
                }
                return getPersonalCourseRecord(player, session.getCourseName(), arguments[4]);

            case "remaining":
                if (arguments.length != 4 || !arguments[3].equals(DEATHS)) {
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
                    .replace(TIME_PLACEHOLDER, getTimeValue(result.getTime()))
                    .replace(DEATHS_PLACEHOLDER, String.valueOf(result.getDeaths()));
        }
    }

    private String getTopFirstPlacePlaceholderValue(String rowNumber) {
        int position = Integer.parseInt(rowNumber);
        FirstPlacesEntry result = parkour.getDatabaseManager().getGlobalFirstPlaces(position - 1);

        if (result == null) {
            return NO_TIME_RECORDED;

        } else {
            return TOP_FIRST_PLACE_RESULT.replace(PLAYER_PLACEHOLDER, result.getPlayerName())
                    .replace(POSITION_PLACEHOLDER, String.valueOf(position))
                    .replace(AMOUNT_PLACEHOLDER, String.valueOf(result.getTotal()));
        }
    }

    private TimeEntry getTopPlayerResultForCourse(Player player, String courseName) {
        List<TimeEntry> time = parkour.getDatabaseManager().getTopPlayerCourseResults(player, courseName, 1);
        return time.isEmpty() ? null : time.get(0);
    }

    private String getCompletedMessage(OfflinePlayer player, String courseName) {
        boolean resultFound = parkour.getConfigManager().getCourseCompletionsConfig().hasCompletedCourse(player, courseName);
        String key = resultFound ? PLACEHOLDER_API_CURRENT_COURSE_COMPLETED : PLACEHOLDER_API_CURRENT_COURSE_NOT_COMPLETED;
        return TranslationUtils.getTranslation(key, false);
    }

    private String getCourseRecord(String courseName, String timeDetail) {
        return getCourseRecord(courseName, timeDetail, 1);
    }

    private String getCourseRecord(String courseName, String timeDetail, Integer position) {
        return getOrRetrieveCache(() -> extractResultDetails(parkour.getDatabaseManager().getNthBestTime(courseName, position), timeDetail),
                courseName, timeDetail, String.valueOf(position));
    }

    private String getPersonalCourseRecord(Player player, String courseName, String timeDetail) {
        return getOrRetrieveCache(() -> extractResultDetails(getTopPlayerResultForCourse(player, courseName), timeDetail),
                player.getName(), courseName, timeDetail);
    }

    private String getLeaderboardPosition(OfflinePlayer player, String courseName) {
        int result = parkour.getDatabaseManager().getPositionOnLeaderboard(player, courseName);
        return result < 0 ? NO_TIME_RECORDED : String.valueOf(result + 1);
    }

    private String extractResultDetails(TimeEntry result, String timeDetail) {
        if (result == null) {
            return NO_TIME_RECORDED;

        } else {
            switch (timeDetail) {
                case TIME:
                    return getTimeValue(result.getTime());

                case MILLISECONDS:
                    return String.valueOf(result.getTime());

                case DEATHS:
                    return String.valueOf(result.getDeaths());

                case PLAYER:
                    return result.getPlayerName();

                default:
                    return INVALID_SYNTAX;
            }
        }
    }

    private String getCheckpointHologramMessage(ParkourSession session, String course, int checkpoint) {
        if (session.getCurrentCheckpoint() + 1 == checkpoint
                && session.getCourseName().equals(course.toLowerCase())) {
            return TranslationUtils.getValueTranslation(PLACEHOLDER_API_CHECKPOINT_HOLOGRAM,
                    Integer.toString(checkpoint), false);
        } else {
            return "";
        }
    }

    private String getOrRetrieveCache(Supplier<String> callback, String... keys) {
        String key = generateCacheKey(keys);
        // check if the key exists or its 'get' is about to expire.
        return cache.computeIfAbsent(key, k -> cache.createCacheValue(callback.get()));
    }

    private String getTimeValue(long milliseconds) {
        return StringUtils.colour(parkour.getParkourConfig().getPlaceholderTimeFormat().format(new Date(milliseconds)));
    }

    private String generateCacheKey(String... values) {
        return String.join(DELIMITER, values);
    }
}
