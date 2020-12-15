package de.raidcraft.economy.entities;

import de.raidcraft.economy.RCEconomy;
import de.raidcraft.economy.TransactionReason;
import de.raidcraft.economy.TransactionStatus;
import io.ebean.annotation.DbJson;
import io.ebean.annotation.Transactional;
import io.ebean.text.json.EJson;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.Value;
import lombok.experimental.Accessors;
import net.silthus.ebean.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Accessors(fluent = true)
@Entity
@Table(name = "rc_eco_transactions")
public class Transaction extends BaseEntity implements Comparable<Transaction> {

    static {
        try {
            EJson.write(new Object());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static Transaction create(@NonNull Account target, double amount) {

        return create(target, amount, TransactionReason.OTHER);
    }

    public static Transaction create(@NonNull Account target, double amount, TransactionReason reason) {

        return create(target, amount).reason(reason);
    }

    public static Transaction create(@NonNull Account target, double amount, TransactionReason reason, String details) {

        return create(target, amount, details).reason(reason);
    }

    public static Transaction create(@NonNull Account target, double amount, String details) {

        return create(Account.getServerAccount(), target, amount, details);
    }

    public static Transaction create(@NonNull Account source, @NonNull Account target, double amount) {

        return create(source, target, amount, TransactionReason.OTHER);
    }

    public static Transaction create(@NonNull Account source, @NonNull Account target, double amount, TransactionReason reason) {

        return new Transaction(source, target).amount(amount).reason(reason);
    }

    public static Transaction create(@NonNull Account source, @NonNull Account target, double amount, TransactionReason reason, String details) {

        return create(source, target, amount, details).reason(reason);
    }

    public static Transaction create(@NonNull Account source, @NonNull Account target, double amount, String details) {

        return new Transaction(source, target).amount(amount).details(details);
    }

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private Account source;
    private double oldSourceBalance;
    private double newSourceBalance;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private Account target;
    private double oldTargetBalance;
    private double newTargetBalance;

    private double amount;

    private TransactionStatus status = TransactionStatus.PENDING;
    private TransactionReason reason = TransactionReason.OTHER;
    private String details;

    @DbJson
    private Map<String, Object> data = new HashMap<>();

    Transaction(Account source, Account target) {

        this.source = source;
        this.oldSourceBalance = source.balance();
        this.target = target;
        this.oldTargetBalance = target.balance();
    }

    private Transaction(Account source, Account target, Transaction transaction, double amount) {

        this(source, target);
        this.amount = amount;
        this.status = transaction.status;
        this.reason = transaction.reason;
        this.details = transaction.details;
        this.data = Map.copyOf(transaction.data);
    }

    public Transaction data(String key, Object value) {

        data.put(key, value);
        return this;
    }

    public boolean done() {

        return status != TransactionStatus.PENDING;
    }

    public boolean success() {

        return status == TransactionStatus.SUCCESS;
    }

    /**
     * Executes this transaction transfering money from the source account to the target account.
     * <p>If the source account is not the server account a has enough check will be performed.
     * The transaction will fail if the source account has not enough money.
     * <p>You can provide additional data for this transaction before calling this method
     * by using the {@link #data(String, Object)}, {@link #reason(TransactionReason)} and
     * {@link #details(String)} methods.
     *
     * @return the result of the transaction
     */
    @Transactional
    public Result execute() {

        if (reason() == TransactionReason.SET_BALANCE) {
            return executeSetBalance();
        }

        if (amount == 0) {
            return new Result(this, TransactionStatus.EMPTY_TRANSACTION, "Die Transaktion erzeugt keine Änderung.");
        }

        if (amount < 0) {
            return new Result(this, TransactionStatus.INVALID, "Der Transaktionsbetrag darf nicht negativ sein.");
        }

        String formattedAmount = RCEconomy.instance().format(amount());
        if (!source.isServerAccount() && !source().has(amount())) {
            return new Result(this, TransactionStatus.NOT_ENOUGH_MONEY,
                    "Der Account enthält nicht genügend Geld (mind. " + formattedAmount + ") für die Transaktion.");
        }

        double balance = source.balance() - amount;
        if (balance < 0) {
            if (!source.isServerAccount()) {
                amount = amount + balance;
            }
            balance = 0;
        }

        newSourceBalance(balance);
        newTargetBalance(target.balance() + amount);

        source.balance(newSourceBalance);
        target.balance(newTargetBalance);

        target.receivedTransactions().add(this);
        source.sentTransactions().add(this);

        status(TransactionStatus.SUCCESS);
        save();

        return new Result(this);
    }

    private Result executeSetBalance() {

        newTargetBalance(amount);

        target.balance(amount);

        target.receivedTransactions().add(this);
        source.sentTransactions().add(this);

        status(TransactionStatus.SUCCESS);
        save();

        return new Result(this);
    }

    /**
     * Performs a rollback of this transaction if the transaction was performed successfully
     * and if the new source (old target) account still has enough money to rollback the transaction.
     * <p>Calling this is the same as inverting the source and target and executing a new transaction.
     *
     * @return the result of the rollback
     * @see #execute()
     */
    public Result rollback() {

        if (!success()) {
            return new Result(this, TransactionStatus.FAILURE,
                    "Die Transaktion kann nicht rückgängig gemacht werden da sie nie ausgeführt wurde.");
        }

        return new Transaction(target(), source())
                .reason(TransactionReason.ROLLBACK)
                .details(details())
                .data(data())
                .execute();
    }

    @Override
    public int compareTo(Transaction o) {

        return whenModified().compareTo(o.whenModified());
    }

    @Value
    public static class Result {

        Transaction transaction;
        TransactionStatus status;
        String error;

        Result(Transaction transaction) {

            this.transaction = transaction;
            this.status = TransactionStatus.SUCCESS;
            this.error = null;
        }

        Result(Transaction transaction, TransactionStatus status, String error) {

            this.transaction = transaction;
            this.status = status;
            this.error = error;
        }

        public double amount() {

            return transaction.amount();
        }

        public boolean success() {

            return status == TransactionStatus.SUCCESS;
        }
    }
}
