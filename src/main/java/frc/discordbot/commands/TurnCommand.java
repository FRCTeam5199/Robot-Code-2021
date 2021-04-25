package frc.discordbot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import frc.misc.ClientSide;
import frc.misc.ServerSide;
import frc.robot.Robot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

/**
 * This very nice command will make the robot drive! Similar to a point-to-point auton, this command does not steer, it
 * just drives in a straight line and it is up to the user to command turns and remain clear of obstacles
 */
public class TurnCommand extends AbstractCommand {
    @Override
    public @Nullable AbstractCommandResponse run(AbstractCommandData message) {
        if (message instanceof TurnCommandData)
            return runChecked((TurnCommandData) message);
        throw new IllegalArgumentException("I cant use that data");
    }

    /**
     * In order to reduce the ugliness of casting, we chose to make a different copy of {@link
     * #run(AbstractCommandData)}
     *
     * @param message the data associated with this command
     * @return A {@link frc.discordbot.commands.AbstractCommand.GenericCommandResponse} when completed, null otherwise.
     */
    public AbstractCommandResponse runChecked(TurnCommandData message) {
        if (DriverStation.getInstance().isDisabled()) {
            return new GenericCommandResponse(message, "Im disabled. F. Cannot drive. Urbad");
        } else {
            if (message.startingPoint == null) {
                message.reInit();
            }
            //System.out.println(message.startingPoint + ", " + message.requestedTurn + ", " + Robot.driver.guidance.imu.relativeYaw());
            //todo hone this
            Robot.driver.driveMPS(0, 0, (message.startingPoint + message.requestedTurn - Robot.driver.guidance.imu.relativeYaw() > 0 ? 1 : -1) * Math.min(Math.abs(message.startingPoint + message.requestedTurn - Robot.driver.guidance.imu.relativeYaw()) / 10, 5));
            if (Math.abs(message.startingPoint + message.requestedTurn - Robot.driver.guidance.imu.relativeYaw()) < 1) {
                Robot.driver.driveMPS(0, 0, 0);
                return new GenericCommandResponse(message, String.format("I turned from %.1f to %.1f", message.startingPoint, Robot.driver.guidance.imu.relativeYaw()));
            }
        }
        return null;
    }

    @Override
    public String getCommand() {
        return "turn";
    }

    @Override
    public String sendHelp() {
        return "Turns the robot to a new heading. CCW is positive, CW is negative because i said so. Turns the passed # of degrees, does **NOT** set heading to passed number";
    }

    @Override
    public boolean isMultiTickCommand() {
        return true;
    }

    @Override
    public String getArgs() {
        return "<distance in degrees>";
    }

    @Override
    public AbstractCommandData extractData(MessageReceivedEvent message) {
        return new TurnCommandData(message);
    }

    /**
     * Holds onto special data like {@link #startingPoint} and parses {@link #requestedSpeed} and {@link #requestedTurn}
     * from the message
     */
    public static class TurnCommandData extends AbstractCommandData {
        @ClientSide
        private transient Double startingPoint;
        private transient double requestedTurn = 1;
        private transient double requestedSpeed = 1;

        @ServerSide
        protected TurnCommandData(MessageReceivedEvent message) {
            super(message);
        }

        private void reInit() {
            requestedTurn = CONTENT.split(" ").length > 1 ? Double.parseDouble(CONTENT.split(" ")[1]) : requestedTurn;
            requestedSpeed = CONTENT.split(" ").length > 2 ? Double.parseDouble(CONTENT.split(" ")[2]) : requestedSpeed;
            startingPoint = Robot.driver.guidance.imu.relativeYaw();
        }
    }
}

