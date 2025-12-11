package cn.superiormc.ultimateshop.gui;

public class GUIStatus {

    private final Status status;

    private final AbstractGUI gui;

    private GUIStatus(Status status, AbstractGUI gui) {
        this.status = status;
        this.gui = gui;
    }

    public Status getStatus() {
        return status;
    }

    public AbstractGUI getGUI() {
        return gui;
    }

    public enum Status {
        CAN_REOPEN,
        ACTION_OPEN_MENU,
        ALREADY_IN_COOLDOWN
    }

    @Override
    public String toString() {
        return "GUI Status: " + status + " GUI: " + gui;
    }

    public static GUIStatus of(AbstractGUI gui, Status status) {
        return new GUIStatus(status, gui);
    }

}
