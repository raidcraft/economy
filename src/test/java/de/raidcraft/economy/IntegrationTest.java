package de.raidcraft.economy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import de.raidcraft.economy.commands.Commands;
import de.raidcraft.economy.entities.EconomyPlayer;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTest {

    private ServerMock server;
    private EconomyPlugin plugin;
    private Player opPlayer;

    @BeforeEach
    void setUp() {

        try {
            this.server = MockBukkit.mock(new de.raidcraft.economy.ServerMock());
            this.plugin = MockBukkit.load(EconomyPlugin.class);
            this.opPlayer = server.addPlayer();
            this.opPlayer.setOp(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {

        MockBukkit.unmock();
    }

    @Nested
    @DisplayName("Commands")
    class Commands {

        private Player player;

        @BeforeEach
        void setUp() {
            player = server.addPlayer("Test");
        }

        @Nested
        @DisplayName("/money")
        class AdminCommands {

            @Nested
            @DisplayName("set")
            class set {

                @Test
                @DisplayName("/money set player amount")
                void setMoneyShouldWork() {

                    server.dispatchCommand(opPlayer,"money set " + player.getName() + " 1000");
                    assertThat(EconomyPlayer.getOrCreate(player).balance())
                            .isEqualTo(1000d);
                }
            }
        }
    }
}
