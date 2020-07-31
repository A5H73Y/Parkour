package io.github.a5h73y.parkour.enums;

import io.github.a5h73y.parkour.gui.AbstractMenu;
import io.github.a5h73y.parkour.gui.JoinCourses;

public enum GuiMenu {

	JOIN_COURSES(new JoinCourses());

	private final AbstractMenu menu;

	GuiMenu(AbstractMenu menu) {
		this.menu = menu;
	}

	public AbstractMenu getMenu() {
		return menu;
	}
}
