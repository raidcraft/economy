package de.raidcraft.economy.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import de.raidcraft.economy.EconomyPlugin;
import de.raidcraft.economy.entities.EconomyPlayer;

import static de.raidcraft.economy.EconomyPlugin.PERMISSION_PREFIX;

@CommandAlias("money|eco|economy")
public class PlayerCommands extends BaseCommand {

    private final EconomyPlugin plugin;

    public PlayerCommands(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @Subcommand("info|account")
    @CommandPermission(PERMISSION_PREFIX + "account.info")
    public void info(EconomyPlayer player) {


    }
}
