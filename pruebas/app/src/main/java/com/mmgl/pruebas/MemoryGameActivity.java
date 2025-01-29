package com.mmgl.pruebas;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

public class MemoryGameActivity extends AppCompatActivity {

    private GridLayout gridMemoryGame;
    private Button btnRestart;
    private TextView tvTitle, tvTimer;
    private ArrayList<Button> buttons;
    private ArrayList<String> verbs;
    private Button firstSelected, secondSelected;
    private boolean isClickable = true;
    private int matches = 0;
    private CountDownTimer countDownTimer;
    private MediaPlayer tickTackPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_game);

        gridMemoryGame = findViewById(R.id.gridMemoryGame);
        btnRestart = findViewById(R.id.btnRestart);
        tvTitle = findViewById(R.id.tvTitle);
        tvTimer = findViewById(R.id.tvTimer);

        loadMemoryGame();

        btnRestart.setOnClickListener(v -> restartGame());
    }

    private void loadMemoryGame() {
        verbs = new ArrayList<>();
        verbs.add("run");
        verbs.add("ran");
        verbs.add("go");
        verbs.add("went");
        verbs.add("eat");
        verbs.add("ate");
        verbs.add("write");
        verbs.add("wrote");
        verbs.add("read");
        verbs.add("read");
        verbs.add("fly");
        verbs.add("flew");

        Collections.shuffle(verbs);

        buttons = new ArrayList<>();
        gridMemoryGame.removeAllViews();
        gridMemoryGame.setRowCount(4);
        gridMemoryGame.setColumnCount(4);

        for (String verb : verbs) {
            Button button = new Button(this);
            button.setText("");
            button.setTag(verb);
            button.setTextSize(18);
            button.setBackgroundResource(android.R.drawable.btn_default);
            button.setOnClickListener(this::onCardClick);
            buttons.add(button);
            gridMemoryGame.addView(button);
        }

        startTimer(); // Inicia el cronómetro
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Reproduce el sonido de tick-tack cada segundo
        tickTackPlayer = MediaPlayer.create(this, R.raw.tick_tack); // Asegúrate de tener el archivo tick_tack.mp3 en res/raw

        countDownTimer = new CountDownTimer(90 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                tvTimer.setText(String.format("Tiempo: %02d:%02d", seconds / 60, seconds % 60));

                // Reproduce el sonido de tick-tack solo si no está sonando otro audio
                if (tickTackPlayer != null && !tickTackPlayer.isPlaying()) {
                    tickTackPlayer.start();
                    tickTackPlayer.setOnCompletionListener(mp -> mp.start()); // Repetir el sonido
                }
            }

            @Override
            public void onFinish() {
                tvTimer.setText("Tiempo: 00:00");
                isClickable = false; // Desactiva los clics
                Toast.makeText(MemoryGameActivity.this, "¡Se acabó el tiempo!", Toast.LENGTH_LONG).show();

                stopTickTackSound();
            }
        };

        countDownTimer.start();
    }

    private void stopTickTackSound() {
        if (tickTackPlayer != null) {
            tickTackPlayer.stop();
            tickTackPlayer.release();
        }
    }

    private void onCardClick(View view) {
        if (!isClickable || (firstSelected != null && secondSelected != null)) return;

        Button selectedButton = (Button) view;
        String verb = (String) selectedButton.getTag();

        // Animación de volteo
        selectedButton.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.flip_out));
        selectedButton.postDelayed(() -> {
            selectedButton.setText(verb); // Muestra el texto después del giro
            selectedButton.setEnabled(false);
            selectedButton.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.flip_in));
        }, 150); // Retardo para sincronizar con el giro

        if (firstSelected == null) {
            firstSelected = selectedButton;
        } else {
            secondSelected = selectedButton;
            isClickable = false; // Bloquea más clics
            checkMatch();
        }
    }

    private void checkMatch() {
        if (firstSelected == null || secondSelected == null) return;

        String firstVerb = (String) firstSelected.getTag();
        String secondVerb = (String) secondSelected.getTag();

        new Handler().postDelayed(() -> {
            if (isPair(firstVerb, secondVerb)) {
                firstSelected.setVisibility(View.INVISIBLE);
                secondSelected.setVisibility(View.INVISIBLE);
                matches++;

                String pastVerb = getPastVerb(firstVerb, secondVerb);
                showCustomToast(pastVerb); // Mostrar imagen única y reproducir audio del pasado

                if (matches == verbs.size() / 2) {
                    countDownTimer.cancel(); // Detiene el cronómetro
                    stopTickTackSound();
                    Toast.makeText(this, "¡Felicidades! Has completado el juego.", Toast.LENGTH_LONG).show();
                }
            } else {
                animateCardFlipBack(firstSelected);
                animateCardFlipBack(secondSelected);
            }

            firstSelected = null;
            secondSelected = null;
            isClickable = true;
        }, 1000);
    }


    private void animateCardFlipBack(Button button) {
        button.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.flip_out));
        button.postDelayed(() -> {
            button.setText(""); // Oculta el texto
            button.setEnabled(true);
            button.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.flip_in));
        }, 150);
    }

    private boolean isPair(String verb1, String verb2) {
        return (verb1.equals("run") && verb2.equals("ran")) || (verb1.equals("ran") && verb2.equals("run")) ||
                (verb1.equals("go") && verb2.equals("went")) || (verb1.equals("went") && verb2.equals("go")) ||
                (verb1.equals("eat") && verb2.equals("ate")) || (verb1.equals("ate") && verb2.equals("eat")) ||
                (verb1.equals("write") && verb2.equals("wrote")) || (verb1.equals("wrote") && verb2.equals("write")) ||
                (verb1.equals("read") && verb2.equals("read")) ||
                (verb1.equals("fly") && verb2.equals("flew")) || (verb1.equals("flew") && verb2.equals("fly"));
    }

    private String getPastVerb(String verb1, String verb2) {
        if (verb1.equals("ran") || verb2.equals("ran")) return "ran";
        if (verb1.equals("went") || verb2.equals("went")) return "went";
        if (verb1.equals("ate") || verb2.equals("ate")) return "ate";
        if (verb1.equals("wrote") || verb2.equals("wrote")) return "wrote";
        if (verb1.equals("read") || verb2.equals("read")) return "read"; // Ambos son iguales
        if (verb1.equals("flew") || verb2.equals("flew")) return "flew";
        return null; // Por seguridad
    }

    private void showCustomToast(String pastVerb) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, null);

        ImageView imageView = layout.findViewById(R.id.toastImage);
        TextView textView = layout.findViewById(R.id.toastText);

        int imageResId = getResources().getIdentifier(pastVerb, "drawable", getPackageName());
        int audioResId = getResources().getIdentifier(pastVerb, "raw", getPackageName());

        imageView.setImageResource(imageResId);
        textView.setText(pastVerb);

        MediaPlayer mediaPlayer = MediaPlayer.create(this, audioResId);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(MediaPlayer::release);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTickTackSound(); // Detener el sonido del "tick-tack" cuando la actividad no esté visible
    }

    private void restartGame() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        firstSelected = null;
        secondSelected = null;
        matches = 0;
        isClickable = true;
        loadMemoryGame();
    }

}