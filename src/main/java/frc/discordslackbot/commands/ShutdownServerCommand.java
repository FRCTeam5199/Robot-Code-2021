package frc.discordslackbot.commands;

import com.slack.api.bolt.App;
import frc.discordslackbot.SlackBot;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

/**
 * Kills the server, leaves the robot unaffected
 */
public class ShutdownServerCommand extends AbstractCommand {
    @Override
    public @NotNull AbstractCommandResponse run(AbstractCommandData message) {
        return new ShutdownServerCommandResponse(message);
    }

    @Override
    public String getCommand() {
        return "die";
    }

    @Override
    public String sendHelp() {
        return "Kills the server";
    }

    @Override
    public boolean isServerSideCommand() {
        return true;
    }

    /**
     * Extra callback needed so {@link frc.discordslackbot.commands.AbstractCommand.GenericCommandResponse} will not suffice.
     * When replying we must do {@code System.exit(0)}
     */
    public static class ShutdownServerCommandResponse extends AbstractCommandResponse {
        public ShutdownServerCommandResponse(AbstractCommandData data) {
            super(data);
        }

        @Override
        public void doYourWorst(JDA client) {
            client.getTextChannelById(CHANNEL_ID).sendMessage("I died of cringe").queue(
                    msg -> System.exit(0)
            );
        }

        @Override
        public void doYourWorst(App client) {
            SlackBot.sendSlackMessage(CHANNEL_ID, "I died of cringe");
            System.exit(0);
        }

        @Override
        public void doYourWorst() {
            System.out.println("I died of cringe");
            System.exit(0);
        }
    }
}
