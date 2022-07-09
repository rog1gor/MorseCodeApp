package com.example.morsecode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Looper;
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
    private Button buttonLearning;

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
       // buttonLearning = findViewById(R.id.button_learning);
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

        // getting access to the vibrator
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        torch = new Torch(cameraID, cameraManager, vibrator);

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

//        buttonLearning.setOnClickListener((new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent learning = new Intent(this, LearningActivity.class);
//                startActivity(learning);
//            }
//        }));

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sendingMessage && validMessage) {
                    // get the message
                    String message = insertedMessage.getText().toString();
                    insertedMessage.setText("");
                    if (message.equals(""))
                        return;

                    // translate to Morse Code
                    sendingThread = new Thread(new Runnable() {
                        public void run() {
                            Looper.prepare();

                            try {
                                torch.MorseCode(message, tool);
                                sendingMessage = false;
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Finished uploading!",
                                        Toast.LENGTH_SHORT
                                ).show();
                            } catch (InterruptedException ignored) {}

                            Looper.loop();
                        }
                    });
                    sendingMessage = true;
                    sendingThread.start();
                }
                else {
                    sendingThread.interrupt();
                    sendingMessage = false;
                    Toast.makeText(
                            getApplicationContext(),
                            "Stopped uploading!",
                            Toast.LENGTH_SHORT
                    ).show();
                }
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