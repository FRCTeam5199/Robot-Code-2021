package frc.discordbot;

import frc.discordbot.commands.AbstractCommand;
import frc.discordbot.commands.PingCommand;
import frc.discordbot.commands.PlaySongCommand;
import frc.discordbot.commands.StatusCommand;
import frc.robot.Main;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MessageHandler extends ListenerAdapter {
    private static final HashMap<String, AbstractCommand> commands = new HashMap<>();
    private static final HashMap<String, AbstractCommand> commandsAlias = new HashMap<>();
    public static MessageHandler messageHandler;
    private static boolean LISTENING;

    public static void loadCommands(boolean listening) {
        LISTENING = listening;
        List<Class<? extends AbstractCommand>> classes = Arrays.asList(PlaySongCommand.class, PingCommand.class, StatusCommand.class);
        for (Class<? extends AbstractCommand> s : classes) {
            try {
                if (Modifier.isAbstract(s.getModifiers())) {
                    continue;
                }
                AbstractCommand c = s.getConstructor().newInstance();
                if (!commands.containsKey(c.getCommand())) {
                    commands.put(c.getCommand(), c);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public MessageHandler() {

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent message) {
        if (message.getAuthor().isBot())
            return;
        if (message.getGuild() == null)
            return;
        if (message.getMessage().getContentRaw().charAt(0) == '!' && commands.containsKey(message.getMessage().getContentRaw().substring(1).split(" ")[0])) {
            System.out.println("Recieved Message: " + message.getMessage().getContentRaw());
            if (!LISTENING) {
                AbstractCommand command = commands.get(message.getMessage().getContentRaw().substring(1).split(" ")[0]);
                if (command.isServerSideCommand())
                    command.run(command.extractData(message)).doYourWorst(DiscordBot.bot.getBotObject());
                else {
                    System.out.println("Sending to bot " + command.extractData(message));
                    Main.pipeline.sendData(command.extractData(message));
                }
            } else
                throw new IllegalStateException("How did you get here as a client?");
        }
    }

    /**
     * A corrolary to {@link #onMessageReceived(MessageReceivedEvent)} that allows the client to process the data as if
     * it were the server.
     *
     * @param message the boiled-down data sent by the server
     */
    public static void onMessageReceived(AbstractCommand.AbstractCommandData message) {
        System.out.println("Recieved Message: " + message.CONTENT);
        if (message.CONTENT.charAt(0) == '!' && commands.containsKey(message.CONTENT.substring(1).split(" ")[0])) {
            if (LISTENING) {
                AbstractCommand command = commands.get(message.CONTENT.substring(1).split(" ")[0]);
                Main.pipeline.sendReply(command.run(message));
            } else
                throw new IllegalStateException("How did you get here as a client?");
        }
    }
}
