package com.example.morsecode;

import android.widget.Button;

// Class wrapping the buttons for choosing the current tool
public class SettingButtons {
    private final int activeButtonColor;
    private final int passiveButtonColor;

    private final Button buttonFlashlight;
    private final Button buttonVibrate;
    private final Button buttonSound;

    SettingButtons(int activeButtonColor, int passiveButtonColor, Button buttonFlashlight,
                   Button buttonVibrate, Button buttonSound) {
        this.activeButtonColor = activeButtonColor;
        this.passiveButtonColor = passiveButtonColor;
        this.buttonFlashlight = buttonFlashlight;
        this.buttonVibrate = buttonVibrate;
        this.buttonSound = buttonSound;

        // onClickListeners for changing the tool and color of the buttons
        buttonFlashlight.setOnClickListener(view -> {
            MorsApp.getInstance().setTool(Torch.Tool.FLASHLIGHT);
            setActiveButton(buttonFlashlight);
        });

        buttonVibrate.setOnClickListener(view -> {
            MorsApp.getInstance().setTool(Torch.Tool.VIBRATION);
            setActiveButton(buttonVibrate);
        });

        buttonSound.setOnClickListener(view -> {
            MorsApp.getInstance().setTool(Torch.Tool.SOUND);
            setActiveButton(buttonSound);
        });
    }

    public void setActiveButton(Button activeButton) {
        Button[] buttons = {
                this.buttonFlashlight,
                this.buttonVibrate,
                this.buttonSound
        };

        for (Button button : buttons) {
            button.getBackground().setTint(
                    (button.equals(activeButton) ? activeButtonColor : passiveButtonColor)
            );
        }
    }


}
