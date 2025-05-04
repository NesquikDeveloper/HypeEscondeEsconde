package nesquikdeveloper.escondeesconde.commands.admin;

import nesquikdeveloper.escondeesconde.Language;
import nesquikdeveloper.escondeesconde.Main;
import nesquikdeveloper.escondeesconde.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CreateArenaCommand implements CommandExecutor {

    private static final Map<String, SetupSession> setupSessions = new HashMap<>();

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
            player.sendMessage(Language.PREFIX + "§cUso correto: /ee criar <nome>");
            return true;
        }

        String arenaName = args[0];

        // Verificar se já existe uma arena com esse nome
        File arenaFile = new File(Main.getInstance().getDataFolder(), "arenas/" + arenaName + ".yml");
        if (arenaFile.exists()) {
            player.sendMessage(Language.PREFIX + "§cJá existe uma arena com esse nome.");
            return true;
        }

        // Verificar se o jogador já está configurando uma arena
        if (setupSessions.containsKey(player.getName())) {
            player.sendMessage(Language.PREFIX + "§cVocê já está configurando uma arena. Use /ee cancelar para cancelar.");
            return true;
        }

        // Criar sessão de configuração
        SetupSession session = new SetupSession(player, arenaName);
        setupSessions.put(player.getName(), session);

        // Iniciar processo de configuração
        player.sendMessage(Language.PREFIX + "§aVocê começou a configurar a arena §e" + arenaName + "§a.");
        player.sendMessage("§aUtilize os itens do seu inventário para configurar a arena.");

        // Dar itens de configuração
        giveSetupItems(player);

        return true;
    }

    private void giveSetupItems(Player player) {
        player.getInventory().clear();

        // Item 1: Definir lobby de espera
        ItemStack waitingLobby = new ItemStack(Material.BEACON);
        ItemMeta waitingMeta = waitingLobby.getItemMeta();
        waitingMeta.setDisplayName("§aSetar Lobby de Espera");
        waitingMeta.setLore(Arrays.asList("§7Clique com este item para", "§7definir o lobby de espera"));
        waitingLobby.setItemMeta(waitingMeta);

        // Item 2: Definir spawn dos escondedores
        ItemStack hidersSpawn = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta hidersMeta = hidersSpawn.getItemMeta();
        hidersMeta.setDisplayName("§aSetar Escondedores");
        hidersMeta.setLore(Arrays.asList("§7Clique com este item para", "§7definir o spawn dos escondedores"));
        hidersSpawn.setItemMeta(hidersMeta);

        // Item 3: Definir spawn dos procuradores
        ItemStack seekersSpawn = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta seekersMeta = seekersSpawn.getItemMeta();
        seekersMeta.setDisplayName("§aSetar Procuradores");
        seekersMeta.setLore(Arrays.asList("§7Clique com este item para", "§7definir o spawn dos procuradores"));
        seekersSpawn.setItemMeta(seekersMeta);

        // Item 4: Confirmar configuração
        ItemStack confirm = new ItemStack(Material.WOOL, 1, (short) 5); // Verde
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName("§aConfirmar Sala");
        confirmMeta.setLore(Arrays.asList("§7Clique com este item para", "§7finalizar a configuração"));
        confirm.setItemMeta(confirmMeta);

        // Item 5: Cancelar configuração
        ItemStack cancel = new ItemStack(Material.NETHER_STAR); // Vermelho
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName("§cCancelar");
        cancelMeta.setLore(Arrays.asList("§7Clique com este item para", "§7cancelar a configuração"));
        cancel.setItemMeta(cancelMeta);

        // Colocar itens no inventário
        player.getInventory().setItem(0, waitingLobby);
        player.getInventory().setItem(1, hidersSpawn);
        player.getInventory().setItem(2, seekersSpawn);
        player.getInventory().setItem(7, confirm);
        player.getInventory().setItem(8, cancel);

        player.updateInventory();
    }

    public static class SetupSession {
        private final Player player;
        private final String arenaName;
        private final World world;
        private final String worldName;

        private org.bukkit.Location waitingLobby;
        private org.bukkit.Location hidersSpawn;
        private org.bukkit.Location seekersSpawn;

        public SetupSession(Player player, String arenaName) {
            this.player = player;
            this.arenaName = arenaName;
            this.worldName = "ee_" + arenaName;

            // Criar novo mundo
            World existingWorld = Bukkit.getWorld(worldName);
            if (existingWorld != null) {
                this.world = existingWorld;
            } else {
                this.world = Bukkit.createWorld(new WorldCreator(worldName));
                this.world.setGameRuleValue("doDaylightCycle", "false");
                this.world.setGameRuleValue("doMobSpawning", "false");
                this.world.setTime(6000); // Meio-dia
            }

            // Teleportar jogador para o mundo
            player.teleport(world.getSpawnLocation());
        }

        public void setWaitingLobby(org.bukkit.Location location) {
            this.waitingLobby = location;
            player.sendMessage(Language.PREFIX + "§aLobby de espera definido!");
        }

        public void setHidersSpawn(org.bukkit.Location location) {
            this.hidersSpawn = location;
            player.sendMessage(Language.PREFIX + "§aSpawn dos escondedores definido!");
        }

        public void setSeekersSpawn(org.bukkit.Location location) {
            this.seekersSpawn = location;
            player.sendMessage(Language.PREFIX + "§aSpawn dos procuradores definido!");
        }

        public boolean canConfirm() {
            return waitingLobby != null && hidersSpawn != null && seekersSpawn != null;
        }

        public void confirm() {
            if (!canConfirm()) {
                player.sendMessage(Language.PREFIX + "§cVocê precisa definir todos os pontos antes de confirmar!");
                return;
            }

            // Criar arquivo de configuração
            File arenaFile = new File(Main.getInstance().getDataFolder(), "arenas/" + arenaName + ".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(arenaFile);

            // Salvar configurações
            config.set("name", arenaName);
            config.set("world", worldName);
            config.set("waiting-lobby", FileUtils.serializeLocation(waitingLobby));
            config.set("hiders-spawn", FileUtils.serializeLocation(hidersSpawn));
            config.set("seekers-spawn", FileUtils.serializeLocation(seekersSpawn));

            try {
                config.save(arenaFile);
            } catch (IOException e) {
                e.printStackTrace();
                player.sendMessage(Language.PREFIX + "§cOcorreu um erro ao salvar a arena.");
                return;
            }

            // Criar backup do mundo
            File worldFolder = world.getWorldFolder();
            File backupFolder = new File(Main.getInstance().getDataFolder(), "mundos/" + worldName);

            if (!backupFolder.exists()) {
                backupFolder.mkdirs();
            }

            // Copiar arquivos do mundo para o backup
            FileUtils.copyFolder(worldFolder, backupFolder);

            player.sendMessage(Language.PREFIX + "§aArena §e" + arenaName + " §acriada com sucesso!");

            // Remover da sessão de configuração e teleportar para o spawn
            cancel();
        }

        public void cancel() {
            setupSessions.remove(player.getName());
            player.getInventory().clear();
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            player.sendMessage(Language.PREFIX + "§cConfiguração cancelada.");
        }
    }

    public static SetupSession getSetupSession(Player player) {
        return setupSessions.get(player.getName());
    }
}