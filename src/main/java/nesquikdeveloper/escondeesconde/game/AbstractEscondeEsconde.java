package nesquikdeveloper.escondeesconde.game;

import nesquikdeveloper.commons.nms.NMS;
import nesquikdeveloper.escondeesconde.Config;
import nesquikdeveloper.escondeesconde.Language;
import nesquikdeveloper.escondeesconde.Main;
import nesquikdeveloper.escondeesconde.game.enums.GameState;
import nesquikdeveloper.escondeesconde.game.enums.PlayerRole;
import nesquikdeveloper.escondeesconde.utils.FileUtils;
import nesquikdeveloper.escondeesconde.utils.TimeFormatter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class AbstractEscondeEsconde {
    
    private static final List<AbstractEscondeEsconde> GAMES = new ArrayList<>();
    
    private final String id;
    private final String mapName;
    private World world;
    private Location waitingLobby;
    private Location hidersSpawn;
    private Location seekersSpawn;
    private Location spectatorSpawn;
    
    private GameState state = GameState.WAITING;
    private int timer;
    private int gameTime;
    private boolean hiding = false;
    
    private final List<UUID> players = new ArrayList<>();
    private final List<UUID> spectators = new ArrayList<>();
    private final Map<UUID, PlayerRole> roles = new HashMap<>();
    private final Map<UUID, Integer> kills = new HashMap<>();
    
    private BukkitRunnable gameTask;
    
    public AbstractEscondeEsconde(String id, String mapName) {
        this.id = id;
        this.mapName = mapName;
        this.timer = Config.LOBBY_COUNTDOWN;
        this.gameTime = Config.GAME_TIME;
        loadArena();
    }
    
    public static void setupGames() {
        File arenasFolder = new File(Main.getInstance().getDataFolder(), "arenas");
        if (!arenasFolder.exists()) {
            arenasFolder.mkdirs();
            return;
        }
        
        for (File file : arenasFolder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".yml")) {
                String id = file.getName().replace(".yml", "");
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                String mapName = config.getString("name", id);
                
                AbstractEscondeEsconde game = new AbstractEscondeEsconde(id, mapName);
                GAMES.add(game);
            }
        }
        
        Main.getInstance().getLogger().info("Carregadas " + GAMES.size() + " arenas de Esconde-Esconde");
    }
    
    public static void stopAllGames() {
        for (AbstractEscondeEsconde game : new ArrayList<>(GAMES)) {
            game.stop(null);
        }
        GAMES.clear();
    }
    
    public static AbstractEscondeEsconde getById(String id) {
        for (AbstractEscondeEsconde game : GAMES) {
            if (game.getId().equals(id)) {
                return game;
            }
        }
        return null;
    }
    
    public static AbstractEscondeEsconde getByPlayer(Player player) {
        for (AbstractEscondeEsconde game : GAMES) {
            if (game.hasPlayer(player)) {
                return game;
            }
        }
        return null;
    }
    
    public static List<AbstractEscondeEsconde> getAvailableGames() {
        return GAMES.stream()
                .filter(game -> game.getState() == GameState.WAITING && game.getPlayers().size() < Config.MAX_PLAYERS)
                .collect(Collectors.toList());
    }
    
    private void loadArena() {
        File arenaFile = new File(Main.getInstance().getDataFolder(), "arenas/" + id + ".yml");
        if (!arenaFile.exists()) {
            Main.getInstance().getLogger().warning("Arquivo de configuração não encontrado para a arena " + id);
            return;
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(arenaFile);
        
        // Carregando mundo
        String worldName = config.getString("world", id);
        this.world = Bukkit.getWorld(worldName);
        if (this.world == null) {
            File worldFolder = new File(Main.getInstance().getDataFolder(), "mundos/" + worldName);
            if (worldFolder.exists() && worldFolder.isDirectory()) {
                // Copiar mundo e carregar
                FileUtils.copyFolder(worldFolder, new File(Bukkit.getWorldContainer(), worldName));
                this.world = Bukkit.createWorld(new org.bukkit.WorldCreator(worldName));
                if (this.world != null) {
                    this.world.setAutoSave(false);
                    this.world.setTime(0);
                    this.world.setGameRuleValue("doDaylightCycle", "false");
                    this.world.setGameRuleValue("doMobSpawning", "false");
                }
            }
        }
        
        // Carregando localizações
        if (config.contains("waiting-lobby")) {
            this.waitingLobby = FileUtils.deserializeLocation(config.getString("waiting-lobby"));
        }
        
        if (config.contains("hiders-spawn")) {
            this.hidersSpawn = FileUtils.deserializeLocation(config.getString("hiders-spawn"));
        }
        
        if (config.contains("seekers-spawn")) {
            this.seekersSpawn = FileUtils.deserializeLocation(config.getString("seekers-spawn"));
        }
        
        if (config.contains("spectator-spawn")) {
            this.spectatorSpawn = FileUtils.deserializeLocation(config.getString("spectator-spawn"));
        } else if (hidersSpawn != null) {
            // Se não houver spawn para espectadores, usar o dos hiders
            this.spectatorSpawn = hidersSpawn.clone();
        }
    }
    
    public void saveArena() {
        File arenaFile = new File(Main.getInstance().getDataFolder(), "arenas/" + id + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(arenaFile);
        
        config.set("name", mapName);
        config.set("world", world.getName());
        
        if (waitingLobby != null) {
            config.set("waiting-lobby", FileUtils.serializeLocation(waitingLobby));
        }
        
        if (hidersSpawn != null) {
            config.set("hiders-spawn", FileUtils.serializeLocation(hidersSpawn));
        }
        
        if (seekersSpawn != null) {
            config.set("seekers-spawn", FileUtils.serializeLocation(seekersSpawn));
        }
        
        if (spectatorSpawn != null) {
            config.set("spectator-spawn", FileUtils.serializeLocation(spectatorSpawn));
        }
        
        try {
            config.save(arenaFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void join(Player player) {
        if (state != GameState.WAITING) {
            player.sendMessage(Language.PREFIX + Language.GAME_STARTED);
            return;
        }
        
        if (players.size() >= Config.MAX_PLAYERS) {
            player.sendMessage(Language.PREFIX + Language.GAME_FULL);
            return;
        }
        
        players.add(player.getUniqueId());
        
        // Teleportar para o lobby de espera
        if (waitingLobby != null) {
            player.teleport(waitingLobby);
        } else {
            player.teleport(world.getSpawnLocation());
        }
        
        // Limpar inventário e definir modo de jogo
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20);
        player.setFoodLevel(20);
        
        // Fornecer itens do lobby
        ItemStack leaveItem = new ItemStack(Material.BED);
        player.getInventory().setItem(8, leaveItem);
        
        // Atualizar placares
        updateScoreboards();
        
        // Enviar mensagem para todos
        broadcastMessage(Language.PLAYER_JOIN
                .replace("{player}", player.getName())
                .replace("{players}", String.valueOf(players.size()))
                .replace("{max_players}", String.valueOf(Config.MAX_PLAYERS)));
        
        // Iniciar temporizador se houver jogadores suficientes
        if (players.size() >= Config.MIN_PLAYERS && gameTask == null) {
            startLobbyTimer();
        }
    }
    
    public void leave(Player player) {
        if (players.contains(player.getUniqueId())) {
            players.remove(player.getUniqueId());
            roles.remove(player.getUniqueId());
            
            // Resetar jogador
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setGameMode(GameMode.ADVENTURE);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.teleport(Config.LOBBY_SPAWN);
            
            // Remover efeitos
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            
            // Enviar mensagem para todos
            broadcastMessage(Language.PLAYER_QUIT
                    .replace("{player}", player.getName())
                    .replace("{players}", String.valueOf(players.size()))
                    .replace("{max_players}", String.valueOf(Config.MAX_PLAYERS)));
            
            // Verificar condições de vitória
            checkWinConditions();
            
            // Cancelar temporizador se não houver jogadores suficientes
            if (players.size() < Config.MIN_PLAYERS && state == GameState.WAITING) {
                if (gameTask != null) {
                    gameTask.cancel();
                    gameTask = null;
                }
                timer = Config.LOBBY_COUNTDOWN;
            }
            
            // Atualizar placares
            updateScoreboards();
        } else if (spectators.contains(player.getUniqueId())) {
            spectators.remove(player.getUniqueId());
            
            // Resetar jogador
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setGameMode(GameMode.ADVENTURE);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.teleport(Config.LOBBY_SPAWN);
            
            // Remover efeitos
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
        }
    }
    
    public void startLobbyTimer() {
        timer = Config.LOBBY_COUNTDOWN;
        
        gameTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (players.size() < Config.MIN_PLAYERS) {
                    broadcastMessage(Language.PREFIX + Language.NOT_ENOUGH_PLAYERS);
                    timer = Config.LOBBY_COUNTDOWN;
                    this.cancel();
                    gameTask = null;
                    return;
                }
                
                if (timer <= 0) {
                    start();
                    this.cancel();
                    gameTask = null;
                    return;
                }
                
                if (timer <= 5 || timer == 10 || timer == 30) {
                    broadcastMessage(Language.PREFIX + Language.SEEKER_INFO.replace("{time}", String.valueOf(timer)));
                    for (UUID uuid : players) {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player != null && player.isOnline()) {
                            NMS.sendTitle(player, "&e" + timer, "&fPrepare-se!", 5, 15, 5);
                        }
                    }
                }
                
                updateScoreboards();
                timer--;
            }
        };
        
        gameTask.runTaskTimer(Main.getInstance(), 20L, 20L);
    }
    
    public void start() {
        if (players.size() < Config.MIN_PLAYERS) {
            broadcastMessage(Language.PREFIX + Language.NOT_ENOUGH_PLAYERS);
            return;
        }
        
        state = GameState.PLAYING;
        timer = Config.HIDE_TIME;
        hiding = true;
        
        // Selecionar procuradores
        List<UUID> playersList = new ArrayList<>(players);
        int seekerCount = Math.min(Config.INITIAL_SEEKERS, players.size() / 3);
        
        for (int i = 0; i < seekerCount; i++) {
            int randomIndex = (int) (Math.random() * playersList.size());
            UUID seekerId = playersList.remove(randomIndex);
            roles.put(seekerId, PlayerRole.SEEKER);
            
            Player seeker = Bukkit.getPlayer(seekerId);
            if (seeker != null) {
                // Teleportar procurador para o spawn
                if (seekersSpawn != null) {
                    seeker.teleport(seekersSpawn);
                }
                
                // Aplicar efeitos
                seeker.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Config.HIDE_TIME * 20, 0));
                
                // Equipar procurador
                equipSeeker(seeker);
                
                // Mostrar objetivos
                for (String line : Language.OBJECTIVE_SEEKERS) {
                    seeker.sendMessage(line);
                }
                
                // Anunciar procurador
                broadcastMessage("&e" + seeker.getName() + " &eÉ o procurador! Escondam-se.");
            }
        }
        
        // Definir todos os outros jogadores como escondedores
        for (UUID uuid : players) {
            if (!roles.containsKey(uuid)) {
                roles.put(uuid, PlayerRole.HIDER);
                
                Player hider = Bukkit.getPlayer(uuid);
                if (hider != null) {
                    // Teleportar escondedor para o spawn
                    if (hidersSpawn != null) {
                        hider.teleport(hidersSpawn);
                    }
                    
                    // Equipar escondedor
                    equipHider(hider);
                    
                    // Mostrar objetivos
                    for (String line : Language.OBJECTIVE_HIDERS) {
                        hider.sendMessage(line);
                    }
                }
            }
        }
        
        // Iniciar temporizador do jogo
        startGameTimer();
        
        // Atualizar placares
        updateScoreboards();
        
        // Enviar mensagem para todos
        broadcastMessage(Language.PREFIX + Language.GAME_START);
    }
    
    public void startGameTimer() {
        gameTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (state != GameState.PLAYING) {
                    this.cancel();
                    gameTask = null;
                    return;
                }
                
                if (hiding) {
                    // Tempo de esconder
                    if (timer <= 0) {
                        hiding = false;
                        timer = gameTime;
                        
                        // Remover efeitos dos procuradores
                        for (UUID uuid : players) {
                            if (roles.get(uuid) == PlayerRole.SEEKER) {
                                Player seeker = Bukkit.getPlayer(uuid);
                                if (seeker != null) {
                                    seeker.removePotionEffect(PotionEffectType.BLINDNESS);
                                    seeker.sendMessage(Language.PREFIX + "&aVocê pode começar a procurar agora!");
                                    NMS.sendTitle(seeker, "&c&lCOMEÇOU!", "&eEncontre os escondedores!", 10, 40, 10);
                                }
                            }
                        }
                        
                        // Avisar escondedores
                        for (UUID uuid : players) {
                            if (roles.get(uuid) == PlayerRole.HIDER) {
                                Player hider = Bukkit.getPlayer(uuid);
                                if (hider != null) {
                                    hider.sendMessage(Language.PREFIX + "&cOs procuradores foram liberados! Esconda-se!");
                                    NMS.sendTitle(hider, "&c&lATENÇÃO!", "&cOs procuradores foram liberados!", 10, 40, 10);
                                }
                            }
                        }
                    } else if (timer <= 5 || timer == 10 || timer == 30) {
                        // Avisos de tempo
                        broadcastMessage(Language.PREFIX + Language.HIDE_TIME_START.replace("{time}", String.valueOf(timer)));
                    }
                } else {
                    // Tempo de jogo
                    if (timer <= 0) {
                        // Vitória dos escondedores por tempo
                        stop(PlayerRole.HIDER);
                        this.cancel();
                        gameTask = null;
                        return;
                    }
                }
                
                updateScoreboards();
                timer--;
            }
        };
        
        gameTask.runTaskTimer(Main.getInstance(), 20L, 20L);
    }
    
    public void stop(PlayerRole winnerRole) {
        if (state == GameState.ENDED) {
            return;
        }
        
        state = GameState.ENDED;
        
        if (gameTask != null) {
            gameTask.cancel();
            gameTask = null;
        }
        
        // Mensagem de vitória
        if (winnerRole == PlayerRole.HIDER) {
            broadcastMessage("\n &aOs escondedores venceram!\n");
            
            // Recompensar escondedores
            for (UUID uuid : players) {
                if (roles.get(uuid) == PlayerRole.HIDER) {
                    Player hider = Bukkit.getPlayer(uuid);
                    if (hider != null) {
                        // TODO: Adicionar pontos e moedas
                        NMS.sendTitle(hider, Language.TITLE_VICTORY_HIDERS, Language.SUBTITLE_VICTORY_HIDERS, 10, 70, 20);
                        
                        // Fogos de artifício
                        spawnFireworks(hider.getLocation(), 20);
                    }
                } else {
                    Player seeker = Bukkit.getPlayer(uuid);
                    if (seeker != null) {
                        NMS.sendTitle(seeker, Language.TITLE_DEFEAT, Language.SUBTITLE_DEFEAT, 10, 70, 20);
                    }
                }
            }
        } else if (winnerRole == PlayerRole.SEEKER) {
            broadcastMessage("\n &cOs procuradores venceram!\n");
            
            // Recompensar procuradores
            for (UUID uuid : players) {
                if (roles.get(uuid) == PlayerRole.SEEKER) {
                    Player seeker = Bukkit.getPlayer(uuid);
                    if (seeker != null) {
                        // TODO: Adicionar pontos e moedas
                        NMS.sendTitle(seeker, "&a&lVITÓRIA!", "&fVocê venceu!", 10, 70, 20);
                        
                        // Fogos de artifício
                        spawnFireworks(seeker.getLocation(), 20);
                    }
                } else {
                    Player hider = Bukkit.getPlayer(uuid);
                    if (hider != null) {
                        NMS.sendTitle(hider, "&c&lFIM DE JOGO!", "&7Os procuradores venceram!", 10, 70, 20);
                    }
                }
            }
        } else {
            broadcastMessage(Language.PREFIX + Language.GAME_END);
        }
        
        // Aguardar antes de finalizar o jogo
        new BukkitRunnable() {
            @Override
            public void run() {
                // Teleportar todos para o lobby
                for (UUID uuid : new ArrayList<>(players)) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        leave(player);
                    }
                }
                
                for (UUID uuid : new ArrayList<>(spectators)) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        leave(player);
                    }
                }
                
                // Resetar jogo
                resetGame();
            }
        }.runTaskLater(Main.getInstance(), 200L); // 10 segundos
    }
    
    public void resetGame() {
        // Limpar listas
        players.clear();
        spectators.clear();
        roles.clear();
        kills.clear();
        
        // Resetar estado
        state = GameState.WAITING;
        timer = Config.LOBBY_COUNTDOWN;
        gameTime = Config.GAME_TIME;
        hiding = false;
        
        // Descarregar e recarregar mundo
        if (world != null) {
            // Descarregar mundo atual
            for (Player player : world.getPlayers()) {
                player.teleport(Config.LOBBY_SPAWN);
            }
            
            String worldName = world.getName();
            Bukkit.unloadWorld(world, false);
            
            // Recarregar mundo do backup
            File worldFolder = new File(Main.getInstance().getDataFolder(), "mundos/" + worldName);
            if (worldFolder.exists() && worldFolder.isDirectory()) {
                // Apagar mundo atual
                FileUtils.deleteFolder(new File(Bukkit.getWorldContainer(), worldName));
                
                // Copiar do backup
                FileUtils.copyFolder(worldFolder, new File(Bukkit.getWorldContainer(), worldName));
                
                // Carregar mundo
                this.world = Bukkit.createWorld(new org.bukkit.WorldCreator(worldName));
                if (this.world != null) {
                    this.world.setAutoSave(false);
                    this.world.setTime(0);
                    this.world.setGameRuleValue("doDaylightCycle", "false");
                    this.world.setGameRuleValue("doMobSpawning", "false");
                }
            }
        }
        
        // Carregar localizações novamente
        loadArena();
    }
    
    public void checkWinConditions() {
        if (state != GameState.PLAYING) {
            return;
        }
        
        // Contar jogadores por função
        int hidersCount = 0;
        int seekersCount = 0;
        
        for (UUID uuid : players) {
            PlayerRole role = roles.get(uuid);
            if (role == PlayerRole.HIDER) {
                hidersCount++;
            } else if (role == PlayerRole.SEEKER) {
                seekersCount++;
            }
        }
        
        // Verificar condições de vitória
        if (hidersCount == 0) {
            // Todos os escondedores foram encontrados
            stop(PlayerRole.SEEKER);
        } else if (seekersCount == 0) {
            // Todos os procuradores saíram
            stop(PlayerRole.HIDER);
        } else if (players.size() < Config.MIN_PLAYERS) {
            // Jogadores insuficientes
            stop(null);
        }
    }
    
    public void updateScoreboards() {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                updateScoreboard(player);
            }
        }
        
        for (UUID uuid : spectators) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                updateScoreboard(player);
            }
        }
    }
    
    private void updateScoreboard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(scoreboard);
        }
        
        // Atualizar times para colorir nomes
        Team hidersTeam = scoreboard.getTeam("hiders");
        if (hidersTeam == null) {
            hidersTeam = scoreboard.registerNewTeam("hiders");
            hidersTeam.setPrefix("§6"); // Laranja para escondedores
        }
        
        Team seekersTeam = scoreboard.getTeam("seekers");
        if (seekersTeam == null) {
            seekersTeam = scoreboard.registerNewTeam("seekers");
            seekersTeam.setPrefix("§9"); // Azul para procuradores
        }
        
        Team spectatorsTeam = scoreboard.getTeam("spectators");
        if (spectatorsTeam == null) {
            spectatorsTeam = scoreboard.registerNewTeam("spectators");
            spectatorsTeam.setPrefix("§7"); // Cinza para espectadores
        }
        
        // Limpar times
        for (Team team : scoreboard.getTeams()) {
            for (String entry : new ArrayList<>(team.getEntries())) {
                team.removeEntry(entry);
            }
        }
        
        // Adicionar jogadores aos times
        for (UUID uuid : players) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                PlayerRole role = roles.get(uuid);
                if (role == PlayerRole.HIDER) {
                    hidersTeam.addEntry(p.getName());
                } else if (role == PlayerRole.SEEKER) {
                    seekersTeam.addEntry(p.getName());
                }
            }
        }
        
        for (UUID uuid : spectators) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                spectatorsTeam.addEntry(p.getName());
            }
        }
        
        // TODO: Integrar com hook de scoreboard
        // Por enquanto, apenas mostrar informações básicas
        if (state == GameState.WAITING) {
            player.sendMessage("§eJogadores: §f" + players.size() + "/" + Config.MAX_PLAYERS);
            player.sendMessage("§eTempo: §f" + timer + "s");
        } else if (state == GameState.PLAYING) {
            int hidersCount = 0;
            int seekersCount = 0;
            
            for (PlayerRole role : roles.values()) {
                if (role == PlayerRole.HIDER) {
                    hidersCount++;
                } else if (role == PlayerRole.SEEKER) {
                    seekersCount++;
                }
            }
            
            player.sendMessage("§eEscondedores: §f" + hidersCount);
            player.sendMessage("§eProcuradores: §f" + seekersCount);
            player.sendMessage("§eTempo: §f" + TimeFormatter.formatTime(hiding ? timer : gameTime));
        }
    }
    
    public void broadcastMessage(String message) {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(message);
            }
        }
        
        for (UUID uuid : spectators) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }
    
    public void onPlayerDeath(Player player, Player killer) {
        if (!players.contains(player.getUniqueId()) || state != GameState.PLAYING) {
            return;
        }
        
        PlayerRole playerRole = roles.get(player.getUniqueId());
        
        if (killer != null && players.contains(killer.getUniqueId())) {
            PlayerRole killerRole = roles.get(killer.getUniqueId());
            
            // Adicionar abate ao matador
            int playerKills = kills.getOrDefault(killer.getUniqueId(), 0);
            kills.put(killer.getUniqueId(), playerKills + 1);
            
            if (playerRole == PlayerRole.HIDER && killerRole == PlayerRole.HIDER) {
                // Escondedor traiu outro escondedor
                broadcastMessage(Language.HIDER_DEATH
                        .replace("{victim}", player.getName())
                        .replace("{killer}", killer.getName()));
                
                // O escondedor traidor se torna procurador
                roles.put(player.getUniqueId(), PlayerRole.SEEKER);
                equipSeeker(player);
                
                NMS.sendTitle(player, Language.TITLE_CAUGHT, Language.SUBTITLE_CAUGHT, 10, 40, 10);
                
                // Teleportar para o spawn dos procuradores
                if (seekersSpawn != null) {
                    player.teleport(seekersSpawn);
                }
            } else if (playerRole == PlayerRole.SEEKER && killerRole == PlayerRole.HIDER) {
                // Escondedor matou um procurador
                broadcastMessage(Language.SEEKER_DEATH
                        .replace("{seeker}", player.getName())
                        .replace("{hider}", killer.getName()));
                
                // O procurador volta como procurador
                equipSeeker(player);
                
                // Teleportar para o spawn dos procuradores
                if (seekersSpawn != null) {
                    player.teleport(seekersSpawn);
                }
            }
        } else if (playerRole == PlayerRole.HIDER) {
            // Escondedor morreu sem assassino (queda, etc)
            broadcastMessage("§e" + player.getName() + " §emorreu e virou um procurador!");
            
            // O escondedor se torna procurador
            roles.put(player.getUniqueId(), PlayerRole.SEEKER);
            equipSeeker(player);
            
            NMS.sendTitle(player, Language.TITLE_CAUGHT, Language.SUBTITLE_CAUGHT, 10, 40, 10);
            
            // Teleportar para o spawn dos procuradores
            if (seekersSpawn != null) {
                player.teleport(seekersSpawn);
            }
        } else if (playerRole == PlayerRole.SEEKER) {
            // Procurador morreu sem assassino
            broadcastMessage("§e" + player.getName() + " §emorreu e voltou como procurador!");
            
            // O procurador volta como procurador
            equipSeeker(player);
            
            // Teleportar para o spawn dos procuradores
            if (seekersSpawn != null) {
                player.teleport(seekersSpawn);
            }
        }
        
        // Restaurar vida e fome
        player.setHealth(20);
        player.setFoodLevel(20);
        
        // Verificar condições de vitória
        checkWinConditions();
        
        // Atualizar placares
        updateScoreboards();
    }
    
    public void onPlayerCaught(Player hider, Player seeker) {
        if (!players.contains(hider.getUniqueId()) || !players.contains(seeker.getUniqueId()) || state != GameState.PLAYING) {
            return;
        }
        
        PlayerRole hiderRole = roles.get(hider.getUniqueId());
        PlayerRole seekerRole = roles.get(seeker.getUniqueId());
        
        if (hiderRole != PlayerRole.HIDER || seekerRole != PlayerRole.SEEKER) {
            return;
        }
        
        // Adicionar abate ao procurador
        int seekerKills = kills.getOrDefault(seeker.getUniqueId(), 0);
        kills.put(seeker.getUniqueId(), seekerKills + 1);
        
        // Transformar escondedor em procurador
        roles.put(hider.getUniqueId(), PlayerRole.SEEKER);
        
        // Enviar mensagem
        broadcastMessage(Language.HIDER_FOUND
                .replace("{hider}", hider.getName())
                .replace("{seeker}", seeker.getName()));
        
        // Enviar título para o escondedor
        NMS.sendTitle(hider, Language.TITLE_CAUGHT, Language.SUBTITLE_CAUGHT, 10, 40, 10);
        
        // Equipar escondedor como procurador
        equipSeeker(hider);
        
        // Verificar condições de vitória
        checkWinConditions();
        
        // Atualizar placares
        updateScoreboards();
    }
    
    private void equipHider(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        
        // Dar itens para escondedor
        ItemStack stick = new ItemStack(Material.STICK);
        stick.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.KNOCKBACK, 2);
        
        player.getInventory().setItem(0, stick);
        player.getInventory().setItem(1, new ItemStack(Material.FIREWORK, 64));
        
        player.updateInventory();
    }
    
    private void equipSeeker(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        
        // Dar itens para procurador
        ItemStack blaze = new ItemStack(Material.BLAZE_ROD);
        blaze.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.DAMAGE_ALL, 3);
        
        player.getInventory().setItem(0, blaze);
        player.getInventory().setItem(1, new ItemStack(Material.FIREWORK, 64));
        
        player.updateInventory();
    }
    
    private void spawnFireworks(Location location, int amount) {
        // TODO: Implementar efeito de fogos de artifício
    }
    
    // Getters e Setters
    
    public String getId() {
        return id;
    }
    
    public String getMapName() {
        return mapName;
    }
    
    public World getWorld() {
        return world;
    }
    
    public GameState getState() {
        return state;
    }
    
    public int getTimer() {
        return timer;
    }
    
    public boolean isHiding() {
        return hiding;
    }
    
    public int getGameTime() {
        return gameTime;
    }
    
    public List<UUID> getPlayers() {
        return players;
    }
    
    public List<UUID> getSpectators() {
        return spectators;
    }
    
    public Map<UUID, PlayerRole> getRoles() {
        return roles;
    }
    
    public Map<UUID, Integer> getKills() {
        return kills;
    }
    
    public Location getWaitingLobby() {
        return waitingLobby;
    }
    
    public void setWaitingLobby(Location waitingLobby) {
        this.waitingLobby = waitingLobby;
        saveArena();
    }
    
    public Location getHidersSpawn() {
        return hidersSpawn;
    }
    
    public void setHidersSpawn(Location hidersSpawn) {
        this.hidersSpawn = hidersSpawn;
        saveArena();
    }
    
    public Location getSeekersSpawn() {
        return seekersSpawn;
    }
    
    public void setSeekersSpawn(Location seekersSpawn) {
        this.seekersSpawn = seekersSpawn;
        saveArena();
    }
    
    public boolean hasPlayer(Player player) {
        return players.contains(player.getUniqueId()) || spectators.contains(player.getUniqueId());
    }
    
    public boolean isSpectator(Player player) {
        return spectators.contains(player.getUniqueId());
    }
    
    public PlayerRole getPlayerRole(Player player) {
        return roles.get(player.getUniqueId());
    }
    
    public int getPlayerKills(Player player) {
        return kills.getOrDefault(player.getUniqueId(), 0);
    }
}