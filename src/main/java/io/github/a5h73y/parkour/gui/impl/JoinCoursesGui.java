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
		GuiElementGroup group = new GuiElementGroup('g');
		PlayerManager playerManager = Parkour.getInstance().getPlayerManager();

		for (String course : Parkour.getInstance().getCourseManager().getCourseNames()) {
			group.addElement(
					new StaticGuiElement('e',
							new ItemStack(Parkour.getDefaultConfig().getGuiMaterial()),
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
									String.valueOf(Parkour.getInstance().getParkourSessionManager()
											.getNumberOfPlayersOnCourse(course)), false),
							TranslationUtils.getValueTranslation("GUI.JoinCourses.Checkpoints",
									String.valueOf(Parkour.getInstance().getConfigManager()
											.getCourseConfig(course).getCheckpointAmount()), false)
					));
		}
		parent.addElement(group);
	}
}
