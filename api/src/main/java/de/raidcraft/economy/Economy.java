package de.raidcraft.economy;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.List;
import java.util.Map;

public class Economy implements net.milkbowl.vault.economy.Economy {

    private static Economy instance;

    public static Economy get() {

        if (instance == null) {
            init();
        }

        return instance;
    }

    private static void init() {
        Plugin vault = Bukkit.getPluginManager().getPlugin("Vault");
        if (vault == null || !vault.isEnabled()) {
            throw new RuntimeException("Vault not found! Please install Vault to use this plugin.");
        }
        Plugin rcEconomy = Bukkit.getPluginManager().getPlugin("RCEconomy");
        if (rcEconomy != null && rcEconomy.isEnabled()) {
            instance = new Economy((RCEconomy) rcEconomy);
        } else {
            RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = Bukkit.getServer().getServicesManager()
                    .getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (rsp == null) {
                throw new RuntimeException("No valid Vault Economy Implementation found!");
            }
            instance = new Economy(rsp.getProvider());
        }
    }

    private final net.milkbowl.vault.economy.Economy economy;
    private RCEconomy rcEconomy;

    public Economy(net.milkbowl.vault.economy.Economy economy) {
        this.economy = economy;
    }

    public Economy(RCEconomy rcEconomy) {
        this.rcEconomy = rcEconomy;
        this.economy = rcEconomy;
    }

    public boolean isRcEconomyEnabled() {

        return rcEconomy != null;
    }

    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount, String details) {

        if (isRcEconomyEnabled()) {
            return rcEconomy.withdrawPlayer(player, amount, details);
        }

        return withdrawPlayer(player, amount);
    }

    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount, String details, Map<String, Object> data) {

        if (isRcEconomyEnabled()) {
            return rcEconomy.withdrawPlayer(player, amount, details, data);
        }

        return withdrawPlayer(player, amount);
    }

    public EconomyResponse depositPlayer(OfflinePlayer player, double amount, String details, Map<String, Object> data) {

        if (isRcEconomyEnabled()) {
            return rcEconomy.depositPlayer(player, amount, details, data);
        }

        return withdrawPlayer(player, amount);
    }

    @Override
    public boolean isEnabled() {
        return economy.isEnabled();
    }

    @Override
    public String getName() {
        return economy.getName();
    }

    @Override
    public boolean hasBankSupport() {
        return economy.hasBankSupport();
    }

    @Override
    public int fractionalDigits() {
        return economy.fractionalDigits();
    }

    @Override
    public String format(double amount) {
        return economy.format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return economy.currencyNamePlural();
    }

    @Override
    public String currencyNameSingular() {
        return economy.currencyNameSingular();
    }

    @Override
    @Deprecated
    public boolean hasAccount(String playerName) {
        return economy.hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return economy.hasAccount(player);
    }

    @Override
    @Deprecated
    public boolean hasAccount(String playerName, String worldName) {
        return economy.hasAccount(playerName, worldName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return economy.hasAccount(player, worldName);
    }

    @Override
    @Deprecated
    public double getBalance(String playerName) {
        return economy.getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return economy.getBalance(player);
    }

    @Override
    @Deprecated
    public double getBalance(String playerName, String world) {
        return economy.getBalance(playerName, world);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return economy.getBalance(player, world);
    }

    @Override
    @Deprecated
    public boolean has(String playerName, double amount) {
        return economy.has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return economy.has(player, amount);
    }

    @Override
    @Deprecated
    public boolean has(String playerName, String worldName, double amount) {
        return economy.has(playerName, worldName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return economy.has(player, worldName, amount);
    }

    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return economy.withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return economy.withdrawPlayer(player, amount);
    }

    @Override
    @Deprecated
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return economy.withdrawPlayer(playerName, worldName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return economy.withdrawPlayer(player, worldName, amount);
    }

    @Override
    @Deprecated
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return economy.depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return economy.depositPlayer(player, amount);
    }

    @Override
    @Deprecated
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return economy.depositPlayer(playerName, worldName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return economy.depositPlayer(player, worldName, amount);
    }

    @Override
    @Deprecated
    public EconomyResponse createBank(String name, String player) {
        return economy.createBank(name, player);
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return economy.createBank(name, player);
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return economy.deleteBank(name);
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return economy.bankBalance(name);
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return economy.bankHas(name, amount);
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return economy.bankWithdraw(name, amount);
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return economy.bankDeposit(name, amount);
    }

    @Override
    @Deprecated
    public EconomyResponse isBankOwner(String name, String playerName) {
        return economy.isBankOwner(name, playerName);
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return economy.isBankOwner(name, player);
    }

    @Override
    @Deprecated
    public EconomyResponse isBankMember(String name, String playerName) {
        return economy.isBankMember(name, playerName);
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return economy.isBankMember(name, player);
    }

    @Override
    public List<String> getBanks() {
        return economy.getBanks();
    }

    @Override
    @Deprecated
    public boolean createPlayerAccount(String playerName) {
        return economy.createPlayerAccount(playerName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return economy.createPlayerAccount(player);
    }

    @Override
    @Deprecated
    public boolean createPlayerAccount(String playerName, String worldName) {
        return economy.createPlayerAccount(playerName, worldName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return economy.createPlayerAccount(player, worldName);
    }
}
