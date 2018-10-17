package GUI;

/**
 * Created by AmirHossein on 11/11/16.
 */
public class Packet {

    String src_ip;
    String des_ip;
    String protocol;

    public Packet (String a, String b, String c){
        this.src_ip = new String (a);
        this.des_ip = new String (b);
        this.protocol = new String (c);
    }
}
