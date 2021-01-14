package de.raidcraft.economy.art;

import de.raidcraft.economy.RCEconomy;
import io.artframework.*;
import io.artframework.annotations.ART;
import io.artframework.annotations.ConfigOption;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;

@ART(value = "money", description = "Checks the balance of the player.")
public class HasMoneyRequirement implements Requirement<OfflinePlayer> {

    @ConfigOption(required = true, description = "The amount of money the player requires.", position = 0)
    private double amount;

    @Override
    public Result test(@NonNull Target<OfflinePlayer> target, @NonNull ExecutionContext<RequirementContext<OfflinePlayer>> context) {

        return resultOf(RCEconomy.instance().has(target.source(), amount), "Not enough money!");
    }
}
