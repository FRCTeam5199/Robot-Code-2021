package frc.discordbot.commands;

import frc.drive.DriveManagerStandard;
import frc.drive.DriveManagerSwerve;
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
        double distTravelled = ((DriveDistanceThenReplyData) message).distanceTravelled;
        switch (Robot.robotSettings.DRIVE_BASE) {
            case SWIVEL:
                distTravelled = ((DriveManagerSwerve) Robot.driver).driverFR.driver.getRotations();
                Robot.driver.driveMPS(Integer.parseInt(message.CONTENT.split(" ")[2]), Integer.parseInt(message.CONTENT.split(" ")[2]), 0);
                break;
            case STANDARD:
                distTravelled = ((DriveManagerStandard) Robot.driver).leaderL.getRotations();
                ((DriveManagerStandard) Robot.driver).driveFPS(Double.parseDouble(message.CONTENT.split(" ")[2]), Double.parseDouble(message.CONTENT.split(" ")[2]));
                break;
        }
        if (distTravelled >= Double.parseDouble(message.CONTENT.split(" ")[1])) {
            Robot.driver.driveMPS(0,0,0);
            Robot.driver.resetDriveEncoders();
            return new DriveDistanceThenReplyResponse(message);
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
        return new DriveDistanceThenReplyData(message);
    }

    public static class DriveDistanceThenReplyData extends AbstractCommandData {
        private double distanceTravelled = 0;

        protected DriveDistanceThenReplyData(MessageReceivedEvent message) {
            super(message);
            if (Robot.robotSettings.ENABLE_DRIVE && Robot.robotSettings.ENABLE_IMU) {

            } else {

            }
        }

        @Override
        public boolean isMultiTickCommand() {
            return true;
        }
    }

    public static class DriveDistanceThenReplyResponse extends AbstractCommandResponse {
        protected DriveDistanceThenReplyResponse(AbstractCommandData originalData) {
            super(originalData);
        }

        @Override
        public void doYourWorst(JDA client) {
            client.getTextChannelById(CHANNEL_ID).sendMessage("Awaited and replied").submit();
        }
    }
}
