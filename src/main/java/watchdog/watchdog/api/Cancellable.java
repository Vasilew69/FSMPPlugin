package watchdog.watchdog.api;

public interface Cancellable {

    public void setCancelled(boolean cancelled);

    public boolean isCancelled();

}
