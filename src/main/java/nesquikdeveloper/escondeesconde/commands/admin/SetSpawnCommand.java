package nesquikdeveloper.escondeesconde.commands.admin;

import nesquikdeveloper.escondeesconde.Config;
import nesquikdeveloper.escondeesconde.Language;
import nesquikdeveloper.escondeesconde.Main;
import nesquikdeveloper.escondeesconde.utils.FileUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Language.PREFIX + "§cApenas jogadores podem executar este comando.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("escondeesconde.admin")) {
            player.sendMessage(Language.PREFIX + "§cVocê não tem permissão para executar este comando.");
            return true;
        }
        
        // Definir o spawn do lobby
        Config.LOBBY_SPAWN = player.getLocation();
        
        // Salvar no arquivo de configuração
        Main.getInstance().getConfig().set("spawn", FileUtils.serializeLocation(player.getLocation()));
        Main.getInstance().saveConfig();
        
        player.sendMessage(Language.PREFIX + "§aSpawn do lobby definido com sucesso!");
        
        return true;
    }
}