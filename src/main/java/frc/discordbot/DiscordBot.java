package frc.discordbot;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;

import java.net.InetAddress;

public class DiscordBot {
    public static DiscordBot bot = null;
    private final JDA botObject;

    public static DiscordBot newInstance() {
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
            return bot = new DiscordBot();
        return bot;
    }

    private DiscordBot() {
        MessageHandler.loadCommands();
        JDA holder = null;
        try {
            System.out.println();
            holder = new JDABuilder(AccountType.BOT).addEventListener(new MessageHandler()).setToken("ODE5OTY0NzI2NDUyMjg5NTQ2.YEuRqA.3LP49vx5BxxibxYVpwEE9gCYSEo").build();
        } catch (Exception e) {
            System.err.println("Discord bot failed to init");
            e.printStackTrace();
        }
        botObject = holder;
    }
}
