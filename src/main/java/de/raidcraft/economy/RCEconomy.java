package de.raidcraft.economy;

import de.raidcraft.economy.entities.Account;
import de.raidcraft.economy.entities.EconomyPlayer;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

@Data
public class RCEconomy implements Economy {

    private final String name = "RCEconomy";

    private final PluginConfig config;
    private DecimalFormat decimalFormat;

    @Setter(AccessLevel.PACKAGE)
    private boolean enabled;

    public void load() {

        getConfig().loadAndSave();
        this.decimalFormat = new DecimalFormat(getConfig().getDecimalFormat());
    }

    @Override
    public boolean hasBankSupport() {
        return getConfig().isBankSupport();
    }

    @Override
    public int fractionalDigits() {
        return getConfig().getFractionalDigits();
    }

    @Override
    public String currencyNamePlural() {
        return getConfig().getCurrencyNamePlural();
    }

    @Override
    public String currencyNameSingular() {
        return getConfig().getCurrencyNameSingular();
    }

    @Override
    public String format(double amount) {
        return getDecimalFormat().format(amount);
    }

    @Override
    @Deprecated
    public double getBalance(String playerName) {

        return Account.byName(playerName, Account.Type.PLAYER)
                .map(Account::balance)
                .orElse(0d);
    }

    @Override
    public double getBalance(OfflinePlayer player) {

        return Account.of(player).balance();
    }

    @Override
    public double getBalance(String playerName, String world) {

        return getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {

        return getBalance(player);
    }

    @Override
    public boolean has(String playerName, double amount) {

        return getBalance(playerName) >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {

        return getBalance(player) >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {

        return has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {

        return has(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {

        Optional<Account> account = Account.byName(playerName, Account.Type.PLAYER);
        if (account.isEmpty()) {
            return new EconomyResponse(0, 0,
                    EconomyResponse.ResponseType.FAILURE,
                    "Der Spieler " + playerName + " besitzt kein Konto und war vermutlich noch nie online.");
        }
        double balance = account.get().balance();
        double newBalance = balance - amount;
        if (newBalance < 0) {
            amount = amount + newBalance;
            newBalance = 0;
        }

        account.get().balance(newBalance).save();

        return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {

        EconomyPlayer.getOrCreate(player);
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player);
    }

    @Override
    @Deprecated
    public boolean hasAccount(String playerName) {

        return Account.byName(playerName, Account.Type.PLAYER).isPresent();
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {

        return true;
    }

    @Override
    @Deprecated
    public boolean hasAccount(String playerName, String worldName) {

        return hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {

        return hasAccount(player);
    }

    @Override
    @Deprecated
    public boolean createPlayerAccount(String playerName) {

        Player player = Bukkit.getPlayerExact(playerName);
        if (player == null) return false;

        EconomyPlayer.getOrCreate(player);
        return true;
    }

    @Override
    @Deprecated
    public boolean createPlayerAccount(String playerName, String worldName) {

        return createPlayerAccount(playerName);
    }
}
