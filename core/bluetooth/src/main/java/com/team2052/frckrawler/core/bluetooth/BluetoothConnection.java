package com.team2052.frckrawler.core.bluetooth;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BluetoothConnection {
    private final BluetoothSocket socket;
    private final ObjectInputStream inputStream;
    private final OutputStreamWrapper outputStreamWrapper;

    public BluetoothConnection(BluetoothSocket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream) {
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStreamWrapper = new OutputStreamWrapper(outputStream);
    }

    public void closeConnection() throws IOException {
        socket.close();
    }

    public ObjectInputStream getInputStream() {
        return inputStream;
    }

    public OutputStreamWrapper getOutputStreamWrapper() {
        return outputStreamWrapper;
    }

    public static class OutputStreamWrapper {
        private final ObjectOutputStream outputStream;

        OutputStreamWrapper(ObjectOutputStream outputStream) {
            this.outputStream = outputStream;
        }

        public OutputStreamWrapper writeInteger(int val) throws IOException {
            outputStream.writeInt(val);
            return this;
        }

        public OutputStreamWrapper writeObject(Object obj) throws IOException {
            if (obj == null)
                throw new IllegalStateException("Object cannot be null!");

            outputStream.writeObject(obj);
            return this;
        }

        public void send() throws IOException {
            outputStream.flush();
        }
    }
}
