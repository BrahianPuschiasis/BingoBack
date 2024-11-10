package com.Sofka.BingkBack;

import com.Sofka.BingkBack.entity.Game;
import com.Sofka.BingkBack.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BingkBackApplicationTests {

	@Test
	void contextLoads() {
	}


	@Autowired
	private GameService gameService;

	@Test
	public void testSaveGame() {
		Game game = new Game();
		game.setGanador("test");
		Game savedGame = gameService.saveGame(game);
	}


}
