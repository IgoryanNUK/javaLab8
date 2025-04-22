package app.product;

import java.io.Serializable;

public class Coordinates implements Printable, Serializable {
    private double x;
    private Double y; //Поле не может быть null

    public Coordinates() {}

    public Coordinates(double x, double y) {
        this.x = x;
        this.y = Double.valueOf(y);
    }

    public double getX() {return x;}

    public Double getY() {return y;}

    public void setX(double x) {this.x = x;}

    public void setY(double y) {this.y =y;}

    public String toString() {
        return "Coordinates[x=" + x +
                ", y=" + y +
                "]";
    }

    public String print() {
        return "(" + x + ", " + y + ")";
    }
}
