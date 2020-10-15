package io.github.a5h73y.parkour.gui;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import io.github.a5h73y.parkour.Parkour;
import io.github.a5h73y.parkour.type.course.CourseInfo;
import io.github.a5h73y.parkour.utility.StringUtils;
import io.github.a5h73y.parkour.utility.TranslationUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Join Parkour Courses GUI.
 */
public class JoinCourses extends AbstractMenu {

	@Override
	public String getTitle() {
		return TranslationUtils.getTranslation("GUI.JoinCourses.Heading", false);
	}

	@Override
	public String[] getGuiSetup() {
		return new String[] {
				TranslationUtils.getTranslation("GUI.JoinCourses.Setup.Line1", false),
				TranslationUtils.getTranslation("GUI.JoinCourses.Setup.Line2", false),
				TranslationUtils.getTranslation("GUI.JoinCourses.Setup.Line3", false)
		};
	}

	@Override
	public GuiElementGroup getGroupContent(InventoryGui parent, Player player) {
		GuiElementGroup group = new GuiElementGroup('g');

		for (String course : CourseInfo.getAllCourses()) {
			group.addElement(
					new StaticGuiElement('e',
							new ItemStack(Material.MINECART),
							click -> {
								Parkour.getInstance().getPlayerManager().joinCourse(player, course);
								parent.close();
								return true;
							},

							// the item heading
							StringUtils.standardizeText(course),

							// the item description
							TranslationUtils.getValueTranslation("GUI.JoinCourses.Description",
									course, false)
					));
		}
		return group;
	}
}
