package io.github.a5h73y.parkour.gui.impl;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.gui.AbstractMenu;
import io.github.a5h73y.parkour.type.course.CourseConfig;
import io.github.a5h73y.parkour.type.player.ParkourMode;
import io.github.a5h73y.parkour.type.player.PlayerConfig;
import io.github.a5h73y.parkour.type.player.PlayerManager;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Join Parkour Courses GUI.
 */
public class JoinAllGui implements AbstractMenu {

	public static final String OWNER_FILTER = "owner";
	public static final String COMPLETED_FILTER = "completed";
	public static final String UNCOMPLETED_FILTER = "uncompleted";
	public static final String NAME = "name";
	public static final String PARKOUR_LEVEL = "level";
	public static final String VIEWS = "views";
	public static final String MINLEVEL_FILTER = "minlevel";
	public static final String WORLD = "world";
	public static final String PARKOUR_MODE = "mode";

	public static final String ASC = "_asc";
	public static final String DESC = "_desc";

	private final Map<String, Comparator<CourseConfig>> sorts = Map.of(
			NAME + ASC, Comparator.comparing(CourseConfig::getCourseName),
			NAME + DESC, Comparator.comparing(CourseConfig::getCourseName).reversed(),
			PARKOUR_LEVEL + ASC, Comparator.comparing(CourseConfig::getMinimumParkourLevel),
			PARKOUR_LEVEL + DESC, Comparator.comparing(CourseConfig::getMinimumParkourLevel).reversed(),
			VIEWS + ASC, Comparator.comparing(CourseConfig::getViews),
			VIEWS + DESC, Comparator.comparing(CourseConfig::getViews).reversed()
	);

	private final List<String> args;

	public JoinAllGui(List<String> args) {
		this.args = args;
	}

	@Override
	public String getTitle() {
		return TranslationUtils.getTranslation("GUI.JoinCourses.Heading", false);
	}

	@Override
	public String[] getGuiLayout() {
		return Stream.of(
						TranslationUtils.getTranslation("GUI.JoinCourses.Setup.Line1", false),
						TranslationUtils.getTranslation("GUI.JoinCourses.Setup.Line2", false),
						TranslationUtils.getTranslation("GUI.JoinCourses.Setup.Line3", false),
						TranslationUtils.getTranslation("GUI.JoinCourses.Setup.Line4", false),
						TranslationUtils.getTranslation("GUI.JoinCourses.Setup.Line5", false),
						TranslationUtils.getTranslation("GUI.JoinCourses.Setup.Line6", false)
				)
				.filter(line -> !line.isEmpty())
				.toArray(String[]::new);
	}

	@Override
	public void addContent(InventoryGui parent, Player player) {
		GuiElementGroup group = new GuiElementGroup('G');
		Parkour parkour = Parkour.getInstance();
		PlayerManager playerManager = parkour.getPlayerManager();
		List<String> courses = parkour.getCourseManager().getCourseNames();
		Stream<CourseConfig> matchingCourses = courses.stream().map(parkour.getConfigManager()::getCourseConfig);
		PlayerConfig playerConfig = parkour.getConfigManager().getPlayerConfig(player);

		if (!args.isEmpty()) {
			Predicate<CourseConfig> predicates = config -> true;

			if (args.stream().anyMatch((a) -> a.startsWith(OWNER_FILTER))) {
				String playerName = args.stream()
						.filter((a) -> a.startsWith(OWNER_FILTER))
						.map((a) -> a.substring((OWNER_FILTER + ":").length()))
						.findFirst()
						.orElse(player.getName());
				predicates = predicates.and(config -> Objects.equals(config.getCreator(), playerName));
			}

			if (args.stream().anyMatch((a) -> a.startsWith(PARKOUR_MODE + ":"))) {
				String modeName = args.stream()
						.filter((a) -> a.startsWith(PARKOUR_MODE + ":"))
						.map((a) -> a.substring((PARKOUR_MODE + ":").length()))
						.map(String::toUpperCase)
						.findFirst()
						.orElse(ParkourMode.NONE.name());
				predicates = predicates.and(config -> Objects.equals(config.getParkourModeName(), modeName));
			}

			if (args.contains(WORLD)) {
				predicates = predicates.and(config -> Objects.equals(config.getStartingWorldName(), player.getWorld().getName()));
			}

			if (args.contains(COMPLETED_FILTER) || args.contains(UNCOMPLETED_FILTER)) {
				List<String> completedCourses = parkour.getConfigManager().getCourseCompletionsConfig().getCompletedCourses(player);
				if (args.contains(UNCOMPLETED_FILTER)) {
					predicates = predicates.and(config -> !completedCourses.contains(config.getCourseName()));
				} else {
					predicates = predicates.and(config -> completedCourses.contains(config.getCourseName()));
				}
			}

			if (args.contains(MINLEVEL_FILTER)) {
				predicates = predicates.and(config -> config.getMinimumParkourLevel() <= playerConfig.getParkourLevel());
			}

			matchingCourses = matchingCourses.filter(predicates);
		}

		Comparator<CourseConfig> comparator = args.stream()
				.filter(sorts::containsKey)
				.map(sorts::get)
				.reduce(Comparator::thenComparing)
				.orElse(Comparator.comparing(CourseConfig::getCourseName));

		matchingCourses = matchingCourses.sorted(comparator);

		for (CourseConfig config : matchingCourses.collect(Collectors.toList())) {
			String course = config.getCourseName();
			group.addElement(
					new StaticGuiElement('e',
							new ItemStack(parkour.getParkourConfig().getGuiMaterial()),
							click -> {
								playerManager.joinCourse(player, course);
								parent.close();
								return true;
							},

							// the item heading
							StringUtils.standardizeText(course),

							// the item description
							TranslationUtils.getValueTranslation("GUI.JoinCourses.Description",
									course, false),
							TranslationUtils.getValueTranslation("GUI.JoinCourses.Players",
									String.valueOf(parkour.getParkourSessionManager()
											.getNumberOfPlayersOnCourse(course)), false),
							TranslationUtils.getValueTranslation("GUI.JoinCourses.Checkpoints",
									String.valueOf(parkour.getConfigManager().getCourseConfig(course)
											.getCheckpointAmount()), false),
							TranslationUtils.getValueTranslation("GUI.JoinCourses.Completed",
									String.valueOf(parkour.getConfigManager().getCourseCompletionsConfig()
											.hasCompletedCourse(player, course)), false)
					));
		}
		parent.addElement(group);
	}
}
