package nesquikdeveloper.escondeesconde;

import nesquikdeveloper.escondeesconde.commands.Commands;
import nesquikdeveloper.escondeesconde.game.AbstractEscondeEsconde;
import nesquikdeveloper.escondeesconde.hook.EECoreHook;
import nesquikdeveloper.escondeesconde.listeners.Listeners;
import nesquikdeveloper.escondeesconde.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {
    
    private static Main instance;
    private static boolean validInit;
    
    public static Main getInstance() {
        return instance;
    }
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Verificando versão do servidor
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        if (!version.equals("v1_8_R3")) {
            getLogger().severe("Este plugin é compatível apenas com a versão 1.8.8 do Minecraft!");
            getLogger().severe("Desativando plugin...");
            setEnabled(false);
            return;
        }
        
        // Criando diretórios
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        File arenasFolder = new File(getDataFolder(), "arenas");
        if (!arenasFolder.exists()) {
            arenasFolder.mkdirs();
        }
        
        File mapsFolder = new File(getDataFolder(), "mundos");
        if (!mapsFolder.exists()) {
            mapsFolder.mkdirs();
        }
        
        // Salvando arquivos de configuração
        saveDefaultConfig();
        
        // Configurando spawn do lobby
        if (getConfig().getString("spawn") != null) {
            Location lobbySpawn = FileUtils.deserializeLocation(getConfig().getString("spawn"));
            if (lobbySpawn != null && lobbySpawn.getWorld() != null) {
                Config.LOBBY_SPAWN = lobbySpawn;
            }
        }
        
        // Carregando configurações
        Config.loadConfig();
        Language.loadLanguage();
        
        // Carregando funcionalidades do plugin
        AbstractEscondeEsconde.setupGames();
        EECoreHook.setupHook();
        Listeners.setupListeners();
        Commands.setupCommands();
        
        validInit = true;
        getLogger().info("EscondeEsconde foi ativado com sucesso!");
    }
    
    @Override
    public void onDisable() {
        if (validInit) {
            // Finalizando jogos em andamento
            AbstractEscondeEsconde.stopAllGames();
            getLogger().info("Todos os jogos foram finalizados.");
        }
        
        getLogger().info("EscondeEsconde foi desativado com sucesso!");
    }
    
    public FileUtils getFileUtils() {
        return new FileUtils();
    }
}