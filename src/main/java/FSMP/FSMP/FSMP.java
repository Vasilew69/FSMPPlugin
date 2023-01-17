package FSMP.FSMP;


import FSMP.FSMP.Votifier.LogFilter;
import FSMP.FSMP.base.*;
import FSMP.FSMP.Votifier.crypto.RSAIO;
import FSMP.FSMP.Votifier.crypto.RSAKeygen;
import FSMP.FSMP.Votifier.model.ListenerLoader;
import FSMP.FSMP.Votifier.model.VoteListener;
import FSMP.FSMP.Votifier.net.VoteReceiver;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class FSMP extends JavaPlugin {
    /**
     * The logger instance.
     */
    private static final Logger LOG = Logger.getLogger("FSMPVotes");

    /**
     * Log entry prefix
     */
    private static final String logPrefix = "[FSMPVotes] ";

    /**
     * The Votifier instance.
     */
    private static FSMP instance;


    /**
     * The current Votifier version.
     */
    private String version;

    /**
     * The vote listeners.
     */
    private final List<VoteListener> listeners = new ArrayList<VoteListener>();

    /**
     * The vote receiver.
     */
    private VoteReceiver voteReceiver;

    /**
     * The RSA key pair.
     */
    private KeyPair keyPair;

    /**
     * Debug mode flag
     */
    private boolean debug;

    /**
     * Attach custom log filter to logger.
     */
    static {
        LOG.setFilter(new LogFilter(logPrefix));
    }


    /**
     * The WatchDog instance
     */


    /**
     * Instantiate helper classes
     */
    public Messenger messenger = new Messenger();

    public PlayerHandler playerHandler = new PlayerHandler();
    public CommandHandler commandHandler = new CommandHandler();
    public Utils utils = new Utils();

    public static FSMP getInstance() {
        return instance;
    }


    /**
     * Enable all the things
     *
     * @return void
     * @since 1.0.0
     */
    @Override
    public void onEnable() {
        getLogger().info("FSMP Plugin Has started");

        instance = this;

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        new UpdateChecker(this, 107408).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                getLogger().info("There is not a new update available.");
            } else {
                getLogger().info("There is a new update available.");
            }
        });

        version = getDescription().getVersion();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File config = new File(getDataFolder() + "/wdconfig.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(config);
        File rsaDirectory = new File(getDataFolder() + "/rsa");
        // Replace to remove a bug with Windows paths - SmilingDevil
        String listenerDirectory = getDataFolder().toString()
                .replace("\\", "/") + "/listeners";

        /*
         * Use IP address from server.properties as a default for
         * configurations. Do not use InetAddress.getLocalHost() as it most
         * likely will return the main server address instead of the address
         * assigned to the server.
         */
        String hostAddr = Bukkit.getServer().getIp();
        if (hostAddr == null || hostAddr.length() == 0)
            hostAddr = "0.0.0.0";

        /*
         * Create configuration file if it does not exists; otherwise, load it
         */
        if (!config.exists()) {
            try {
                // First time run - do some initialization.
                LOG.info("Configuring Votes for the first time...");

                // Initialize the configuration file.
                config.createNewFile();

                cfg.set("host", hostAddr);
                cfg.set("port", 8192);
                cfg.set("debug", false);

                /*
                 * Remind hosted server admins to be sure they have the right
                 * port number.
                 */
                LOG.info("------------------------------------------------------------------------------");
                LOG.info("Assigning Votes to listen on port 8192. If you are hosting Craftbukkit on a");
                LOG.info("shared server please check with your hosting provider to verify that this port");
                LOG.info("is available for your use. Chances are that your hosting provider will assign");
                LOG.info("a different port, which you need to specify in wdconfig.yml");
                LOG.info("------------------------------------------------------------------------------");

                cfg.set("listener_folder", listenerDirectory);
                cfg.save(config);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Error creating configuration file", ex);
                gracefulExit();
                return;
            }
        } else {
            // Load configuration.
            cfg = YamlConfiguration.loadConfiguration(config);
        }

        /*
         * Create RSA directory and keys if it does not exist; otherwise, read
         * keys.
         */
        try {
            if (!rsaDirectory.exists()) {
                rsaDirectory.mkdir();
                new File(listenerDirectory).mkdir();
                keyPair = RSAKeygen.generate(2048);
                RSAIO.save(rsaDirectory, keyPair);
            } else {
                keyPair = RSAIO.load(rsaDirectory);
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE,
                    "Error reading configuration file or RSA keys", ex);
            gracefulExit();
            return;
        }

        // Load the vote listeners.
        listenerDirectory = cfg.getString("listener_folder");
        listeners.addAll(ListenerLoader.load(listenerDirectory));

        // Initialize the receiver.
        String host = cfg.getString("host", hostAddr);
        int port = cfg.getInt("port", 8192);
        debug = cfg.getBoolean("debug", false);
        if (debug)
            LOG.info("DEBUG mode enabled!");

        try {
            voteReceiver = new VoteReceiver(this, host, port);
            voteReceiver.start();

            LOG.info("Votes enabled.");
        } catch (Exception ex) {
            gracefulExit();
            return;
        }

        /**
         * GAListener
         *
         * @return void
         * @since 1.0.0
         */


    }

    private void gracefulExit() {

    }


    /**
     * Command handler
     *
     * @return void
     * @since 1.0.0
     */
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Process calls to 'wd' and 'watchdog' only
        if (!command.getName().equalsIgnoreCase("wd") && !command.getName().equalsIgnoreCase("watchdog")) {
            return false;
        }

        // Bail if user doesn't have at least the 'use' permission
        if (!sender.hasPermission("watchdog.use")) {
            return false;
        }

        // Bail if no option is specified
        if (args.length == 0) {
            FSMP.getInstance().messenger.sendHelp(sender);
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
        if (!FSMP.getInstance().playerHandler.canAccess(sender, option)) {
            FSMP.getInstance().messenger.sendHelp(sender);
            return false;
        }

        switch (option) {
            case "add":
                reason = FSMP.getInstance().utils.parseReason(args);

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
                FSMP.getInstance().messenger.sendHelp(sender);
                break;
        }

        return true;
    }

    @Override
    public void onDisable() {

        getLogger().info("Shutting Down the FSMP");
        if (voteReceiver != null) {
            voteReceiver.shutdown();
        }
        LOG.info("Votes disabled.");
    }
    public String getVersion() {
        return version;
    }
    public List<VoteListener> getListeners() {
        return listeners;
    }

    public VoteReceiver getVoteReceiver() {
        return voteReceiver;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public boolean isDebug() {
        return debug;
    }

}