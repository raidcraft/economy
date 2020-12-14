package de.raidcraft.economy;

import de.raidcraft.economy.entities.Account;
import de.raidcraft.economy.entities.BankAccount;
import de.raidcraft.economy.entities.EconomyPlayer;
import de.raidcraft.economy.entities.Transaction;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.common.util.report.qual.ReportCreation;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;

public final class Messages {

    private Messages() {}

    public static void send(UUID playerId, Component message) {
        if (EconomyPlugin.isTesting()) return;
        BukkitAudiences.create(EconomyPlugin.instance())
                .player(playerId)
                .sendMessage(message);
    }

    public static void send(UUID playerId, Consumer<TextComponent.Builder> message) {

        TextComponent.Builder builder = text();
        message.accept(builder);
        send(playerId, builder.build());
    }

    public static <TIssuer> void send(TIssuer commandIssuer, Component message) {

        if (commandIssuer instanceof Player) {
            send((Player) commandIssuer, message);
        } else if (commandIssuer instanceof ConsoleCommandSender) {
            send((ConsoleCommandSender) commandIssuer, message);
        } else if (commandIssuer instanceof RemoteConsoleCommandSender) {
            send((RemoteConsoleCommandSender) commandIssuer, message);
        }
    }

    public static void send(Player player, Component message) {
        send(player.getUniqueId(), message);
    }

    public static void send(ConsoleCommandSender sender, Component message) {

    }

    public static void send(RemoteConsoleCommandSender sender, Component message) {

    }

    public static Component player(EconomyPlayer player) {

        return text().append(text(player.name(), GOLD, BOLD)
                .hoverEvent(playerInfo(player)))
                .build();
    }

    public static Component playerInfo(EconomyPlayer player) {


        return text().append(header(player.name()))
                .append(text("Kontostand: ", YELLOW)).append(balance(player.account()))
                .build();
    }

    public static Component balance(Account account) {

        String balance = RCEconomy.instance().format(account.balance());

        return text().append(text(balance, AQUA, BOLD)
                .append(text(" [?]", GRAY))
                .hoverEvent(balanceInfo(account)))
                .build();
    }

    public static Component balanceInfo(Account account) {

        String balance = RCEconomy.instance().format(account.balance());

        return text().append(header(balance))
                .append(transactions(account))
                .build();
    }

    public static Component balanceSet(EconomyPlayer player, double amount) {

        String format = RCEconomy.instance().format(amount);
        return text().append(text("Der Kontostand von ", GREEN))
                .append(player(player))
                .append(text(" wurde auf ", GREEN))
                .append(text(format, AQUA))
                .append(text(" gesetzt.", GREEN))
                .build();
    }

    public static Component account(Account account) {

        TextComponent.Builder builder = text();

        if (account.isServerAccount()) {
            return text("Server", GREEN);
        }

        BankAccount.of(account).ifPresent(bank -> builder.append(bankAccount(bank)));
        EconomyPlayer.of(account).ifPresent(economyPlayer -> builder.append(player(economyPlayer)));

        return builder.build();
    }

    public static Component bankAccount(BankAccount account) {

        return text().append(text("[Bank] ", DARK_PURPLE)
                .append(text(account.name(), DARK_GREEN, BOLD)
                        .hoverEvent(bankAccountInfo(account))))
                .build();
    }

    public static Component bankAccountInfo(BankAccount account) {

        return text(account.name());
    }

    public static Component transactions(Collection<Transaction> transactions) {

        TextComponent.Builder builder = text();
        for (Transaction transaction : transactions) {
            builder.append(transaction(transaction)).append(newline());
        }
        return builder.build();
    }

    public static Component transactions(Account account) {

        return transactions(account.transactions().stream()
                .limit(5)
                .collect(Collectors.toUnmodifiableList()));
    }

    public static Component transaction(Transaction transaction) {

        String format = RCEconomy.instance().format(transaction.amount());

        return text().append(account(transaction.source()))
                .append(text(" --> ", DARK_AQUA))
                .append(account(transaction.target()))
                .append(text(": ", YELLOW))
                .append(text(format, AQUA))
                .append(text(" [?]", GRAY, ITALIC)
                        .hoverEvent(transactionInfo(transaction)))
                .build();
    }

    public static Component transactionInfo(Transaction transaction) {

        String formattedAmount = RCEconomy.instance().format(transaction.amount());

        return text().append(header(text(formattedAmount, GOLD)
                .append(text(" - ", DARK_AQUA))
                .append(text(transaction.reason().name(), GRAY)))
                .append(text("Quelle: ", YELLOW))
                .append(account(transaction.source())).append(newline())
                .append(text("Ziel: ", YELLOW))
                .append(account(transaction.target())).append(newline())
                .append(text("Betrag: ", YELLOW))
                .append(text(formattedAmount, AQUA))
        ).build();
    }

    public static Component transactionSuccess(Transaction.Result result) {

        String format = RCEconomy.instance().format(result.amount());
        return text()
                .append(account(result.transaction().source()))
                .append(text(" hat ", GREEN))
                .append(account(result.transaction().target()))
                .append(text(" erfolgreich ", GREEN))
                .append(text(format, AQUA))
                .append(text(" überwiesen.", GREEN))
                .build();
    }

    public static Component transactionError(Transaction.Result result) {

        return text("Die ", RED)
                .append(text("Transaktion", GOLD, BOLD).hoverEvent(transactionInfo(result.transaction())))
                .append(text(" konnte nicht durchgeführt werden: ", RED))
                .append(text(result.error(), DARK_RED));
    }

    private static Component header(String name) {

        return header(text(name, GOLD));
    }

    private static Component header(Component name) {

        return text().append(text("--- [ ", DARK_AQUA))
                .append(name)
                .append(text(" ] ---", DARK_AQUA)).append(newline())
                .build();
    }
}
