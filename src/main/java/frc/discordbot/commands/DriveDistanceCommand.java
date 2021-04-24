package frc.discordbot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import frc.drive.auton.Point;
import frc.misc.ClientSide;
import frc.misc.ServerSide;
import frc.robot.Robot;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

/**
 * This very nice command will make the robot drive! Similar to a point-to-point auton, this command does not steer, it
 * just drives in a straight line and it is up to the user to command turns and remain clear of obstacles
 */
public class DriveDistanceCommand extends AbstractCommand {
    @Override
    public @Nullable AbstractCommandResponse run(AbstractCommandData message) {
        if (message instanceof DriveDistanceCommandData)
            return runChecked((DriveDistanceCommandData) message);
        throw new IllegalArgumentException("I cant use that data");
    }

    /**
     * In order to reduce the ugliness of casting, we chose to make a different copy of {@link #run(AbstractCommandData)}
     *
     * @param message the data associated with this command
     * @return A {@link frc.discordbot.commands.AbstractCommand.GenericCommandResponse} when completed, null otherwise.
     */
    public AbstractCommandResponse runChecked(DriveDistanceCommandData message) {
        if (DriverStation.getInstance().isDisabled()) {
            return new GenericCommandResponse(message, "Im disabled. F. Cannot drive. Urbad");
        } else {
            if (message.startingPoint == null) {
                message.startingPoint = new Point(Robot.driver.guidance.fieldX(), Robot.driver.guidance.fieldY());
            }
            System.out.println("Driving " + message.requestedSpeed);
            Robot.driver.driveMPS(message.requestedSpeed, 0, 0);
            if (!new Point(Robot.driver.guidance.fieldX(), Robot.driver.guidance.fieldY()).isWithin(message.requestedTravel, message.startingPoint)) {
                Robot.driver.driveMPS(0, 0, 0);
                return new GenericCommandResponse(message, "I finnished driving");
            }
        }
        return null;
    }

    @Override
    public String getCommand() {
        return "drive";
    }

    @Override
    public boolean isMultiTickCommand() {
        return true;
    }

    @Override
    public String getArgs() {
        return "<distance in meters> <speed in meters per second>";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"move", "forward"};
    }

    @Override
    public AbstractCommandData extractData(MessageReceivedEvent message) {
        return new DriveDistanceCommandData(message);
    }

    /**
     * Holds onto special data like {@link #startingPoint} and parses {@link #requestedSpeed} and {@link
     * #requestedTravel} from the message
     */
    public static class DriveDistanceCommandData extends AbstractCommandData {
        @ClientSide
        private transient Point startingPoint;
        private double requestedTravel = 1;
        private double requestedSpeed = 1;

        @ServerSide
        protected DriveDistanceCommandData(MessageReceivedEvent message) {
            super(message);
            requestedTravel = CONTENT.split(" ").length > 1 ? Double.parseDouble(CONTENT.split(" ")[1]) : requestedTravel;
            requestedSpeed = CONTENT.split(" ").length > 2 ? Double.parseDouble(CONTENT.split(" ")[2]) : requestedSpeed;
        }
    }
}
