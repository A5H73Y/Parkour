package io.github.a5h73y.parkour.gui.impl;

import com.cryptomorin.xseries.XMaterial;
import de.themoep.inventorygui.GuiStateElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.conversation.CoursePrizeConversation;
import io.github.a5h73y.parkour.conversation.ParkourConversation;
import io.github.a5h73y.parkour.conversation.ParkourModeConversation;
import io.github.a5h73y.parkour.conversation.SetCourseConversation;
import io.github.a5h73y.parkour.conversation.other.SingleQuestionConversation;
import io.github.a5h73y.parkour.gui.AbstractMenu;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.type.course.CourseManager;
import io.github.a5h73y.parkour.utility.DateTimeUtils;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;

/**
 * Course Settings GUI.
 */
public class CourseSettingsGui extends AbstractMenu {

	private final transient String courseName;

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
		CourseManager courseManager = Parkour.getInstance().getCourseManager();

		// toggleable
		parent.addElement(createSettingToggle('z', "Ready Status", CourseInfo.getReadyStatus(courseName),
				click -> CourseInfo.toggleReadyStatus(courseName)));
		parent.addElement(createSettingToggle('x', "Reward Once", CourseInfo.getRewardOnce(courseName),
				click -> CourseInfo.toggleRewardOnce(courseName)));
		parent.addElement(createSettingToggle('c', "Challenge Only", CourseInfo.getChallengeOnly(courseName),
				click -> CourseInfo.toggleChallengeOnly(courseName)));

		// input required
		parent.addElement(createTextInput('q', player, "Owner",
				CourseInfo.getCreator(courseName),
				input -> SetCourseConversation.performAction(player, courseName, "creator", input)));
		parent.addElement(createTextInput('w', player, "Minimum Parkour Level",
				CourseInfo.getMinimumParkourLevel(courseName),
				input -> SetCourseConversation.performAction(player, courseName, "minlevel", input)));
		parent.addElement(createTextInput('e', player, "Maximum Deaths",
				CourseInfo.getMaximumDeaths(courseName),
				input -> SetCourseConversation.performAction(player, courseName, "maxdeath", input)));
		parent.addElement(createTextInput('r', player, "Maximum Time",
				DateTimeUtils.convertSecondsToTime(CourseInfo.getMaximumTime(courseName)),
				input -> SetCourseConversation.performAction(player, courseName, "maxtime", input)));
		parent.addElement(createTextInput('t', player, "Player Limit",
				CourseInfo.getPlayerLimit(courseName),
				input -> courseManager.setPlayerLimit(player, courseName, input)));
		parent.addElement(createTextInput('y', player, "ParkourKit",
				CourseInfo.getParkourKit(courseName),
				input -> courseManager.setParkourKit(player, courseName, input)));
		parent.addElement(createTextInput('u', player, "Reward Parkour Level",
				CourseInfo.getRewardParkourLevel(courseName),
				input -> courseManager.setRewardParkourLevel(player, courseName, input)));
		parent.addElement(createTextInput('i', player, "Reward Parkour Level Increase",
				CourseInfo.getRewardParkourLevelIncrease(courseName),
				input -> courseManager.setRewardParkourLevelIncrease(player, courseName, input)));
		parent.addElement(createTextInput('o', player, "Reward Delay", DateTimeUtils.displayTimeRemaining(
				DateTimeUtils.convertHoursToMilliseconds(CourseInfo.getRewardDelay(courseName))),
				input -> courseManager.setRewardDelay(player, courseName, input)));
		parent.addElement(createTextInput('a', player, "Reward Parkoins",
				CourseInfo.getRewardParkoins(courseName),
				input -> courseManager.setRewardParkoins(player, courseName, input)));

		// start conversation
		parent.addElement(createConversationStarter('s', "Prize", courseName,
				new CoursePrizeConversation(player)));
		parent.addElement(createConversationStarter('d', "ParkourMode", courseName,
				new ParkourModeConversation(player)));
		parent.addElement(createConversationStarter('g', "Event Message", courseName,
				new SetCourseConversation.CourseMessageConversation(player)));
		parent.addElement(createConversationStarter('h', "Event Command", courseName,
				new SetCourseConversation.CourseCommandConversation(player)));

	}

	private GuiStateElement createSettingToggle(char key, String title, boolean enabled,
	                                            GuiStateElement.State.Change change) {
		return new GuiStateElement(key, enabled ? 0 : 1,
				new GuiStateElement.State(
						change,
						title + "Enabled", // a key to identify this state by
						XMaterial.GREEN_WOOL.parseItem(), // the item to display as an icon
						ChatColor.GREEN + title + " Enabled", // explanation text what this element does
						StringUtils.colour("&7By clicking you will &cdisable &7" + title
								+ " for &b" + courseName)
				),
				new GuiStateElement.State(
						change,
						title + "Disabled",
						XMaterial.RED_WOOL.parseItem(),
						ChatColor.RED + title + " Disabled",
						StringUtils.colour("&7By clicking you will &aenable &7" + title
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
				StringUtils.colour("&7By clicking, you will input a new value for &b" + courseName)
		);
	}

	private StaticGuiElement createConversationStarter(char key, String title, String courseName,
	                                                   ParkourConversation conversation) {
		return new StaticGuiElement(key, XMaterial.BOOK.parseItem(), 1, click -> {
			click.getGui().close();
			conversation.withCourseName(courseName).begin();
			return false;
		},
				StringUtils.colour("&fSet &b" + title),
				StringUtils.colour("&7By clicking, you will start a conversation for &b" + courseName)
		);
	}
}
