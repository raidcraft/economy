package de.raidcraft.economy.art;

import com.google.common.base.Strings;
import de.raidcraft.economy.RCEconomy;
import io.artframework.*;
import io.artframework.annotations.ART;
import io.artframework.annotations.ConfigOption;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;

@ART(value = "money.add", alias = {"money", "deposit"}, description = "Adds money to the players account.")
public class AddMoneyAction implements Action<OfflinePlayer> {

    @ConfigOption(required = true, description = "The amount of money that is added to the player.", position = 0)
    private double amount;
    @ConfigOption(description = "The optional details that are recorded in the transaction.", position = 1)
    private String details;

    @Override
    public Result execute(@NonNull Target<OfflinePlayer> target, @NonNull ExecutionContext<ActionContext<OfflinePlayer>> context) {

        if (Strings.isNullOrEmpty(details)) {
            RCEconomy.instance().depositPlayer(target.source(), amount);
        } else {
            RCEconomy.instance().depositPlayer(target.source(), amount, details);
        }

        return success();
    }
}
