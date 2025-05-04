package nesquikdeveloper.escondeesconde.commands.admin;

import nesquikdeveloper.escondeesconde.Language;
import nesquikdeveloper.escondeesconde.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class NPCCommand implements CommandExecutor {

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

        if (args.length < 1) {
            player.sendMessage(Language.PREFIX + "§cUso correto: /ee npc <add/remove> [id]");
            return true;
        }

        String action = args[0].toLowerCase();

        if (action.equals("add")) {
            if (args.length < 2) {
                player.sendMessage(Language.PREFIX + "§cUso correto: /ee npc add <id>");
                return true;
            }

            String id = args[1];
            Location loc = player.getLocation();

            // Save NPC to config
            FileConfiguration config = Main.getInstance().getConfig();
            String path = "npcs." + id;

            if (config.contains(path)) {
                player.sendMessage(Language.PREFIX + "§cJá existe um NPC com este ID.");
                return true;
            }

            config.set(path + ".world", loc.getWorld().getName());
            config.set(path + ".x", loc.getX());
            config.set(path + ".y", loc.getY());
            config.set(path + ".z", loc.getZ());
            config.set(path + ".yaw", loc.getYaw());
            config.set(path + ".pitch", loc.getPitch());

            Main.getInstance().saveConfig();

            player.sendMessage(Language.PREFIX + "§aNPC adicionado com sucesso!");

        } else if (action.equals("remove")) {
            if (args.length < 2) {
                player.sendMessage(Language.PREFIX + "§cUso correto: /ee npc remove <id>");
                return true;
            }

            String id = args[1];
            FileConfiguration config = Main.getInstance().getConfig();
            String path = "npcs." + id;

            if (!config.contains(path)) {
                player.sendMessage(Language.PREFIX + "§cNão existe um NPC com este ID.");
                return true;
            }

            config.set(path, null);
            Main.getInstance().saveConfig();

            player.sendMessage(Language.PREFIX + "§aNPC removido com sucesso!");
        } else {
            player.sendMessage(Language.PREFIX + "§cUso correto: /ee npc <add/remove> [id]");
        }

        return true;
    }
}