package org.photonvision.common.dataflow.events;

public enum DataChangePropertyCodes {
    FULL_SETTINGS("fullsettings"),
    NT_CONNECTED("networkTablesConnected")
    ;

    public final String uiName;

    private DataChangePropertyCodes(String uiName) {
        this.uiName = uiName;
    }
}
