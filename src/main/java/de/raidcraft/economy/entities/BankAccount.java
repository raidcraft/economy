package de.raidcraft.economy.entities;

import de.raidcraft.economy.TransactionReason;
import io.ebean.Finder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.ebean.BaseEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@Accessors(fluent = true)
@Entity
@Table(name = "rc_eco_bank_accounts")
public class BankAccount extends BaseEntity {

    public static final Finder<UUID, BankAccount> find = new Finder<>(BankAccount.class);

    public static BankAccount getOrCreate(@NonNull String name, @NonNull EconomyPlayer owner) {

        return find.query()
                .where().eq("owner_id", owner.id())
                .and().eq("name", name)
                .findOneOrEmpty()
                .orElseGet(() -> {
                    BankAccount bankAccount = new BankAccount(name, owner);
                    bankAccount.save();
                    return bankAccount;
                });
    }

    public static Optional<BankAccount> byName(String name) {

        return find.query()
                .where().eq("name", name)
                .findOneOrEmpty();
    }

    public static Optional<BankAccount> of(@NonNull Account account) {

        return find.query()
                .where().eq("account_id", account.id())
                .findOneOrEmpty();
    }

    private String name;
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private Account account = new Account();
    @ManyToOne(optional = false)
    private EconomyPlayer owner;
    @ManyToMany
    private List<EconomyPlayer> members = new ArrayList<>();

    public BankAccount(String name, EconomyPlayer owner) {
        this.name = name;
        this.owner = owner;
    }

    public double balance() {
        return account.balance();
    }

    public boolean has(double amount) {
        return account.has(amount);
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

    public Transaction.Result deposit(double amount) {
        return account.deposit(amount);
    }
}
