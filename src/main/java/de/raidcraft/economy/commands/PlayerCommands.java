package de.raidcraft.economy.commands;

import co.aikar.commands.BaseCommand;
import de.raidcraft.economy.EconomyPlugin;

public class PlayerCommands extends BaseCommand {

    private final EconomyPlugin plugin;

    public PlayerCommands(EconomyPlugin plugin) {
        this.plugin = plugin;
    }
}
