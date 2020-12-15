package de.raidcraft.economy.entities;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import de.raidcraft.economy.EconomyPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AccountTest {

    private ServerMock server;
    private EconomyPlugin plugin;

    @BeforeEach
    void setUp() {

        this.server = MockBukkit.mock(new de.raidcraft.economy.ServerMock());
        this.plugin = MockBukkit.load(EconomyPlugin.class);
    }

    @AfterEach
    void tearDown() {

        MockBukkit.unmock();
    }

    @Test
    @DisplayName("should create and store new accounts")
    void shouldCreateAccount() {

        Account account = new Account();
        account.save();
        UUID id = account.id();

        assertThat(Account.find.byId(id)).isNotNull();
    }

    @Test
    @DisplayName("should auto create server account")
    void shouldCreateServerAccount() {

        Account.getServerAccount().save();

        assertThat(Account.find.byId(Account.SERVER_ID)).isNotNull();
    }
}