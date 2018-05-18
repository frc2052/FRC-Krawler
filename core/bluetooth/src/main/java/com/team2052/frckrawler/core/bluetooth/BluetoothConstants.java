package com.team2052.frckrawler.core.bluetooth;

public class BluetoothConstants {
    public static final String UUID = "d6035ed0-8f10-11e2-9e96-0800200c9a66";
    public static final String SERVICE_NAME = "FRCKrawler";
    public static final int SCOUT_SYNC = 1;

    public static class ReturnCodes {
        public static final int OK = 1;
        public static final int VERSION_ERROR = -1;
        public static final int EVENT_MATCH_ERROR = -2;
    }
}
