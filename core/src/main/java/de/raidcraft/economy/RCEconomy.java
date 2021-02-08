package de.raidcraft.economy;

import de.raidcraft.economy.entities.Account;
import de.raidcraft.economy.entities.BankAccount;
import de.raidcraft.economy.entities.EconomyPlayer;
import de.raidcraft.economy.entities.Transaction;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Log(topic = "RCEconomy")
public class RCEconomy implements Economy {

    @Getter
    @Accessors(fluent = true)
    private static RCEconomy instance;

    private final String name = "RCEconomy";

    private final PluginConfig config;
    private DecimalFormat decimalFormat;

    @Setter(AccessLevel.PACKAGE)
    private boolean enabled;

    public RCEconomy(PluginConfig config) {

        this.config = config;
        instance = this;
    }

    void enable() {

        if (isEnabled()) return;

        load();

        try {
            Plugin vault = Bukkit.getPluginManager().getPlugin("Vault");
            if (vault == null) {
                log.severe("cannot inject Vault: the Vault plugin was not found!");
                return;
            }

            Bukkit.getServicesManager().register(Economy.class,this, vault, ServicePriority.Normal);
            Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
            setEnabled(true);

            log.info("injected custom Economy provider: " + getClass().getCanonicalName());
        } catch (Exception e) {
            log.severe("cannot inject Vault: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void load() {

        this.decimalFormat = new DecimalFormat(getConfig().getDecimalFormat());
    }

    @Override
    public boolean hasBankSupport() {
        return true;
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
        return NumberFormat.getCurrencyInstance(Locale.GERMANY)
                .format(amount)
                .replace("\u00A0", "")
                .replace("€", getConfig().getCurrencySymbol());
    }

    public double getBalance(Account account) {

        return account.balance();
    }

    @Override
    @Deprecated
    public double getBalance(String playerName) {

        return EconomyPlayer.byName(playerName)
                .map(EconomyPlayer::balance)
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

    public EconomyResponse withdraw(Account account, double amount) {

        return withdraw(account, amount, null);
    }

    public EconomyResponse withdraw(Account account, double amount, String details) {

        return withdraw(account, amount, details, null);
    }

    public EconomyResponse withdraw(Account account, double amount, String details, Map<String, Object> data) {

        Transaction.Result result = account.withdraw(amount, details, data);

        if (result.success()) {
            return new EconomyResponse(result.amount(), account.balance(), EconomyResponse.ResponseType.SUCCESS, result.error());
        }

        return new EconomyResponse(result.amount(), account.balance(), EconomyResponse.ResponseType.FAILURE, result.error());
    }

    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount, String details) {

        return withdraw(EconomyPlayer.getOrCreate(player).account(), amount, details);
    }

    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount, String details, Map<String, Object> data) {

        return withdraw(EconomyPlayer.getOrCreate(player).account(), amount, details, data);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {

        Optional<Account> account = EconomyPlayer.byName(playerName)
                .map(EconomyPlayer::account);
        if (account.isEmpty()) {
            return new EconomyResponse(0, 0,
                    EconomyResponse.ResponseType.FAILURE,
                    "Der Spieler " + playerName + " besitzt kein Konto und war vermutlich noch nie online.");
        }

        return withdraw(account.get(), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {

        return withdraw(Account.of(player), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {

        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {

        return withdrawPlayer(player, amount);
    }

    public EconomyResponse deposit(Account account, double amount) {

        return deposit(account, amount, null);
    }

    public EconomyResponse deposit(Account account, double amount, String details) {

        return deposit(account, amount, details, null);
    }

    public EconomyResponse deposit(Account account, double amount, String details, Map<String, Object> data) {

        Transaction.Result result = account.deposit(amount, details, data);

        if (!result.success()) {
            return new EconomyResponse(result.amount(), account.balance(), EconomyResponse.ResponseType.FAILURE, result.error());
        }

        return new EconomyResponse(result.amount(), account.balance(), EconomyResponse.ResponseType.SUCCESS, result.error());
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {

        Optional<Account> account = EconomyPlayer.byName(playerName).map(EconomyPlayer::account);
        if (account.isEmpty()) {
            return new EconomyResponse(0, 0,
                    EconomyResponse.ResponseType.FAILURE,
                    "Der Spieler " + playerName + " besitzt kein Konto und war vermutlich noch nie online.");
        }

        return deposit(account.get(), amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {

        return deposit(Account.of(player), amount);
    }

    public EconomyResponse depositPlayer(OfflinePlayer player, double amount, String details) {

        return deposit(Account.of(player), amount, details);
    }

    public EconomyResponse depositPlayer(OfflinePlayer player, double amount, String details, Map<String, Object> data) {

        return deposit(Account.of(player), amount, details, data);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {

        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {

        return depositPlayer(player, amount);
    }

    public EconomyResponse createBank(String name, EconomyPlayer owner) {

        BankAccount account = BankAccount.getOrCreate(name, owner);
        return new EconomyResponse(0, account.balance(), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {

        Optional<EconomyPlayer> economyPlayer = EconomyPlayer.byName(player);
        if (economyPlayer.isEmpty()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
                    "Der Spieler " + player + " existiert nicht und war vermutlich noch nie online");
        }

        return createBank(name, economyPlayer.get());
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {

        return createBank(name, EconomyPlayer.getOrCreate(player));
    }

    @Override
    public EconomyResponse deleteBank(String name) {

        Optional<BankAccount> account = BankAccount.byName(name);
        if (account.isEmpty()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
                    "Der Bank Account " + name + " existiert nicht.");
        }
        account.get().delete();
        return new EconomyResponse(0, account.get().balance(), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse bankBalance(String name) {

        Optional<BankAccount> account = BankAccount.byName(name);
        if (account.isEmpty()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
                    "Der Bank Account " + name + " existiert nicht.");
        }
        return new EconomyResponse(0, account.get().balance(), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        Optional<BankAccount> account = BankAccount.byName(name);
        if (account.isEmpty()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
                    "Der Bank Account " + name + " existiert nicht.");
        }
        double balance = account.get().balance();
        if (balance >= amount) {
            return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.SUCCESS, null);
        }

        return new EconomyResponse(amount, balance, EconomyResponse.ResponseType.FAILURE, "Die Bank hat nicht genug Geld.");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {

        Optional<Account> account = BankAccount.byName(name).map(BankAccount::account);
        if (account.isEmpty()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
                    "Der Bank Account " + name + " existiert nicht.");
        }
        return withdraw(account.get(), amount);
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {

        Optional<Account> account = BankAccount.byName(name).map(BankAccount::account);
        if (account.isEmpty()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
                    "Der Bank Account " + name + " existiert nicht.");
        }
        return deposit(account.get(), amount);
    }

    public EconomyResponse isBankOwner(BankAccount account, EconomyPlayer player) {

        if (account.owner().equals(player)) {
            return new EconomyResponse(0, account.balance(), EconomyResponse.ResponseType.SUCCESS, null);
        }
        return new EconomyResponse(0, account.balance(), EconomyResponse.ResponseType.FAILURE,
                "Der Spieler " + player.name() + " ist nicht der Besitzer des Bank Accounts " + name);
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {

        Optional<BankAccount> account = BankAccount.byName(name);
        if (account.isEmpty()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
                    "Der Bank Account " + name + " existiert nicht.");
        }

        Optional<EconomyPlayer> economyPlayer = EconomyPlayer.byName(playerName);
        if (economyPlayer.isEmpty()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
                    "Der Spieler " + playerName + " existiert nicht.");
        }

        return isBankOwner(account.get(), economyPlayer.get());
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {

        Optional<BankAccount> account = BankAccount.byName(name);
        if (account.isEmpty()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
                    "Der Bank Account " + name + " existiert nicht.");
        }

        return isBankOwner(account.get(), EconomyPlayer.getOrCreate(player));
    }

    public EconomyResponse isBankMember(BankAccount account, EconomyPlayer player) {

        if (account.members().contains(player)) {
            return new EconomyResponse(0, account.balance(), EconomyResponse.ResponseType.SUCCESS, null);
        }

        return new EconomyResponse(0, account.balance(), EconomyResponse.ResponseType.FAILURE,
                "Der Spieler " + player.name() + " ist kein Mitglied des Bank Accounts " + account.name());
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {

        Optional<BankAccount> account = BankAccount.byName(name);
        if (account.isEmpty()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
                    "Der Bank Account " + name + " existiert nicht.");
        }

        Optional<EconomyPlayer> economyPlayer = EconomyPlayer.byName(playerName);
        if (economyPlayer.isEmpty()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
                    "Der Spieler " + playerName + " existiert nicht.");
        }

        return isBankMember(account.get(), economyPlayer.get());
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {

        Optional<BankAccount> account = BankAccount.byName(name);
        if (account.isEmpty()) {
            return new EconomyResponse(0, 0, EconomyResponse.ResponseType.FAILURE,
                    "Der Bank Account " + name + " existiert nicht.");
        }

        return isBankMember(account.get(), EconomyPlayer.getOrCreate(player));
    }

    @Override
    public List<String> getBanks() {

        return BankAccount.find.all().stream()
                .map(BankAccount::name)
                .collect(Collectors.toList());
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

        return EconomyPlayer.byName(playerName).isPresent();
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
