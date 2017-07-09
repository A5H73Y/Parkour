package me.A5H73Y.Parkour.Listeners;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import me.A5H73Y.Parkour.Parkour;
import me.A5H73Y.Parkour.ParkourListener;
import me.A5H73Y.Parkour.Player.PlayerMethods;
import me.A5H73Y.Parkour.Utilities.Settings;
import me.A5H73Y.Parkour.Utilities.Static;
import me.A5H73Y.Parkour.Utilities.Utils;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PlayerJoinEvent.class, ParkourListener.class, Parkour.class, Settings.class, Utils.class, Static.class, PlayerMethods.class})
public class OnPlayerJoinTest {
	
	private Player mockPlayer;
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

		if (Parkour.getPlugin().getConfig().getBoolean("OnLeaveServer.TeleportToLastCheckpoint"))
			PlayerMethods.playerDie(event.getPlayer());

		if (Parkour.getSettings().isResetOnLeave())
			PlayerMethods.playerLeave(event.getPlayer());
	}
	 */
	
	@Test
    public void onPlayerJoin_NoWelcomeMessage_NotPlaying() {
		/*
		PlayerJoinEvent event = mock(PlayerJoinEvent.class);
		ParkourListener listener = new ParkourListener();
		
		when(event.getPlayer()).thenReturn(mockPlayer);
		when(Parkour.getSettings().isDisplayWelcome()).thenReturn(false);
		when(PlayerMethods.isPlaying(PLAYER_NAME)).thenReturn(false);
		
		listener.onPlayerJoin(event);
		*/
		
	}
}
