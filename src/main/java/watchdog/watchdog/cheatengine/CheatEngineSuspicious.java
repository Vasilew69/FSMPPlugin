package watchdog.watchdog.cheatengine;

import watchdog.watchdog.api.suspicios.Suspicious;

public class CheatEngineSuspicious implements Suspicious {

    public long updatePerSecond = -1l;
    public long updatePerTick = -1l;

    public int count = 0;
    public int stack = 0;

}
