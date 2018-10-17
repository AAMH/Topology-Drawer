package GUI;

import java.util.ArrayList;

public class DockerNode{


    ArrayList<Packet> packets = new ArrayList<Packet>();
    String IP = new String (" ");
    double x = 0,y = 0;


    public DockerNode(String i){
        this.IP = new String (i);
    }

    public void addPacket(String src, String des, String pro){
        this.packets.add(new Packet(src,des,pro));
    }

    public String getIP(){
        return this.IP;
    }

    public String convertedIP(){

        long ip = Long.parseLong(this.getIP());
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

}
