package frc.ballstuff.shooting;

import frc.controllers.ControllerEnums;
import frc.robot.Robot;
import frc.robot.RobotSettings;

import java.util.function.Consumer;

import static frc.robot.Robot.hopper;

/**
 * Contains Multiple different firing modes for the shooter
 */
public enum ShootingEnums {
    //Used when solid speed button is held down
    //TODO make controller dynamic
    FIRE_SOLID_SPEED(shooter -> {
        shooter.setSpeed(4200 * (shooter.joystickController.getPositive(ControllerEnums.JoystickAxis.SLIDER) * 0.25 + 1));
        if (RobotSettings.ENABLE_HOPPER) {
            hopper.setAll(shooter.isAtSpeed() && shooter.joystickController.get(ControllerEnums.JoystickButtons.ONE) == ControllerEnums.ButtonStatus.DOWN);
        }
    }),

    FIRE_TEST_SPEED(shooter -> {
        //shooter.setPercentSpeed(1);
        shooter.setSpeed(4200);
        if (RobotSettings.ENABLE_HOPPER) {
            hopper.setAll(shooter.isAtSpeed());
        }
    }),

    //Used by our current vision tracking
    FIRE_HIGH_SPEED(shooter -> {
        shooter.setSpeed(4200); //* (shooter.joystickController.getPositive(ControllerEnums.JoystickAxis.SLIDER) * 0.25 + 1)
        if (RobotSettings.ENABLE_HOPPER) {
            hopper.setAll((shooter.isAtSpeed() && shooter.isValidTarget()));
        }
    }),
    FIRE_SINGLE_SHOT(shooter -> {
        shooter.setSpeed(4200);
        if (RobotSettings.ENABLE_HOPPER){
            shooter.ticksPassed = (shooter.isAtSpeed() ? Robot.shooter.ticksPassed + 1 : 0);
            if (shooter.ticksPassed >= 50) {
                hopper.setAgitator(true);
            }
            if (!hopper.indexed){
                shooter.singleShot = false;
                hopper.setAgitator(false);
                shooter.ticksPassed = 0;
            }
        }
    });

    public final Consumer<Shooter> function;

    ShootingEnums(Consumer<Shooter> f) {
        function = f;
    }

    public void shoot(Shooter shooter) {
        //System.out.println("Shooting " + this.name());
        this.function.accept(shooter);
    }
}
