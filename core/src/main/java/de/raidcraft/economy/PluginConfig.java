package de.raidcraft.economy;

import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;

@Setter
public class PluginConfig {

    private final ConfigurationSection config;
    private DatabaseConfig database;

    public PluginConfig(ConfigurationSection config) {

        this.config = config;
        ConfigurationSection database = config.getConfigurationSection("database");
        if (database == null) {
            database = config.createSection("database");
        }
        this.database = new DatabaseConfig(database);
    }

    public int getFractionalDigits() {

        return config.getInt("fractional_digits", 2);
    }

    public String getCurrencyNameSingular() {

        return config.getString("currency_name_singular", "Coin");
    }

    public String getCurrencyNamePlural() {

        return config.getString("currency_name_plural", "Coins");
    }

    public String getCurrencySymbol() {

        return config.getString("currency_symbol", "c");
    }

    public String getDecimalFormat() {

        return config.getString("decimal_format", "#,##c");
    }

    public DatabaseConfig getDatabase() {
        return database;
    }

    public static class DatabaseConfig {

        private final ConfigurationSection config;

        public DatabaseConfig(ConfigurationSection config) {
            this.config = config;
        }

        public String getUsername() {

            return config.getString("username", "sa");
        }

        public String getPassword() {

            return config.getString("password", "sa");
        }

        public String getDriver() {

            return config.getString("driver", "h2");
        }

        public String getUrl() {

            return config.getString("url", "jdbc:h2:~/economy.db");
        }
    }
}
