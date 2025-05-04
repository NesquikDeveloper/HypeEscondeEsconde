package nesquikdeveloper.escondeesconde.listeners.player;

import nesquikdeveloper.escondeesconde.Config;
import nesquikdeveloper.escondeesconde.Language;
import nesquikdeveloper.escondeesconde.commands.admin.CreateArenaCommand;
import nesquikdeveloper.escondeesconde.game.AbstractEscondeEsconde;
import nesquikdeveloper.escondeesconde.game.enums.GameState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        // Verificar se está em modo de configuração
        CreateArenaCommand.SetupSession session = CreateArenaCommand.getSetupSession(player);
        if (session != null) {
            handleSetupInteraction(event, session);
            return;
        }
        
        // Verificar se está em uma partida
        AbstractEscondeEsconde game = AbstractEscondeEsconde.getByPlayer(player);
        if (game != null) {
            handleGameInteraction(event, game);
            return;
        }
        
        // Jogador está no lobby - verifique interações com menus, etc.
    }
    
    private void handleSetupInteraction(PlayerInteractEvent event, CreateArenaCommand.SetupSession session) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null) {
            return;
        }
        
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Cancelar interações durante setup
            event.setCancelled(true);
            
            if (item.getType() == Material.BEACON && item.hasItemMeta() && 
                    item.getItemMeta().getDisplayName().equals("§aSetar Lobby de Espera")) {
                session.setWaitingLobby(player.getLocation());
            } else if (item.getType() == Material.EMERALD_BLOCK && item.hasItemMeta() && 
                    item.getItemMeta().getDisplayName().equals("§aSetar Escondedores")) {
                session.setHidersSpawn(player.getLocation());
            } else if (item.getType() == Material.REDSTONE_BLOCK && item.hasItemMeta() && 
                    item.getItemMeta().getDisplayName().equals("§aSetar Procuradores")) {
                session.setSeekersSpawn(player.getLocation());
            } else if (item.getType() == Material.WOOL && item.getItemMeta().getDisplayName().equals("§aConfirmar Sala")) {
                session.confirm();
            } else if (item.getType() == Material.WOOL && item.getItemMeta().getDisplayName().equals("§cCancelar")) {
                session.cancel();
            }
        }
    }
    
    private void handleGameInteraction(PlayerInteractEvent event, AbstractEscondeEsconde game) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        // Cancelar interações durante o jogo
        if (game.getState() != GameState.PLAYING) {
            event.setCancelled(true);
            
            // Item para sair da partida
            if (item != null && item.getType() == Material.BED) {
                game.leave(player);
                player.teleport(Config.LOBBY_SPAWN);
                player.sendMessage(Language.PREFIX + "§aVocê saiu da partida.");
            }
            
            return;
        }
        
        // Jogador está em jogo - permitir uso de items do jogo
        if (game.isSpectator(player)) {
            event.setCancelled(true);
        }
    }
}