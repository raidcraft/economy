package de.raidcraft.economy.entities;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import de.raidcraft.economy.EconomyPlugin;
import net.silthus.ebean.BaseEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EconomyPlayerTest {

    private ServerMock server;
    private EconomyPlugin plugin;

    @BeforeEach
    void setUp() {

        this.server = MockBukkit.mock();
        this.plugin = MockBukkit.load(EconomyPlugin.class);
    }

    @AfterEach
    void tearDown() {

        MockBukkit.unmock();
    }

    @Test
    @DisplayName("should create account table for new economy players")
    void shouldCreateAccountForNewPlayers() {

        PlayerMock player = server.addPlayer();
        UUID id = EconomyPlayer.getOrCreate(player).account().id();

        assertThat(Account.of(player))
                .extracting(BaseEntity::id, Account::name)
                .contains(id, player.getName());
    }
}