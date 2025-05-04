package nesquikdeveloper.escondeesconde;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    
    // Configurações gerais
    public static Location LOBBY_SPAWN;
    public static int MIN_PLAYERS = 3;
    public static int MAX_PLAYERS = 12;
    
    // Configurações de tempo
    public static int LOBBY_COUNTDOWN = 30;
    public static int START_COUNTDOWN = 10;
    public static int HIDE_TIME = 30;
    public static int GAME_TIME = 60 * 50; // 50 minutos em segundos
    
    // Configurações de times
    public static int INITIAL_SEEKERS = 2;
    
    // Configurações de pontos e moedas
    public static int COINS_PER_KILL = 10;
    public static int COINS_PER_WIN = 50;
    public static int COINS_PER_GAME = 5;
    
    public static void loadConfig() {
        FileConfiguration config = Main.getInstance().getConfig();
        
        MIN_PLAYERS = config.getInt("game.min-players", MIN_PLAYERS);
        MAX_PLAYERS = config.getInt("game.max-players", MAX_PLAYERS);
        
        LOBBY_COUNTDOWN = config.getInt("time.lobby-countdown", LOBBY_COUNTDOWN);
        START_COUNTDOWN = config.getInt("time.start-countdown", START_COUNTDOWN);
        HIDE_TIME = config.getInt("time.hide-time", HIDE_TIME);
        GAME_TIME = config.getInt("time.game-time", GAME_TIME);
        
        INITIAL_SEEKERS = config.getInt("teams.initial-seekers", INITIAL_SEEKERS);
        
        COINS_PER_KILL = config.getInt("rewards.coins-per-kill", COINS_PER_KILL);
        COINS_PER_WIN = config.getInt("rewards.coins-per-win", COINS_PER_WIN);
        COINS_PER_GAME = config.getInt("rewards.coins-per-game", COINS_PER_GAME);
    }
}