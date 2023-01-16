package watchdog.watchdog.autoclick;

import watchdog.watchdog.api.suspicios.Suspicious;

public class AutoClickSuspicious implements Suspicious {

    public long lastHit = -1l;
    public long hitDelay = -1l;
}