package frc.discordbot.commands;

import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.Nullable;

public class ShutdownServerCommand extends AbstractCommand {
    @Override
    public @Nullable AbstractCommandResponse run(AbstractCommandData message) {
        return new ShutdownServerCommandResponse(message);
    }

    @Override
    public String getCommand() {
        return "die";
    }

    @Override
    public boolean isServerSideCommand() {
        return true;
    }

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
    }
}
