package watchdog.watchdog.fastbow;

import watchdog.watchdog.api.suspicios.Suspicious;

public class FastbowSuspicious implements Suspicious {

    public int shots = 0;
    public long timer = System.currentTimeMillis();

    public long lastShot = -1l;
    public long shotDelay = -1l;

    public int stacks = 0;
}
