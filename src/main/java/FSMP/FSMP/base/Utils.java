package FSMP.FSMP.base;

import net.minecraft.network.protocol.Packet;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class Utils {
    public String parseReason(String[] args) {
        StringBuffer reason = new StringBuffer();

        for (int i = 2; i < args.length; i++) {
            reason.append(args[i]);

            if (i < args.length) {
                reason.append(" ");
            }
        }

        return reason.toString();
    }

}
