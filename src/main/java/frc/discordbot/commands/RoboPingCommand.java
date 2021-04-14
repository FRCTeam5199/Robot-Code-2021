package frc.discordbot.commands;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class RoboPingCommand extends AbstractCommand {
    @Override
    public boolean isServerSideCommand() {
        return false;
    }

    @Override
    public AbstractCommandResponse run(AbstractCommandData message) {
        return new RoboCommandResponse(message);
    }

    @Override
    public String getCommand() {
        return "roboping";
    }

    @Override
    public String getAliases() {
        return "rp";
    }

    @Override
    public AbstractCommandData extractData(MessageReceivedEvent message) {
        return new RoboPingCommand.RoboCommandData(message);
    }

    public static class RoboCommandData extends AbstractCommandData {
        private final long TIMESTAMP;

        public RoboCommandData(MessageReceivedEvent data) {
            super(data);
            TIMESTAMP = System.currentTimeMillis();
        }
    }

    public static class RoboCommandResponse extends AbstractCommandResponse {
        private final long TIMESTAMP;

        public RoboCommandResponse(AbstractCommandData data) {
            super(data);
            if (data instanceof RoboCommandData)
                TIMESTAMP = ((RoboCommandData) data).TIMESTAMP;
            else
                throw new IllegalArgumentException("How did that command get here?");
        }

        @Override
        public void doYourWorst(JDA client) {
            client.getTextChannelById(CHANNEL_ID).sendMessage(":outbox_tray: checking ping").queue(
                    msg -> msg.editMessage(":inbox_tray: ping is " + (System.currentTimeMillis() - TIMESTAMP) + "ms").submit());
        }
    }
}
