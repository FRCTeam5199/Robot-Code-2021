package frc.discordslackbot.commands;

import com.slack.api.bolt.App;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import frc.discordslackbot.SlackBot;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

/**
 * Gets the ping between Discord and Server
 */
public class PingCommand extends AbstractCommand {
    /**
     * Used for fancy ping
     */
    private static final String[] pingMessages = new String[]{
            ":ping_pong::white_small_square::black_small_square::black_small_square::ping_pong:",
            ":ping_pong::black_small_square::white_small_square::black_small_square::ping_pong:",
            ":ping_pong::black_small_square::black_small_square::white_small_square::ping_pong:",
            ":ping_pong::black_small_square::white_small_square::black_small_square::ping_pong:",
    };

    @Override
    public @NotNull AbstractCommandResponse run(AbstractCommandData message) {
        return new PingCommandResponse(message);
    }

    @Override
    public String getCommand() {
        return "ping";
    }

    @Override
    public String sendHelp() {
        return "Pings the server to get the discord-server ping";
    }

    @Override
    public boolean isServerSideCommand() {
        return true;
    }

    /**
     * Only needed for {@link #doYourWorst(JDA)}
     */
    public static class PingCommandResponse extends AbstractCommandResponse {
        public PingCommandResponse(AbstractCommandData message) {
            super(message);
        }

        @Override
        public void doYourWorst(JDA client) {
            String[] args = CONTENT.split(" ");
            if (args.length > 1 && args[1].matches("fancy")) {
                client.getTextChannelById(CHANNEL_ID).sendMessage("Checking ping...").queue(msg -> {
                    int pings = 5;
                    int lastResult;
                    int sum = 0, min = 999, max = 0;
                    long start = System.currentTimeMillis();
                    for (int j = 0; j < pings; j++) {
                        msg.editMessage(pingMessages[j % pingMessages.length]).submit();
                        lastResult = (int) (System.currentTimeMillis() - start);
                        sum += lastResult;
                        min = Math.min(min, lastResult);
                        max = Math.max(max, lastResult);
                        try {
                            Thread.sleep(1_500L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        start = System.currentTimeMillis();
                    }
                    msg.editMessage(String.format("Average ping is %dms (min: %d, max: %d)", (int) Math.ceil(sum / 5f), min, max)).submit();
                });
            } else {
                long start = System.currentTimeMillis();
                client.getTextChannelById(CHANNEL_ID).sendMessage(":outbox_tray: checking ping").queue(
                        msg -> msg.editMessage(":inbox_tray: ping is " + (System.currentTimeMillis() - start) + "ms").submit());
            }
        }

        @Override
        public void doYourWorst(App client) {
            long start = System.currentTimeMillis();
            try {
                ChatPostMessageResponse result = SlackBot.sendSlackMessage(CHANNEL_ID, ":outbox_tray: checking ping", "");
                if (result.isOk()) {
                    SlackBot.updateSlackMessage(CHANNEL_ID, result.getTs(), ":inbox_tray: ping is " + (System.currentTimeMillis() - start) + "ms.");
                } else {
                    System.out.println("Your Slack bot is cringe, and so are you. Either connect to the internet, or yell at Slack support for a few hours, maybe even days.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void doYourWorst() {
            System.out.println("Ping pong you're wrong");
        }
    }
}