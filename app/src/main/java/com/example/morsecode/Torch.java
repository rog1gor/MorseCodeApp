package com.example.morsecode;

import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraAccessException;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import java.util.HashMap;

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

    private ToneGenerator toneGenerator;

    static {
        HashMap<Character, Boolean[]> signSignals = new HashMap<>();
        // false - short signal, true - long signal

        // capital letters
        {
            signSignals.put('A', new Boolean[]{false, true});
            signSignals.put('B', new Boolean[]{true, false, false, false});
            signSignals.put('C', new Boolean[]{true, false, true, false});
            signSignals.put('D', new Boolean[]{true, false, false});
            signSignals.put('E', new Boolean[]{false});
            signSignals.put('F', new Boolean[]{false, false, true, false});
            signSignals.put('G', new Boolean[]{true, true, false});
            signSignals.put('H', new Boolean[]{false, false, false, false});
            signSignals.put('I', new Boolean[]{false, false});
            signSignals.put('J', new Boolean[]{false, true, true, true});
            signSignals.put('K', new Boolean[]{true, false, true});
            signSignals.put('L', new Boolean[]{false, true, false, false});
            signSignals.put('M', new Boolean[]{true, true});
            signSignals.put('N', new Boolean[]{true, false});
            signSignals.put('O', new Boolean[]{true, true, true});
            signSignals.put('P', new Boolean[]{false, true, true, false});
            signSignals.put('Q', new Boolean[]{true, true, false, true});
            signSignals.put('R', new Boolean[]{false, true, false});
            signSignals.put('S', new Boolean[]{false, false, false});
            signSignals.put('T', new Boolean[]{true});
            signSignals.put('U', new Boolean[]{false, false, true});
            signSignals.put('V', new Boolean[]{false, false, false, true});
            signSignals.put('W', new Boolean[]{false, true, true});
            signSignals.put('X', new Boolean[]{true, false, false, true});
            signSignals.put('Y', new Boolean[]{true, false, true, true});
            signSignals.put('Z', new Boolean[]{true, true, false, false});
        }

        // small letters
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
        this.short_signal = this.signal_unit;
        this.long_signal = 3 * this.signal_unit;
        this.sign_interspace = 3 * this.signal_unit;
        this.word_interspace = 7 * this.signal_unit;

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

    private void ShortSignal() {
        switch (tool) {
            case FLASHLIGHT:
                TurnFlashlightOn();

                try {
                    Thread.sleep(this.short_signal);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                TurnFlashlightOff();
                break;

            case VIBRATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                            VibrationEffect.createOneShot(
                                    this.short_signal, VibrationEffect.DEFAULT_AMPLITUDE
                            )
                    );
                } else {
                    //deprecated in API 26
                    vibrator.vibrate(this.short_signal);
                }

                try {
                    Thread.sleep(this.short_signal);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                break;

            case SOUND:
                toneGenerator.startTone(ToneGenerator.TONE_CDMA_DIAL_TONE_LITE, this.short_signal);

                try {
                    Thread.sleep(this.short_signal);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                break;
        }

        // adding sleep so that the morse code is more readable
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void LongSignal() {
        switch (tool) {
            case FLASHLIGHT:
                TurnFlashlightOn();

                try {
                    Thread.sleep(this.long_signal);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                TurnFlashlightOff();

                break;

            case VIBRATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                            VibrationEffect.createOneShot(
                                    this.long_signal, VibrationEffect.DEFAULT_AMPLITUDE
                            )
                    );
                } else {
                    //deprecated in API 26
                    vibrator.vibrate(this.long_signal);
                }

                try {
                    Thread.sleep(this.long_signal);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                break;

            case SOUND:
                toneGenerator.startTone(ToneGenerator.TONE_CDMA_DIAL_TONE_LITE, this.long_signal);

                try {
                    Thread.sleep(this.long_signal);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                break;
        }
        // adding sleep so that the morse code is more readable
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void MorseCodeLetter(char sign) {
        Boolean[] code = signals.get(sign);
        assert code != null;
        for (Boolean signal : code) {
            if (signal)
                LongSignal();
            else
                ShortSignal();
        }
    }

    private void MorseCodeAfterSign() {
        try {
            Thread.sleep(this.sign_interspace);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void MorseCodeBlank() {
        try {
            Thread.sleep(this.word_interspace);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void MorseCode(String message, Tool selectedTool) {
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
