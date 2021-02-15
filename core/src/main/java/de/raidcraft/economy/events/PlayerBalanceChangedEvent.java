package de.raidcraft.economy.events;

import de.raidcraft.economy.entities.Transaction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class PlayerBalanceChangedEvent extends RCEconomyEvent implements Cancellable {

    @Getter
    private static HandlerList handlerList = new HandlerList();

    private final OfflinePlayer player;
    private final Transaction transaction;
    private boolean cancelled = false;

    public PlayerBalanceChangedEvent(OfflinePlayer player, Transaction transaction) {
        this.player = player;
        this.transaction = transaction;
    }

    public double getOldBalance() {

        return getTransaction().oldTargetBalance();
    }

    public double getBalance() {

        return getTransaction().newTargetBalance();
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
