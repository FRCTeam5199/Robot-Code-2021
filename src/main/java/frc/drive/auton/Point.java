package frc.drive.auton;

public class Point {
    public final double X;
    public final double Y;

    public Point(double x, double y) {
        X = x;
        Y = y;
    }

    public boolean isWithin(double distance, Point otherPoint) {
        return distance > getDistanceFromPoint(otherPoint);
    }

    public double getDistanceFromPoint(Point otherPoint) {
        return Math.sqrt(Math.pow(X - otherPoint.X, 2) + Math.pow(Y - otherPoint.Y, 2));
    }
    
    public Point subtract(Point other){
        return new Point(X - other.X, Y - other.Y);
    }

    @Override
    public String toString(){
        return "(" + X + ", " + Y + ") ";
    }
}
