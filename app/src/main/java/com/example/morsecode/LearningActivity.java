package com.example.morsecode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Random;

public class LearningActivity extends AppCompatActivity {

    private int correctGuesses = 0;
    private int allGuesses = 0;
    private int correctAnswer = 0;

    private Button[] guessButtons;

    private void newPuzzle() {
        // Generates a new puzzle for the user by choosing possible answers randomly.
        Random rand = new Random();

        StringBuilder charList = new StringBuilder("abcdefghijklmnopqrstuvwxyz0123456789");
        for (Button button : guessButtons) {
            int chosen_index = rand.nextInt(charList.length());
            button.setText(String.valueOf(charList.charAt(chosen_index)));
            // Makes sure not to choose the same answer more than once.
            charList.deleteCharAt(chosen_index);
        }
        correctAnswer = guessButtons[rand.nextInt(guessButtons.length)].getId();
    }

    @SuppressLint("DefaultLocale")
    private void updateCount() {
        // Updates the score - " Correct guesses / All guesses ".
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

        // onClickListener for the answer buttons.
        View.OnClickListener onClickListener = view -> {
            if (correctAnswer == view.getId()) {
                correctGuesses++;
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(R.string.correct),
                        Toast.LENGTH_SHORT
                ).show();
            }
            else {
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(R.string.wrong) +
                                ((Button) findViewById(correctAnswer)).getText().toString().toUpperCase(),
                        Toast.LENGTH_SHORT
                ).show();
            }

            // Update count and generate a new puzzle.
            allGuesses++;
            updateCount();
            newPuzzle();
        };

        for (Button button : guessButtons)
            button.setOnClickListener(onClickListener);

        // Declaration of the buttons for choosing the current tool.
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

        findViewById(R.id.play_button).setOnClickListener(view -> {
            try {
                MorsApp.getInstance().getTorch().MorseCode(
                        ((Button) findViewById(correctAnswer)).getText().toString()
                );
            } catch (InterruptedException ignored) {}
        });

        newPuzzle();
    }
}