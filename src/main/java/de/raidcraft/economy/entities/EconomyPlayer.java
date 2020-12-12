package de.raidcraft.economy.entities;

import de.raidcraft.economy.EconomyPlugin;
import io.ebean.Finder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.ebean.BaseEntity;
import org.bukkit.OfflinePlayer;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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
                    economyPlayer.account(new Account(economyPlayer));
                    economyPlayer.save();
                    return economyPlayer;
                });
    }

    private String name;
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private Account account;

    EconomyPlayer(OfflinePlayer player) {
        this.id(player.getUniqueId());
        this.name(player.getName());
    }
}
