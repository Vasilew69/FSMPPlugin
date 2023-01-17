package FSMP.FSMP.base;

import FSMP.FSMP.FSMP;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {


    /**
     * Track player joins
     *
     * @since       1.0.0
     * @return      void
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player newPlayer = event.getPlayer();
        final String name = newPlayer.getDisplayName();
        final String playerUUID = newPlayer.getUniqueId().toString();

        // Convert to UUID if necessary
        if (FSMP.getInstance().getConfig().getString("users." + name.toLowerCase()) != null) {
            String node = ("players." + playerUUID);

            String addedBy = FSMP.getInstance().getConfig().getString("users." + name.toLowerCase() + ".addedby");
            String addedOn = FSMP.getInstance().getConfig().getString("users." + name.toLowerCase() + ".addedon");
            String reason = FSMP.getInstance().getConfig().getString("users." + name.toLowerCase() + ".reason");

            FSMP.getInstance().getConfig().set((node + ".name"), name);
            FSMP.getInstance().getConfig().set((node + ".addedby"), addedBy);
            FSMP.getInstance().getConfig().set((node + ".addedon"), addedOn);
            FSMP.getInstance().getConfig().set((node + ".reason"), reason);

            FSMP.getInstance().getConfig().set("users." + name.toLowerCase(), null);
            FSMP.getInstance().saveConfig();
        }

        if (FSMP.getInstance().getConfig().get("players." + playerUUID) != null) {
            String onlineNotice = FSMP.getInstance().getConfig().getString("messages.playeronline", "Player &c%PLAYER% &fhas logged in! Run &c/wd info %PLAYER% &ffor details.");
            onlineNotice = onlineNotice.replace("%PLAYER%", name);

            for (final Player player : FSMP.getInstance().getServer().getOnlinePlayers()) {
                if ((player != null) && (player instanceof Player) && (player.hasPermission("watchdog.statusupdates") || player.isOp())) {
                    FSMP.getInstance().messenger.printMessage(player, "notice", onlineNotice);
                }
            }
        }
    }
}