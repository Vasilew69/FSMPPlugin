package watchdog.watchdog.commons;

import watchdog.watchdog.api.suspicios.Suspicious;

public class Reports implements Suspicious {

    public int limit = 0;

    public int reports = 0;
    public long lastReport = -1L;
}
