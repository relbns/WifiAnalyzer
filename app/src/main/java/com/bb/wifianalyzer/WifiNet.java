package com.bb.wifianalyzer;

public class WifiNet {

    // ssid name
    private String ssidName;
    // ssid security type
    private String securityType;
    // ssid signal strength
    private int signalStrength;
    // ssid mac address
    private String macAddress;
    // is the phone currently connected to this ssid
    private boolean connected;

    // c'tor
    public WifiNet(String ssidName, String securityType, int signalStrength, String macAddress) {
        this.ssidName = ssidName;
        this.securityType = securityType;
        this.signalStrength = signalStrength;
        this.macAddress = macAddress;
        this.connected = false;
    }

    public String getSsidName() {
        return ssidName;
    }

    public String getSecurityType() {
        return securityType;
    }

    public int getSignalStrength() {
        return signalStrength;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public String getMacAddress() {
        return macAddress;
    }
}
