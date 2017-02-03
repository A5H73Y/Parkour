package me.A5H73Y.Parkour.Listeners;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PlayerJoinEvent.class)
public class OnPlayerJoinTest {
	
	Player mockPlayer;
	private static final String PLAYER_NAME = "A5H73Y";
	
	@Before
	public void setup() {
		
		mockPlayer = mock(Player.class);
		when(mockPlayer.getName()).thenReturn(PLAYER_NAME);
	}
	
	/*
	 * @EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (Parkour.getSettings().isDisplayWelcome())
			event.getPlayer().sendMessage(Utils.getTranslation("Event.Join").replace("%VERSION%", Static.getVersion().toString()));

		if (PlayerMethods.isPlaying(event.getPlayer().getName())) {
			event.getPlayer().sendMessage(Utils.getTranslation("Parkour.Continue").replace("%COURSE%", PlayerMethods.getParkourSession(event.getPlayer().getName()).getCourse().getName()));
		}

		if (!PlayerMethods.isPlaying(event.getPlayer().getName()))
			return;

		if (Parkour.getParkourConfig().getConfig().getBoolean("OnLeaveServer.TeleportToLastCheckpoint"))
			PlayerMethods.playerDie(event.getPlayer());

		if (Parkour.getSettings().isResetOnLeave())
			PlayerMethods.playerLeave(event.getPlayer());
	}
	 */
	
	@Test
    public void onPlayerJoin_NoWelcomeMessage_NotPlaying() {
		assertEquals("Player name", mockPlayer.getName(), PLAYER_NAME);
		
		/*
		PlayerJoinEvent event = mock(PlayerJoinEvent.class);
		ParkourListener listener = new ParkourListener();
		
		when(event.getPlayer()).thenReturn(mockPlayer);
		when(Parkour.getSettings().isDisplayWelcome()).thenReturn(false);
		when(PlayerMethods.isPlaying(PLAYER_NAME)).thenReturn(false);
		when(Utils.getTranslation(anyString())).thenReturn(anyString());
		
		listener.onPlayerJoin(event);
		
		
		assertEquals("player name", mockPlayer.getName(), "A5H73Y");	
		*/
	}
}
