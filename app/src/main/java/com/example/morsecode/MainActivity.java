package com.example.morsecode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

    private EditText insertedMessage;

    private Button buttonFlashlight;
    private Button buttonVibrate;
    private Button buttonSound;

    private String cameraID;

    private Thread sendingThread;

    Vibrator vibrator;

    private Torch torch;

    private Torch.Tool tool;

    private Boolean sendingMessage;

    @SuppressLint("UseCompatLoadingForColorStateLists")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set private attributes
        Button uploadButton = findViewById(R.id.upload_button);
        insertedMessage = findViewById(R.id.insert_message);
        buttonFlashlight = findViewById(R.id.button_flashlight);
        buttonVibrate = findViewById(R.id.button_vibrate);
        buttonSound = findViewById(R.id.button_sound);
        sendingMessage = false;
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent);
            }
        }

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
                if (!sendingMessage) {
                    // get the message
                    String message = insertedMessage.getText().toString();
                    insertedMessage.setText("");
                    if (message.equals(""))
                        return;

                    // inform user that the message was uploaded correctly
                    Toast.makeText(
                            getApplicationContext(),
                            "Message uploaded successfully!",
                            Toast.LENGTH_SHORT
                    ).show();

                    // translate to Morse Code
                    sendingThread = new Thread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            try {
                                torch.MorseCode(message, tool);
                                sendingMessage = false;
                                uploadButton.setText("Upload");
                            } catch (InterruptedException ignored) {}
                        }
                    });
                    sendingThread.start();
                }
                else {
                    sendingThread.interrupt();
                    Toast.makeText(
                            getApplicationContext(),
                            "Stopped uploading!",
                            Toast.LENGTH_SHORT
                    ).show();
                }
                sendingMessage = !sendingMessage;
                uploadButton.setText((sendingMessage ? "Stop" : "Upload"));
            }
        });
    }

    private void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            insertedMessage.setText(sharedText);
        }
    }
}