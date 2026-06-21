package com.server.network;

import com.common.network.Request;
import com.common.network.Response;
import com.common.util.Deserializer;
import com.common.util.Serializer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public final class UDPServer {
    private final DatagramSocket socket;

    public UDPServer(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }

    public ReceivedRequest receiveRequest() throws IOException, ClassNotFoundException {
        byte[] buffer = new byte[65536];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        socket.receive(packet);

        byte[] data = new byte[packet.getLength()];
        System.arraycopy(packet.getData(), 0, data, 0, packet.getLength());
        Request request = (Request) Deserializer.toObject(data);

        return new ReceivedRequest(request, packet.getAddress(), packet.getPort());
    }

    public void sendResponse(Response response, InetAddress clientAddress, int clientPort) throws IOException {
        byte[] sentData = Serializer.toBytes(response);
        DatagramPacket packet = new DatagramPacket(sentData, sentData.length, clientAddress, clientPort);
        socket.send(packet);
    }

    public record ReceivedRequest(Request request, InetAddress clientAddress, int clientPort) {}
}
