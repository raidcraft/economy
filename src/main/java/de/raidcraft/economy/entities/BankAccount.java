package de.raidcraft.economy.entities;

import io.ebean.Finder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Accessors(fluent = true)
@Entity
@Table(name = "rc_eco_bank_accounts")
public class BankAccount extends Account {

    public static final Finder<UUID, BankAccount> find = new Finder<>(BankAccount.class);

    public static BankAccount getOrCreate(@NonNull String name, @NonNull EconomyPlayer player) {

        return find.query()
                .where().eq("owner_id", player.id())
                .and().eq("name", name)
                .findOneOrEmpty()
                .orElseGet(() -> {
                    BankAccount bankAccount = new BankAccount(name, player);
                    bankAccount.save();
                    return bankAccount;
                });
    }

    @ManyToOne
    private EconomyPlayer owner;
    @ManyToMany
    private List<EconomyPlayer> members = new ArrayList<>();

    public BankAccount(String name, EconomyPlayer owner) {
        super(name);
        type(Type.BANK);
        this.owner = owner;
    }
}
