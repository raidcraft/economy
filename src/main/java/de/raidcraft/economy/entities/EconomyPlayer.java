package de.raidcraft.economy.entities;

import de.raidcraft.economy.TransactionReason;
import io.ebean.Finder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.ebean.BaseEntity;
import org.bukkit.OfflinePlayer;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@Accessors(fluent = true)
@Entity
@Table(name = "rc_eco_players")
public class EconomyPlayer extends BaseEntity {

    public static final Finder<UUID, EconomyPlayer> find = new Finder<>(EconomyPlayer.class);

    public static EconomyPlayer getOrCreate(OfflinePlayer player) {

        return Optional.ofNullable(find.byId(player.getUniqueId()))
                .orElseGet(() -> {
                    EconomyPlayer economyPlayer = new EconomyPlayer(player);
                    economyPlayer.save();
                    return economyPlayer;
                });
    }

    public static Optional<EconomyPlayer> byName(String playerName) {

        return find.query()
                .where().eq("name", playerName)
                .findOneOrEmpty();
    }

    public static Optional<EconomyPlayer> of(@NonNull Account account) {

        return find.query()
                .where().eq("account_id", account.id())
                .findOneOrEmpty();
    }

    private String name;
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private Account account = new Account();
    @OneToMany
    private List<BankAccount> ownedBankAccounts = new ArrayList<>();
    @ManyToMany
    private List<BankAccount> bankAccounts = new ArrayList<>();

    EconomyPlayer(OfflinePlayer player) {
        this.id(player.getUniqueId());
        this.name(player.getName());
    }

    public List<Transaction> transactions() {

        return account.transactions();
    }

    public boolean has(double amount) {

        return account.has(amount);
    }

    public double balance() {
        return account.balance();
    }

    public Transaction.Result setBalance(double balance) {

        return account.setBalance(balance);
    }

    public Transaction.Result setBalance(double balance, String details) {

        return account.setBalance(balance, details);
    }

    public Transaction.Result transfer(@NonNull Account target, double amount) {

        return account.transfer(target, amount);
    }

    public Transaction.Result transfer(@NonNull Account target, double amount, TransactionReason reason) {

        return account.transfer(target, amount, reason);
    }

    public Transaction.Result transfer(@NonNull Account target, double amount, TransactionReason reason, String details) {

        return account.transfer(target, amount, reason, details);
    }

    public Transaction.Result withdraw(double amount) {

        return account.withdraw(amount);
    }

    public Transaction.Result withdraw(double amount, String details) {
        return account.withdraw(amount, details);
    }

    public Transaction.Result deposit(double amount) {

        return account.deposit(amount);
    }

    public Transaction.Result deposit(double amount, String details) {
        return account.deposit(amount, details);
    }
}
