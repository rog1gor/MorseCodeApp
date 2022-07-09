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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements TextWatcher {

    private EditText insertedMessage;

    private Thread sendingThread;

    private Boolean sendingMessage;

    private Boolean validMessage;

    @SuppressLint("UseCompatLoadingForColorStateLists")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set private attributes
        Button uploadButton = findViewById(R.id.upload_button);
        insertedMessage = findViewById(R.id.insert_message);
        insertedMessage.addTextChangedListener(this);
        Button buttonLearning = findViewById(R.id.learn_button);
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

        SettingButtons settingButtons = new SettingButtons(
                ResourcesCompat.getColor(
                        getResources(),
                        R.color.purple_200,
                        null
                ),
                ResourcesCompat.getColor(
                        getResources(),
                        R.color.purple_500,
                        null
                ),
                findViewById(R.id.button_flashlight),
                findViewById(R.id.button_vibrate),
                findViewById(R.id.button_sound)
        );

        // try to get access to a torch
        String cameraID = "";
        try {
            cameraID = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        // getting access to the vibrator
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        MorsApp.getInstance().setTorch(new Torch(cameraID, cameraManager, vibrator));

        MorsApp.getInstance().setTool(Torch.Tool.FLASHLIGHT);
        settingButtons.setActiveButton(findViewById(R.id.button_flashlight));

        Intent learning = new Intent(this, LearningActivity.class);
        buttonLearning.setOnClickListener((view -> {
            if (sendingMessage) {
                sendingThread.interrupt();
                sendingMessage = false;
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(R.string.stopped_uploading),
                        Toast.LENGTH_SHORT
                ).show();
            }
            startActivity(learning);
        }));

        uploadButton.setOnClickListener(view -> {
            if (!validMessage) {
                return;
            }
            if (!sendingMessage) {
                // get the message
                String message = insertedMessage.getText().toString();
                insertedMessage.setText("");
                if (message.equals(""))
                    return;

                // translate to Morse Code
                sendingThread = new Thread(() -> {
                    Looper.prepare();

                    try {
                        MorsApp.getInstance().getTorch().MorseCode(message);
                        sendingMessage = false;
                        Toast.makeText(
                                getApplicationContext(),
                                getResources().getString(R.string.finished_uploading),
                                Toast.LENGTH_SHORT
                        ).show();
                    } catch (InterruptedException ignored) {}

                    Looper.loop();
                });
                sendingMessage = true;
                sendingThread.start();
            }
            else {
                sendingThread.interrupt();
                sendingMessage = false;
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(R.string.stopped_uploading),
                        Toast.LENGTH_SHORT
                ).show();
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
            this.insertedMessage.setError(getResources().getString(R.string.invalid_char));
        }
    }

    @Override
    final public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* Don't care */ }

    @Override
    final public void onTextChanged(CharSequence s, int start, int before, int count) { /* Don't care */ }
}