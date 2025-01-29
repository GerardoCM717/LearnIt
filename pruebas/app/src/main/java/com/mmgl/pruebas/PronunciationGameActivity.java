package com.mmgl.pruebas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class PronunciationGameActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 123;
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;
    private TextView wordTextView;
    private TextView scoreTextView;
    private TextView levelTextView;
    private TextView meaningTextView;
    private ImageButton speakerButton;
    private Button nextButton;
    private Button startRecordingButton;
    private int currentScore = 0;
    private int currentLevel = 1;
    private MediaPlayer correctSound;
    private MediaPlayer incorrectSound;
    private boolean isListening = false;

    // HashMap para almacenar palabras y sus significados
    private HashMap<String, String> words = new HashMap<String, String>() {{
        put("Apple", "A round fruit with red or green skin and white flesh");
        put("Beautiful", "Pleasing to the senses or mind aesthetically");
        put("Computer", "An electronic device for storing and processing data");
        put("Dancing", "Moving rhythmically to music");
        put("Elephant", "A very large plant-eating mammal with a trunk");
        put("Family", "A group of people who are related to each other");
        put("Garden", "A piece of ground used to grow plants, fruits, or vegetables");
        put("Hospital", "A place where sick or injured people receive medical care");
        put("Internet", "A global computer network providing information and communication");
        put("Journey", "An act of traveling from one place to another");
    }};

    private ArrayList<String> wordList;
    private String currentWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pronunciation_game);



        initializeViews();
        initializeTextToSpeech();
        initializeSpeechRecognizer();
        setupButtons();
        initializeSoundEffects();
        checkPermissions();

        // Inicializar lista de palabras y mostrar primera palabra
        wordList = new ArrayList<>(words.keySet());
        showNextWord();
    }

    private void initializeViews() {
        wordTextView = findViewById(R.id.wordTextView);
        scoreTextView = findViewById(R.id.scoreTextView);
        levelTextView = findViewById(R.id.levelTextView);
        meaningTextView = findViewById(R.id.meaningTextView);
        speakerButton = findViewById(R.id.speakerButton);
        nextButton = findViewById(R.id.nextButton);
        startRecordingButton = findViewById(R.id.startRecordingButton);
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                startRecordingButton.setBackgroundResource(R.drawable.rounded_button_active);
            }

            @Override
            public void onBeginningOfSpeech() {
                Toast.makeText(PronunciationGameActivity.this, "Escuchando...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRmsChanged(float v) {}

            @Override
            public void onBufferReceived(byte[] bytes) {}

            @Override
            public void onEndOfSpeech() {
                resetRecordingButton();
            }

            @Override
            public void onError(int errorCode) {
                resetRecordingButton();
                handleSpeechError(errorCode);
            }

            @Override
            public void onResults(Bundle results) {
                resetRecordingButton();
                handleSpeechResults(results);
            }

            @Override
            public void onPartialResults(Bundle bundle) {}

            @Override
            public void onEvent(int i, Bundle bundle) {}
        });
    }

    private void setupButtons() {
        startRecordingButton.setOnClickListener(v -> {
            if (!isListening) {
                startRecording();
            }
        });
        speakerButton.setOnClickListener(v -> speakCurrentWord());
        nextButton.setOnClickListener(v -> showNextWord());
    }

    private void initializeSoundEffects() {
        correctSound = MediaPlayer.create(this, R.raw.correct_sound);
        incorrectSound = MediaPlayer.create(this, R.raw.incorrect_sound);
    }

    private void startRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            isListening = true;
            startRecordingButton.setText("Escuchando...");
            startRecordingButton.setEnabled(false);
            startListening();
        } else {
            checkPermissions();
        }
    }

    private void resetRecordingButton() {
        isListening = false;
        startRecordingButton.setText("Presiona para hablar");
        startRecordingButton.setEnabled(true);
        startRecordingButton.setBackgroundResource(R.drawable.rounded_button);
    }

    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        try {
            speechRecognizer.startListening(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error iniciando el reconocimiento de voz", Toast.LENGTH_SHORT).show();
            resetRecordingButton();
        }
    }

    private void speakCurrentWord() {
        if (currentWord != null) {
            textToSpeech.speak(currentWord, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void showNextWord() {
        if (!wordList.isEmpty()) {
            Random random = new Random();
            int index = random.nextInt(wordList.size());
            currentWord = wordList.get(index);

            wordTextView.setText(currentWord);
            meaningTextView.setText(words.get(currentWord));
            levelTextView.setText("Level: " + currentLevel);
            scoreTextView.setText("Score: " + currentScore);

            speakCurrentWord();
            enableGameControls(true);
        } else {
            showGameComplete();
        }
    }

    private void handleSpeechResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        if (matches != null && !matches.isEmpty()) {
            boolean correctPronunciation = false;
            for (String result : matches) {
                if (result.toLowerCase().trim().equals(currentWord.toLowerCase().trim())) {
                    correctPronunciation = true;
                    break;
                }
            }

            if (correctPronunciation) {
                handleCorrectPronunciation();
            } else {
                handleIncorrectPronunciation(matches.get(0));
            }
        }
    }

    private void handleCorrectPronunciation() {
        currentScore += 10;
        playCorrectSound();
        Toast.makeText(this, "¡Excelente pronunciación! +10 puntos", Toast.LENGTH_SHORT).show();
        scoreTextView.setText("Score: " + currentScore);

        // Animación de éxito
        wordTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        wordTextView.postDelayed(() -> {
            wordTextView.setTextColor(getResources().getColor(R.color.text_primary));
            wordList.remove(currentWord);
            checkLevelProgress();
            showNextWord();
        }, 1000);
    }

    private void handleIncorrectPronunciation(String spokenText) {
        playIncorrectSound();
        Toast.makeText(this, "Intenta de nuevo. Dijiste: " + spokenText, Toast.LENGTH_LONG).show();
    }

    private void handleSpeechError(int errorCode) {
        String errorMessage;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                errorMessage = "Error de audio";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                errorMessage = "Error en el cliente";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                errorMessage = "Permisos insuficientes";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                errorMessage = "Error de red";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                errorMessage = "Tiempo de espera agotado";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                errorMessage = "No se pudo reconocer la palabra";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                errorMessage = "Reconocedor ocupado";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                errorMessage = "Error en el servidor";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                errorMessage = "No se detectó audio";
                break;
            default:
                errorMessage = "Error desconocido";
                break;
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void checkLevelProgress() {
        if (currentScore > 0 && currentScore % 30 == 0) {
            currentLevel++;
            levelTextView.setText("Level: " + currentLevel);
            Toast.makeText(this, "¡Felicidades! Has alcanzado el nivel " + currentLevel,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void showGameComplete() {
        wordTextView.setText("¡Felicitaciones!");
        meaningTextView.setText("Has completado el juego\nPuntaje final: " + currentScore);
        enableGameControls(false);
    }

    private void enableGameControls(boolean enable) {
        startRecordingButton.setEnabled(enable);
        speakerButton.setEnabled(enable);
        nextButton.setEnabled(enable);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.INTERNET
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }

    private void playCorrectSound() {
        if (correctSound != null) {
            correctSound.start();
        }
    }

    private void playIncorrectSound() {
        if (incorrectSound != null) {
            incorrectSound.start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Se necesitan permisos para el micrófono",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        if (correctSound != null) {
            correctSound.release();
        }
        if (incorrectSound != null) {
            incorrectSound.release();
        }
        super.onDestroy();
    }
}