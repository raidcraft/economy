package de.raidcraft.economy.entities;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import de.raidcraft.economy.EconomyPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EconomyPlayerTest {

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
    @DisplayName("should create account table for new economy players")
    void shouldCreateAccountForNewPlayers() {

        PlayerMock player = server.addPlayer();
        EconomyPlayer.getOrCreate(player);

        assertThat(EconomyPlayer.find.byId(player.getUniqueId()))
                .isNotNull()
                .extracting(EconomyPlayer::name)
                .isEqualTo(player.getName());
    }

    @Nested
    class Balance {

        @Test
        @DisplayName("should only create one transaction if balance is set directly")
        void shouldOnlyCreateOneTransactionWhenBalanceIsSet() {

            EconomyPlayer player = EconomyPlayer.getOrCreate(server.addPlayer());
            player.account().balance(1000d).save();

            player.setBalance(100d);

            assertThat(player.account())
                    .extracting(Account::balance, a -> a.transactions().size())
                    .contains(100d, 1);
        }
    }
}