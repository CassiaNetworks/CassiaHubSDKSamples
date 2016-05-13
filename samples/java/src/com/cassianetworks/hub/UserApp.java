package com.cassianetworks.hub;
import org.apache.thrift.*;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.cassianetworks.hub.sdk.*;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

/**
 * Created by liyi on 3/17/16.
 * author : Sirius
 * All Copyright Reserved By CassiaNetworks
 */
public class UserApp {
    public static final String LOCAL_IP = "192.168.1.100";//本地ip地址
    public static final int    LOCAL_PORT = 9091;//本地端口号

    public void runClient(String ip, int port) throws TException {
        TTransport transport = new TSocket(ip,port,3000);
        TProtocol protocol = new TBinaryProtocol(transport);
        transport.open();
        ControlService.Client client = new ControlService.Client(protocol);
        client.setupNotify(LOCAL_IP, LOCAL_PORT);
        client.startScan("0", 3000);
    }

    public void runServer() throws TException{
        TServerSocket serverTransport = new TServerSocket(LOCAL_PORT);
        TProcessor    processor = new NotificationService.Processor<NotificationService.Iface>(new NotificationServiceImpl());
        TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(processor));
        System.out.println("Start server on port "+ LOCAL_PORT + "......");
        server.serve();
    }

    public Hub discover() {
        String ip = "255.255.255.255"; //广播地址
        int port = 0x8888;  //udp广播端口
        try {
            DatagramSocket clientSocket = new DatagramSocket(); //发送和接收数据报的DatagramSocket对象
            InetAddress address = InetAddress.getByName(ip);
            String sendData = "CASSIA_HUB_DISCOVERY";
            DatagramPacket sendPacket = new DatagramPacket(sendData.getBytes(), sendData.getBytes().length, address, port);
            clientSocket.send(sendPacket); //向hub发送数据报
            byte[] buf = new byte[1024];
            DatagramPacket recvPacket = new DatagramPacket(buf, buf.length);
            clientSocket.receive(recvPacket); //接收hub回复的数据报
            String data = new String(recvPacket.getData(), 0, recvPacket.getLength());
            System.out.println("receive： " + data);
            clientSocket.close();
            String[] segment = data.split("-");
            return new Hub(segment[0], Integer.parseInt(segment[1]));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) throws TException {
        final UserApp app = new UserApp();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    app.runServer();
                } catch (TException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Hub hub = app.discover();
        if (hub == null) {
            System.out.println("Hub not found");
        } else {
            app.runClient(hub.ip, hub.port);
        }
    }

    private class Hub {
        public String ip;
        public int port;
        public Hub(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }
    }
    private class NotificationServiceImpl implements NotificationService.Iface {
        @Override
        public AuthInfo userChallenge() throws TException {
        	System.out.println("user challenge");
            return null;
        }

        @Override
        public void onConnectionStateChange(String chipId, String deviceId, int status) throws TException {

        }

        @Override
        public void onScan(String chipId, Device device, String hexScanRecord, int rssi) throws TException {
            System.out.println(chipId);
            System.out.println(device.getName());
            System.out.println(hexScanRecord);
            System.out.println(rssi + "");
        }

        @Override
        public void onServicesDiscovered(String deviceId, List<GattService> s) throws TException {

        }

        @Override
        public void onNotify(String deviceId, int handle, String hexData) throws TException {

        }

        @Override
        public void onReadByHandle(String deviceId, int handle, String hexData) throws TException {

        }

        @Override
        public void onMessage(String messageKey, String params) throws TException {

        }
    }
}
