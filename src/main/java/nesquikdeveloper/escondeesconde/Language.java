package nesquikdeveloper.escondeesconde;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Language {
    
    private static File file;
    private static FileConfiguration config;
    
    // Mensagens gerais
    public static String PREFIX = "&6&lESCONDE-ESCONDE &7» &f";
    public static String GAME_FULL = "&cEsta partida está cheia!";
    public static String GAME_STARTED = "&cEsta partida já começou!";
    public static String PLAYER_JOIN = "{player} &eentrou na partida! &a[{players}/{max_players}]";
    public static String PLAYER_QUIT = "{player} &esaiu da partida! &c[{players}/{max_players}]";
    public static String GAME_START = "&fO jogo começou!";
    public static String GAME_END = "&fO jogo terminou!";
    public static String NOT_ENOUGH_PLAYERS = "&cNão há jogadores suficientes para iniciar a partida!";
    
    // Mensagens de jogabilidade
    public static String HIDER_DEATH = "&eO &7{victim} &efoi traído por &c{killer}";
    public static String SEEKER_DEATH = "&eO &c{seeker} &efoi vítima de &a{hider}";
    public static String HIDER_FOUND = "&eO &7{hider} &efoi encontrado por &c{seeker}";
    public static String SEEKER_INFO = "&eA partida começará em {time} segundos!";
    public static String HIDE_TIME_START = "&eOs procuradores serão liberados em {time} segundos!";
    
    // Títulos
    public static String TITLE_CAUGHT = "&c&lPARTIU!";
    public static String SUBTITLE_CAUGHT = "&7Agora você é um procurador!";
    public static String TITLE_VICTORY_HIDERS = "&a&lVITÓRIA!";
    public static String SUBTITLE_VICTORY_HIDERS = "&fVocê venceu!";
    public static String TITLE_DEFEAT = "&c&lFIM DE JOGO!";
    public static String SUBTITLE_DEFEAT = "&7Os escondedores venceram!";
    
    // Objetivos
    public static List<String> OBJECTIVE_SEEKERS = Arrays.asList(
            "",
            "&f&l PROCURADORES",
            "",
            " &7Seu objetivo é eliminar todos os escondedores",
            " &7enquanto o tempo do jogo é: 50 minutos",
            " &7Não fique sozinho é fique mais forte eliminando inimigos.",
            ""
    );
    
    public static List<String> OBJECTIVE_HIDERS = Arrays.asList(
            "",
            "&f&l ESCONDEDORES",
            "",
            " &7Seu objetivo é se esconder de todos os",
            " &7procuradores, não se preocupe com o tempo",
            " &7Pós seu tempo e de &f50 minutos&7.",
            ""
    );
    
    // Scoreboard
    public static String SCOREBOARD_LOBBY_TITLE = "&6&lESCONDE-ESCONDE";
    public static List<String> SCOREBOARD_LOBBY = Arrays.asList(
            "",
            "&b  ❙ &fAbates: &c%HypeCommons_EscondeEsconde_kills%",
            "&b  ❙ &fVitórias: &a%HypeCommons_EscondeEsconde_wins%",
            "&b  ❙ &fPartidas: &b%HypeCommons_EscondeEsconde_games%",
            "",
            "&b  ❙ &fJogadores: &b%HypeCommons_online%",
            "&b  ❙ &fCoins: &6%HypeCommons_EscondeEsconde_coins%",
            "",
            "   &ehypemc.fun"
    );
    
    public static List<String> SCOREBOARD_WAITING = Arrays.asList(
            "&6&lESCONDE-ESCONDE",
            "",
            "&b  ❙ &fMapa: &e{map}",
            "&b  ❙ &fJogadores: &a{players}/{max_players}",
            "",
            "  &f{time}",
            "",
            "   &ehypemc.fun"
    );
    
    public static List<String> SCOREBOARD_GAME = Arrays.asList(
            "&6&lESCONDE-ESCONDE",
            "",
            "&fProcurando: &b{seekers}",
            "&fEscondedores: &7{hidders}",
            "",
            "&fMapa: &e{map}",
            "",
            "   &ehypemc.fun"
    );
    
    public static void loadLanguage() {
        file = new File(Main.getInstance().getDataFolder(), "language.yml");
        
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        config = YamlConfiguration.loadConfiguration(file);
        
        PREFIX = getColoredString("messages.prefix", PREFIX);
        GAME_FULL = getColoredString("messages.game-full", GAME_FULL);
        GAME_STARTED = getColoredString("messages.game-started", GAME_STARTED);
        PLAYER_JOIN = getColoredString("messages.player-join", PLAYER_JOIN);
        PLAYER_QUIT = getColoredString("messages.player-quit", PLAYER_QUIT);
        GAME_START = getColoredString("messages.game-start", GAME_START);
        GAME_END = getColoredString("messages.game-end", GAME_END);
        NOT_ENOUGH_PLAYERS = getColoredString("messages.not-enough-players", NOT_ENOUGH_PLAYERS);
        
        HIDER_DEATH = getColoredString("gameplay.hider-death", HIDER_DEATH);
        SEEKER_DEATH = getColoredString("gameplay.seeker-death", SEEKER_DEATH);
        HIDER_FOUND = getColoredString("gameplay.hider-found", HIDER_FOUND);
        SEEKER_INFO = getColoredString("gameplay.seeker-info", SEEKER_INFO);
        HIDE_TIME_START = getColoredString("gameplay.hide-time-start", HIDE_TIME_START);
        
        TITLE_CAUGHT = getColoredString("titles.caught", TITLE_CAUGHT);
        SUBTITLE_CAUGHT = getColoredString("titles.subtitle-caught", SUBTITLE_CAUGHT);
        TITLE_VICTORY_HIDERS = getColoredString("titles.victory-hiders", TITLE_VICTORY_HIDERS);
        SUBTITLE_VICTORY_HIDERS = getColoredString("titles.subtitle-victory-hiders", SUBTITLE_VICTORY_HIDERS);
        TITLE_DEFEAT = getColoredString("titles.defeat", TITLE_DEFEAT);
        SUBTITLE_DEFEAT = getColoredString("titles.subtitle-defeat", SUBTITLE_DEFEAT);
        
        OBJECTIVE_SEEKERS = getColoredStringList("objectives.seekers", OBJECTIVE_SEEKERS);
        OBJECTIVE_HIDERS = getColoredStringList("objectives.hiders", OBJECTIVE_HIDERS);
        
        SCOREBOARD_LOBBY_TITLE = getColoredString("scoreboard.lobby-title", SCOREBOARD_LOBBY_TITLE);
        SCOREBOARD_LOBBY = getColoredStringList("scoreboard.lobby", SCOREBOARD_LOBBY);
        SCOREBOARD_WAITING = getColoredStringList("scoreboard.waiting", SCOREBOARD_WAITING);
        SCOREBOARD_GAME = getColoredStringList("scoreboard.game", SCOREBOARD_GAME);
        
        saveDefaults();
    }
    
    private static String getColoredString(String path, String defaultValue) {
        if (config.contains(path)) {
            return ChatColor.translateAlternateColorCodes('&', config.getString(path));
        } else {
            config.set(path, defaultValue.replace("§", "&"));
            return ChatColor.translateAlternateColorCodes('&', defaultValue);
        }
    }
    
    private static List<String> getColoredStringList(String path, List<String> defaultValue) {
        if (config.contains(path)) {
            List<String> list = config.getStringList(path);
            for (int i = 0; i < list.size(); i++) {
                list.set(i, ChatColor.translateAlternateColorCodes('&', list.get(i)));
            }
            return list;
        } else {
            for (int i = 0; i < defaultValue.size(); i++) {
                defaultValue.set(i, defaultValue.get(i).replace("§", "&"));
            }
            config.set(path, defaultValue);
            for (int i = 0; i < defaultValue.size(); i++) {
                defaultValue.set(i, ChatColor.translateAlternateColorCodes('&', defaultValue.get(i)));
            }
            return defaultValue;
        }
    }
    
    private static void saveDefaults() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}