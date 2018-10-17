package GUI;

/**
 * Created by AmirHossein on 11/11/16.
 */
public class Myline {

    public int weight = 1;

    double src_x,src_y;
    double des_x,des_y;

    boolean isRight = true;

    public String des_ip = new String();
    public String src_ip = new String();

    public Myline(double a,double b,double c,double d){
        this.src_x = a;
        this.src_y = b;
        this.des_x = c;
        this.des_y = d;
    }

    boolean isThisLine(double x1,double y1,double x2, double y2){

        if(this.src_x == x1 && this.src_y == y1 && this.des_x == x2 && this.des_y == y2)
            return true;
        return false;
    }

    boolean isOppositeLine(double x1,double y1,double x2, double y2){

        if(this.src_x == x2&& this.src_y == y2 && this.des_x == x1 && this.des_y == y1)
            return true;
        return false;
    }

    public void Update(double x1,double y1,double x2, double y2){
        this.src_x = x1;
        this.src_y = y1;
        this.des_x = x2;
        this.des_y = y2;
    }

}
