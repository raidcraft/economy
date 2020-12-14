package de.raidcraft.economy.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import de.raidcraft.economy.EconomyPlugin;
import de.raidcraft.economy.Messages;
import de.raidcraft.economy.TransactionReason;
import de.raidcraft.economy.entities.EconomyPlayer;
import de.raidcraft.economy.entities.Transaction;

import static de.raidcraft.economy.EconomyPlugin.PERMISSION_PREFIX;
import static de.raidcraft.economy.Messages.*;

@CommandAlias("money|eco|economy")
public class PlayerCommands extends BaseCommand {

    private final EconomyPlugin plugin;

    public PlayerCommands(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @CommandAlias("account|balance")
    @Subcommand("info|account|balance")
    @CommandCompletion("@players")
    @CommandPermission(PERMISSION_PREFIX + "account.info")
    public void info(@Conditions("others:perm=info") EconomyPlayer player) {

        send(getCurrentCommandIssuer(), playerInfo(player));
    }

    @CommandAlias("moneyflow|transactions")
    @Subcommand("transactions|history|flow")
    @CommandCompletion("@players")
    @CommandPermission(PERMISSION_PREFIX + "account.transactions")
    public void transactions(@Conditions("others:perm=transactions") EconomyPlayer player) {

        send(getCurrentCommandIssuer(), Messages.transactions(player.transactions()));
    }

    @Subcommand("pay|transfer")
    @CommandAlias("pay")
    @CommandCompletion("@players")
    @CommandPermission(PERMISSION_PREFIX + "account.transfer")
    public void transfer(@Conditions("hasEnough|others:perm=transfer") EconomyPlayer source, EconomyPlayer target, double amount, @Optional String details) {

        if (source.equals(target)) {
            throw new InvalidCommandArgument("Du kannst dir nicht selbst Geld überweisen.");
        }

        Transaction.Result result = source.transfer(target.account(), amount, TransactionReason.COMMAND, details);
        if (result.success()) {
            send(getCurrentCommandIssuer(), transactionSuccess(result));
        } else {
            send(getCurrentCommandIssuer(), transactionError(result));
        }
    }

    @Subcommand("set")
    @CommandCompletion("@players")
    @CommandPermission(PERMISSION_PREFIX + "admin.balance.set")
    public void setMoney(EconomyPlayer player, double amount) {

        player.account().balance(amount).save();
        send(getCurrentCommandIssuer(), balanceSet(player, amount));
    }
}
