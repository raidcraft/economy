package de.raidcraft.economy.wrapper;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.util.Map;

public interface Economy extends net.milkbowl.vault.economy.Economy {

    static Economy get() {

        return EconomyWrapper.get();
    }

    /**
     * Checks if the RCEconomy plugin exists and is enabled.
     * <p>This wrapper will automatically fallback to the default Vault implementation
     * if the RCEconomy plugin is not found.
     *
     * @return true if RCEconomy exists and is enabled
     */
    boolean isRcEconomyEnabled();

    /**
     * Withdraws the given amount from the player and logs the transaction with the given details.
     * <p>Use this to show rich information to players when they execute the <pre>/transactions</pre> command.
     * <p>The details are silently omitted if {@link #isRcEconomyEnabled()} is false.
     *
     * @param player the player to withdraw money from
     * @param amount the amount to withdraw
     * @param details the details to log into the transaction log
     * @return the result of the transaction
     */
    EconomyResponse withdrawPlayer(OfflinePlayer player, double amount, String details);

    EconomyResponse withdrawPlayer(OfflinePlayer player, double amount, String details, Map<String, Object> data);

    EconomyResponse depositPlayer(OfflinePlayer player, double amount, String details);

    EconomyResponse depositPlayer(OfflinePlayer player, double amount, String details, Map<String, Object> data);
}
