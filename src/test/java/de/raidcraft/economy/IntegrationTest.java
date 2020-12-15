package de.raidcraft.economy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import de.raidcraft.economy.entities.EconomyPlayer;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
                @DisplayName("\"/money set player amount\" works")
                void setMoneyShouldWork() {

                    server.dispatchCommand(opPlayer,"money set " + player.getName() + " 1000");
                    assertThat(EconomyPlayer.getOrCreate(player).balance())
                            .isEqualTo(1000d);
                }
            }

            @Nested
            class pay {

                PlayerMock source;
                PlayerMock target;

                @BeforeEach
                void setUp() {

                    source = server.addPlayer("source");
                    source.addAttachment(plugin, EconomyPlugin.PERMISSION_PREFIX + "money.pay", true);
                    target = server.addPlayer("target");

                    EconomyPlayer.getOrCreate(source).setBalance(100);
                    EconomyPlayer.getOrCreate(target).setBalance(0);
                }

                @Test
                @DisplayName("/money pay player amount should transfer money correctly")
                void payShouldTransferMoney() {

                    server.dispatchCommand(source, "money pay " + target.getName() + " 100");

                    assertThat(EconomyPlayer.getOrCreate(source).balance()).isEqualTo(0d);
                    assertThat(EconomyPlayer.getOrCreate(target).balance()).isEqualTo(100d);
                }

                @Test
                @DisplayName("/money pay should not work if source has not enough money")
                void payShouldNotWorkIfSourceHasNotEnoughMoney() {

                    server.dispatchCommand(source, "money pay " + target.getName() + " 101");

                    assertThat(EconomyPlayer.getOrCreate(source).balance()).isEqualTo(100d);
                    assertThat(EconomyPlayer.getOrCreate(target).balance()).isEqualTo(0d);
                }

                @Test
                @DisplayName("/money pay should not work with negative amounts")
                void payShouldNotWorkWithNegativeAmounts() {

                    server.dispatchCommand(source, "money pay " + target.getName() + " -50");

                    assertThat(EconomyPlayer.getOrCreate(source).balance()).isEqualTo(100d);
                    assertThat(EconomyPlayer.getOrCreate(target).balance()).isEqualTo(0d);
                }
            }
        }
    }
}
