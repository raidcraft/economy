package de.raidcraft.economy.art;

import com.google.common.base.Strings;
import de.raidcraft.economy.RCEconomy;
import io.artframework.*;
import io.artframework.annotations.ART;
import io.artframework.annotations.ConfigOption;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;

@ART(value = "money.remove", alias = {"withdraw"}, description = "Removes money from the players account.")
public class RemoveMoneyAction implements Action<OfflinePlayer> {

    @ConfigOption(required = true, description = "The amount of money that is removed from the player.", position = 0)
    private double amount;
    @ConfigOption(description = "The optional details that are recorded in the transaction.", position = 1)
    private String details;

    @Override
    public Result execute(@NonNull Target<OfflinePlayer> target, @NonNull ExecutionContext<ActionContext<OfflinePlayer>> context) {

        if (Strings.isNullOrEmpty(details)) {
            RCEconomy.instance().withdrawPlayer(target.source(), amount);
        } else {
            RCEconomy.instance().withdrawPlayer(target.source(), amount, details);
        }

        return success();
    }
}
