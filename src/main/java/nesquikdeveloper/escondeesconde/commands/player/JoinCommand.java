package nesquikdeveloper.escondeesconde.commands.player;

import nesquikdeveloper.escondeesconde.Language;
import nesquikdeveloper.escondeesconde.game.AbstractEscondeEsconde;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class JoinCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.PREFIX + "§cApenas jogadores podem executar este comando.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            // Entrar em uma partida aleatória
            List<AbstractEscondeEsconde> availableGames = AbstractEscondeEsconde.getAvailableGames();
            
            if (availableGames.isEmpty()) {
                player.sendMessage(Language.PREFIX + "§cNão há partidas disponíveis no momento.");
                return true;
            }
            
            // Entrar na partida com mais jogadores
            AbstractEscondeEsconde bestGame = availableGames.get(0);
            for (AbstractEscondeEsconde game : availableGames) {
                if (game.getPlayers().size() > bestGame.getPlayers().size()) {
                    bestGame = game;
                }
            }
            
            bestGame.join(player);
        } else {
            // Entrar em uma partida específica
            String arenaId = args[0];
            AbstractEscondeEsconde game = AbstractEscondeEsconde.getById(arenaId);
            
            if (game == null) {
                player.sendMessage(Language.PREFIX + "§cPartida não encontrada.");
                return true;
            }
            
            game.join(player);
        }
        
        return true;
    }
}