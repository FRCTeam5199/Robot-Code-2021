package frc.drive.auton;

public class Point {
    public final double X;
    public final double Y;

    public Point(double x, double y) {
        X = x;
        Y = y;
    }

    public boolean isWithin(double distance, Point otherPoint){
        return distance > Math.sqrt(Math.pow(X - otherPoint.X, 2) + Math.pow(Y - otherPoint.Y, 2));
    }
}
