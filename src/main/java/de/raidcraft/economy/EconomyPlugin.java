package de.raidcraft.economy;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.google.common.base.Strings;
import de.raidcraft.economy.commands.AdminCommands;
import de.raidcraft.economy.commands.PlayerCommands;
import de.raidcraft.economy.entities.BankAccount;
import de.raidcraft.economy.entities.EconomyPlayer;
import de.raidcraft.economy.entities.PlayerAccount;
import de.raidcraft.economy.entities.Transaction;
import io.ebean.Database;
import kr.entree.spigradle.annotations.PluginMain;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.ebean.Config;
import net.silthus.ebean.EbeanWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.util.Optional;

@PluginMain
public class EconomyPlugin extends JavaPlugin {

    public static final String PERMISSION_PREFIX = "rceconomy.";

    @Getter
    @Accessors(fluent = true)
    private static EconomyPlugin instance;

    private Database database;
    @Getter
    @Setter(AccessLevel.PACKAGE)
    private PluginConfig pluginConfig;

    @Getter
    private RCEconomy economy;
    private PaperCommandManager commandManager;

    @Getter
    private static boolean testing = false;

    public EconomyPlugin() {
        instance = this;
    }

    public EconomyPlugin(
            JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        instance = this;
        testing = true;
    }

    @Override
    public void onEnable() {

        loadConfig();
        setupDatabase();
        setupDefaultAccounts();
        if (!testing) {
            setupVault();
            setupListener();
            setupCommands();
        }
    }

    public void reload() {

        loadConfig();
    }

    private void loadConfig() {

        getDataFolder().mkdirs();
        pluginConfig = new PluginConfig(new File(getDataFolder(), "config.yml").toPath());
        pluginConfig.loadAndSave();
    }

    private void setupVault() {

        this.economy = new RCEconomy(getPluginConfig());
        economy.enable();
    }

    private void setupDefaultAccounts() {

        BankAccount.getServerAccount().save();
    }

    private void setupListener() {


    }

    private void setupCommands() {

        this.commandManager = new PaperCommandManager(this);

        // contexts
        registerEconomyPlayerContext(commandManager);

        // completions

        // conditions
        registerHasEnoughCondition(commandManager);

        commandManager.registerCommand(new AdminCommands(this));
        commandManager.registerCommand(new PlayerCommands(this));
    }

    private void registerEconomyPlayerContext(PaperCommandManager commandManager) {

        commandManager.getCommandContexts().registerIssuerAwareContext(EconomyPlayer.class, c -> {
            String playerName = c.popFirstArg();
            if (Strings.isNullOrEmpty(playerName)) {
                Player player = c.getPlayer();
                if (player == null) {
                    throw new InvalidCommandArgument("Bitte gebe einen Spieler an wenn du den Befehl von der Console ausführst.");
                }
                return EconomyPlayer.getOrCreate(player);
            }

            Optional<EconomyPlayer> economyPlayer = EconomyPlayer.byName(playerName);
            Player player = Bukkit.getPlayerExact(playerName);
            if (economyPlayer.isEmpty() && player == null) {
                throw new InvalidCommandArgument("Es wurde kein Spieler mit dem Namen " + playerName + " gefunden.");
            }

            return economyPlayer.orElseGet(() -> EconomyPlayer.getOrCreate(player));
        });
    }

    private void registerHasEnoughCondition(PaperCommandManager commandManager) {

        commandManager.getCommandConditions().addCondition(EconomyPlayer.class, "money", (context, execContext, player) -> {
            double money = execContext.getResolvedArg("money", Double.class);
            if (!player.has(money)) {
                throw new ConditionFailedException("Du hast nicht genügend " + getEconomy().currencyNamePlural()
                        + "! Du benötigst mindestens " + getEconomy().format(money));
            }
        });
    }

    private void setupDatabase() {

        this.database = new EbeanWrapper(Config.builder(this)
                .entities(
                        EconomyPlayer.class,
                        PlayerAccount.class,
                        BankAccount.class,
                        Transaction.class
                )
                .build()).connect();
    }
}
