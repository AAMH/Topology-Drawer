package GUI;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by AmirHossein on 11/10/16.
 */
public class DataReceiver implements Runnable{

    File logFile;

    boolean bb = false;

    String lastSeenSourceIP = new String();
    String lastSeenProtocol = new String("0");

    Drawer parent;

    public static ArrayList<Packet> allPackets = new ArrayList<Packet>();
    ArrayList<String> allProtocols = new ArrayList<String>();

    public DataReceiver(Drawer d){

        this.parent = d;

        logFile = new File("/users/amirhossein/netconsole2.log");

        (new Thread(this)).start();

    }

    public String convertIP(String ipp){

        long ip = Long.parseLong(ipp);
        StringBuilder result = new StringBuilder(15);
        for (int i = 0; i < 4; i++) {
            result.insert(0, Long.toString(ip & 0xff));
            if (i < 3) {
                result.insert(0, '.');
            }
            ip = ip >> 8;
        }
        String iPCounterNumbers = result.toString();
        String[] iPNumbers = iPCounterNumbers.split("\\.");
        String iPAddress = iPNumbers[3] + "." + iPNumbers[2] + "." + iPNumbers[1] + "." + iPNumbers[0];

        return iPAddress;
    }

    public void processLine(String line){

        if (line.contains("Protocol")) {
            String[] proto = line.split("Protocol: ");
            lastSeenProtocol = proto[1].split(" ")[0];
            if(!allProtocols.contains(proto[1].split(" ")[0])) {
                lastSeenProtocol = proto[1].split(" ")[0];
                allProtocols.add(new String(lastSeenProtocol));
                System.out.println("proto -> "+ lastSeenProtocol);
                this.parent.addPanel(lastSeenProtocol);
            }
        }

        if (line.contains("Source")) {
            String[] sourceIP = line.split("IP: ");
            bb = true;
           // System.out.println(sourceIP[0].split(" "));
            if(this.convertIP(sourceIP[1].split(" ")[0]).contains("172.17"))
                addNode(sourceIP[1].split(" ")[0]);
        }
        if (line.contains("Dest")) {
            String[] destIP = line.split("IP: ");
            bb = false;
            if(this.convertIP(destIP[1].split(" ")[0]).contains("172.17")) {
                addNode(destIP[1].split(" ")[0]);
                addPacket(destIP[1].split(" ")[0]);
            }
        }

    }

    void addNode(String ip){

        if(true) {
            if (bb)
                lastSeenSourceIP = ip;
            this.parent.AddThisClient(ip, lastSeenProtocol);

        }

    }

    void addPacket(String desIP){

        if(!desIP.equals(lastSeenSourceIP)) {
            this.parent.AddThisPacket(desIP,lastSeenProtocol,lastSeenSourceIP);
            allPackets.add(new Packet(lastSeenSourceIP, desIP, lastSeenProtocol));
        }

    }

    @Override
    public void run() {

        try {

            BufferedReader sc = new BufferedReader(new FileReader(logFile));

            String s = new String(" ");
            while (s != null) {
                s = sc.readLine();
                if(s != null) processLine(s);
            }

            while (true) {

                String line = sc.readLine();
                if(line == null){
                    Thread.sleep(1000);
               //     System.out.println("waiting");

                }
                else {
                //    System.out.println("something was added -> updated  "+line);
                    processLine(line);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}