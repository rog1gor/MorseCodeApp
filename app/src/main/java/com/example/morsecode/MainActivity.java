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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements TextWatcher {

    private int activeButtonColor;
    private int passiveButtonColor;

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

    private Boolean validMessage;

    private void setActiveButton(Button activeButton) {
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

    @SuppressLint("UseCompatLoadingForColorStateLists")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set private attributes
        Button uploadButton = findViewById(R.id.upload_button);
        insertedMessage = findViewById(R.id.insert_message);
        insertedMessage.addTextChangedListener(this);
        buttonFlashlight = findViewById(R.id.button_flashlight);
        buttonVibrate = findViewById(R.id.button_vibrate);
        buttonSound = findViewById(R.id.button_sound);
        sendingMessage = false;
        validMessage= true;
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
        torch = new Torch(cameraID, cameraManager, vibrator);

        // getting access to the vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

         activeButtonColor = ResourcesCompat.getColor(
                getResources(),
                R.color.purple_200,
                null
        );
         passiveButtonColor = ResourcesCompat.getColor(
                getResources(),
                R.color.purple_500,
                null
        );

        tool = Torch.Tool.FLASHLIGHT;
        setActiveButton(buttonFlashlight);

        buttonFlashlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tool = Torch.Tool.FLASHLIGHT;
                setActiveButton(buttonFlashlight);
            }
        });

        buttonVibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tool = Torch.Tool.VIBRATION;
                setActiveButton(buttonVibrate);
            }
        });

        buttonSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tool = Torch.Tool.SOUND;
                setActiveButton(buttonSound);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sendingMessage && validMessage) {
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

    @Override
    public void afterTextChanged(Editable s) {
        Pattern p = Pattern.compile("[A-Za-z0-9 ]*");
        Matcher m = p.matcher(s.toString());
        if (m.matches()) {
            this.validMessage = true;
        }
        else {
            this.validMessage = false;
            this.insertedMessage.setError("Invalid character used. Please use only english alphabet letters, digits and spaces.");
        }
    }

    @Override
    final public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Don't care */ }

    @Override
    final public void onTextChanged(CharSequence s, int start, int before, int count) { /* Don't care */ }
}