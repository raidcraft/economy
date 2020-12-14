package de.raidcraft.economy;

import de.raidcraft.economy.entities.Account;
import de.raidcraft.economy.entities.EconomyPlayer;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

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

    public static void send(Player player, Component message) {
        send(player.getUniqueId(), message);
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

    public static Component transactions(Account account) {

        return text().build();
    }

    private static Component header(String name) {

        return text().append(text("--- [ ", DARK_AQUA))
                .append(text(name, GOLD))
                .append(text(" ] ---", DARK_AQUA)).append(newline())
                .build();
    }
}
