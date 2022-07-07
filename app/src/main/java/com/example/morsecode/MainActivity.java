package com.example.morsecode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button uploadButton;
    private EditText insertedMessage;

    private Button buttonFlashlight;
    private Button buttonVibrate;
    private Button buttonSound;

    private CameraManager cameraManager;
    private String cameraID;

    Vibrator vibrator;

    private Torch torch;

    private Torch.Tool tool;

    @SuppressLint("UseCompatLoadingForColorStateLists")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set private attributes
        uploadButton = findViewById(R.id.upload_button);
        insertedMessage = findViewById(R.id.insert_message);
        buttonFlashlight = findViewById(R.id.button_flashlight);
        buttonVibrate = findViewById(R.id.button_vibrate);
        buttonSound = findViewById(R.id.button_sound);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        // try to get access to a torch
        try {
            cameraID = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        // getting access to the vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        torch = new Torch(cameraID, cameraManager, vibrator);

        tool = Torch.Tool.FLASHLIGHT;
        buttonFlashlight.getBackground().setTint(
                ResourcesCompat.getColor(
                        getResources(),
                        R.color.purple_200,
                        null)
        );

        buttonFlashlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tool = Torch.Tool.FLASHLIGHT;

                buttonFlashlight.getBackground().setTint(
                        ResourcesCompat.getColor(
                                getResources(),
                                R.color.purple_200,
                                null)
                );
                buttonVibrate.getBackground().setTint(
                        ResourcesCompat.getColor(
                                getResources(),
                                R.color.purple_500,
                                null)
                );
                buttonSound.getBackground().setTint(
                        ResourcesCompat.getColor(
                                getResources(),
                                R.color.purple_500,
                                null)
                );
            }
        });

        buttonVibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tool = Torch.Tool.VIBRATION;

                buttonFlashlight.getBackground().setTint(
                        ResourcesCompat.getColor(
                                getResources(),
                                R.color.purple_500,
                                null)
                );
                buttonVibrate.getBackground().setTint(
                        ResourcesCompat.getColor(
                                getResources(),
                                R.color.purple_200,
                                null)
                );
                buttonSound.getBackground().setTint(
                        ResourcesCompat.getColor(
                                getResources(),
                                R.color.purple_500,
                                null)
                );
            }
        });

        buttonSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tool = Torch.Tool.SOUND;

                buttonFlashlight.getBackground().setTint(
                        ResourcesCompat.getColor(
                                getResources(),
                                R.color.purple_500,
                                null)
                );
                buttonVibrate.getBackground().setTint(
                        ResourcesCompat.getColor(
                                getResources(),
                                R.color.purple_500,
                                null)
                );
                buttonSound.getBackground().setTint(
                        ResourcesCompat.getColor(
                                getResources(),
                                R.color.purple_200,
                                null)
                );
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get the message
                String message = insertedMessage.getText().toString();
                insertedMessage.setText("");

                // inform user that the message was uploaded correctly
                Toast myToast = Toast.makeText(
                        getApplicationContext(),
                        "Message Uploaded Correctly!",
                        Toast.LENGTH_SHORT);
                myToast.show();

                // translate to Morse Code
                torch.MorseCode(message, tool);
            }
        });
    }
}