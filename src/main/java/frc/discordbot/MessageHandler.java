package frc.discordbot;

import frc.discordbot.commands.AbstractCommand;
import frc.discordbot.commands.PingCommand;
import frc.discordbot.commands.PlaySongCommand;
import frc.discordbot.commands.StatusCommand;
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

    public static void loadCommands() {
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

    @Override
    public void onMessageReceived(MessageReceivedEvent message) {
        if (message.getAuthor().isBot())
            return;
        if (message.getGuild() == null)
            return;
        if (message.getMessage().getContentRaw().charAt(0) == '!' && commands.containsKey(message.getMessage().getContentRaw().substring(1).split(" ")[0])) {
            commands.get(message.getMessage().getContentRaw().substring(1).split(" ")[0]).run(message);
        }
    }
}
