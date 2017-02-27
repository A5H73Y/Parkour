package me.A5H73Y.Parkour.Utilities;

import static org.junit.Assert.*;

import org.bukkit.GameMode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Utils.class)
public class UtilitiesTest {

	@Test
	public void utils_getTranslation() {
		String expected = "Invalid translation.";
		
		String actual = Utils.getTranslation(null);
		assertEquals("getTranslation emptyMessage null", expected, actual);
		
		actual = Utils.getTranslation("");
		assertEquals("getTranslation emptyMessage empty", expected, actual);
	}
	
	@Test
	public void utils_standardizeText() {
		String expected = "Hello";
		
		String invalid = Utils.standardizeText(null);
		assertEquals(null, invalid);
		
		String test1 = Utils.standardizeText("HELLO");
		assertEquals(expected, test1);
		
		String test2 = Utils.standardizeText("hello");
		assertEquals(expected, test2);
		
		String test3 = Utils.standardizeText("hElLo");
		assertEquals(expected, test3);
	}
	
	@Test
	public void utils_isString() {
		boolean isNumber = Utils.isNumber("");
		assertEquals(false, isNumber);
		
		isNumber = Utils.isNumber("test");
		assertEquals(false, isNumber);
		
		isNumber = Utils.isNumber("1test");
		assertEquals(false, isNumber);
		
		isNumber = Utils.isNumber("1");
		assertEquals(true, isNumber);
	}
	
	@Test
	public void utils_getGamemode() {
		GameMode gamemode = Utils.getGamemode(0);
		assertEquals(gamemode, GameMode.SURVIVAL);
		
		gamemode = Utils.getGamemode(1);
		assertEquals(gamemode, GameMode.CREATIVE);
		
		gamemode = Utils.getGamemode(2);
		assertEquals(gamemode, GameMode.ADVENTURE);
		
		gamemode = Utils.getGamemode(3);
		assertEquals(gamemode, GameMode.SPECTATOR);
		
		gamemode = Utils.getGamemode(4);
		assertEquals(gamemode, GameMode.SURVIVAL);
	}
	
}
