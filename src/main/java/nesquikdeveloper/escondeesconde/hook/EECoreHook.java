package nesquikdeveloper.escondeesconde.hook;

import nesquikdeveloper.escondeesconde.Config;
import nesquikdeveloper.escondeesconde.Language;
import nesquikdeveloper.escondeesconde.Main;
import nesquikdeveloper.escondeesconde.game.AbstractEscondeEsconde;
import nesquikdeveloper.escondeesconde.game.enums.GameState;
import nesquikdeveloper.escondeesconde.game.enums.PlayerRole;
import nesquikdeveloper.escondeesconde.utils.TimeFormatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class EECoreHook {
    
    public static void setupHook() {
        // Iniciar tarefa para atualizar scoreboards a cada segundo
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), EECoreHook::updateScoreboards, 20L, 20L);
    }
    
    private static void updateScoreboards() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateScoreboard(player);
        }
    }
    
    public static void updateScoreboard(Player player) {
        AbstractEscondeEsconde game = AbstractEscondeEsconde.getByPlayer(player);
        
        if (game == null) {
            // Jogador no lobby
            showLobbyScoreboard(player);
        } else {
            // Jogador em uma partida
            if (game.getState() == GameState.WAITING) {
                showWaitingScoreboard(player, game);
            } else if (game.getState() == GameState.PLAYING) {
                showGameScoreboard(player, game);
            }
        }
    }
    
    private static void showLobbyScoreboard(Player player) {
        Scoreboard scoreboard = getScoreboard(player);
        Objective objective = getObjective(scoreboard, "lobby");
        
        // Definir título
        objective.setDisplayName(Language.SCOREBOARD_LOBBY_TITLE);
        
        // Limpar scoreboard
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }
        
        // Adicionar linhas
        List<String> lines = new ArrayList<>(Language.SCOREBOARD_LOBBY);
        
        // TODO: Substituir placeholders como %HypeCommons_EscondeEsconde_kills%
        // Por enquanto, usando valores placeholder
        
        int lineCount = lines.size();
        for (int i = 0; i < lineCount; i++) {
            String line = lines.get(i);
            // Placeholder simples para demonstração
            line = line.replace("%HypeCommons_EscondeEsconde_kills%", "0")
                       .replace("%HypeCommons_EscondeEsconde_wins%", "0")
                       .replace("%HypeCommons_EscondeEsconde_games%", "0")
                       .replace("%HypeCommons_online%", String.valueOf(Bukkit.getOnlinePlayers().size()))
                       .replace("%HypeCommons_EscondeEsconde_coins%", "0");
            
            objective.getScore(line).setScore(lineCount - i);
        }
    }
    
    private static void showWaitingScoreboard(Player player, AbstractEscondeEsconde game) {
        Scoreboard scoreboard = getScoreboard(player);
        Objective objective = getObjective(scoreboard, "waiting");
        
        // Definir título
        objective.setDisplayName(Language.SCOREBOARD_LOBBY_TITLE);
        
        // Limpar scoreboard
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }
        
        // Adicionar linhas
        List<String> lines = new ArrayList<>(Language.SCOREBOARD_WAITING);
        
        // Substituir placeholders
        String timeDisplay = game.getTimer() > Config.LOBBY_COUNTDOWN ? 
                "Esperando jogadores..." : 
                "Iniciando em " + game.getTimer() + "s";
                
        int lineCount = lines.size();
        for (int i = 0; i < lineCount; i++) {
            String line = lines.get(i)
                    .replace("{map}", game.getMapName())
                    .replace("{players}", String.valueOf(game.getPlayers().size()))
                    .replace("{max_players}", String.valueOf(Config.MAX_PLAYERS))
                    .replace("{time}", timeDisplay);
            
            objective.getScore(line).setScore(lineCount - i);
        }
    }
    
    private static void showGameScoreboard(Player player, AbstractEscondeEsconde game) {
        Scoreboard scoreboard = getScoreboard(player);
        Objective objective = getObjective(scoreboard, "game");
        
        // Definir título
        objective.setDisplayName(Language.SCOREBOARD_LOBBY_TITLE);
        
        // Limpar scoreboard
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }
        
        // Contar jogadores por função
        int hidersCount = 0;
        int seekersCount = 0;
        
        for (PlayerRole role : game.getRoles().values()) {
            if (role == PlayerRole.HIDER) {
                hidersCount++;
            } else if (role == PlayerRole.SEEKER) {
                seekersCount++;
            }
        }
        
        // Adicionar linhas
        List<String> lines = new ArrayList<>(Language.SCOREBOARD_GAME);
        
        // Substituir placeholders
        String timeDisplay = game.isHiding() ? 
                "Escondendo: " + TimeFormatter.formatTime(game.getTimer()) : 
                "Tempo: " + TimeFormatter.formatTime(game.getGameTime() - game.getTimer());
                
        int lineCount = lines.size();
        for (int i = 0; i < lineCount; i++) {
            String line = lines.get(i)
                    .replace("{map}", game.getMapName())
                    .replace("{time}", timeDisplay)
                    .replace("{seekers}", String.valueOf(seekersCount))
                    .replace("{hidders}", String.valueOf(hidersCount));
            
            objective.getScore(line).setScore(lineCount - i);
        }
        
        // Atualizar times para TAB
        updateTeams(player, game, scoreboard);
    }
    
    private static Scoreboard getScoreboard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        
        if (scoreboard == null || scoreboard.equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(scoreboard);
        }
        
        return scoreboard;
    }
    
    private static Objective getObjective(Scoreboard scoreboard, String name) {
        Objective objective = scoreboard.getObjective(name);
        
        if (objective == null) {
            objective = scoreboard.registerNewObjective(name, "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        
        return objective;
    }
    
    private static void updateTeams(Player player, AbstractEscondeEsconde game, Scoreboard scoreboard) {
        // Configurar times para lista de jogadores
        Team hidersTeam = scoreboard.getTeam("hiders");
        if (hidersTeam == null) {
            hidersTeam = scoreboard.registerNewTeam("hiders");
            hidersTeam.setPrefix("§6"); // Laranja para escondedores
        }
        
        Team seekersTeam = scoreboard.getTeam("seekers");
        if (seekersTeam == null) {
            seekersTeam = scoreboard.registerNewTeam("seekers");
            seekersTeam.setPrefix("§9"); // Azul para procuradores
        }
        
        Team spectatorsTeam = scoreboard.getTeam("spectators");
        if (spectatorsTeam == null) {
            spectatorsTeam = scoreboard.registerNewTeam("spectators");
            spectatorsTeam.setPrefix("§7"); // Cinza para espectadores
        }
        
        // Limpar times
        for (Team team : scoreboard.getTeams()) {
            for (String entry : new ArrayList<>(team.getEntries())) {
                team.removeEntry(entry);
            }
        }
        
        // Adicionar jogadores aos times
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (game.hasPlayer(p)) {
                if (game.isSpectator(p)) {
                    spectatorsTeam.addEntry(p.getName());
                } else {
                    PlayerRole role = game.getPlayerRole(p);
                    if (role == PlayerRole.HIDER) {
                        hidersTeam.addEntry(p.getName());
                    } else if (role == PlayerRole.SEEKER) {
                        seekersTeam.addEntry(p.getName());
                    }
                }
            }
        }
    }
}