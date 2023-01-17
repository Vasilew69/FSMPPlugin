package FSMP.FSMP.base;

import java.util.*;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import FSMP.FSMP.FSMP;

public class CommandHandler {


    /**
     * Add a user to the watchlist
     *
     * @since       1.0.0
     * @return      void
     */
    public void addPlayer(CommandSender sender, String player, String reason) {
        Player targetPlayer = FSMP.getInstance().getServer().getPlayerExact(player);
        String playerUUID = "", node = "";
        String notice = FSMP.getInstance().getConfig().getString("messages.playeradded", "Player %PLAYER% has been added to the watchlist!");
        notice = notice.replace("%PLAYER%", player);

        Calendar cal = Calendar.getInstance();
        int mon = cal.get(Calendar.MONTH)+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);
        String time = String.format("%02d-%02d %02d:%02d:%02d", mon, day, hour, min, sec);

        if (! player.isEmpty()) {
            if (targetPlayer != null) {
                playerUUID = targetPlayer.getUniqueId().toString();
            }

            if (! playerUUID.isEmpty()) {
                node = ("players." + playerUUID);

                FSMP.getInstance().getConfig().set((node + ".name"), player);
            } else {
                node = ("users." + player.toLowerCase());
            }

            FSMP.getInstance().getConfig().set((node + ".addedby"), sender.getName());
            FSMP.getInstance().getConfig().set((node + ".addedon"), time);
            FSMP.getInstance().getConfig().set((node + ".reason"), reason);
            FSMP.getInstance().saveConfig();

            FSMP.getInstance().messenger.printMessage(sender, "success", notice);
        }
    }


    /**
     * Remove a user from the watchlist
     *
     * @since       1.0.0
     * @return      void
     */
    public void removePlayer(CommandSender sender, String player) {
        Player targetPlayer = FSMP.getInstance().getServer().getPlayerExact(player);
        String playerUUID = "";
        Boolean found = false;
        String removedNotice = FSMP.getInstance().getConfig().getString("messages.playerremoved", "Player %PLAYER% has been removed from the watchlist!");
        String notFoundNotice = FSMP.getInstance().getConfig().getString("messages.playernotfound", "Player %PLAYER% is not in the watchlist!");

        if (! player.isEmpty()) {
            if (targetPlayer != null) {
                playerUUID = targetPlayer.getUniqueId().toString();
            }

            removedNotice = removedNotice.replace("%PLAYER%", player);
            notFoundNotice = notFoundNotice.replace("%PLAYER%", player);

            if (FSMP.getInstance().getConfig().get("users." + player.toLowerCase()) != null) {
                // Check old player name method
                found = true;

                FSMP.getInstance().getConfig().set("users." + player.toLowerCase(), null);
                FSMP.getInstance().saveConfig();
            } else if (! playerUUID.isEmpty() && FSMP.getInstance().getConfig().getString("players." + playerUUID) != null) {
                // Check new UUID method
                found = true;

                removedNotice = removedNotice.replace("%PLAYER%", FSMP.getInstance().getConfig().getString("players." + playerUUID + ".name"));
                notFoundNotice = notFoundNotice.replace("%PLAYER%", FSMP.getInstance().getConfig().getString("players." + playerUUID + ".name"));

                FSMP.getInstance().getConfig().set("players." + playerUUID, null);
                FSMP.getInstance().saveConfig();
            } else {
                Set<String> players = FSMP.getInstance().getConfig().getConfigurationSection("players").getKeys(true);

                if (players != null) {
                    for (String playerRecord: players) {
                        if (! playerRecord.contains(".")) {
                            String playerName = FSMP.getInstance().getConfig().getString("players." + playerRecord + ".name");

                            if (playerName.equalsIgnoreCase(player)) {
                                found = true;

                                FSMP.getInstance().getConfig().set("players." + playerRecord, null);
                                FSMP.getInstance().saveConfig();
                            }
                        }
                    }
                }
            }

            if (found) {
                FSMP.getInstance().messenger.printMessage(sender, "success", removedNotice);
            } else {
                FSMP.getInstance().messenger.printMessage(sender, "success", notFoundNotice);
            }
        }
    }


    /**
     * Toggle notification
     *
     * @since       1.2.0
     * @return      void
     */
    public void toggleNotify(CommandSender sender, String param) {
        String senderUUID = FSMP.getInstance().getServer().getPlayerExact(sender.getName()).getUniqueId().toString();

        String oldNode = ("notify." + sender.getName()).toLowerCase();
        String node = ("notify." + senderUUID);
        String notice = "", status = "";

        // Convert to UUID if necessary
        if (FSMP.getInstance().getConfig().getString(oldNode) != null) {
            status = FSMP.getInstance().getConfig().getString(oldNode);

            FSMP.getInstance().getConfig().set(node, status);
            FSMP.getInstance().getConfig().set(oldNode, null);
        }

        if (param.equals("status") || param == null) {
            status = FSMP.getInstance().getConfig().getString(node);

            if (status == "disabled") {
                notice = FSMP.getInstance().getConfig().getString("messages.notificationsdisabled", "Your notifications are disabled.");
            } else {
                notice = FSMP.getInstance().getConfig().getString("messages.notificationsenabled", "Your notifications are enabled.");
            }
        } else if (param.equals("enable") || param.equals("on")) {
            FSMP.getInstance().getConfig().set(node, "enabled");
            FSMP.getInstance().saveConfig();

            notice = FSMP.getInstance().getConfig().getString("messages.notifyenable", "Notifications enabled!");
        } else if(param.equals("disable") || param.equals("off")) {
            FSMP.getInstance().getConfig().set(node, "disabled");
            FSMP.getInstance().saveConfig();

            notice = FSMP.getInstance().getConfig().getString("messages.notifydisable", "Notifications disabled!");
        }

        if (notice == "") {
            FSMP.getInstance().messenger.sendHelp(sender);
        } else {
            FSMP.getInstance().messenger.printMessage(sender, "success", notice);
        }
    }


    /**
     *  Retrieve the watchlist
     *
     *  @since      1.2.2
     *  @return     void
     */
    public void getWatchlist(CommandSender sender, String online, String displayType) {
        int userCount = 0;
        String userList = "";
        String onlineStatus = " ";

        // Check old player name method
        if (FSMP.getInstance().getConfig().isConfigurationSection("users")) {
            Set<String> users = FSMP.getInstance().getConfig().getConfigurationSection("users").getKeys(true);

            if (users != null) {
                for(String user: users) {
                    if (! user.contains(".")) {
                        if (online.equals("online")) {
                            Player targetPlayer = FSMP.getInstance().getServer().getPlayerExact(user);
                            if (targetPlayer != null && targetPlayer.isOnline()) {
                                userCount++;
                                userList += user + ", ";
                            }
                        } else {
                            userCount++;
                            userList += user + ", ";
                        }
                    }
                }
            }
        }

        // Check new UUID method
        if (FSMP.getInstance().getConfig().isConfigurationSection("players")) {
            Set<String> players = FSMP.getInstance().getConfig().getConfigurationSection("players").getKeys(true);

            if (players != null) {
                for (String player: players) {
                    if (! player.contains(".")) {
                        String playerName = FSMP.getInstance().getConfig().getString("players." + player + ".name");

                        if (online.equals("online")) {
                            Player targetPlayer = FSMP.getInstance().getServer().getPlayer(playerName);
                            if (targetPlayer != null && targetPlayer.isOnline()) {
                                userCount++;
                                userList += playerName + ", ";
                            }
                        } else {
                            userCount++;
                            userList += playerName + ", ";
                        }
                    }
                }
            }
        }

        if (online.equals("online")) {
            onlineStatus = " online ";
        }

        if (userCount == 0) {
            FSMP.getInstance().messenger.printMessage(sender, "success", "There are no" + onlineStatus + "users in the watchlist.");
        } else if (userCount == 1) {
            FSMP.getInstance().messenger.printMessage(sender, "success", "There is 1" + onlineStatus + "user in the watchlist.");

            if (displayType.equals("list")) {
                sender.sendMessage(userList.substring(0, userList.length() - 2));
            }
        } else {
            FSMP.getInstance().messenger.printMessage(sender, "success", "There are " + userCount + onlineStatus + "users in the watchlist.");

            if (displayType.equals("list")) {
                sender.sendMessage(userList.substring(0, userList.length() - 2));
            }
        }
    }


    /**
     * Search for a player in the watchlist
     *
     * @since       1.0.0
     * @return      void
     */
    public void searchUsers(CommandSender sender, String player) {
        Player targetPlayer = FSMP.getInstance().getServer().getPlayerExact(player);
        String playerUUID = "";

        if (! player.isEmpty()) {
            if (targetPlayer != null) {
                playerUUID = targetPlayer.getUniqueId().toString();
            }

            if (FSMP.getInstance().getConfig().get("users." + player.toLowerCase()) != null) {
                // Check old name based method
                getInfo(sender, player);
            } else if(! playerUUID.isEmpty() && FSMP.getInstance().getConfig().get("players." + playerUUID) != null) {
                // Check new UUID method
                getInfo(sender, player);
            } else {
                Set<String> users = FSMP.getInstance().getConfig().getConfigurationSection("users").getKeys(true);
                Set<String> players = FSMP.getInstance().getConfig().getConfigurationSection("players").getKeys(true);
                Set<String> found = new HashSet<String>();
                int userCount = 0;

                if (users != null) {
                    for (String user: users) {
                        if (! user.contains(".") && user.toLowerCase().contains(player.toLowerCase())) {
                            found.add(user);
                            userCount++;
                        }
                    }
                }

                if (players != null) {
                    for (String playerRecord: players) {
                        if (! playerRecord.contains(".")) {
                            String targetPlayerName = FSMP.getInstance().getConfig().getString("players." + playerRecord + ".name");

                            if (targetPlayerName.toLowerCase().contains(player.toLowerCase())) {
                                found.add(targetPlayerName);
                                userCount++;
                            }
                        }
                    }
                }

                if (! found.isEmpty()) {
                    FSMP.getInstance().messenger.printMessage(sender, "success", "Found the following " + userCount + " players:");

                    for(String foundUser: found) {
                        getInfo(sender, foundUser);
                    }
                } else {
                    FSMP.getInstance().messenger.printMessage(sender, "success", "No users found matching \"" + player + "\"");
                }
            }
        }
    }


    /**
     * Retrieve the info for a player in the watchlist
     *
     * @since       1.0.0
     * @return      void
     */
    public void getInfo(CommandSender sender, String player) {
        Boolean found = false;
        String addedBy = "", addedOn = "", reason = "";
        Player targetPlayer = FSMP.getInstance().getServer().getPlayerExact(player);
        String playerUUID = "";

        if (! player.isEmpty()) {
            if (targetPlayer != null) {
                playerUUID = targetPlayer.getUniqueId().toString();
            }

            if (FSMP.getInstance().getConfig().get("users." + player.toLowerCase()) != null) {
                found = true;

                addedBy = FSMP.getInstance().getConfig().getString("users." + player.toLowerCase() + ".addedby", "console");
                addedOn = FSMP.getInstance().getConfig().getString("users." + player.toLowerCase() + ".addedon", "unknown");
                reason = FSMP.getInstance().getConfig().getString("users." + player.toLowerCase() + ".reason", "unknown");
            } else if (! playerUUID.isEmpty() && FSMP.getInstance().getConfig().getString("players." + playerUUID) != null) {
                found = true;

                addedBy = FSMP.getInstance().getConfig().getString("players." + playerUUID + ".addedby", "console");
                addedOn = FSMP.getInstance().getConfig().getString("players." + playerUUID + ".addedon", "unknown");
                reason = FSMP.getInstance().getConfig().getString("players." + playerUUID + ".reason", "unknown");
            } else {
                Set<String> players = FSMP.getInstance().getConfig().getConfigurationSection("players").getKeys(true);

                if (players != null) {
                    for (String playerRecord: players) {
                        String playerName = FSMP.getInstance().getConfig().getString("players." + playerRecord + ".name");

                        if (playerName.toLowerCase() == player.toLowerCase()) {
                            found = true;

                            addedBy = FSMP.getInstance().getConfig().getString("players." + playerRecord + ".addedby", "console");
                            addedOn = FSMP.getInstance().getConfig().getString("players." + playerRecord + ".addedon", "unknown");
                            reason = FSMP.getInstance().getConfig().getString("players." + playerRecord + ".reason", "unknown");
                        }
                    }
                }
            }

            if (found) {
                FSMP.getInstance().messenger.printMessage(sender, "success", "Player " + player + " entry:");
                sender.sendMessage(ChatColor.GOLD + "    Added: " + ChatColor.WHITE + addedOn);
                sender.sendMessage(ChatColor.GOLD + "    Added By: " + ChatColor.WHITE + addedBy);
                sender.sendMessage(ChatColor.GOLD + "    Reason: " + ChatColor.WHITE + reason);
            } else {
                String notFoundNotice = FSMP.getInstance().getConfig().getString("messages.playernotfound", "Player %PLAYER% is not in the watchlist!");
                notFoundNotice = notFoundNotice.replace("%PLAYER%", player);

                FSMP.getInstance().messenger.printMessage(sender, "success", notFoundNotice);
            }
        }
    }
}