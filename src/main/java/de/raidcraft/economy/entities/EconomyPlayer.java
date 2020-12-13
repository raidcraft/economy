package de.raidcraft.economy.entities;

import io.ebean.Finder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.ebean.BaseEntity;
import org.bukkit.OfflinePlayer;

import javax.persistence.*;
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
                    economyPlayer.account(new PlayerAccount(economyPlayer));
                    economyPlayer.save();
                    return economyPlayer;
                });
    }

    public static Optional<EconomyPlayer> byName(String playerName) {

        return find.query()
                .where().eq("name", playerName)
                .findOneOrEmpty();
    }

    private String name;
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private PlayerAccount account;
    @OneToMany
    private List<BankAccount> ownedBankAccounts = new ArrayList<>();

    @ManyToMany
    private List<BankAccount> bankAccounts = new ArrayList<>();

    EconomyPlayer(OfflinePlayer player) {
        this.id(player.getUniqueId());
        this.name(player.getName());
    }
}
