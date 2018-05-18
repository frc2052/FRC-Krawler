package com.team2052.frckrawler.core.bluetooth.scout;

public class ScoutSyncStatus {
    private final boolean successful;
    private final String message;

    public ScoutSyncStatus(boolean successful, String message) {
        this.successful = successful;
        this.message = message;
    }

    public ScoutSyncStatus(boolean successful) {
        this(successful, null);
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getMessage() {
        return message;
    }
}
