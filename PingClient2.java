import java.io.*;
import java.net.*;
import java.util.*;

public class PingClient2 {
    private static final int TIMEOUT = 1000; 
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

      
        Timer timer = new Timer();
        RemindTask remindTask = new RemindTask(MAX_PING_REQUEST, socket, serverHost, serverPort);
        timer.schedule(remindTask, 0, 1000);
    }
    
    
    public static void ping(DatagramSocket socket, int sequence_number, InetAddress serverHost, int serverPort) {
        
        DatagramPacket response = new DatagramPacket(new byte[1024], 1024);

        
        Date date = new Date();
        long timestamp = date.getTime();

        
        String sendMessage = "PING " + sequence_number + " " + timestamp + " \r\n";

        
        byte[] buffer = new byte[1024];
        buffer = sendMessage.getBytes();

        
        DatagramPacket pingRequest = new DatagramPacket(buffer, buffer.length, serverHost, serverPort);

        try {
            socket.send(pingRequest);

          
            socket.receive(response);

            
            date = new Date();
            long delayReceived = date.getTime() - timestamp;

            System.out.print("Delay " + delayReceived + " ms: ");
            printData(response);
        } catch (SocketTimeoutException e) {
            System.out.print("Pacote perdido: " + sendMessage);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   
    private static void printData(DatagramPacket request) throws Exception {

        byte[] buf = request.getData();

      
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);

        
        InputStreamReader isr = new InputStreamReader(bais);

      
        BufferedReader br = new BufferedReader(isr);

        
        String line = br.readLine();

     
        System.out.println("Received from " + request.getAddress().getHostAddress() + ": " + new String(line));
    }
}

class RemindTask extends TimerTask {
    private int maxPingRequests;
    private int times = -1;
    private DatagramSocket socket;
    private InetAddress serverHost;
    private int serverPort;
    

    public RemindTask(int maxPingRequests, DatagramSocket socket, InetAddress serverHost, int serverPort) {
        this.maxPingRequests = maxPingRequests;
        this.socket = socket;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void run() {
        if (++this.times < this.maxPingRequests) {
            PingClient2.ping(this.socket, this.times, this.serverHost, this.serverPort);
        } else {
            System.exit(0);
        }
    }
}