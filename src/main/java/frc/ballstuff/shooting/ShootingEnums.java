package frc.ballstuff.shooting;

import frc.controllers.ControllerEnums;
import frc.robot.RobotSettings;

import java.util.function.Consumer;

import static frc.robot.Robot.hopper;

/**
 * Contains Multiple different firing modes for the shooter
 */
public enum ShootingEnums {
    //Used when solid speed button is held down
    FIRE_SOLID_SPEED(shooter -> {
        shooter.setSpeed(4200 * (shooter.joystickController.getPositive(ControllerEnums.JoystickAxis.SLIDER) * 0.25 + 1));
        if (RobotSettings.ENABLE_HOPPER) {
            hopper.setAll(shooter.spunUp() && shooter.joystickController.get(ControllerEnums.JoystickButtons.ONE) == ControllerEnums.ButtonStatus.DOWN);
        }
    }),

    //Used by our current vision tracking
    FIRE_HIGH_SPEED(shooter -> {
        //shooter.setSpeed(0);
        if (RobotSettings.ENABLE_VISION) {
            //shooter.setSpeed(shooter.interpolateSpeed());
            shooter.setSpeed(4200); //* (shooter.joystickController.getPositive(ControllerEnums.JoystickAxis.SLIDER) * 0.25 + 1)
        } else {
            shooter.setSpeed(4200); //* (shooter.joystickController.getPositive(ControllerEnums.JoystickAxis.SLIDER) * 0.25 + 1)
        }

        if (RobotSettings.ENABLE_HOPPER) {
            hopper.setAll((shooter.spunUp() || shooter.recovering()) && (shooter.validTarget()));
        }
    }),

    FIRE_HIGH_ACCURACY(shooter -> {
        // boolean visOverride = hopper.visionOverride.getBoolean(false);
        // boolean spinOverride = hopper.spinupOverride.getBoolean(false);
        boolean runDisable = false;//hopper.disableOverride.getBoolean(false);
        shooter.toggle(true);
        if (RobotSettings.ENABLE_HOPPER) hopper.setAll(shooter.atSpeed());
    }),

    FIRE_INDEXER_INDEPENDENT(shooter -> {
        if (shooter.joystickController.get(ControllerEnums.JoystickButtons.ONE) == ControllerEnums.ButtonStatus.DOWN) {
            if (RobotSettings.ENABLE_HOPPER) hopper.setIndexer(shooter.atSpeed() && hopper.indexed);
        }
    }),

    FIRE_TIMED(shooter -> {
        if (shooter.joystickController.get(ControllerEnums.JoystickButtons.ONE) == ControllerEnums.ButtonStatus.DOWN) {
            shooter.shooting = true;
            if (shooter.atSpeed()) {
                shooter.ensureTimerStarted();
                if (shooter.getShootTimer().hasPeriodPassed(0.5)) {
                    if (RobotSettings.ENABLE_HOPPER) hopper.setIndexer(true);
                    //hopper.setAgitator(true);
                }
            } else {
                if (RobotSettings.ENABLE_HOPPER) hopper.setAll(false);
                shooter.resetShootTimer();
            }
        } else {
            shooter.shooting = false;
            if (RobotSettings.ENABLE_HOPPER) hopper.setAll(false);
            shooter.resetShootTimer();
        }
    }),

    FIRE_MIXED(shooter -> {
        shooter.shooting = shooter.joystickController.get(ControllerEnums.JoystickButtons.ONE) == ControllerEnums.ButtonStatus.DOWN;
        if (shooter.shooting) {
            if (shooter.atSpeed() && (!RobotSettings.ENABLE_HOPPER || hopper.indexed)) {
                shooter.ensureTimerStarted();
                if (shooter.getShootTimer().hasPeriodPassed(0.1)) {
                    hopper.setIndexer(true);
                    //hopper.setAgitator(true);
                }
            } else {
                if (RobotSettings.ENABLE_HOPPER) hopper.setAll(false);
                shooter.resetShootTimer();
            }
        } else {
            if (RobotSettings.ENABLE_HOPPER) hopper.setAll(false);
            shooter.resetShootTimer();
        }
    });

    public final Consumer<Shooter> function;

    ShootingEnums(Consumer<Shooter> f) {
        function = f;
    }

    public void shoot(Shooter shooter) {
        this.function.accept(shooter);
    }
}
