package com.example.morsecode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

public class LearningActivity extends AppCompatActivity {

    private int correctGuesses = 0;
    private int allGuesses = 0;
    private int correctAnswer = 0;

    private Button[] guessButtons;

    private void newPuzzle() {
        Random rand = new Random();

        StringBuilder charList = new StringBuilder("abcdefghijklmnopqrstuvwxyz0123456789");
        for (Button button : guessButtons) {
            int chosen_index = rand.nextInt(charList.length());
            button.setText(String.valueOf(charList.charAt(chosen_index)));
            charList.deleteCharAt(chosen_index);
        }
        correctAnswer = guessButtons[rand.nextInt(guessButtons.length)].getId();
    }

    @SuppressLint("DefaultLocale")
    private void updateCount() {
        TextView counter = findViewById(R.id.score);
        counter.setText(String.format("%d/%d", correctGuesses, allGuesses));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

         guessButtons = new Button[] {
            findViewById(R.id.option1),
            findViewById(R.id.option2),
            findViewById(R.id.option3),
            findViewById(R.id.option4)
        };

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (correctAnswer == view.getId())
                    correctGuesses++;
                allGuesses++;
                updateCount();
                newPuzzle();
            }
        };

        for (Button button : guessButtons)
            button.setOnClickListener(onClickListener);

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
        MorsApp.getInstance().setTool(Torch.Tool.FLASHLIGHT);
        settingButtons.setActiveButton(findViewById(R.id.button_flashlight));

        ((Button) findViewById(R.id.play_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MorsApp.getInstance().getTorch().MorseCode(
                            ((Button) findViewById(correctAnswer)).getText().toString()
                    );
                } catch (InterruptedException ignored) {}
            }
        });

        newPuzzle();
    }
}