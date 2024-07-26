/*
 * MIT License
 *
 * Copyright (c) PhotonVision
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.photonvision;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import org.photonvision.common.dataflow.structures.Packet;

/** For when mom says that you have NTP at home */
public class TimeSyncServer {
    public static final int TS_PORT = 5801;

    private static String teamToBroadcast(int team) {
        // logic from
        // https://github.com/wpilibsuite/allwpilib/blob/a7173dbd3c46eb0602c67c7a3b919801d6e2f23c/ntcore/src/main/native/cpp/ntcore_cpp.cpp#L681
        // 10.TE.AM.255
        return new StringBuilder()
                .append("10.")
                .append((int) (team / 100))
                .append(".")
                .append((int) (team % 100))
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
            DatagramPacket packet =
                    new DatagramPacket(photonPacket.getData(), photonPacket.getSize(), address, 4445);
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
