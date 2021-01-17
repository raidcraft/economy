package de.raidcraft.economy.events;

import de.raidcraft.economy.entities.Transaction;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

@Getter
@Setter
public class MoneyTransactionEvent extends RCEconomyEvent implements Cancellable {

    @Getter
    private static HandlerList handlerList = new HandlerList();

    private final Transaction transaction;
    private boolean cancelled = false;

    public MoneyTransactionEvent(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
