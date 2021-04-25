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
    public String sendHelp() {
        return "What are you doing? You really do need some help";
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
            StringBuilder out = new StringBuilder();
            if (CONTENT.split(" ").length == 2) {
                AbstractCommand command = MessageHandler.getCommand(CONTENT.split(" ")[1]);
                if (command != null) {
                    client.getTextChannelById(CHANNEL_ID).sendMessage(command.sendHelp()).queue();
                    return;
                }
                out.append("**Could not find command** ").append(CONTENT.split(" ")[1]).append('\n');
            }else if (CONTENT.split(" ").length > 2){
                client.getTextChannelById(CHANNEL_ID).sendMessage("Sorry bro, I feel for you and hope that whatever gremlins or voodoo responsible for it fixes it soon.").queue();
                return;
            }
            out.append("Command name: <args> (Runs on Server/Robot)");
            EmbedBuilder builder = new EmbedBuilder();
            for (AbstractCommand command : MessageHandler.commands.values()) {
                out.append('\n').append(command.getCommand()).append(": ").append(command.getArgs()).append(" (").append(command.isServerSideCommand() ? "Server" : "Robot").append(")");
            }
            builder.setTitle("Commnds").setDescription(out.toString());
            client.getTextChannelById(CHANNEL_ID).sendMessage(builder.build()).submit();
        }
    }
}
