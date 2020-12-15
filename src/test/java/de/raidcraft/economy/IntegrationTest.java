package de.raidcraft.economy;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import de.raidcraft.economy.entities.EconomyPlayer;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTest {

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

    @Nested
    @DisplayName("Commands")
    class Commands {

        private Player player;

        @BeforeEach
        void setUp() {
            player = server.addPlayer();
        }

        @Nested
        @DisplayName("/money")
        class AdminCommands {

            @Nested
            @DisplayName("set")
            class set {

                @Test
                @DisplayName("should work")
                void shouldWork() {

                    server.dispatchCommand(server.getConsoleSender(),"money set " + player.getName() + " 1000");
                    assertThat(EconomyPlayer.getOrCreate(player).balance())
                            .isEqualTo(1000d);
                }
            }
        }
    }
}
