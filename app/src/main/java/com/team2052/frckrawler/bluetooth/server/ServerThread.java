package com.team2052.frckrawler.bluetooth.server;

import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.team2052.frckrawler.FRCKrawler;
import com.team2052.frckrawler.bluetooth.BluetoothInfo;
import com.team2052.frckrawler.bluetooth.ScoutPackage;
import com.team2052.frckrawler.bluetooth.ServerPackage;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.util.BluetoothUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ServerThread extends Thread {

    private final BluetoothAdapter mBluetoothAdapter;
    private final Server server;
    private DaoSession mDaoSession = null;
    private boolean isOpen;
    private Context context;
    private Event hostedEvent;
    private BluetoothServerSocket serverSocket;

    public ServerThread(ServerService c, Event e) {
        isOpen = false;
        context = c.getApplicationContext();
        hostedEvent = e;
        serverSocket = null;
        server = Server.getInstance(c);
        mDaoSession = ((FRCKrawler) c.getApplicationContext()).getDaoSession();
        mBluetoothAdapter = BluetoothUtil.getBluetoothAdapter();
    }

    @Override
    public void run() {
        //Init the socket
        String deviceName = "device";

        isOpen = true;
        while (isOpen) {
            try {
                serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(BluetoothInfo.SERVICE_NAME, UUID.fromString(BluetoothInfo.UUID));
            } catch (IOException io) {
                io.printStackTrace();
            }

            try {
                long startTime = System.currentTimeMillis();
                BluetoothSocket clientSocket = serverSocket.accept();
                deviceName = clientSocket.getRemoteDevice().getName();
                //handler.onSyncStart(deviceName);
                //Close the server socket
                serverSocket.close();
                //Create the streams
                OutputStream outputStream = clientSocket.getOutputStream();
                ObjectOutputStream oStream = new ObjectOutputStream(outputStream);
                InputStream inputStream = clientSocket.getInputStream();
                ObjectInputStream inStream = new ObjectInputStream(inputStream);

                //Read the type of sync
                int connectionType = inStream.readInt();

                if (connectionType == BluetoothInfo.SCOUT) {
                    //Get the data from the stream
                    ((ServerPackage) inStream.readObject()).save(mDaoSession);
                    oStream.writeObject(new ScoutPackage(mDaoSession, hostedEvent));
                }

                oStream.flush();
                clientSocket.close();
                //handler.onSyncSuccess(deviceName);
                Log.d("FRCKrawler", "Synced in: " + (System.currentTimeMillis() - startTime) + "ms");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("FRCKrawler", "IO mErrorView in server.");
                if (!e.getMessage().trim().equals("Operation Canceled") && isOpen) {
                    //handler.onSyncError(deviceName);
                }
                ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(ServerCallbackHandler.SYNC_ONGOING_ID);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeServer() {
        isOpen = false;
        if (serverSocket != null)
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
        NotificationManager m = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        m.cancel(ServerService.SERVER_OPEN_ID);
        m.cancel(ServerCallbackHandler.SYNC_ONGOING_ID);
    }
}
