package com.mmgl.pruebas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {

    // Constantes
    private static final float MALLET_RADIUS = 50f;
    private static final float PUCK_RADIUS = 40f;
    private static final float GOAL_WIDTH_PERCENT = 0.3f;
    private static final float FRICTION = 0.98f;
    private static final float START_ZONE_HEIGHT = 100f;
    private static final float START_ZONE_WIDTH = 200f;

    private boolean player1Ready = false;
    private boolean player2Ready = false;
    private boolean gameStarted = false;

    private RectF player1StartZone;
    private RectF player2StartZone;

    private Paint startZonePaint;
    private Paint textPaint;

    // Pintura y objetos gráficos
    private Paint tablePaint;
    private Paint linePaint;
    private Paint malletPaint1;
    private Paint malletPaint2;
    private Paint puckPaint;
    private Path centerLine;

    // Estado del juego
    private float puckX, puckY;
    private float puckSpeedX, puckSpeedY;
    private float mallet1X, mallet1Y;
    private float mallet2X, mallet2Y;
    private int player1Score = 0;
    private int player2Score = 0;
    private boolean player1Touching = false;
    private boolean player2Touching = false;
    private boolean isGameRunning = true;


    private float tableWidth;
    private float tableHeight;
    private float goalWidth;

    private SparseArray<TouchInfo> activePointers;

    private static class TouchInfo {
        int playerId;  // 1 para jugador superior, 2 para jugador inferior
        float x, y;    // Coordenadas del toque

        TouchInfo(int playerId, float x, float y) {
            this.playerId = playerId;
            this.x = x;
            this.y = y;
        }
    }

    public interface GameStateListener {
        void onGameStart();
        void onGameEnd(int winner); // 1 para jugador 1, 2 para jugador 2, 0 para empate
    }
    private GameStateListener gameStateListener;

    // Listener para actualización de puntaje
    public interface ScoreUpdateListener {
        void onScoreUpdate(int player1Score, int player2Score);
    }
    private ScoreUpdateListener scoreUpdateListener;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        startZonePaint = new Paint();
        startZonePaint.setStyle(Paint.Style.STROKE);
        startZonePaint.setColor(Color.YELLOW);
        startZonePaint.setStrokeWidth(5f);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(40f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // Inicializar zonas de inicio
        player1StartZone = new RectF();
        player2StartZone = new RectF();

        // Inicializar pinturas
        setupPaints();
        activePointers = new SparseArray<>();

        setupPaints();
        centerLine = new Path();
    }

    private void setupPaints() {
        // Pintura para la mesa
        tablePaint = new Paint();
        tablePaint.setColor(Color.WHITE);
        tablePaint.setStyle(Paint.Style.FILL);

        // Pintura para líneas
        linePaint = new Paint();
        linePaint.setColor(Color.rgb(200, 200, 200));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(3f);

        // Pintura para mazo verde (jugador 1)
        malletPaint1 = new Paint();
        malletPaint1.setColor(Color.rgb(76, 175, 80));
        malletPaint1.setStyle(Paint.Style.FILL);
        malletPaint1.setAntiAlias(true);

        // Pintura para mazo rojo (jugador 2)
        malletPaint2 = new Paint();
        malletPaint2.setColor(Color.rgb(244, 67, 54));
        malletPaint2.setStyle(Paint.Style.FILL);
        malletPaint2.setAntiAlias(true);

        // Pintura para el disco
        puckPaint = new Paint();
        puckPaint.setColor(Color.rgb(33, 150, 243));
        puckPaint.setStyle(Paint.Style.FILL);
        puckPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        tableWidth = w;
        tableHeight = h;
        goalWidth = tableWidth * GOAL_WIDTH_PERCENT;

        // Posición inicial del disco
        resetPuck();

        // Posición inicial de los mazos
        mallet1X = tableWidth / 2;
        mallet1Y = tableHeight / 4;
        mallet2X = tableWidth / 2;
        mallet2Y = tableHeight * 3 / 4;

        float centerX = w / 2;
        player1StartZone.set(
                centerX - START_ZONE_WIDTH/2,
                START_ZONE_HEIGHT,
                centerX + START_ZONE_WIDTH/2,
                START_ZONE_HEIGHT * 2
        );

        player2StartZone.set(
                centerX - START_ZONE_WIDTH/2,
                h - START_ZONE_HEIGHT * 2,
                centerX + START_ZONE_WIDTH/2,
                h - START_ZONE_HEIGHT
        );

        // Ocultar mazos inicialmente
        mallet1Y = -MALLET_RADIUS * 2;
        mallet2Y = tableHeight + MALLET_RADIUS * 2;

        // Configurar gradiente para la mesa
        tablePaint.setShader(new LinearGradient(
                0, 0, 0, h,
                new int[]{Color.rgb(240, 240, 240), Color.WHITE, Color.rgb(240, 240, 240)},
                null,
                Shader.TileMode.CLAMP
        ));
    }

    private void resetPuck() {
        puckX = tableWidth / 2;
        puckY = tableHeight / 2;
        puckSpeedX = 0;
        puckSpeedY = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Dibujar mesa
        drawTable(canvas);

        if (!gameStarted) {
            // Dibujar zonas de inicio
            canvas.drawRect(player1StartZone, startZonePaint);
            canvas.drawRect(player2StartZone, startZonePaint);

            // Dibujar indicadores de estado
            String player1Text = player1Ready ? "Jugador 1 Listo!" : "Toca para comenzar";
            String player2Text = player2Ready ? "Jugador 2 Listo!" : "Toca para comenzar";

            canvas.drawText(player1Text, tableWidth/2, START_ZONE_HEIGHT * 1.5f, textPaint);
            canvas.drawText(player2Text, tableWidth/2, tableHeight - START_ZONE_HEIGHT * 1.5f, textPaint);
        } else {
            // Dibujar elementos del juego normalmente
            drawGameElements(canvas);

            if (isGameRunning) {
                updatePhysics();
                invalidate();
            }
        }
    }

    private void drawTable(Canvas canvas) {
        // Fondo de la mesa
        canvas.drawRect(0, 0, tableWidth, tableHeight, tablePaint);

        // Línea central
        canvas.drawLine(0, tableHeight/2, tableWidth, tableHeight/2, linePaint);

        // Círculo central
        canvas.drawCircle(tableWidth/2, tableHeight/2, tableWidth/6, linePaint);

        // Porterías
        drawGoals(canvas);
    }

    private void drawGoals(Canvas canvas) {
        float goalStart = (tableWidth - goalWidth) / 2;
        float goalEnd = goalStart + goalWidth;

        // Portería superior
        canvas.drawRect(goalStart, 0, goalEnd, 20, linePaint);

        // Portería inferior
        canvas.drawRect(goalStart, tableHeight - 20, goalEnd, tableHeight, linePaint);
    }

    private void drawGameElements(Canvas canvas) {
        // Dibujar mazos
        canvas.drawCircle(mallet1X, mallet1Y, MALLET_RADIUS, malletPaint1);
        canvas.drawCircle(mallet2X, mallet2Y, MALLET_RADIUS, malletPaint2);

        // Dibujar disco
        canvas.drawCircle(puckX, puckY, PUCK_RADIUS, puckPaint);
    }

    private void updatePhysics() {
        // Actualizar posición del disco
        puckX += puckSpeedX;
        puckY += puckSpeedY;

        // Colisiones con paredes
        handleWallCollisions();

        // Aplicar fricción
        puckSpeedX *= FRICTION;
        puckSpeedY *= FRICTION;

        // Detectar goles
        checkForGoals();
    }

    private void handleWallCollisions() {
        // Colisión con paredes laterales
        if (puckX - PUCK_RADIUS < 0) {
            puckX = PUCK_RADIUS;
            puckSpeedX = -puckSpeedX * 0.8f;
        } else if (puckX + PUCK_RADIUS > tableWidth) {
            puckX = tableWidth - PUCK_RADIUS;
            puckSpeedX = -puckSpeedX * 0.8f;
        }

        // Colisión con paredes superior e inferior (fuera de las porterías)
        float goalStart = (tableWidth - goalWidth) / 2;
        float goalEnd = goalStart + goalWidth;

        if (puckY - PUCK_RADIUS < 0) {
            if (puckX < goalStart || puckX > goalEnd) {
                puckY = PUCK_RADIUS;
                puckSpeedY = -puckSpeedY * 0.8f;
            }
        } else if (puckY + PUCK_RADIUS > tableHeight) {
            if (puckX < goalStart || puckX > goalEnd) {
                puckY = tableHeight - PUCK_RADIUS;
                puckSpeedY = -puckSpeedY * 0.8f;
            }
        }
    }

    private void checkForGoals() {
        float goalStart = (tableWidth - goalWidth) / 2;
        float goalEnd = goalStart + goalWidth;

        // Gol en la portería superior
        if (puckY - PUCK_RADIUS < 0 && puckX > goalStart && puckX < goalEnd) {
            player2Score++;
            updateScore();
            resetPuck();
        }

        // Gol en la portería inferior
        if (puckY + PUCK_RADIUS > tableHeight && puckX > goalStart && puckX < goalEnd) {
            player1Score++;
            updateScore();
            resetPuck();
        }
    }

    private void updateMalletPositions(int pointerId, float x, float y) {
        TouchInfo touchInfo = activePointers.get(pointerId);
        if (touchInfo == null) return;

        if (touchInfo.playerId == 1) {
            // Jugador 1 (superior)
            player1Touching = true;
            mallet1X = constrain(x, MALLET_RADIUS, tableWidth - MALLET_RADIUS);
            mallet1Y = constrain(y, MALLET_RADIUS, tableHeight / 2 - MALLET_RADIUS);
            checkMalletPuckCollision(mallet1X, mallet1Y, 1);
        } else {
            // Jugador 2 (inferior)
            player2Touching = true;
            mallet2X = constrain(x, MALLET_RADIUS, tableWidth - MALLET_RADIUS);
            mallet2Y = constrain(y, tableHeight / 2 + MALLET_RADIUS, tableHeight - MALLET_RADIUS);
            checkMalletPuckCollision(mallet2X, mallet2Y, 2);
        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);
        int maskedAction = event.getActionMasked();
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

        // Manejo de la fase inicial del juego
        if (!gameStarted) {
            switch (maskedAction) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    // Verificar si el toque está en las zonas de inicio
                    if (player1StartZone.contains(x, y)) {
                        player1Ready = true;
                        mallet1X = x;
                        mallet1Y = y;
                    } else if (player2StartZone.contains(x, y)) {
                        player2Ready = true;
                        mallet2X = x;
                        mallet2Y = y;
                    }

                    // Si ambos jugadores están listos, iniciar el juego
                    if (player1Ready && player2Ready) {
                        gameStarted = true;
                        if (gameStateListener != null) {
                            gameStateListener.onGameStart();
                        }
                    }
                    invalidate();
                    return true;
            }
            return true;
        }

        // Manejo del juego en curso
        switch (maskedAction) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                // Nuevo toque
                TouchInfo touchInfo = null;
                if (y < tableHeight / 2) {
                    // Zona del jugador 1
                    touchInfo = new TouchInfo(1, x, y);
                    player1Touching = true;
                } else {
                    // Zona del jugador 2
                    touchInfo = new TouchInfo(2, x, y);
                    player2Touching = true;
                }

                if (touchInfo != null) {
                    activePointers.put(pointerId, touchInfo);
                    updateMalletPositions(pointerId, x, y);
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // Actualizar todos los toques activos
                for (int i = 0; i < event.getPointerCount(); i++) {
                    int id = event.getPointerId(i);
                    TouchInfo touchInfo = activePointers.get(id);
                    if (touchInfo != null) {
                        float touchX = event.getX(i);
                        float touchY = event.getY(i);
                        touchInfo.x = touchX;
                        touchInfo.y = touchY;
                        updateMalletPositions(id, touchX, touchY);
                    }
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL: {
                // Eliminar el toque
                TouchInfo touchInfo = activePointers.get(pointerId);
                if (touchInfo != null) {
                    if (touchInfo.playerId == 1) {
                        player1Touching = false;
                    } else {
                        player2Touching = false;
                    }
                    activePointers.remove(pointerId);
                }
                break;
            }
        }

        invalidate();
        return true;
    }



    private float constrain(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }


    private void checkMalletPuckCollision(float malletX, float malletY, int player) {
        float dx = puckX - malletX;
        float dy = puckY - malletY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance < (MALLET_RADIUS + PUCK_RADIUS)) {
            // Calcular nuevo vector de velocidad
            float angle = (float) Math.atan2(dy, dx);
            // Aumentar la velocidad cuando se golpea el disco
            float power = 50f;  // Ajusta este valor para el incremento de velocidad deseado

            puckSpeedX = (float) (power * Math.cos(angle));
            puckSpeedY = (float) (power * Math.sin(angle));

            // Reposicionar el disco para evitar que se pegue al mazo
            puckX = malletX + (MALLET_RADIUS + PUCK_RADIUS) * (float)Math.cos(angle);
            puckY = malletY + (MALLET_RADIUS + PUCK_RADIUS) * (float)Math.sin(angle);
        }
    }

    private void updateScore() {
        if (scoreUpdateListener != null) {
            scoreUpdateListener.onScoreUpdate(player1Score, player2Score);
        }
    }

    public void setScoreUpdateListener(ScoreUpdateListener listener) {
        this.scoreUpdateListener = listener;
    }

    public void endGame() {
        isGameRunning = false;
        // Determinar el ganador
        int winner;
        if (player1Score > player2Score) {
            winner = 1;
        } else if (player2Score > player1Score) {
            winner = 2;
        } else {
            winner = 0; // Empate
        }

        if (gameStateListener != null) {
            gameStateListener.onGameEnd(winner);
        }
    }

    public void setGameStateListener(GameStateListener listener) {
        this.gameStateListener = listener;
    }

    public void resetGame() {
        player1Ready = false;
        player2Ready = false;
        gameStarted = false;
        player1Score = 0;
        player2Score = 0;
        resetPuck();
        updateScore();
        invalidate();
    }
}