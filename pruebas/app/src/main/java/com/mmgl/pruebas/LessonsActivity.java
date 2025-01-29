package com.mmgl.pruebas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class LessonsActivity extends AppCompatActivity {

    private CardView cardBasicPhrases, cardVocabulary, cardConversations, cardTravel;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons);

        // Referencias a los CardViews
        cardBasicPhrases = findViewById(R.id.cardBasicPhrases);
        cardVocabulary = findViewById(R.id.cardVocabulary);
        cardConversations = findViewById(R.id.cardConversations);
        cardTravel = findViewById(R.id.cardTravel);

        // Configurar los Intents para cada CardView
        cardBasicPhrases.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir la actividad de frases básicas
                Intent intent = new Intent(LessonsActivity.this, BasicPhrasesActivity.class);
                startActivity(intent);
            }
        });

        cardVocabulary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir la actividad de vocabulario
                Intent intent = new Intent(LessonsActivity.this, VocabularyActivity.class);
                startActivity(intent);
            }
        });

        cardConversations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir el juego de lectura (ReadingGameActivity)
                Intent intent = new Intent(LessonsActivity.this, ReadingGameActivity.class);
                startActivity(intent);
            }
        });

        cardTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir el juego de adivinar frases (PhraseGuessActivity)
                Intent intent = new Intent(LessonsActivity.this, PhraseGuessActivity.class);
                startActivity(intent);
            }
        });
    }
}
