package watchdog.watchdog;


import jdk.jfr.internal.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import watchdog.watchdog.packet.PacketHandler;
import watchdog.watchdog.repeatable.RepeatHandler;


public class WatchDog extends JavaPlugin {


    /**
     * The WatchDog instance
     */
    private static WatchDog instance;


    /**
     * Instantiate helper classes
     */
    public Messenger messenger = new Messenger();

    public PlayerHandler playerHandler = new PlayerHandler();
    public CommandHandler commandHandler = new CommandHandler();
    public Utils utils = new Utils();


    /**
     * Enable all the things
     *
     * @since       1.0.0
     * @return      void
     */
    @Override
    public void onEnable() {
        getLogger().info("Watchdog is watching");

        instance = this;

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        new UpdateChecker(this, 12345).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                getLogger().info("There is not a new update available.");
            } else {
                getLogger().info("There is a new update available.");
            }
        });

        new RepeatHandler(this);
        new PacketHandler(this);

        WatchDogFactory.loadDogs(this);
    }


    /**
     * Command handler
     *
     * @since       1.0.0
     * @return      void
     */
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Process calls to 'wd' and 'watchdog' only
        if (! command.getName().equalsIgnoreCase("wd") && ! command.getName().equalsIgnoreCase("watchdog")) {
            return false;
        }

        // Bail if user doesn't have at least the 'use' permission
        if (! sender.hasPermission("watchdog.use")) {
            return false;
        }

        // Bail if no option is specified
        if (args.length == 0) {
            WatchDog.getInstance().messenger.sendHelp(sender);
            return true;
        }

        // Parse out the passed option
        String option = args[0];
        String reason, param;
        String player = "";

        if (args.length > 1) {
            player = args[1];
        }

        // Bail if the user can't access the specified option
        if (! WatchDog.getInstance().playerHandler.canAccess(sender, option)) {
            WatchDog.getInstance().messenger.sendHelp(sender);
            return false;
        }

        switch (option) {
            case "add":
                reason = WatchDog.getInstance().utils.parseReason(args);

                commandHandler.addPlayer(sender, player, reason);
                break;
            case "remove":
                commandHandler.removePlayer(sender, player);
                break;
            case "notify":
                param = args[1];

                commandHandler.toggleNotify(sender, param);
                break;
            case "count":
            case "list":
                String status = "";

                if (args.length > 1) {
                    status = args[1];
                }

                commandHandler.getWatchlist(sender, status, option);
                break;
            case "search":
                commandHandler.searchUsers(sender, player);
                break;
            case "info":
                commandHandler.getInfo(sender, player);
                break;
            case "help":
            default:
                WatchDog.getInstance().messenger.sendHelp(sender);
                break;
        }

        return true;
    }

    @Override
    public  void onDisable() {

        getLogger().info("Shutting Down the Dog");
    }


    /**
     * Return instance
     *
     * @since       1.0.0
     * @return      void
     */
    public static WatchDog getInstance() {
        return instance;
    }
}
