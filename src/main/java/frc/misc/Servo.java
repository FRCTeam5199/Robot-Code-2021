package frc.misc;

/**
 * A wrapper for the {@link edu.wpi.first.wpilibj.Servo} library
 */
public class Servo {
    private final edu.wpi.first.wpilibj.Servo SERVO;

    public Servo(int channel) {
        SERVO = new edu.wpi.first.wpilibj.Servo(channel);
    }

    /**
     * Moves the servo to a given degree between 0 and 360. Assumes angle is linear with respect to PWM value
     *
     * @param degrees the angle in degrees to move the servo to
     */
    public void moveToAngle(double degrees) {
        SERVO.setAngle(degrees);
    }

    /**
     * Moves the servo to the given position (bounds 0.0 to 1.0)
     *
     * @param position the position between 0.0 and 1.0
     */
    public void moveToPosition(double position) {
        SERVO.setPosition(position);
    }

    /**
     * Where could the servo possibly be?
     *
     * @return the servo's position between 0.0 and 1.0
     */
    public double getPosition() {
        return SERVO.get();
    }

    /**
     * What angle could I possibly be at?
     *
     * @return the servo's angle assuming that the servo angle is linear with respect to the PWM value
     */
    public double getAngle() {
        return SERVO.getAngle();
    }
}
