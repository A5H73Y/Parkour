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
		CourseSettingsManager courseSettings = Parkour.getInstance().getCourseSettingsManager();
		CourseConfig courseConfig = Parkour.getInstance().getConfigManager().getCourseConfig(courseName);

		// toggleable
		parent.addElement(createSettingToggle('q', "Ready Status", courseConfig.getReadyStatus(),
				click -> courseSettings.setReadyStatus(player, courseName, null)));
		parent.addElement(createSettingToggle('w', "Reward Once", courseConfig.getRewardOnce(),
				click -> courseSettings.setRewardOnceStatus(player, courseName, null)));
		parent.addElement(createSettingToggle('e', "Challenge Only", courseConfig.getChallengeOnly(),
				click -> courseSettings.setChallengeOnlyStatus(player, courseName, null)));
		parent.addElement(createSettingToggle('r', "Resumable", courseConfig.getResumable(),
				click -> courseSettings.setResumable(player, courseName, null)));
		parent.addElement(createSettingToggle('t', "Die in Lava", courseConfig.getDieInLava(),
				click -> courseSettings.setDieInLava(player, courseName, null)));
		parent.addElement(createSettingToggle('y', "Die in Void", courseConfig.getDieInVoid(),
				click -> courseSettings.setDieInVoid(player, courseName, null)));
		parent.addElement(createSettingToggle('u', "Fall Damage", courseConfig.getHasFallDamage(),
				click -> courseSettings.setHasFallDamage(player, courseName, null)));
		parent.addElement(createSettingToggle('i', "Manual Checkpoints", courseConfig.getManualCheckpoints(),
				click -> courseSettings.setManualCheckpoints(player, courseName, null)));
		parent.addElement(createSettingToggle('o', "Die in Water", courseConfig.getDieInWater(),
				click -> courseSettings.setDieInWater(player, courseName, null)));

		// input required
		parent.addElement(createTextInput('a', player, "Creator",
				courseConfig.getCreator(),
				input -> courseSettings.setCreator(player, courseName, input)));
		parent.addElement(createTextInput('s', player, "Minimum Parkour Level",
				courseConfig.getMinimumParkourLevel(),
				input -> courseSettings.setMinimumParkourLevel(player, courseName, input)));
		parent.addElement(createTextInput('d', player, "Maximum Deaths",
				courseConfig.getMaximumDeaths(),
				input -> courseSettings.setMaxDeaths(player, courseName, input)));
		parent.addElement(createTextInput('f', player, "Maximum Time",
				DateTimeUtils.convertSecondsToTime(courseConfig.getMaximumTime()),
				input -> courseSettings.setMaxTime(player, courseName, input)));
		parent.addElement(createTextInput('g', player, "Player Limit",
				courseConfig.getPlayerLimit(),
				input -> courseSettings.setPlayerLimit(player, courseName, input)));
		parent.addElement(createTextInput('h', player, "ParkourKit",
				courseConfig.getParkourKit(),
				input -> courseSettings.setParkourKit(player, courseName, input)));
		parent.addElement(createTextInput('j', player, "Reward Parkour Level",
				courseConfig.getRewardParkourLevel(),
				input -> courseSettings.setRewardParkourLevel(player, courseName, input)));
		parent.addElement(createTextInput('k', player, "Reward Parkour Level Increase",
				courseConfig.getRewardParkourLevelIncrease(),
				input -> courseSettings.setRewardParkourLevelIncrease(player, courseName, input)));
		parent.addElement(createTextInput('l', player, "Reward Delay",
				DateTimeUtils.convertMillisecondsToDateTime(
						DateTimeUtils.convertHoursToMilliseconds(courseConfig.getRewardDelay())),
				input -> courseSettings.setRewardDelay(player, courseName, input)));
		parent.addElement(createTextInput('z', player, "Display Name",
				StringUtils.colour(courseConfig.getCourseDisplayName()),
				input -> courseSettings.setDisplayName(player, courseName, input)));
		parent.addElement(createTextInput('x', player, "Max Fall Ticks",
				courseConfig.getMaximumFallTicks(),
				input -> courseSettings.setMaxFallTicks(player, courseName, input)));
		parent.addElement(createTextInput('c', player, "Rename Course",
				courseConfig.getCourseName(),
				input -> courseSettings.setRenameCourse(player, courseName, input)));

		// start conversation
		parent.addElement(createConversationStarter('v', "Prize", courseName,
				course -> new CoursePrizeConversation(player).withCourseName(course).begin()));
		parent.addElement(createConversationStarter('b', "ParkourMode", courseName,
				course -> new ParkourModeConversation(player).withCourseName(course).begin()));
		parent.addElement(createConversationStarter('n', "Event Message", courseName,
				course -> new SetCourseConversation.CourseMessageConversation(player)
						.withCourseName(course).begin()));
		parent.addElement(createConversationStarter('m', "Event Command", courseName,
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
