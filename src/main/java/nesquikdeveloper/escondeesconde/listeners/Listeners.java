package nesquikdeveloper.escondeesconde.listeners;

import nesquikdeveloper.escondeesconde.Main;
import nesquikdeveloper.escondeesconde.listeners.player.PlayerDamageListener;
import nesquikdeveloper.escondeesconde.listeners.player.PlayerInteractListener;
import nesquikdeveloper.escondeesconde.listeners.player.PlayerJoinQuitListener;
import nesquikdeveloper.escondeesconde.listeners.player.PlayerMoveListener;
import org.bukkit.plugin.PluginManager;

public class Listeners {
    
    public static void setupListeners() {
        PluginManager pm = Main.getInstance().getServer().getPluginManager();
        
        // Registrar listeners
        pm.registerEvents(new PlayerInteractListener(), Main.getInstance());
        pm.registerEvents(new PlayerDamageListener(), Main.getInstance());
        pm.registerEvents(new PlayerJoinQuitListener(), Main.getInstance());
        pm.registerEvents(new PlayerMoveListener(), Main.getInstance());
    }
}