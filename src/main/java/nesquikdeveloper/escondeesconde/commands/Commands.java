package nesquikdeveloper.escondeesconde.commands;

import nesquikdeveloper.escondeesconde.Main;
import nesquikdeveloper.escondeesconde.commands.admin.CreateArenaCommand;
import nesquikdeveloper.escondeesconde.commands.admin.NPCCommand;
import nesquikdeveloper.escondeesconde.commands.admin.SetSpawnCommand;
import nesquikdeveloper.escondeesconde.commands.player.JoinCommand;
import nesquikdeveloper.escondeesconde.commands.player.LeaveCommand;
import nesquikdeveloper.escondeesconde.commands.player.StartCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class Commands {

    public static void setupCommands() {
        JavaPlugin plugin = Main.getInstance();

        // Comandos de administração
        plugin.getCommand("escondeesconde").setExecutor(new EscondeEscondeCommand());
        plugin.getCommand("ee").setExecutor(new EscondeEscondeCommand());

        // Comandos para jogadores
        plugin.getCommand("entrar").setExecutor(new JoinCommand());
        plugin.getCommand("sair").setExecutor(new LeaveCommand());
        plugin.getCommand("iniciar").setExecutor(new StartCommand());
    }

    /**
     * Comando principal do plugin
     */
    public static class EscondeEscondeCommand implements CommandExecutor {

        private final SetSpawnCommand setSpawnCommand = new SetSpawnCommand();
        private final CreateArenaCommand createArenaCommand = new CreateArenaCommand();
        private final NPCCommand npcCommand = new NPCCommand();

        @Override
        public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
            if (args.length == 0) {
                sender.sendMessage("§6EscondeEsconde §7v" + Main.getInstance().getDescription().getVersion());
                sender.sendMessage("§7Plugin desenvolvido por §6NesquikIDE");
                return true;
            }

            String subCommand = args[0].toLowerCase();
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);

            switch (subCommand) {
                case "setspawn":
                    return setSpawnCommand.onCommand(sender, command, label, subArgs);
                case "criar":
                    return createArenaCommand.onCommand(sender, command, label, subArgs);
                case "npc":
                    return npcCommand.onCommand(sender, command, label, subArgs);
                default:
                    sender.sendMessage("§cComando não encontrado. Use §f/ee §cpara ver os comandos disponíveis.");
                    return true;
            }
        }
    }
}