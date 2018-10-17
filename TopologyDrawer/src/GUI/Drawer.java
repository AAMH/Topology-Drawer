package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by AmirHossein on 11/9/16.
 */
public class Drawer {

    private static final Integer WIDTH = 800;
    private static final Integer HEIGHT = 700;

    public static int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
    public static int SCREEN_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

    double pi = 3.14159265358979323846;
    JFrame frame;
    ArrayList<MyPanel> panels = new ArrayList<MyPanel>();
    JTabbedPane jtp;

    int Radius = 200;
    int NodeRadius = 70;

    double [][] NodePositions;


    public Drawer() {

        DataReceiver reciever = new DataReceiver(this);

        jtp = new JTabbedPane();

        frame = new JFrame();
        frame.setLocation(SCREEN_WIDTH / 2 - WIDTH / 2, SCREEN_HEIGHT / 2 - HEIGHT / 2);
        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        //frame.setLayout(null);

        frame.getContentPane().add(jtp);
        frame.getContentPane().revalidate();

    }

    public void addPanel(String pro){

        panels.add(new MyPanel(pro));

        panels.get(panels.size() - 1).setSize(WIDTH - 100, HEIGHT - 100);
        panels.get(panels.size() - 1).setLocation(50, 50);
        panels.get(panels.size() - 1).setBackground(Color.WHITE);

        jtp.addTab(pro, panels.get(panels.size() - 1));

    }

    public static void main(String s[]) {

        Drawer D = new Drawer();

    }

    DockerNode returnNode(String ip, String pr){

        Vector<DockerNode> ConnectedClients = getPanel(pr).ConnectedClients;
        for(int i = 0;i <= ConnectedClients.size() - 1;i++){
            if(ConnectedClients.get(i).getIP().equals(ip)) {
                return ConnectedClients.get(i);
            }
        }
        return null;
    }

    void isSeenBefore(Myline line,String pr){

        ArrayList<Myline> lines = getPanel(pr).lines;
        for(int j = 0; j<= lines.size() -1;j++){
            if(lines.get(j).isOppositeLine(line.src_x, line.src_y, line.des_x, line.des_y))
                line.isRight = false;
        }

    }

    public void updateLines(Packet pack){

        boolean updated = false;
        ArrayList<Myline> lines = getPanel(pack.protocol).lines;

        for(int j = 0; j<= lines.size() -1;j++)
            if(lines.get(j).isThisLine(returnNode(pack.src_ip,pack.protocol).x,returnNode(pack.src_ip,pack.protocol).y , returnNode(pack.des_ip,pack.protocol).x,returnNode(pack.des_ip,pack.protocol).y)){
                lines.get(j).weight++;
                updated = true;
                break;
            }

        if(!updated) {
            Myline l = new Myline(returnNode(pack.src_ip,pack.protocol).x, returnNode(pack.src_ip,pack.protocol).y, returnNode(pack.des_ip,pack.protocol).x, returnNode(pack.des_ip,pack.protocol).y);
            l.des_ip = pack.des_ip;
            l.src_ip = pack.src_ip;
            isSeenBefore(l,pack.protocol);
            lines.add(l);
        }

    }

    MyPanel getPanel(String s){

        for(int i = 0;i< panels.size();i++){
            if(panels.get(i).Protocol.equals(s))
                return panels.get(i);
        }
        return null;
    }

    public void AddThisClient(String ip, String pr) {

        boolean exists = false;

        for (int i = 0; i <= getPanel(pr).ConnectedClients.size() - 1; i++) {
            if (getPanel(pr).ConnectedClients.get(i).getIP().equals(ip)) {
                exists = true;
                break;
            }
            else {
                exists = false;
            }
        }
        if(!exists){
            getPanel(pr).ConnectedClients.add(new DockerNode(ip));
            getPanel(pr).NumberOfNodes++;
            getPanel(pr).CalculateNodePositions();
            System.out.println(ip + " Added");
        }

    }

    public void AddThisPacket(String desip, String pr,String lastsourceip){

        for (int i = 0; i <=  getPanel(pr).ConnectedClients.size() - 1; i++) {
            if ( getPanel(pr).ConnectedClients.get(i).getIP().equals(lastsourceip)) {
                getPanel(pr).ConnectedClients.get(i).addPacket(lastsourceip, desip, pr);
                updateLines(getPanel(pr).ConnectedClients.get(i).packets.get(getPanel(pr).ConnectedClients.get(i).packets.size() - 1));
                //       System.out.println("packet added src: " + lastSeenSourceIP + " des: " + desIP + " Number of packets for this src =" + ConnectedClients.get(i).packets.size());
            }
        }

    }

    private class MyPanel extends JPanel implements Runnable{

        BufferedImage img = null;
        boolean drawMonitoringString = true;
        String Protocol = new String();
        public int NumberOfNodes = 0;

        public Vector<DockerNode> ConnectedClients = new Vector<DockerNode>();
        public ArrayList<Myline> lines = new ArrayList<Myline>();

        public MyPanel(String pro){

            super();
            this.Protocol = new String(pro);

            CalculateNodePositions();
            (new Thread(this)).start();

            try {
                img = ImageIO.read(new File("./a.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        public void CalculateNodePositions() {

            NodePositions = new double[NumberOfNodes][2];
            double temp = (2 * pi) / NumberOfNodes;

            for (int i = 0; i <= NumberOfNodes - 1; i++) {
                NodePositions[i][0] = 300 + Radius * Math.cos(i * temp - pi / 2);
                NodePositions[i][1] = 250 + Radius * Math.sin(i * temp - pi / 2);

                ConnectedClients.get(i).x = NodePositions[i][0];
                ConnectedClients.get(i).y = NodePositions[i][1];

                for (int j = 0; j <= lines.size() - 1; j++) {
                    if (lines.get(j).src_ip.equals(ConnectedClients.get(i).getIP())) {
                        lines.get(j).Update(NodePositions[i][0], NodePositions[i][1], lines.get(j).des_x, lines.get(j).des_y);
                    }
                    if (lines.get(j).des_ip.equals(ConnectedClients.get(i).getIP())) {
                        lines.get(j).Update(lines.get(j).src_x, lines.get(j).src_y, NodePositions[i][0], NodePositions[i][1]);
                    }
                }
            }

        }

        private Color setColor(int weight) {

            if (weight > 0 && weight <= 100)
                return Color.GRAY;
            if (weight > 100 && weight <= 200)
                return Color.PINK;
            if (weight > 200 && weight <= 300)
                return Color.GREEN;
            if (weight > 300 && weight <= 400)
                return Color.BLUE;
            if (weight > 400 && weight <= 500)
                return Color.CYAN;
            if (weight > 500 && weight <= 600)
                return Color.YELLOW;
            if (weight > 600 && weight <= 700)
                return Color.ORANGE;
            if (weight > 700)
                return Color.RED;
            return Color.WHITE;

        }

        @Override
        public synchronized void paint(Graphics g) {

            super.paint(g);
            g.setFont(new Font("Courier New", Font.BOLD, 17));
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));

            if(drawMonitoringString)
                g.drawString("Monitoring : ON",10,30);

            for(int i = 0; i<= NumberOfNodes - 1;i++){
            //    System.out.println("a:  "+  NodePositions[i][0]+"  b:  "+ NodePositions[i][1]+"      "+WIDTH);
            //    g.setColor(Color.GREEN);
            //    g2.fillOval((int) DataReceiver.ConnectedClients.get(i).x, (int) DataReceiver.ConnectedClients.get(i).y, NodeRadius, NodeRadius);
                g2.drawImage(img, (int) ConnectedClients.get(i).x - 10, (int) ConnectedClients.get(i).y, null);
                g.drawString(ConnectedClients.get(i).convertedIP(),(int) ConnectedClients.get(i).x,(int) ConnectedClients.get(i).y);
            }

            for(int i = 0;i <= lines.size() - 1;i++){
                g.setColor(this.setColor(lines.get(i).weight));
                if(lines.get(i).isRight) {
                    g2.fillOval((int) lines.get(i).src_x + NodeRadius / 2 + 3, (int) lines.get(i).src_y + NodeRadius / 2 -5,10,10);
                    g2.drawLine((int) lines.get(i).src_x + NodeRadius / 2 + 10, (int) lines.get(i).src_y + NodeRadius / 2, (int) lines.get(i).des_x + NodeRadius / 2 + 10, (int) lines.get(i).des_y + NodeRadius / 2);
                    g.drawString(Integer.toString(lines.get(i).weight), (int) (lines.get(i).src_x + lines.get(i).des_x) / 2 + 40, (int) (lines.get(i).src_y + lines.get(i).des_y) / 2 + 20);
                }
                    else {
                    g2.fillOval((int) lines.get(i).src_x + NodeRadius / 2 - 12, (int) lines.get(i).src_y + NodeRadius / 2 - 15,10,10);
                    g2.drawLine((int) lines.get(i).src_x + NodeRadius / 2 - 10, (int) lines.get(i).src_y + NodeRadius / 2 - 15, (int) lines.get(i).des_x + NodeRadius / 2 - 10, (int) lines.get(i).des_y + NodeRadius / 2 - 15);
                    g.drawString(Integer.toString(lines.get(i).weight), (int) (lines.get(i).src_x + lines.get(i).des_x) / 2 , (int) (lines.get(i).src_y + lines.get(i).des_y) / 2 + 50);
                }
            }

        }

        @Override
        public void run() {

            while(true){
                try {
                    this.repaint();
                    Thread.sleep(300);
                    drawMonitoringString = !drawMonitoringString;

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }

}


