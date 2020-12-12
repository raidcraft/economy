package de.raidcraft.economy.commands;

import co.aikar.commands.BaseCommand;
import de.raidcraft.economy.EconomyPlugin;

public class AdminCommands extends BaseCommand {

    private final EconomyPlugin plugin;

    public AdminCommands(EconomyPlugin plugin) {
        this.plugin = plugin;
    }
}
