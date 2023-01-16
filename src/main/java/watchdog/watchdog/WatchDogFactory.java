package watchdog.watchdog;

import org.bukkit.plugin.java.JavaPlugin;
import watchdog.watchdog.autoclick.AutoClickWatcher;
import watchdog.watchdog.cheatengine.CheatEngineWatcher;
import watchdog.watchdog.fastbow.FastbowWatcher;
import watchdog.watchdog.killaura.KillauraWatcher;

public class WatchDogFactory {

    public static void loadDogs(JavaPlugin plugin) {
        new KillauraWatcher(plugin);
        new FastbowWatcher(plugin);
        new AutoClickWatcher(plugin);
        new CheatEngineWatcher(plugin);
    }
}
