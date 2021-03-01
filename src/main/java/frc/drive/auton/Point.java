package frc.drive.auton;

/**
 * A utility class that helps keep the x and the y from being lost
 */
public class Point {
    public final double X;
    public final double Y;

    public Point(double x, double y) {
        X = x;
        Y = y;
    }

    /**
     * Use this to determine if two points are within a tolerance while keeping code clean
     *
     * @param distance   max tolerance between this and another point
     * @param otherPoint the other point to compare distance to
     * @return whether the distance between this and the other point is less than the provided tolerance
     */
    public boolean isWithin(double distance, Point otherPoint) {
        return distance > getDistanceFromPoint(otherPoint);
    }

    /**
     * Find the distance between this and another point
     *
     * @param otherPoint the other point to measure from
     * @return the distance between this point and the passed point
     */
    public double getDistanceFromPoint(Point otherPoint) {
        return Math.sqrt(Math.pow(X - otherPoint.X, 2) + Math.pow(Y - otherPoint.Y, 2));
    }

    public boolean isWithinEllipse(double distx, double disty, Point otherPoint) {
        return Math.pow(otherPoint.X - X, 2) / Math.pow(distx, 2) + Math.pow(otherPoint.Y - Y, 2) / Math.pow(disty, 2) <= 1;
    }

    /**
     * its just like subtracting numbers but all wrapped for maximum onelining capabilities
     *
     * @param other the point to subtract from this point
     * @return these coordinates minus the provided coordinates wrapped in a new point
     */
    public Point subtract(Point other) {
        return new Point(X - other.X, Y - other.Y);
    }

    /**
     * This method means you can just System.out.println(point) without having to format it Result format "(x, y) "
     *
     * @return these coordinates formatted in a readable string
     */
    @Override
    public String toString() {
        return "(" + X + ", " + Y + ") ";
    }
}
