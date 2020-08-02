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
 * Car Store Gui.
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

							// the car type heading
							StringUtils.standardizeText(course)

							// maximum speed
//							TranslationUtils.getValueTranslation("CarDetails.MaxSpeed",
//									String.valueOf(details.getStartMaxSpeed()), false),
//
//							// acceleration
//							TranslationUtils.getValueTranslation("CarDetails.Acceleration",
//									String.valueOf(details.getAcceleration()), false),
//
//							// fuel usage
//							TranslationUtils.getValueTranslation("CarDetails.FuelUsage",
//									String.valueOf(details.getAcceleration()), false),
//
//							// economy cost
//							TranslationUtils.getValueTranslation("CarDetails.Cost",
//									displayCost, false)
					));
		}
		return group;
	}
}
