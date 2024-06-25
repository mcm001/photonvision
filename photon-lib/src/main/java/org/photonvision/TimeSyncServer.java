package org.photonvision;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.photonvision.common.dataflow.structures.Packet;

/**
 * For when mom says that you have NTP at home
 */
public class TimeSyncServer {
    public static final int TS_PORT = 5801;

    private static String teamToBroadcast(int team) {
        // logic from https://github.com/wpilibsuite/allwpilib/blob/a7173dbd3c46eb0602c67c7a3b919801d6e2f23c/ntcore/src/main/native/cpp/ntcore_cpp.cpp#L681
        // 10.TE.AM.255
        return new StringBuilder()
            .append("10.")
            .append((int)(team/100))
            .append(".")
            .append((int)(team%100))
            .append(".255")
            .toString();
    }

    private DatagramSocket socket = null;
    private InetAddress address = null;

    public TimeSyncServer(int team) {
        // This should never throw anyways
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);

            address = InetAddress.getByName(teamToBroadcast(team));

            // hack for testing at home
            address = InetAddress.getByName("192.168.0.255");

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void broadcastTime() {
        var photonPacket = new Packet(Long.BYTES);
        photonPacket.clear();

        // photonPacket.encode(WPIUtilJNI.now());
        photonPacket.encode(System.currentTimeMillis());

        try {
            DatagramPacket packet = new DatagramPacket(photonPacket.getData(), photonPacket.getSize(), address, 4445);
            socket.send(packet);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        var server = new TimeSyncServer(5940);
        while (true) {
            server.broadcastTime();
            Thread.sleep(500);
        }
    }
}
