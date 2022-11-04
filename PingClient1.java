import java.io.*;
import java.net.*;
import java.util.*;

public class PingClient1 {
    private static final int TIMEOUT = 1000; // milliseconds
    private static final int MAX_PING_REQUEST = 10; 
    private static final int CLIENT_PORT = 5000;
    private static InetAddress serverHost = null;
    private static int serverPort = 0;
    private static DatagramSocket socket = null;

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.out.println("Required arguments: host port");
            return;
        }

       
        serverHost = InetAddress.getByName(args[0]);
        serverPort = Integer.parseInt(args[1]);

        socket = new DatagramSocket(CLIENT_PORT);
        socket.setSoTimeout(TIMEOUT);

        int sequence_number = -1;

        
        Long[] delay = new Long[MAX_PING_REQUEST];

        while (++sequence_number < MAX_PING_REQUEST) {
        
            DatagramPacket response = new DatagramPacket(new byte[1024], 1024);

           
            Date date = new Date();
            long timestamp = date.getTime();

           
            String sendMessage = "PING " + sequence_number + " " + timestamp + " \r\n";

            
            byte[] buffer = new byte[1024];
            buffer = sendMessage.getBytes();

           
            DatagramPacket pingRequest = new DatagramPacket(buffer, buffer.length, serverHost, serverPort);
            socket.send(pingRequest);

           
            try {
               
                socket.receive(response);

               
                date = new Date();
                long delayReceived = date.getTime() - timestamp;
                
                
                delay[sequence_number] = delayReceived;

                System.out.print("Delay " + delayReceived + " ms: ");
                printData(response);
            }
            catch (SocketTimeoutException e) {
                System.out.print("Pacote perdido: " + sendMessage);
                delay[sequence_number] = Long.valueOf(TIMEOUT);
            }
        }

       
        roundTripTime(delay);
        
    }

   
    private static void printData(DatagramPacket request) throws Exception {

      
        byte[] buf = request.getData();

        
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);

        InputStreamReader isr = new InputStreamReader(bais);

        
        BufferedReader br = new BufferedReader(isr);

       
        String line = br.readLine();

        
        System.out.println("Received from " + request.getAddress().getHostAddress() + ": " + new String(line));
    }

    private static void roundTripTime(Long[] delay) {

        long minDelay = delay[0];
        long maxDelay = delay[0]; 
        long averageDelay = 0;

        for (int i = 0; i < delay.length; i++) {
            long d = delay[i];
            if (d < minDelay) {
                minDelay = d;
            }

            if (d > maxDelay) {
                maxDelay = d;
            }

            averageDelay += d;
        }

        averageDelay /= delay.length;

        System.out.println("RTT: minDelay: " + minDelay + "ms / maxDelay: " + maxDelay + "ms / averageDelay: " + averageDelay + "ms");

    }
}