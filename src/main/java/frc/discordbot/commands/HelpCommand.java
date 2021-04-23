package frc.discordbot.commands;

import frc.discordbot.MessageHandler;
import frc.misc.ServerSide;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.Nullable;

/**
 * Simple command to list all the commands, their arguments, and where they run
 */
@ServerSide
public class HelpCommand extends AbstractCommand {
    @Override
    public @Nullable AbstractCommandResponse run(AbstractCommandData message) {
        return new HelpCommandResponse(message);
    }

    @Override
    public String getCommand() {
        return "help";
    }

    @Override
    public boolean isServerSideCommand() {
        return true;
    }

    /**
     * Manufactues and embed and does all of the heavy lifting from here
     */
    public static class HelpCommandResponse extends AbstractCommandResponse {
        public HelpCommandResponse(AbstractCommandData data) {
            super(data);
        }

        @Override
        public void doYourWorst(JDA client) {
            EmbedBuilder builder = new EmbedBuilder();
            StringBuilder out = new StringBuilder("Command name: <args> (Runs on Server/Robot)");
            for (AbstractCommand command : MessageHandler.commands.values()) {
                out.append('\n').append(command.getCommand()).append(": ").append(command.getArgs()).append(" (").append(command.isServerSideCommand() ? "Server" : "Robot").append(")");
            }
            builder.setTitle("Commnds").setDescription(out.toString());
            client.getTextChannelById(CHANNEL_ID).sendMessage(builder.build()).submit();
        }
    }
}
