package frc.discordslackbot.commands;

import com.slack.api.bolt.App;
import frc.discordslackbot.DiscordBot;
import frc.discordslackbot.MessageHandler;
import frc.discordslackbot.SlackBot;
import frc.misc.ServerSide;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

/**
 * Simple command to list all the commands, their arguments, and where they run
 */
@ServerSide
public class HelpCommand extends AbstractCommand {
    @Override
    public @NotNull AbstractCommandResponse run(AbstractCommandData message) {
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
     * Manufactures embed and does all of the heavy lifting from here
     */
    public static class HelpCommandResponse extends AbstractCommandResponse {
        public HelpCommandResponse(AbstractCommandData data) {
            super(data);
        }

        /**
         * Theres a bit going on here but heres the important stuff. If no message args, returns a list of loaded
         * commands. If one arg, attempts to get and send the help text for that command. If no scuh, returns the list
         * of commands. If more than 1 arg, it will console your sad sad self.
         *
         * @param client {@link DiscordBot#getBotObject() the bot object}
         */
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
            } else if (CONTENT.split(" ").length > 2) {
                client.getTextChannelById(CHANNEL_ID).sendMessage("Sorry bro, I feel for you and hope that whatever gremlins or voodoo responsible for it fixes it soon.").queue();
                return;
            }
            out.append("Command name: <args> (Runs on Server/Robot)");
            EmbedBuilder builder = new EmbedBuilder();
            for (AbstractCommand command : MessageHandler.commands.values()) {
                out.append('\n').append(command.getCommand()).append(": ").append(command.getArgs()).append(" (").append(command.isServerSideCommand() ? "Server" : "Robot").append(")");
            }
            builder.setTitle("Commands").setDescription(out.toString());
            client.getTextChannelById(CHANNEL_ID).sendMessage(builder.build()).submit();
        }

        @Override
        public void doYourWorst(App client) {
            StringBuilder out = new StringBuilder();
            if (CONTENT.split(" ").length == 2) {
                AbstractCommand command = MessageHandler.getCommand(CONTENT.split(" ")[1]);
                if (command != null) {
                    SlackBot.sendSlackMessage(CHANNEL_ID, command.sendHelp());
                    return;
                }
                out.append("**Could not find command** ").append(CONTENT.split(" ")[1]).append('\n');
            } else if (CONTENT.split(" ").length > 2) {
                SlackBot.sendSlackMessage(CHANNEL_ID, "Sorry bro, I feel for you and hope that whatever gremlins or voodoo responsible for it fixes it soon.");
                return;
            }
            out.append("Command name: <args> (Runs on Server/Robot)");
            for (AbstractCommand command : MessageHandler.commands.values()) {
                out.append('\n').append(command.getCommand()).append(": ").append(command.getArgs()).append(" (").append(command.isServerSideCommand() ? "Server" : "Robot").append(")");
            }
            SlackBot.sendSlackMessage(CHANNEL_ID, out.toString());
        }

        @Override
        public void doYourWorst() {
            StringBuilder out = new StringBuilder();
            if (CONTENT.split(" ").length == 2) {
                AbstractCommand command = MessageHandler.getCommand(CONTENT.split(" ")[1]);
                if (command != null) {
                    System.out.println(command.sendHelp());
                    return;
                }
                out.append("**Could not find command** ").append(CONTENT.split(" ")[1]).append('\n');
            } else if (CONTENT.split(" ").length > 2) {
                SlackBot.sendSlackMessage(CHANNEL_ID, "Sorry bro, I feel for you and hope that whatever gremlins or voodoo responsible for it fixes it soon.");
                return;
            }
            out.append("Command name: <args> (Runs on Server/Robot)");
            for (AbstractCommand command : MessageHandler.commands.values()) {
                out.append('\n').append(command.getCommand()).append(": ").append(command.getArgs()).append(" (").append(command.isServerSideCommand() ? "Server" : "Robot").append(")");
            }
            System.out.println(out);
        }
    }
}
