package de.raidcraft.economy.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import de.raidcraft.economy.EconomyPlugin;
import de.raidcraft.economy.Messages;
import de.raidcraft.economy.TransactionReason;
import de.raidcraft.economy.entities.Account;
import de.raidcraft.economy.entities.EconomyPlayer;
import de.raidcraft.economy.entities.Transaction;

import static de.raidcraft.economy.EconomyPlugin.PERMISSION_PREFIX;
import static de.raidcraft.economy.Messages.*;

@CommandAlias("money|eco|economy")
public class Commands extends BaseCommand {

    private final EconomyPlugin plugin;

    public Commands(EconomyPlugin plugin) {
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

        Transaction.Result result = Transaction.create(source.account(), target.account(), amount, TransactionReason.COMMAND, details)
                .data("issuer", getCurrentCommandIssuer().getUniqueId())
                .execute();

        if (result.success()) {
            send(getCurrentCommandIssuer(), transactionSuccess(result));
        } else {
            send(getCurrentCommandIssuer(), transactionError(result));
        }
    }

    @Subcommand("set")
    @CommandCompletion("@players")
    @CommandPermission(PERMISSION_PREFIX + "admin.balance.set")
    public void setMoney(EconomyPlayer player, double balance) {

        double oldBalance = player.account().balance();
        double amount = balance - oldBalance;
        Transaction.Result result = Transaction.create(Account.getServerAccount(), player.account(), amount, TransactionReason.SET_MONEY)
                .data("issuer", getCurrentCommandIssuer().getUniqueId())
                .data("action", "command")
                .execute();

        if (!result.success()) {
            send(getCurrentCommandIssuer(), transactionError(result));
        } else {
            send(getCurrentCommandIssuer(), balanceSet(player, balance));
        }
    }

    @Subcommand("add")
    @CommandCompletion("@players")
    @CommandPermission(PERMISSION_PREFIX + "admin.balance.add")
    public void addMoney(EconomyPlayer player, double amount) {

        Transaction.Result result = Transaction.create(Account.getServerAccount(), player.account(), amount, TransactionReason.COMMAND)
                .data("issuer", getCurrentCommandIssuer().getUniqueId())
                .execute();

        if (result.success()) {
            send(getCurrentCommandIssuer(), transactionSuccess(result));
        } else {
            send(getCurrentCommandIssuer(), transactionError(result));
        }
    }

    @Subcommand("remove")
    @CommandCompletion("@players")
    @CommandPermission(PERMISSION_PREFIX + "admin.balance.remove")
    public void removeMoney(EconomyPlayer player, double amount) {

        Transaction.Result result = Transaction.create(player.account(), Account.getServerAccount(), amount, TransactionReason.COMMAND)
                .data("issuer", getCurrentCommandIssuer().getUniqueId())
                .execute();

        if (result.success()) {
            send(getCurrentCommandIssuer(), transactionSuccess(result));
        } else {
            send(getCurrentCommandIssuer(), transactionError(result));
        }
    }
}
