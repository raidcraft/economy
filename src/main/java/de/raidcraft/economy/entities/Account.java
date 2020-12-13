package de.raidcraft.economy.entities;

import io.ebean.Finder;
import io.ebean.annotation.Index;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.ebean.BaseEntity;
import org.bukkit.OfflinePlayer;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@Accessors(fluent = true)
@MappedSuperclass
public abstract class Account extends BaseEntity {

    public static final Finder<UUID, Account> find = new Finder<>(Account.class);

    public static Account of(OfflinePlayer player) {

        return EconomyPlayer.getOrCreate(player).account();
    }

    public static Optional<Account> byName(String name) {

        return find.query().where()
                .eq("name", name)
                .findOneOrEmpty();
    }

    public static Optional<Account> byName(String name, String type) {

        return find.query().where()
                .eq("name", name)
                .and()
                .eq("type", type)
                .findOneOrEmpty();
    }

    public static List<Account> ofType(String type) {

        return find.query()
                .where().eq("type", type)
                .findList();
    }

    public static List<Account> getBankAccounts() {

        return ofType(Type.BANK);
    }

    @Index
    private String name;
    private String type;
    private double balance;

    Account(String name) {
        this.name = name;
        this.type = Type.SERVER;
    }

    Account(EconomyPlayer player) {
        this.id(player.id());
        this.name = player.name();
        this.type = Type.PLAYER;
    }

    public static class Type {

        private Type() {}

        public static final String PLAYER = "PLAYER";
        public static final String SERVER = "SERVER";
        public static final String BANK = "BANK";
    }
}
