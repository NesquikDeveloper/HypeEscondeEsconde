package nesquikdeveloper.escondeesconde.commands.player;

import nesquikdeveloper.escondeesconde.Language;
import nesquikdeveloper.escondeesconde.game.AbstractEscondeEsconde;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LeaveCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.PREFIX + "§cApenas jogadores podem executar este comando.");
            return true;
        }
        
        Player player = (Player) sender;
        AbstractEscondeEsconde game = AbstractEscondeEsconde.getByPlayer(player);
        
        if (game == null) {
            player.sendMessage(Language.PREFIX + "§cVocê não está em uma partida.");
            return true;
        }
        
        game.leave(player);
        player.sendMessage(Language.PREFIX + "§aVocê saiu da partida.");
        
        return true;
    }
}