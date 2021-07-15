package frc.discordslackbot.commands;

import frc.robot.Robot;
import org.jetbrains.annotations.NotNull;

/**
 * Simple command that works with {@link frc.telemetry.AbstractRobotTelemetry} to get the current location and heading
 * of the robot
 */
public class CurrentTelemetryCommand extends AbstractCommand {
    @Override
    public @NotNull AbstractCommandResponse run(AbstractCommandData message) {
        return new GenericCommandResponse(message, "Currently at **" + Robot.driver.guidance.getLocation() + "** facing " + String.format("%.1f", Robot.driver.guidance.imu.relativeYaw()));
    }

    @Override
    public String getCommand() {
        return "locate";
    }

    @Override
    public String sendHelp() {
        return "Gets the current location and heading of the robot";
    }
}
