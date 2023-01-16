package watchdog.watchdog.killaura;

import watchdog.watchdog.api.suspicios.Suspicious;

public class KillauraSuspicious implements Suspicious {

    public int hits;
    public long timer = System.currentTimeMillis();

    public long lastHit = -1L;
    public long hitDelay = -1L;

    public int stacks = 0;

}
