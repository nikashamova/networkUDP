import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Main {
    private static final Map<InetAddress, Long> MAP = new HashMap<>();

    public static void main(String args[]) {
        try {

            //configure main information
            InetAddress address = InetAddress.getByName("255.255.255.255");
            int port = 7777;
            byte buf[] = new byte[1024];
            DatagramSocket socket = new DatagramSocket(port);
            socket.setBroadcast(true);
            socket.setSoTimeout(4500);
            Long lastSendingTime = System.currentTimeMillis();

            //configure datagram sending packet
            DatagramPacket sendingPacket = new DatagramPacket("Message is here!".getBytes(), 16);
            sendingPacket.setAddress(address);
            sendingPacket.setPort(port);


            while(true) {
                //if time limit is achieved then sending message again
                if (System.currentTimeMillis() - lastSendingTime > 3000) {
                    socket.send(sendingPacket);
                    lastSendingTime = System.currentTimeMillis();
                }

                //receive Packet
                DatagramPacket receivingPacket = null;
                try {
                    receivingPacket = new DatagramPacket(buf, 1024);
                    socket.receive(receivingPacket);
                } catch (SocketTimeoutException e) {
                    update();
                    continue;
                }

                //update connections info: changing time
                if (!MAP.containsKey(receivingPacket.getAddress())) {
                    System.out.println("Connected " + receivingPacket.getAddress());
                    System.out.println("Total " + (MAP.size() + 1));
                }
                MAP.put(receivingPacket.getAddress(), System.currentTimeMillis());

                update();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //update connections info: removing not available connections
    private static void update() {
        Iterator<Map.Entry<InetAddress, Long>> it = MAP.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry <InetAddress, Long> entry = it.next();
            if (System.currentTimeMillis() - entry.getValue() > 5000) {
                it.remove();
                System.out.println("Disconnected" + entry.getKey() + "Total " + MAP.size());
            }
        }

    }
}
