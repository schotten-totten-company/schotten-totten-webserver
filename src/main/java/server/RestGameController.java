package server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boradgames.bastien.schotten_totten.core.exceptions.GameCreationException;
import com.boradgames.bastien.schotten_totten.core.model.Game;
import com.boradgames.bastien.schotten_totten.core.model.Player;

@RestController
public class RestGameController {

	private static Map<String, Game> gameMap = new HashMap<>();

	@RequestMapping("/ping")
	public String ping() {
		return new Date().toString() + " - it is time to SCHOTTEN !!!!";
	}

	@RequestMapping("/createGame")
	public Boolean createGame(@RequestParam(value="gamename") String gamename) {
		LogManager.getLogger(getClass()).info("createGame");
		if (gameMap.containsKey(gamename)) {
			return false;
		} else {
			try {
				final Game game = new Game("Player 1", "Player 2");
				gameMap.put(gamename, game);
				return true;
			} catch (final GameCreationException e) {
				return false;
			}
		}
	}

	@RequestMapping("/updateGame")
	public Boolean updateGame(@RequestParam(value="gamename") String gamename, @RequestBody Game game) {
		LogManager.getLogger(getClass()).info("updateGame");
		if (!gameMap.containsKey(gamename)) {
			return false;
		} else {
			gameMap.put(gamename, game);
			return true;
		}
	}
	
	@RequestMapping("/getGame")
	public Game getGame(@RequestParam(value="gamename") String gamename) {
		return gameMap.get(gamename);
	}

	@RequestMapping("/listGames")
	public List<String> listGames() {
		return new ArrayList<String>(gameMap.keySet());
	}

	@RequestMapping("/deleteGame")
	public Boolean deleteGame(@RequestParam(value="gamename") String gamename) {
		return gameMap.remove(gamename) != null;
	}

	@RequestMapping("/getPlayingPlayer")
	public Player getPlayingPlayer(@RequestParam(value="gamename") String gamename) {
		return gameMap.get(gamename).getPlayingPlayer();
	}

}
