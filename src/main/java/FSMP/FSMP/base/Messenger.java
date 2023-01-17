package FSMP.FSMP.base;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import FSMP.FSMP.FSMP;

public class Messenger {


    /**
     * Outputs a plugin message
     *
     * @since       1.0.0
     * @return      void
     */
    public void printMessage(CommandSender sender, String messageType, String message) {
        if (!(!(sender != null) || !(sender instanceof Player))) {
            String status = FSMP.getInstance().getConfig().getString("notify." + sender.getName().toLowerCase());

            if(! messageType.equals("notice") || ! status.equals("disabled")) {
                String prefix = FSMP.getInstance().getConfig().getString("prefix." + messageType, "[FSMP]");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + ChatColor.WHITE + " " + message));
            }
        }
    }


    /**
     * Show the plugin help
     *
     * @since       1.0.0
     * @return      void
     */
    public void sendHelp(CommandSender sender) {
        if (FSMP.getInstance().playerHandler.canAccess(sender, "help")) {
            sender.sendMessage(ChatColor.BOLD + (ChatColor.GOLD + "----------"+ ChatColor.RED + " FSMP Help: " + ChatColor.GOLD + "----------"));

            if (FSMP.getInstance().playerHandler.canAccess(sender, "add")) {
                sender.sendMessage(ChatColor.BOLD + (ChatColor.RED + "/wd add [player] [reason] -" + ChatColor.WHITE + " Adds a player to the watchlist"));
            }

            if (FSMP.getInstance().playerHandler.canAccess(sender, "remove")) {
                sender.sendMessage(ChatColor.BOLD + (ChatColor.RED + "/wd remove [player] - " + ChatColor.WHITE + "Removes a player from the watchlist"));
            }

            if (FSMP.getInstance().playerHandler.canAccess(sender, "notify")) {
                sender.sendMessage(ChatColor.BOLD + (ChatColor.RED + "/wd notify [status|on|off] - " + ChatColor.WHITE + " Enables/disables your notifications"));
            }

            sender.sendMessage(ChatColor.BOLD + (ChatColor.RED + "/wd count [online] - " + ChatColor.WHITE + " Shows the number of players in the watchlist"));
            sender.sendMessage(ChatColor.BOLD + (ChatColor.RED + "/wd list [online] - " + ChatColor.WHITE + " Shows a list of the players in the watchlist"));
            sender.sendMessage(ChatColor.BOLD + (ChatColor.RED + "/wd search [player] - " + ChatColor.WHITE + " Search for a player in the watchlist"));
            sender.sendMessage(ChatColor.BOLD + (ChatColor.RED + "/wd info [player] - " + ChatColor.WHITE + " Display the details of a watchlist entry"));
        }
    }
}