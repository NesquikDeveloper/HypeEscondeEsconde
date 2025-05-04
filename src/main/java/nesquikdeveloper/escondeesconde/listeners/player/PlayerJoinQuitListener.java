package nesquikdeveloper.escondeesconde.listeners.player;

import nesquikdeveloper.escondeesconde.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Teleportar para o spawn do lobby
        if (Config.LOBBY_SPAWN != null) {
            player.teleport(Config.LOBBY_SPAWN);
        }
        
        // Resetar jogador
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExp(0);
        player.setLevel(0);
        
        // Mensagem de boas-vindas
        // TODO: Implementar mensagem de boas-vindas
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Remover jogador de qualquer partida
        nesquikdeveloper.escondeesconde.game.AbstractEscondeEsconde game = 
                nesquikdeveloper.escondeesconde.game.AbstractEscondeEsconde.getByPlayer(player);
        
        if (game != null) {
            game.leave(player);
        }
    }
}