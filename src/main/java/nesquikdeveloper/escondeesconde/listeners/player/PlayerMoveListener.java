package nesquikdeveloper.escondeesconde.listeners.player;

import nesquikdeveloper.escondeesconde.Config;
import nesquikdeveloper.escondeesconde.game.AbstractEscondeEsconde;
import nesquikdeveloper.escondeesconde.game.enums.GameState;
import nesquikdeveloper.escondeesconde.game.enums.PlayerRole;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Ignorar pequenos movimentos de cabeça
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && 
            event.getFrom().getBlockY() == event.getTo().getBlockY() && 
            event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        
        Player player = event.getPlayer();
        AbstractEscondeEsconde game = AbstractEscondeEsconde.getByPlayer(player);
        
        if (game == null) {
            // Jogador não está em uma partida
            return;
        }
        
        // Verificar queda no void
        if (event.getTo().getY() < 0) {
            if (game.getState() == GameState.WAITING) {
                // Teleportar de volta para o lobby
                if (game.getWaitingLobby() != null) {
                    player.teleport(game.getWaitingLobby());
                }
            } else if (game.getState() == GameState.PLAYING) {
                if (game.isSpectator(player)) {
                    // Teleportar espectador de volta
                    player.teleport(game.getHidersSpawn());
                } else {
                    // Tratar como morte e converter para procurador
                    game.onPlayerDeath(player, null);
                }
            }
        }
        
        // Durante o tempo de esconder, impedir que procuradores se movam
        if (game.getState() == GameState.PLAYING && game.isHiding() && 
                game.getPlayerRole(player) == PlayerRole.SEEKER) {
            event.setCancelled(true);
            return;
        }
        
        // Impedir que escondedores entrem na área dos procuradores
        if (game.getState() == GameState.PLAYING && 
                game.getPlayerRole(player) == PlayerRole.HIDER &&
                isNearLocation(event.getTo(), game.getSeekersSpawn(), 10)) {
            event.setCancelled(true);
            player.sendMessage("§cVocê não pode entrar na área dos procuradores!");
            return;
        }
        
        // Impedir que jogadores saiam do mundo durante o jogo
        if (game.getState() == GameState.PLAYING && !player.getGameMode().equals(GameMode.SPECTATOR) &&
                !isInsideWorldBorder(event.getTo())) {
            event.setCancelled(true);
            player.sendMessage("§cVocê não pode sair do mapa!");
        }
    }
    
    private boolean isNearLocation(Location loc1, Location loc2, int radius) {
        if (loc1 == null || loc2 == null || !loc1.getWorld().equals(loc2.getWorld())) {
            return false;
        }
        
        double distance = loc1.distance(loc2);
        return distance <= radius;
    }
    
    private boolean isInsideWorldBorder(Location location) {
        // Implementação simples - verificar se está dentro de uma área fixa
        // Em uma implementação real, você pode querer verificar a borda do mundo
        int maxDistance = 1000; // 1000 blocos do spawn
        Location spawn = location.getWorld().getSpawnLocation();
        
        return isNearLocation(location, spawn, maxDistance);
    }
}