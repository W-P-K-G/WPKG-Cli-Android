package me.wpkg.cli.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class TCPClient extends Client {
    private static Socket socket;
    private static InputStream in;
    private static OutputStream out;

    public static void connect(String ip, int p) throws IOException {
        /* Creating Socket */
        socket = new Socket(ip,p);
        in = socket.getInputStream();
        out = socket.getOutputStream();

        /* Setting Timeout */
        socket.setSoTimeout(20 * 1000);

        connected = true;
    }

    public static void sendString(String msg) throws IOException {
        System.out.println("Sended: " + msg);

        byte[] buf = msg.getBytes();
        out.write(buf,0,buf.length);
    }

    public static String receiveString() throws IOException {
        byte[] buffer = new byte[65536];
        int len = in.read(buffer);

        if (len == -1)
            throw new IOException("Connection closed");

        String msg = new String(buffer, 0, len, StandardCharsets.UTF_8);
        System.out.println("Received: " + msg);
        return msg;
    }

    public static String sendCommand(String command) throws IOException {
        sendString(command);
        return receiveString();
    }

    public static byte[] rawdata_receive() throws IOException {
        byte[] buffer = new byte[65536];

        /* Receiving Packet */
        int len = in.read(buffer);
        byte[] ret = new byte[len];

        System.arraycopy(buffer, 0, ret, 0, len);

        return ret;
    }

    public static void rawdata_send(byte[] b) throws IOException {
        out.write(b);
        out.flush();
    }

    public static void logOff() throws IOException {
        if (socket.isClosed())
            return;
        sendString("/disconnect");
        socket.close();
    }
}
