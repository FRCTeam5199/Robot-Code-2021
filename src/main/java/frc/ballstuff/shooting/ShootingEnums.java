package frc.ballstuff.shooting;

import frc.controllers.ControllerEnums;
import frc.robot.Robot;

import java.util.function.Consumer;

import static frc.robot.Robot.hopper;
import static frc.robot.Robot.robotSettings;


/**
 * Contains Multiple different firing modes for the shooter
 */
public enum ShootingEnums {

    //Used when solid speed button is held down
    //TODO make controller dynamic
    FIRE_SOLID_SPEED(shooter -> {
        shooter.setSpeed(4200 * (shooter.joystickController.getPositive(ControllerEnums.JoystickAxis.SLIDER) * 0.25 + 1));
        if (robotSettings.ENABLE_HOPPER) {
            hopper.setAll(shooter.isAtSpeed() && shooter.joystickController.get(ControllerEnums.JoystickButtons.ONE) == ControllerEnums.ButtonStatus.DOWN);
        }
    }),

    FIRE_TEST_SPEED(shooter -> {
        //shooter.setPercentSpeed(1);
        shooter.setSpeed(4200);
        if (robotSettings.ENABLE_HOPPER) {
            hopper.setAll(shooter.isAtSpeed());
        }
    }),

    //Used by our current vision tracking
    FIRE_HIGH_SPEED(shooter -> {
        shooter.setSpeed(4200); //* (shooter.joystickController.getPositive(ControllerEnums.JoystickAxis.SLIDER) * 0.25 + 1)
        if (robotSettings.ENABLE_HOPPER) {
            hopper.setAll((shooter.isAtSpeed() && shooter.isValidTarget()));
        }
    }),

    FIRE_SINGLE_SHOT(shooter -> {
        if (robotSettings.ENABLE_HOPPER) {
            shooter.ticksPassed = (shooter.isAtSpeed() ? Robot.shooter.ticksPassed + 1 : 0);
            if (shooter.ticksPassed >= 50) {
                hopper.setAgitator(true);
            }
            if (!hopper.isIndexed()) {
                shooter.singleShot = false;
                hopper.setAgitator(false);
                shooter.ticksPassed = 0;
            }
        }
        shooter.setSpeed(4200);
    }),

    FIRE_TIMED(shooter -> {
        shooter.setSpeed(4400);
        if (Shooter.DEBUG) {
            System.out.println("Balls shot: " + shooter.ballsShot);
            System.out.println("Ticks passed: " + shooter.ticksPassed);
        }
        if (shooter.getSpeed() >= 4200) {
            if (++shooter.ticksPassed >= 17) {
                hopper.setIndexer(true);
                if (!hopper.isIndexed()) {
                    hopper.setIndexer(false);
                    shooter.ballsShot++;
                    shooter.ticksPassed = 0;
                }
            }
        } else {
            shooter.ticksPassed = 0;
        }
    }),
    FIRE_WITH_NO_REGARD_TO_ACCURACY(shooter -> {
        shooter.setSpeed(4400);
        if (robotSettings.ENABLE_HOPPER) {
            hopper.setAll(shooter.getSpeed() >= 4200);
        }
    }),
    FIRE_WITH_HOPPER_CONTROLLED(shooter -> {
        shooter.setSpeed(4400);
        if (robotSettings.ENABLE_HOPPER) {
            hopper.setIndexer(shooter.getSpeed() >= 4200);
            hopper.setAgitator(!hopper.isIndexed() && shooter.getSpeed() >= 4200);
        }
    });
    public final Consumer<Shooter> function;
    boolean DEBUG = false;

    ShootingEnums(Consumer<Shooter> f) {
        function = f;
    }

    public void shoot(Shooter shooter) {

        if (robotSettings.DEBUG && DEBUG) {
            System.out.println("Shooting " + this.name());
        }
        this.function.accept(shooter);
        shooter.setShooting(true);
    }
}