package io.github.a5h73y.parkour.gui;

import io.github.a5h73y.parkour.gui.AbstractMenu;
import io.github.a5h73y.parkour.gui.impl.JoinCoursesGui;

public enum GuiMenu {

	JOIN_COURSES(new JoinCoursesGui());

	private final AbstractMenu menu;

	GuiMenu(AbstractMenu menu) {
		this.menu = menu;
	}

	public AbstractMenu getMenu() {
		return menu;
	}
}
