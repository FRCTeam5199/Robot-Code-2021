package frc.discordbot.commands;

import frc.drive.DriveManagerStandard;
import frc.drive.DriveManagerSwerve;
import frc.drive.auton.Point;
import frc.misc.ClientSide;
import frc.misc.ServerSide;
import frc.robot.Robot;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.Nullable;

public class DriveDistanceCommand extends AbstractCommand {
    @Override
    public boolean isServerSideCommand() {
        return false;
    }

    @Override
    public @Nullable AbstractCommandResponse run(AbstractCommandData message) {
        if (message instanceof DriveDistanceCommandData)
            return runChecked((DriveDistanceCommandData) message);
        throw new IllegalArgumentException("I cant use that data");
    }

    public AbstractCommandResponse runChecked(DriveDistanceCommandData message){
        if (message.startingPoint == null){
            message.startingPoint = new Point(Robot.driver.guidance.fieldX(),Robot.driver.guidance.fieldY());
        }
        Robot.driver.driveMPS(message.requestedSpeed, 0, 0);
        if (!new Point(Robot.driver.guidance.fieldX(),Robot.driver.guidance.fieldY()).isWithin(message.requestedTravel, message.startingPoint)) {
            Robot.driver.driveMPS(0,0,0);
            return new DriveDistanceCommandResponse(message);
        }
        return null;
    }

    @Override
    public String getCommand() {
        return "drive";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"move","forward"};
    }

    @Override
    public AbstractCommandData extractData(MessageReceivedEvent message) {
        return new DriveDistanceCommandData(message);
    }

    public static class DriveDistanceCommandData extends AbstractCommandData {
        @ClientSide
        private Point startingPoint;
        private double requestedTravel = 1;
        private double requestedSpeed = 1;

        @ServerSide
        protected DriveDistanceCommandData(MessageReceivedEvent message) {
            super(message);
            requestedTravel = Double.parseDouble(CONTENT.split(" ")[1]);
            requestedSpeed = Double.parseDouble(CONTENT.split(" ")[2]);
        }

        @Override
        public boolean isMultiTickCommand() {
            return true;
        }
    }

    public static class DriveDistanceCommandResponse extends AbstractCommandResponse {
        protected DriveDistanceCommandResponse(AbstractCommandData originalData) {
            super(originalData);
        }

        @Override
        public void doYourWorst(JDA client) {
            client.getTextChannelById(CHANNEL_ID).sendMessage("Awaited and replied").submit();
        }
    }
}
