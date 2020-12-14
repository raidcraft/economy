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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@Accessors(fluent = true)
@Entity
@Table(name = "rc_eco_accounts")
public class Account extends BaseEntity {

    public static final UUID SERVER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    public static final Finder<UUID, Account> find = new Finder<>(Account.class);

    public static Account of(OfflinePlayer player) {

        return EconomyPlayer.getOrCreate(player).account();
    }

    public static Account getServerAccount() {

        return Optional.ofNullable(find.byId(SERVER_ID))
                .orElseGet(() -> {
                    Account account = new Account();
                    account.id(SERVER_ID);
                    account.save();
                    return account;
                });
    }

    private double balance;
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "source")
    private List<Transaction> sentTransactions = new ArrayList<>();
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "target")
    private List<Transaction> receivedTransactions = new ArrayList<>();

    Account() {
    }

    /**
     * Checks if this account is the server account.
     * <p>The server account can withdraw and deposit unlimited amounts if money.
     * <p>Get the server account by calling {@link #getServerAccount()}.
     *
     * @return true if this is the server account
     */
    public boolean isServerAccount() {

        return id().equals(SERVER_ID);
    }

    public boolean isPlayerAccount() {

        return EconomyPlayer.of(this).isPresent();
    }

    public boolean isBankAccount() {

        return BankAccount.of(this).isPresent();
    }

    public List<Transaction> transactions() {

        return Stream.concat(
                sentTransactions().stream(),
                receivedTransactions().stream()
        ).sorted(Comparator.reverseOrder()).collect(Collectors.toUnmodifiableList());
    }

    /**
     * Checks if the balance of this account is at least the given amount.
     *
     * @param amount the amount to check
     * @return true if this account has enough money
     */
    public boolean has(double amount) {

        return balance >= amount;
    }

    /**
     * Transfers the given amount of money from this account to the target account.
     * @param target
     * @param amount
     * @return
     */
    public Transaction.Result transfer(@NonNull Account target, double amount) {

        return Transaction.create(this, target, amount).execute();
    }

    public Transaction.Result transfer(@NonNull Account target, double amount, TransactionReason reason) {

        return Transaction.create(this, target, amount, reason).execute();
    }

    public Transaction.Result transfer(@NonNull Account target, double amount, TransactionReason reason, String details) {

        return Transaction.create(this, target, amount, details).reason(reason).execute();
    }

    public Transaction.Result withdraw(double amount) {

        return transfer(getServerAccount(), amount, TransactionReason.WITHDRAW);
    }

    public Transaction.Result deposit(double amount) {

        return Transaction.create(getServerAccount(), this, amount, TransactionReason.DEPOSIT).execute();
    }
}
