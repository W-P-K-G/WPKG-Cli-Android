package me.wpkg.cli.net;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDPClient extends Client {
    private static DatagramSocket socket;
    private static InetAddress address;
    private static int port;

    public static void connect(String ip, int p) throws SocketException, UnknownHostException {
        /* Getting IP Address */
        address = InetAddress.getByName(ip);
        port = p;

        /* Creating Socket */
        socket = new DatagramSocket();

        /* Setting Timeout */
        socket.setSoTimeout(20 * 1000);

        connected = true;
    }

    public static void sendString(String msg) throws IOException {
        System.out.println("Sended: " + msg);

        byte[] buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);

        /* Sending Packet */
        socket.send(packet);
    }

    public static String receiveString() throws IOException {
        byte[] buf = new byte[65536];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        /* Receiving Packet */
        socket.receive(packet);

        String msg = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
        System.out.println("Received: " + msg);
        return msg;
    }

    public static String sendCommand(String command) throws IOException {
        sendString(command);
        return receiveString();
    }

    public static byte[] rawdata_receive() throws IOException {
        byte[] buf = new byte[65536];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        /* Receiving Packet */
        socket.receive(packet);

        int len = packet.getLength();
        byte[] ret = new byte[len];

        System.arraycopy(buf, 0, ret, 0, len);

        return ret;
    }

    public static void rawdata_send(byte[] b) throws IOException {
        DatagramPacket p = new DatagramPacket(b, b.length, address, port);
        socket.send(p);
    }

    public static void logOff() throws IOException {
        if (socket.isClosed())
            return;
        sendString("/disconnect");
        socket.close();
    }
}
