package watchdog.watchdog.api.packet;

public interface PacketListener {

    public void in(PacketInfo info);

    public void out(PacketInfo info);

}
