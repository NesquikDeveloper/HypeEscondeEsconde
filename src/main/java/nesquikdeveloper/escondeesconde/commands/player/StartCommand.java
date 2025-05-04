package nesquikdeveloper.escondeesconde.commands.player;

import nesquikdeveloper.escondeesconde.Config;
import nesquikdeveloper.escondeesconde.Language;
import nesquikdeveloper.escondeesconde.game.AbstractEscondeEsconde;
import nesquikdeveloper.escondeesconde.game.enums.GameState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.PREFIX + "§cApenas jogadores podem executar este comando.");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Verificar permissão para iniciar
        if (!player.hasPermission("escondeesconde.start")) {
            player.sendMessage(Language.PREFIX + "§cVocê não tem permissão para iniciar a partida.");
            return true;
        }
        
        // Obter a partida do jogador
        AbstractEscondeEsconde game = AbstractEscondeEsconde.getByPlayer(player);
        
        if (game == null) {
            player.sendMessage(Language.PREFIX + "§cVocê não está em uma partida.");
            return true;
        }
        
        // Verificar se a partida pode ser iniciada
        if (game.getState() != GameState.WAITING) {
            player.sendMessage(Language.PREFIX + "§cA partida já foi iniciada.");
            return true;
        }
        
        // Verificar número mínimo de jogadores
        if (game.getPlayers().size() < Config.MIN_PLAYERS) {
            player.sendMessage(Language.PREFIX + "§cSão necessários pelo menos " + Config.MIN_PLAYERS + " jogadores para iniciar.");
            return true;
        }
        
        // Iniciar partida
        game.start();
        player.sendMessage(Language.PREFIX + "§aVocê iniciou a partida!");
        
        return true;
    }
}