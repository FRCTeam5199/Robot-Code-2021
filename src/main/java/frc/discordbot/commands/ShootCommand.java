package frc.discordbot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.Robot;
import org.jetbrains.annotations.Nullable;

public class ShootCommand extends AbstractCommand {
    @Override
    public @Nullable AbstractCommandResponse run(AbstractCommandData message) {
        if (!Robot.robotSettings.ENABLE_SHOOTER) {
            return new GenericCommandResponse(message, "Please enable shooter");
        }
        if (!DriverStation.getInstance().isDisabled() && Robot.shooter.fireSingleShot()) {
            return new GenericCommandResponse(message, "BOOOM :boom:");
        }
        return null;
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
