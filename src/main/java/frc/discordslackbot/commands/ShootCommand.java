package frc.discordslackbot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.Robot;
import org.jetbrains.annotations.NotNull;

/**
 * Shoots a single ball with no regard to safety because screw that. Uses {@link frc.discordslackbot.commands.AbstractCommand.GenericCommandResponse}
 * and {@link frc.discordslackbot.commands.AbstractCommand.GenericCommandData} for comms
 */
public class ShootCommand extends AbstractCommand {
    @Override
    public @NotNull AbstractCommandResponse run(AbstractCommandData message) {
        if (!Robot.robotSettings.ENABLE_SHOOTER) {
            return new GenericCommandResponse(message, "Please enable shooter");
        }
        if (!DriverStation.getInstance().isDisabled() && Robot.shooter.fireAmount(1)) {
            return new GenericCommandResponse(message, "BOOOM :boom:");
        }
        return ContinuePersistingCommandResponse.PASS;
    }

    @Override
    public String getCommand() {
        return "shoot";
    }

    @Override
    public String sendHelp() {
        return "Shoots one ball from the shooter";
    }

    @Override
    public boolean isMultiTickCommand() {
        return true;
    }
}
