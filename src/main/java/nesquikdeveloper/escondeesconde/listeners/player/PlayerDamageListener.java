package nesquikdeveloper.escondeesconde.listeners.player;

import nesquikdeveloper.escondeesconde.game.AbstractEscondeEsconde;
import nesquikdeveloper.escondeesconde.game.enums.GameState;
import nesquikdeveloper.escondeesconde.game.enums.PlayerRole;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDamageListener implements Listener {
    
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        AbstractEscondeEsconde game = AbstractEscondeEsconde.getByPlayer(player);
        
        if (game == null) {
            // Jogador não está em uma partida
            return;
        }
        
        if (game.getState() != GameState.PLAYING || game.isSpectator(player)) {
            // Cancelar dano se não estiver jogando ou for espectador
            event.setCancelled(true);
            return;
        }
        
        // Se for dano de queda durante o tempo de esconder, cancelar
        if (game.isHiding() && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
            return;
        }
        
        // Se o dano for fatal, tratar morte manualmente
        if (player.getHealth() - event.getFinalDamage() <= 0) {
            event.setCancelled(true);
            handlePlayerDeath(player, event);
        }
    }
    
    @EventHandler
    public void onPlayerDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }
        
        Player victim = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        
        AbstractEscondeEsconde game = AbstractEscondeEsconde.getByPlayer(victim);
        
        if (game == null || game.getState() != GameState.PLAYING) {
            return;
        }
        
        // Obter funções dos jogadores
        PlayerRole victimRole = game.getPlayerRole(victim);
        PlayerRole damagerRole = game.getPlayerRole(damager);
        
        if (victimRole == null || damagerRole == null) {
            event.setCancelled(true);
            return;
        }
        
        // Procurador não pode atacar procurador
        if (victimRole == PlayerRole.SEEKER && damagerRole == PlayerRole.SEEKER) {
            event.setCancelled(true);
            return;
        }
        
        // Durante o tempo de esconder, cancelar danos
        if (game.isHiding()) {
            event.setCancelled(true);
            return;
        }
        
        // Se for procurador atacando escondedor, tratar como captura
        if (victimRole == PlayerRole.HIDER && damagerRole == PlayerRole.SEEKER) {
            if (victim.getHealth() - event.getFinalDamage() <= 0) {
                // Mortal
                event.setCancelled(true);
                game.onPlayerCaught(victim, damager);
            }
        }
        
        // Deixar eventos normais para escondedor vs escondedor (traição)
        // e escondedor vs procurador (defesa)
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        AbstractEscondeEsconde game = AbstractEscondeEsconde.getByPlayer(player);
        
        if (game == null || game.getState() != GameState.PLAYING) {
            return;
        }
        
        // Impedir drop de itens e mensagem de morte
        event.getDrops().clear();
        event.setDeathMessage(null);
        event.setKeepInventory(true);
        
        // Tratar morte
        game.onPlayerDeath(player, player.getKiller());
    }
    
    private void handlePlayerDeath(Player player, EntityDamageEvent event) {
        AbstractEscondeEsconde game = AbstractEscondeEsconde.getByPlayer(player);
        
        if (game == null || game.getState() != GameState.PLAYING) {
            return;
        }
        
        Player killer = null;
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) event;
            if (entityEvent.getDamager() instanceof Player) {
                killer = (Player) entityEvent.getDamager();
            }
        }
        
        // Tratar morte
        game.onPlayerDeath(player, killer);
    }
}