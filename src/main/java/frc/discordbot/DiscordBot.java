package frc.discordbot;

import frc.discordbot.commands.AbstractCommand;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import java.net.InetAddress;

public class DiscordBot {
    public static DiscordBot bot = null;
    private final JDA botObject;

    public static DiscordBot newInstance(boolean listening) {
        try {
            if (!InetAddress.getByName("google.com").isReachable(1000)) {
                System.out.println("NO INTERNET DETECTED");
                //return null;
            } else {
                System.out.println("Internet detected !!!!!!!");
            }
        } catch (Exception ignored) {
            System.out.println("NO INTERNET?");
        }
        if (bot == null)
            return bot = new DiscordBot(listening);
        return bot;
    }

    private DiscordBot(boolean listener) {
        AbstractCommand.DRIVEN = listener;
        MessageHandler.loadCommands(listener);
        JDA holder = null;
        if (!listener) {
            try {
                System.out.println();
                holder = new JDABuilder(AccountType.BOT).addEventListener(new MessageHandler()).setToken("hahayourfunny").build();
            } catch (Exception e) {
                System.err.println("Discord bot failed to init");
                e.printStackTrace();
            }
        }
        botObject = holder;
    }

    public JDA getBotObject() {
        return botObject;
    }
}
