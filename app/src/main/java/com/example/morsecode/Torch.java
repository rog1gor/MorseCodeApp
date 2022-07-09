package com.example.morsecode;

import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraAccessException;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import java.util.HashMap;
import java.lang.*;

public class Torch {

    enum Tool {
        FLASHLIGHT,
        VIBRATION,
        SOUND
    }

    private static final HashMap<Character, Boolean[]> signals;

    private static final int signal_unit = 250;
    private final int short_signal;
    private final int long_signal;
    private final int sign_interspace;
    private final int word_interspace;

    private Tool tool;

    private final CameraManager cameraManager;
    private final String cameraID;

    Vibrator vibrator;

    private final ToneGenerator toneGenerator;

    static {
        HashMap<Character, Boolean[]> signSignals = new HashMap<>();
        // false - short signal, true - long signal

        // letters
        {
            signSignals.put('a', new Boolean[]{false, true});
            signSignals.put('b', new Boolean[]{true, false, false, false});
            signSignals.put('c', new Boolean[]{true, false, true, false});
            signSignals.put('d', new Boolean[]{true, false, false});
            signSignals.put('e', new Boolean[]{false});
            signSignals.put('f', new Boolean[]{false, false, true, false});
            signSignals.put('g', new Boolean[]{true, true, false});
            signSignals.put('h', new Boolean[]{false, false, false, false});
            signSignals.put('i', new Boolean[]{false, false});
            signSignals.put('j', new Boolean[]{false, true, true, true});
            signSignals.put('k', new Boolean[]{true, false, true});
            signSignals.put('l', new Boolean[]{false, true, false, false});
            signSignals.put('m', new Boolean[]{true, true});
            signSignals.put('n', new Boolean[]{true, false});
            signSignals.put('o', new Boolean[]{true, true, true});
            signSignals.put('p', new Boolean[]{false, true, true, false});
            signSignals.put('q', new Boolean[]{true, true, false, true});
            signSignals.put('r', new Boolean[]{false, true, false});
            signSignals.put('s', new Boolean[]{false, false, false});
            signSignals.put('t', new Boolean[]{true});
            signSignals.put('u', new Boolean[]{false, false, true});
            signSignals.put('v', new Boolean[]{false, false, false, true});
            signSignals.put('w', new Boolean[]{false, true, true});
            signSignals.put('x', new Boolean[]{true, false, false, true});
            signSignals.put('y', new Boolean[]{true, false, true, true});
            signSignals.put('z', new Boolean[]{true, true, false, false});
        }

        // digits
        {
            signSignals.put('1', new Boolean[] {false, true, true, true, true});
            signSignals.put('2', new Boolean[] {false, false, true, true, true});
            signSignals.put('3', new Boolean[] {false, false, false, true, true});
            signSignals.put('4', new Boolean[] {false, false, false, false, true});
            signSignals.put('5', new Boolean[] {false, false, false, false, false});
            signSignals.put('6', new Boolean[] {true, false, false, false, false});
            signSignals.put('7', new Boolean[] {true, true, false, false,false});
            signSignals.put('8', new Boolean[] {true, true, true, false, false});
            signSignals.put('9', new Boolean[] {true, true, true, true, false});
            signSignals.put('0', new Boolean[] {true, true, true, true, true});
        }

        signals = new HashMap<>(signSignals);
    }

    Torch(String cameraID, CameraManager cameraManager, Vibrator vibrator) {
        this.short_signal = signal_unit;
        this.long_signal = 3 * signal_unit;
        this.sign_interspace = 3 * signal_unit;
        this.word_interspace = 7 * signal_unit;

        this.tool = Tool.FLASHLIGHT;

        this.cameraManager = cameraManager;
        this.cameraID = cameraID;

        this.vibrator = vibrator;

        this.toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
    }

    private void TurnFlashlightOn() {
        try {
            cameraManager.setTorchMode(cameraID, true);
        }
        catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void TurnFlashlightOff() {
        try {
            cameraManager.setTorchMode(cameraID, false);
        }
        catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void signal(int signal_duration) throws InterruptedException {
        switch (tool) {
            case FLASHLIGHT:
                TurnFlashlightOn();

                try {
                    Thread.sleep(signal_duration);
                }
                catch (InterruptedException e) {
                    TurnFlashlightOff();
                    throw new InterruptedException();
                }

                TurnFlashlightOff();
                break;

            case VIBRATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                            VibrationEffect.createOneShot(
                                    signal_duration, VibrationEffect.DEFAULT_AMPLITUDE
                            )
                    );
                } else {
                    //deprecated in API 26
                    vibrator.vibrate(signal_duration);
                }

                Thread.sleep(signal_duration);
                break;

            case SOUND:
                toneGenerator.startTone(ToneGenerator.TONE_CDMA_DIAL_TONE_LITE, signal_duration);

                Thread.sleep(signal_duration);
                break;
        }

        // adding sleep so that the morse code is more readable
        Thread.sleep(50);
    }

    private void MorseCodeLetter(char sign) throws InterruptedException {
        Boolean[] code = signals.get(Character.toLowerCase(sign));
        assert code != null;
        for (Boolean duration : code) {
            if (duration)
                signal(this.long_signal);
            else
                signal(this.short_signal);
        }
    }

    private void MorseCodeAfterSign() throws InterruptedException {
        Thread.sleep(this.sign_interspace);
    }

    private void MorseCodeBlank() throws InterruptedException {
        Thread.sleep(this.word_interspace);
    }

    public void MorseCode(String message, Tool selectedTool) throws InterruptedException {
        this.tool = selectedTool;
        for (char sign : message.toCharArray()) {
            if (sign == ' ') {
                MorseCodeBlank();
            }
            else {
                MorseCodeLetter(sign);
                MorseCodeAfterSign();
            }
        }
    }
}
