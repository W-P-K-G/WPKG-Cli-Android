package me.wpkg.cli.net;

import java.io.IOException;

public class Client {
    protected static boolean connected;

    private static Protocol protocol;

    public static void setProtocol(Protocol protocol) {
        Client.protocol = protocol;
    }

    public static Protocol getProtocol() {
        return protocol;
    }

    public static void connect(String ip, int p) throws IOException {
        switch (protocol) {
            case TCP: TCPClient.connect(ip,p); break;
            case UDP: UDPClient.connect(ip,p); break;
        }
    }

    public static boolean isConnected() {
        return connected;
    }

    public static void sendString(String msg) throws IOException {
        switch (protocol) {
            case TCP: TCPClient.sendString(msg); break;
            case UDP: UDPClient.sendString(msg); break;
        }
    }

    public static String receiveString() throws IOException {
        switch (protocol) {
            case TCP: return TCPClient.receiveString();
            case UDP: return UDPClient.receiveString();
        }
        return null;
    }

    public static String sendCommand(String command) throws IOException {
        switch (protocol) {
            case TCP: return TCPClient.sendCommand(command);
            case UDP: return UDPClient.sendCommand(command);
        }
        return null;
    }

    public static byte[] rawdata_receive() throws IOException {
        switch (protocol) {
            case TCP: return TCPClient.rawdata_receive();
            case UDP: return UDPClient.rawdata_receive();
        }
        return null;
    }

    public static void rawdata_send(byte[] b) throws IOException {
        switch (protocol) {
            case TCP: TCPClient.rawdata_send(b); break;
            case UDP: UDPClient.rawdata_send(b); break;
        }
    }

    public static void logOff() throws IOException {
        switch (protocol) {
            case TCP: TCPClient.logOff(); break;
            case UDP: UDPClient.logOff(); break;
        }
    }
}
