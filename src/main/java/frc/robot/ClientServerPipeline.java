package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.discordbot.DiscordBot;
import frc.discordbot.MessageHandler;
import frc.discordbot.commands.AbstractCommand;
import frc.gpws.Sound;
import frc.gpws.SoundManager;
import frc.misc.ClientSide;
import frc.misc.ServerSide;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;

/**
 * Acts as a bridge between the internet-connected bot and the terrible robot. Implements {@link Runnable} because the
 * {@link #run()} method will create a new thread and prevent exiting.
 */
public class ClientServerPipeline implements Runnable {
    public static boolean SERVER;
    private static NetworkTable serverNetworkTable;
    private static ClientServerPipeline SERVER_PIPELINE, CLIENT_PIPELINE;

    /**
     * This is why we use getters, kids. It means that we <i>should</i> only create a server if requested, or a client
     * if requested
     *
     * @return a stored server object. created and stored if not already stored
     */
    @ServerSide
    public static ClientServerPipeline getServer() {
        return Objects.requireNonNullElseGet(SERVER_PIPELINE, () -> SERVER_PIPELINE = new ClientServerPipeline(true));
    }

    /**
     * This is why we use getters, kids. It means that we <i>should</i> only create a client if requested, or a server
     * if requested
     *
     * @return a stored client object. created and stored if not already stored
     */
    @ClientSide
    public static ClientServerPipeline getClient() {
        return Objects.requireNonNullElseGet(CLIENT_PIPELINE, () -> CLIENT_PIPELINE = new ClientServerPipeline(false));
    }

    /**
     * Creates the requested Pipeline object
     *
     * @param server true: the device running this pipeline can access the internet and host the bot for the client.
     *               false: the device running this pipeline cannot host the bot on its own and is listening via the
     *               pipeline
     */
    private ClientServerPipeline(boolean server) {
        serverNetworkTable = NetworkTableInstance.getDefault().getTable("Brobot");
        wipeNetworkTable(serverNetworkTable);
        SERVER = server;
        DiscordBot.newInstance(!SERVER);
    }

    /**
     * Clears the networktable from all its keys.
     *
     * @param table The networktable being deleted
     */
    private void wipeNetworkTable(NetworkTable table) {
        for (String key : table.getKeys()) {
            table.delete(key);
        }
    }

    /**
     * {@link frc.discordbot.commands.AbstractCommand.AbstractCommandData Data} is just a stripped down way of sending a
     * {@link net.dv8tion.jda.core.events.message.MessageReceivedEvent message from the bot}
     *
     * @param outbound the data packet to send
     * @return true if data was changed, false otherwise
     */
    @ServerSide
    public boolean sendData(AbstractCommand.AbstractCommandData outbound) {
        return sendData(outbound, false);
    }

    /**
     * {@link frc.discordbot.commands.AbstractCommand.AbstractCommandData Data} is just a stripped down way of sending a
     * {@link net.dv8tion.jda.core.events.message.MessageReceivedEvent message from the bot}
     *
     * @param outbound       the data packet to send
     * @param skipDirtyCheck activating this flag will bypass the redundancy check that skips uploading when the
     *                       existing data is identical
     * @return true if data was changed, false otherwise
     */
    @ServerSide
    public boolean sendData(AbstractCommand.AbstractCommandData outbound, boolean skipDirtyCheck) {
        if (outbound == null)
            return false;
        byte[] outboundPacket = writeToBytes(outbound);
        if (!skipDirtyCheck && checkDirty(outboundPacket, serverNetworkTable.getEntry("command").getRaw(new byte[0]))) {
            return false;
        }
        serverNetworkTable.getEntry("command").setRaw(outboundPacket);
        serverNetworkTable.getEntry("read_reciept_command").setBoolean(false);
        return true;
    }

    /**
     * Does as the name suggests. Takes a serializeable object and serializes it
     *
     * @param objectToWrite the object to serialize
     * @return the result of serialization
     */
    private static byte[] writeToBytes(Serializable objectToWrite) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(objectToWrite);
            oos.close();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks to see if the two data objects are unique
     *
     * @param newData New message data
     * @param oldData Old message data
     * @return true if a deep equals is true, false otherwise
     */
    private static boolean checkDirty(byte[] newData, byte[] oldData) {
        if (newData.length != oldData.length)
            return false;
        for (int i = 0; i < newData.length; i++) {
            if (oldData[i] != newData[i])
                return true;
        }
        return false;
    }

    /**
     * Creates a new {@link Thread} and runs this code on it. Due to sync stuff, it is just like starting anew. We here
     * at jojo2357 inc do NOT mess with java sync for it is too messy. Only use this when running {@link
     * #SERVER_PIPELINE a dedicated pipeline}
     */
    @Override
    @ServerSide
    public void run() {
        NetworkTableInstance.getDefault().startClientTeam(5199);  // where TEAM=190, 294, etc, or use inst.startClient("hostname") or similar
        SoundManager.init();
        if (DiscordBot.detectedInternet() && Main.RANDOM.nextInt(4) == 0) {
            try {
                Runtime.getRuntime().exec(new String[]{"cmd", "/c","start chrome https://www.youtube.com/watch?v=bxqLsrlakK8"});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        while (true) {
            try {
                Thread.sleep(20);
                SoundManager.update();
                updatePipeline();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Based on if the current pipeline is a {@link #SERVER}, checks for, and handles new data.
     */
    public void updatePipeline() {
        if (SERVER) {
            if (checkReply()) {
                readReply().doYourWorst(DiscordBot.bot.getBotObject());
                System.out.println("Recieved message from teddy");
            }
            if (checkSound()) {
                SoundManager.enqueueSound(readSound());
            }
        } else {
            if (checkMessage()) {
                System.out.println("I got a message.");
                MessageHandler.onMessageReceived(readCommandData());
            }
        }
    }

    /**
     * Checks if the client has replied yet and if they replied with anything meaningful (null check and blank check
     * only)
     *
     * @return true if unread flag is set and reply is fresh
     * @see #checkMessage()
     */
    @ServerSide
    public boolean checkReply() {
        return !serverNetworkTable.getEntry("read_reciept_data").getBoolean(false) && serverNetworkTable.getEntry("response").getRaw(new byte[0]).length != 0;
    }

    /**
     * Reads the client reply currently in the pipeline, regardless of whether or not the {@link #checkReply() reply is
     * fresh}
     *
     * @return The current reply from the client in the pipeline
     */
    @ServerSide
    public AbstractCommand.AbstractCommandResponse readReply() {
        byte[] inboundPacket = serverNetworkTable.getEntry("response").getRaw(new byte[0]);
        serverNetworkTable.getEntry("read_reciept_data").setBoolean(true);
        if (readFromBytes(inboundPacket) instanceof AbstractCommand.AbstractCommandResponse)
            return (AbstractCommand.AbstractCommandResponse) readFromBytes(inboundPacket);
        throw new IllegalStateException("Not sure what happened but the command that I read isnt a known command");
    }

    /**
     * Checks if the server has posted new data yet and if they posted anything meaningful (null check and blank check
     * only)
     *
     * @return true if unread flag is set and data is fresh
     */
    @ServerSide
    public boolean checkSound() {
        return serverNetworkTable.getEntry("sound").getRaw(new byte[0]).length != 0;
    }

    /**
     * Reads the client reply currently in the pipeline, regardless of whether or not the {@link #checkReply() reply is
     * fresh}
     *
     * @return The current reply from the client in the pipeline
     */
    @ServerSide
    public Sound readSound() {
        byte[] inboundPacket = serverNetworkTable.getEntry("sound").getRaw(new byte[0]);
        serverNetworkTable.getEntry("sound").setRaw(new byte[0]);
        if (readFromBytes(inboundPacket) instanceof Sound) {
            return (Sound) readFromBytes(inboundPacket);
        }
        throw new IllegalStateException("Not sure what happened but the command that I read isnt a known command");
    }

    /**
     * Checks if the server has posted new data yet and if they posted anything meaningful (null check and blank check
     * only)
     *
     * @return true if unread flag is set and data is fresh
     */
    @ClientSide
    public boolean checkMessage() {
        return !serverNetworkTable.getEntry("read_reciept_command").getBoolean(false) && serverNetworkTable.getEntry("command").getRaw(new byte[0]).length != 0;
    }

    /**
     * Reads the server command currently in the pipeline, regardless of whether or not the {@link #checkMessage() reply
     * is fresh}
     *
     * @return The current reply from the client in the pipeline
     */
    @ClientSide
    public AbstractCommand.AbstractCommandData readCommandData() {
        byte[] inboundPacket = serverNetworkTable.getEntry("command").getRaw(new byte[0]);
        serverNetworkTable.getEntry("read_reciept_command").setBoolean(true);
        System.out.println("I read the command data. Length:" + inboundPacket.length);
        if (readFromBytes(inboundPacket) instanceof AbstractCommand.AbstractCommandData)
            return (AbstractCommand.AbstractCommandData) readFromBytes(inboundPacket);
        throw new IllegalStateException("Not sure what happened but the command that I read isnt a known command");
    }

    /**
     * Reads and returns an object as interpreted from the passed data
     *
     * @param rawdata serialized object data, represented in bytes (sorry if u have a string idk where u got it from but
     *                put it back)
     * @return the input, deserialized
     */
    private static Object readFromBytes(byte[] rawdata) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(rawdata);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object objectOut = ois.readObject();
            ois.close();
            return objectOut;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Posts the results of a command to the server from the client.
     *
     * @param reply the command response to send
     * @return true if data was exchanged, false otherwise
     */
    @ClientSide
    public boolean sendReply(AbstractCommand.AbstractCommandResponse reply) {
        return sendReply(reply, false);
    }

    /**
     * Posts the results of a command to the server from the client.
     *
     * @param reply          the command response to send
     * @param skipDirtyCheck activating this flag will bypass the redundancy check that skips uploading when the
     *                       existing data is identical
     * @return true if data was exchanged, false otherwise
     */
    @ClientSide
    public boolean sendReply(AbstractCommand.AbstractCommandResponse reply, boolean skipDirtyCheck) {
        if (reply == null)
            return false;
        byte[] outboundPacket = writeToBytes(reply);
        if (!skipDirtyCheck && checkDirty(outboundPacket, serverNetworkTable.getEntry("response").getRaw(new byte[0]))) {
            return false;
        }
        serverNetworkTable.getEntry("response").setRaw(outboundPacket);
        serverNetworkTable.getEntry("read_reciept_data").setBoolean(false);
        return true;
    }

    @ClientSide
    public boolean sendSound(Sound sound) {
        return sendSound(sound, false);
    }

    /**
     * Posts the results of a command to the server from the client.
     *
     * @param sound          the command response to send
     * @param skipDirtyCheck activating this flag will bypass the redundancy check that skips uploading when the
     *                       existing data is identical
     * @return true if data was exchanged, false otherwise
     */
    @ClientSide
    public boolean sendSound(Sound sound, boolean skipDirtyCheck) {
        if (sound == null)
            return false;
        byte[] outboundPacket = writeToBytes(sound);
        if (!skipDirtyCheck && checkDirty(outboundPacket, serverNetworkTable.getEntry("sound").getRaw(new byte[0]))) {
            return false;
        }
        serverNetworkTable.getEntry("sound").setRaw(outboundPacket);
        return true;
    }
}
