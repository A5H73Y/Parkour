package io.github.a5h73y.parkour.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface CommandProcessor {

	void processCommand(@NotNull final CommandSender commandSender, final String... args);

}
