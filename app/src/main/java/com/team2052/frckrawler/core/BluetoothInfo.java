package com.team2052.frckrawler.core;

public class BluetoothInfo {
    public static final String UUID = "d6035ed0-8f10-11e2-9e96-0800200c9a66";
    public static final String SERVICE_NAME = "FRCKrawler";

    public static enum ConnectionType {
        SCOUT_SYNC;

        public static ConnectionType[] VALID_CONNECTION_TYPES = values();
    }
}
