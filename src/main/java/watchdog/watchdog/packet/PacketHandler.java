package watchdog.watchdog.packet;

import net.minecraft.server.v1_10_R1.*;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import watchdog.watchdog.api.packet.PacketListener;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Created by Avis Network on 2017-06-20.
 */
public class PacketHandler implements Listener {

    private static PacketHandler instance;

    private List<PacketListener> listeners = new ArrayList<PacketListener>();

    public PacketHandler(JavaPlugin plugin) {
        instance = this;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static PacketHandler self() {
        return instance;
    }

    public void addPacketListener(PacketListener listener) {
        listeners.add(listener);
    }

    public void removePacketListener(PacketListener listener) {
        listeners.remove(listener);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(PlayerLoginEvent event) {
        EntityPlayer player = ((CraftPlayer) event.getPlayer()).getHandle();
        PlayerConnection original = player.playerConnection;
        // Replace to dummy
        player.playerConnection = new PacketConnection(this.listeners, player.server, original.networkManager, player);
    }

}