package io.github.a5h73y.parkour.gui.impl;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.gui.AbstractMenu;
import io.github.a5h73y.parkour.type.player.PlayerManager;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Join Parkour Courses GUI.
 */
public class JoinCoursesGui implements AbstractMenu {

	@Override
	public String getTitle() {
		return TranslationUtils.getTranslation("GUI.JoinCourses.Heading", false);
	}

	@Override
	public String[] getGuiLayout() {
		return new String[] {
				TranslationUtils.getTranslation("GUI.JoinCourses.Setup.Line1", false),
				TranslationUtils.getTranslation("GUI.JoinCourses.Setup.Line2", false),
				TranslationUtils.getTranslation("GUI.JoinCourses.Setup.Line3", false)
		};
	}

	@Override
	public void addContent(InventoryGui parent, Player player) {
		GuiElementGroup group = new GuiElementGroup('G');
		Parkour parkour = Parkour.getInstance();
		PlayerManager playerManager = parkour.getPlayerManager();

		for (String course : parkour.getCourseManager().getCourseNames()) {
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
