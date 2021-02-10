package de.raidcraft.economy;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.google.common.base.Strings;
import de.raidcraft.economy.art.AddMoneyAction;
import de.raidcraft.economy.art.HasMoneyRequirement;
import de.raidcraft.economy.art.RemoveMoneyAction;
import de.raidcraft.economy.commands.Commands;
import de.raidcraft.economy.entities.Account;
import de.raidcraft.economy.entities.BankAccount;
import de.raidcraft.economy.entities.EconomyPlayer;
import de.raidcraft.economy.entities.Transaction;
import de.raidcraft.economy.events.MoneyTransactionEvent;
import io.artframework.Scope;
import io.artframework.annotations.ArtModule;
import io.artframework.annotations.OnLoad;
import io.ebean.Database;
import kr.entree.spigradle.annotations.PluginMain;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.silthus.ebean.Config;
import net.silthus.ebean.EbeanWrapper;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@PluginMain
@ArtModule(value = "rceconomy")
public class EconomyPlugin extends JavaPlugin implements Listener {

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

    @OnLoad
    public void onArtLoad(Scope scope) {

        scope.configuration().art()
                .actions()
                    .add(AddMoneyAction.class)
                    .add(RemoveMoneyAction.class)
                .requirements()
                    .add(HasMoneyRequirement.class);
    }

    @Override
    public void onEnable() {

        loadConfig();
        setupDatabase();
        setupDefaultAccounts();
        setupVault();
        setupCommands();
        setupListener();
    }

    public void reload() {

        loadConfig();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTransaction(MoneyTransactionEvent event) {

        Account target = event.getTransaction().target();
        if (target.isPlayerAccount()) {
            Messages.send(EconomyPlayer.of(target), Messages.payReceive(event.getTransaction()));
        }
    }

    private void loadConfig() {

        try {
            getDataFolder().mkdirs();
            saveDefaultConfig();
            FileConfiguration config = getConfig();
            config.load(new File(getDataFolder(), "config.yml"));
            pluginConfig = new PluginConfig(config);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void setupVault() {

        this.economy = new RCEconomy(getPluginConfig());
        economy.enable();
    }

    private void setupDefaultAccounts() {

        Account.getServerAccount().save();
    }

    private void setupListener() {

        getServer().getPluginManager().registerEvents(this, this);
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
