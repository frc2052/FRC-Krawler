package com.team2052.frckrawler.bluetooth;

public interface ClientThreadListener {
	
	public void onSuccessfulSync();
	public void onUnsuccessfulSync(String errorMessage);
	public void onUpdate(String message);
}
