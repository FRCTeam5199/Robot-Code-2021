package frc.motors;

/**
 * This should be one-for-one replicated for each {@link AbstractMotorController motor controller} in order to create
 * settings to switch between motor implementations
 */
public enum SupportedMotors {
    CAN_SPARK_MAX(5676), TALON_FX(6380), VICTOR(0);

    public final int MAX_SPEED_RPM;

    SupportedMotors(int speed) {
        MAX_SPEED_RPM = speed;
    }
}
