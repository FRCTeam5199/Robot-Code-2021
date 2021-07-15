package frc.discordslackbot;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static frc.misc.UtilFunctions.detectedInternet;

public class ListeningSpy {
    public static ListeningSpy listeningSpy;
    private BufferedReader is;
    private Process spy;
    private final int readBytes = 0;

    public static void startSpying() {
        if (detectedInternet()) {
            if (listeningSpy == null) {
                listeningSpy = new ListeningSpy();
            } else if (!listeningSpy.spy.isAlive()) {
                listeningSpy.reviveSpy();
            }
        }
    }

    private void reviveSpy() {
        spy.destroy();
        createChild();
    }

    private void createChild() {
        if (new File("listentospeech.exe").exists()) {
            try {
                spy = new ProcessBuilder().command("listentospeech").start();
                is = new BufferedReader(new InputStreamReader(spy.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Listening exe not found in root directory.");
        }
    }

    private ListeningSpy() {
        createChild();
    }

    public String getText() {
        try {
            return is.readLine().toLowerCase();
        } catch (Exception e) {
            return "Something Went Wrong.";
        }
    }
}
