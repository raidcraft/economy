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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.feature.pagination.Pagination;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static de.raidcraft.economy.EconomyPlugin.PERMISSION_PREFIX;
import static de.raidcraft.economy.Messages.*;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;

@CommandAlias("money|eco|economy")
public class Commands extends BaseCommand {

    private final EconomyPlugin plugin;

    public Commands(EconomyPlugin plugin) {
        this.plugin = plugin;
    }

    @CommandPermission(PERMISSION_PREFIX + "reload")
    @Subcommand("reload")
    public void reload() {

        plugin.reload();
        getCurrentCommandIssuer().sendMessage(ChatColor.GREEN + "RCEconomy wurde erfolgreich neugeladen.");
    }

    @Default
    @CommandAlias("account|balance")
    @Subcommand("info|account|balance")
    @CommandPermission(PERMISSION_PREFIX + "account.info")
    public void info(@Conditions("others:perm=info") EconomyPlayer player) {

        send(getCurrentCommandIssuer(), playerInfo(player));
    }

    @CommandAlias("moneyflow|transactions|flow")
    @Subcommand("flow|transactions|history")
    @CommandPermission(PERMISSION_PREFIX + "account.transactions")
    public void transactions(@Conditions("others:perm=transactions") EconomyPlayer player, @Default(value = "1") int page) {

        TextComponent header = text("Transaktionen von ", DARK_AQUA).append(player(player));
        Pagination<Transaction> pagination = Pagination.builder()
                .width(Pagination.WIDTH - 2)
                .build(header, new Pagination.Renderer.RowRenderer<>() {
            @Override
            public @NonNull Collection<Component> renderRow(@Nullable Transaction value, int index) {

                if (value == null) return Collections.singletonList(text().build());
                return Collections.singletonList(transaction(value));
            }
        }, p -> "/money transactions " + player.name() + " " + p);
        pagination.render(player.transactions(), page).forEach(component -> send(getCurrentCommandIssuer(), component));
    }

    @Subcommand("pay")
    @CommandAlias("pay")
    @CommandCompletion("@players * *")
    @CommandPermission(PERMISSION_PREFIX + "pay")
    public void pay(EconomyPlayer target, double amount, @Optional String details) {

        if (!getCurrentCommandIssuer().isPlayer()) {
            throw new InvalidCommandArgument("Du kannst diesen Befehl nur als Spieler ausführen.");
        }

        EconomyPlayer source = EconomyPlayer.getOrCreate(getCurrentCommandIssuer().getIssuer());

        if (source.equals(target)) {
            throw new InvalidCommandArgument("Du kannst dir selbst kein Geld überweisen.");
        }

        Transaction.Result result = Transaction.create(source.account(), target.account(), amount, TransactionReason.COMMAND, details)
                .data("command", "/money pay")
                .data("issuer", getCurrentCommandIssuer().getUniqueId())
                .execute();

        if (result.success()) {
            send(getCurrentCommandIssuer(), paySuccess(result));
        } else {
            send(getCurrentCommandIssuer(), transactionError(result));
        }
    }

    @Subcommand("transfer")
    @CommandCompletion("@players @players * *")
    @CommandPermission(PERMISSION_PREFIX + "account.admin.transfer")
    public void transfer(@Conditions("hasEnough") EconomyPlayer source, EconomyPlayer target, double amount, @Optional String details) {

        if (source.equals(target)) {
            throw new InvalidCommandArgument("Der Ziel-Spieler darf nicht der Quell-Spieler sein.");
        }

        Transaction.Result result = Transaction.create(source.account(), target.account(), amount, TransactionReason.COMMAND, details)
                .data("command", "/money transfer")
                .data("issuer", getCurrentCommandIssuer().getUniqueId())
                .execute();

        if (result.success()) {
            send(getCurrentCommandIssuer(), transactionSuccess(result));
        } else {
            send(getCurrentCommandIssuer(), transactionError(result));
        }
    }

    @Subcommand("set")
    @CommandCompletion("@players * *")
    @CommandPermission(PERMISSION_PREFIX + "admin.balance.set")
    public void setMoney(EconomyPlayer player, double balance, @Optional String details) {

        double oldBalance = player.account().balance();
        double amount = balance - oldBalance;
        Transaction.Result result = Transaction.create(Account.getServerAccount(), player.account(), amount, TransactionReason.SET_MONEY, details)
                .data("command", "/money set")
                .data("issuer", getCurrentCommandIssuer().getUniqueId())
                .execute();

        if (!result.success()) {
            send(getCurrentCommandIssuer(), transactionError(result));
        } else {
            send(getCurrentCommandIssuer(), balanceSet(player, balance));
        }
    }

    @Subcommand("add")
    @CommandCompletion("@players * *")
    @CommandPermission(PERMISSION_PREFIX + "admin.balance.add")
    public void addMoney(EconomyPlayer player, double amount, @Optional String details) {

        Transaction.Result result = Transaction.create(Account.getServerAccount(), player.account(), amount, TransactionReason.COMMAND, details)
                .data("command", "/money add")
                .data("issuer", getCurrentCommandIssuer().getUniqueId())
                .execute();

        if (result.success()) {
            send(getCurrentCommandIssuer(), transactionSuccess(result));
        } else {
            send(getCurrentCommandIssuer(), transactionError(result));
        }
    }

    @Subcommand("remove")
    @CommandCompletion("@players * *")
    @CommandPermission(PERMISSION_PREFIX + "admin.balance.remove")
    public void removeMoney(EconomyPlayer player, double amount, @Optional String details) {

        Transaction.Result result = Transaction.create(player.account(), Account.getServerAccount(), amount, TransactionReason.COMMAND, details)
                .data("command", "/money remove")
                .data("issuer", getCurrentCommandIssuer().getUniqueId())
                .execute();

        if (result.success()) {
            send(getCurrentCommandIssuer(), transactionSuccess(result));
        } else {
            send(getCurrentCommandIssuer(), transactionError(result));
        }
    }
}
