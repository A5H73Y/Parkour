package io.github.a5h73y.parkour.gui.impl;

import com.cryptomorin.xseries.XMaterial;
import de.themoep.inventorygui.GuiStateElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.CoursePrizeConversation;
import io.github.a5h73y.parkour.conversation.ParkourModeConversation;
import io.github.a5h73y.parkour.conversation.SetCourseConversation;
import io.github.a5h73y.parkour.conversation.other.SingleQuestionConversation;
import io.github.a5h73y.parkour.gui.AbstractMenu;
import io.github.a5h73y.parkour.type.course.CourseConfig;
import io.github.a5h73y.parkour.type.course.CourseSettingsManager;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import io.github.a5h73y.parkour.utility.time.DateTimeUtils;
import java.util.function.Consumer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Course Settings GUI.
 */
public class CourseSettingsGui implements AbstractMenu {

	private final String courseName;

	public CourseSettingsGui(String courseName) {
		this.courseName = courseName;
	}

	@Override
	public String getTitle() {
		return TranslationUtils.getValueTranslation("GUI.CourseSettings.Heading", courseName.toLowerCase(), false);
	}

	@Override
	public String[] getGuiLayout() {
		return new String[] {
				TranslationUtils.getTranslation("GUI.CourseSettings.Setup.Line1", false),
				TranslationUtils.getTranslation("GUI.CourseSettings.Setup.Line2", false),
				TranslationUtils.getTranslation("GUI.CourseSettings.Setup.Line3", false)
		};
	}

	@Override
	public void addContent(InventoryGui parent, Player player) {
		CourseSettingsManager courseSettingsManager = Parkour.getInstance().getCourseSettingsManager();
		CourseConfig courseConfig = Parkour.getInstance().getConfigManager().getCourseConfig(courseName);

		// toggleable
		parent.addElement(createSettingToggle('z', "Ready Status", courseConfig.getReadyStatus(),
				click -> courseConfig.toggleReadyStatus()));
		parent.addElement(createSettingToggle('x', "Reward Once", courseConfig.getRewardOnce(),
				click -> courseConfig.toggleRewardOnce()));
		parent.addElement(createSettingToggle('c', "Challenge Only", courseConfig.getChallengeOnly(),
				click -> courseConfig.toggleChallengeOnly()));
		parent.addElement(createSettingToggle('v', "Resumable", courseConfig.getResumable(),
				click -> courseConfig.toggleResumable()));

		// input required
		parent.addElement(createTextInput('q', player, "Creator",
				courseConfig.getCreator(),
				input -> courseSettingsManager.setCreator(player, courseName, input)));
		parent.addElement(createTextInput('w', player, "Minimum Parkour Level",
				courseConfig.getMinimumParkourLevel(),
				input -> courseSettingsManager.setMinimumParkourLevel(player, courseName, input)));
		parent.addElement(createTextInput('e', player, "Maximum Deaths",
				courseConfig.getMaximumDeaths(),
				input -> courseSettingsManager.setMaxDeaths(player, courseName, input)));
		parent.addElement(createTextInput('r', player, "Maximum Time",
				DateTimeUtils.convertSecondsToTime(courseConfig.getMaximumTime()),
				input -> courseSettingsManager.setMaxTime(player, courseName, input)));
		parent.addElement(createTextInput('t', player, "Player Limit",
				courseConfig.getPlayerLimit(),
				input -> courseSettingsManager.setPlayerLimit(player, courseName, input)));
		parent.addElement(createTextInput('y', player, "ParkourKit",
				courseConfig.getParkourKit(),
				input -> courseSettingsManager.setParkourKit(player, courseName, input)));
		parent.addElement(createTextInput('u', player, "Reward Parkour Level",
				courseConfig.getRewardParkourLevel(),
				input -> courseSettingsManager.setRewardParkourLevel(player, courseName, input)));
		parent.addElement(createTextInput('i', player, "Reward Parkour Level Increase",
				courseConfig.getRewardParkourLevelIncrease(),
				input -> courseSettingsManager.setRewardParkourLevelIncrease(player, courseName, input)));
		parent.addElement(createTextInput('o', player, "Reward Delay", DateTimeUtils.convertMillisecondsToDateTime(
						DateTimeUtils.convertHoursToMilliseconds(courseConfig.getRewardDelay())),
				input -> courseSettingsManager.setRewardDelay(player, courseName, input)));
		parent.addElement(createTextInput('a', player, "Display Name",
				StringUtils.colour(courseConfig.getCourseDisplayName()),
				input -> courseSettingsManager.setDisplayName(player, courseName, input)));

		// start conversation
		parent.addElement(createConversationStarter('s', "Prize", courseName,
				course -> new CoursePrizeConversation(player).withCourseName(course).begin()));
		parent.addElement(createConversationStarter('d', "ParkourMode", courseName,
				course -> new ParkourModeConversation(player).withCourseName(course).begin()));
		parent.addElement(createConversationStarter('g', "Event Message", courseName,
				course -> new SetCourseConversation.CourseMessageConversation(player)
						.withCourseName(course).begin()));
		parent.addElement(createConversationStarter('h', "Event Command", courseName,
				course -> new SetCourseConversation.CourseCommandConversation(player)
						.withCourseName(course).begin()));
	}

	private GuiStateElement createSettingToggle(char key, String title, boolean enabled,
	                                            GuiStateElement.State.Change change) {
		return new GuiStateElement(key, enabled ? 0 : 1,
				new GuiStateElement.State(
						change,
						title + "Enabled", // a key to identify this state by
						XMaterial.GREEN_WOOL.parseItem(), // the item to display as an icon
						ChatColor.GREEN + title + " Enabled", // explanation text what this element does
						StringUtils.colour("&7Clicking will &cdisable &7" + title
								+ " for &b" + courseName)
				),
				new GuiStateElement.State(
						change,
						title + "Disabled",
						XMaterial.RED_WOOL.parseItem(),
						ChatColor.RED + title + " Disabled",
						StringUtils.colour("&7Clicking will &aenable &7" + title
								+ " for &b" + courseName)
				)
		);
	}

	private StaticGuiElement createTextInput(char key, Player player, String title,
	                                         Number currentValue,
	                                         Consumer<String> valueSetter) {
		return createTextInput(key, player, title, currentValue.toString(), valueSetter);
	}

	private StaticGuiElement createTextInput(char key, Player player, String title,
	                                         String currentValue,
	                                         Consumer<String> valueSetter) {
		return new StaticGuiElement(key, XMaterial.BOOK.parseItem(), 1, click -> {
			click.getGui().close();
			new SingleQuestionConversation(player, valueSetter).begin();
			return false;
		},
				StringUtils.colour("&fSet &b" + title),
				StringUtils.colour("&fCurrent Value: &3" + currentValue),
				StringUtils.colour("&7Clicking will input a new value for &b" + courseName)
		);
	}

	private StaticGuiElement createConversationStarter(char key, String title, String courseName,
	                                                   Consumer<String> conversation) {
		return new StaticGuiElement(key, XMaterial.BOOK.parseItem(), 1, click -> {
			click.getGui().close();
			conversation.accept(courseName);
			return false;
		},
				StringUtils.colour("&fSet &b" + title),
				StringUtils.colour("&7Clicking will start a conversation for &b" + courseName)
		);
	}
}
