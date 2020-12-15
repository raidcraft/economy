package de.raidcraft.economy;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.google.common.base.Strings;
import de.raidcraft.economy.commands.Commands;
import de.raidcraft.economy.entities.Account;
import de.raidcraft.economy.entities.BankAccount;
import de.raidcraft.economy.entities.EconomyPlayer;
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
        if (!isTesting()) {
            setupVault();
            setupCommands();
            setupListener();
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

        Account.getServerAccount().save();
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
        registerOthersCondition(commandManager);

        commandManager.registerCommand(new Commands(this));
    }

    private void registerEconomyPlayerContext(PaperCommandManager commandManager) {

        commandManager.getCommandContexts().registerIssuerOnlyContext(EconomyPlayer.class, c -> EconomyPlayer.getOrCreate(c.getPlayer()));

        commandManager.getCommandContexts().registerIssuerAwareContext(EconomyPlayer.class, c -> {
            String playerName = c.popFirstArg();
            if (Strings.isNullOrEmpty(playerName)) {
                if (c.hasFlag("not-self")) {
                    throw new InvalidCommandArgument("Bitte gebe einen Spieler an.");
                }
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

        commandManager.getCommandConditions().addCondition(EconomyPlayer.class, "hasEnough", (context, execContext, player) -> {
            for (String arg : execContext.getArgs()) {
                try {
                    double amount = Double.parseDouble(arg);
                    if (!player.has(amount)) {
                        throw new ConditionFailedException("Du hast nicht genügend " + getEconomy().currencyNamePlural()
                                + "! Du benötigst mindestens " + getEconomy().format(amount));
                    }
                    return;
                } catch (NumberFormatException ignored) {
                }
            }
            throw new ConditionFailedException("Keinen gültigen Überweisungsbetrag gefunden!");
        });
    }

    private void registerOthersCondition(PaperCommandManager commandManager) {

        commandManager.getCommandConditions().addCondition(EconomyPlayer.class, "others", (context, execContext, value) -> {
            if (context.getIssuer().hasPermission(PERMISSION_PREFIX + context.getConfigValue("perm", "cmd") + ".others")) {
                return;
            }
            if (context.getIssuer().getUniqueId().equals(value.id())) {
                return;
            }
            throw new ConditionFailedException("Du hast nicht genügend Rechte um Befehle im Namen von anderen Spielern auszuführen.");
        });
    }

    private void setupDatabase() {

        this.database = new EbeanWrapper(Config.builder(this)
                .entities(
                        EconomyPlayer.class,
                        Account.class,
                        BankAccount.class,
                        Transaction.class
                )
                .build()).connect();
    }
}
