package frc.discordbot.commands;

import frc.misc.QuoteOfTheDay;
import frc.misc.ServerSide;
import net.dv8tion.jda.api.JDA;
import org.jetbrains.annotations.NotNull;

/**
 * Returns a random quote and replies with {@link frc.discordbot.commands.AbstractCommand.GenericCommandResponse#doYourWorst(JDA)
 * standard callback}
 */
@ServerSide
public class RandomQuoteCommand extends AbstractCommand {
    @Override
    public @NotNull AbstractCommandResponse run(AbstractCommandData message) {
        return new GenericCommandResponse(message, "A wise man once said:\n" + QuoteOfTheDay.getRandomQuote());
    }

    @Override
    public String getCommand() {
        return "randomquote";
    }

    @Override
    public String sendHelp() {
        return "ALIIIIIIIII WHY ISNT IT WORKING???";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"awisemanoncesaid", "wiseman", "quote", "rq"};
    }

    @Override
    public boolean isServerSideCommand() {
        return true;
    }
}
