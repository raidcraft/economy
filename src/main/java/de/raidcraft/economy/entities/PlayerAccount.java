package de.raidcraft.economy.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Accessors(fluent = true)
@Entity
@Table(name = "rc_eco_player_accounts")
public class PlayerAccount extends Account {

    PlayerAccount(EconomyPlayer player) {
        super(player);
    }
}
