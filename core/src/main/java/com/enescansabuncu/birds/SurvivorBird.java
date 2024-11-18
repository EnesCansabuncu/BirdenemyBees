package com.enescansabuncu.birds;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import java.util.Random;

public class SurvivorBird extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture background;
    private Texture bird;
    private Texture bee1;
    private Texture bee2;
    private Texture bee3;
    private BitmapFont font;
    private BitmapFont font2;

    private Circle birdCircle;
    private Circle[] enemyCircle1;
    private Circle[] enemyCircle2;
    private Circle[] enemyCircle3;

    private int gameState = 0; // 0 = Başlangıç ekranı, 1 = Oynanıyor, 2 = Oyun bitti
    private int score = 0; // Skor
    private int scoredEnemy = 0; // Skoru sayan düşman

    private float birdx = 0;
    private float birdy = 0;
    private float velocity = 0;
    private float gravity = 0.5F; // Yer çekimi azaltıldı
    private float enemyVelocity = 20; // Düşman hızı azaltıldı
    private float distance = 0;

    private int numberOfEnemies = 4;
    private float[] enemyX = new float[numberOfEnemies];
    private float[] enemyOffset1 = new float[numberOfEnemies];
    private float[] enemyOffset2 = new float[numberOfEnemies];
    private float[] enemyOffset3 = new float[numberOfEnemies];

    private Random random;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("background.png");
        bird = new Texture("birds.png");
        bee1 = new Texture("enemy.png");
        bee2 = new Texture("enemy.png");
        bee3 = new Texture("enemy.png");

        font = new BitmapFont();
        font.setColor(Color.BLUE);
        font.getData().scale(4);

        font2 = new BitmapFont();
        font2.setColor(Color.RED);
        font2.getData().scale(8);

        birdx = Gdx.graphics.getWidth() / 2 - bird.getHeight();
        birdy = Gdx.graphics.getHeight() / 2;

        distance = Gdx.graphics.getWidth() / 2;
        random = new Random();

        birdCircle = new Circle();
        enemyCircle1 = new Circle[numberOfEnemies];
        enemyCircle2 = new Circle[numberOfEnemies];
        enemyCircle3 = new Circle[numberOfEnemies];

        resetGame();
    }

    private void resetGame() {
        birdy = Gdx.graphics.getHeight() / 2;
        velocity = 0;
        score = 0;
        scoredEnemy = 0;

        for (int i = 0; i < numberOfEnemies; i++) {
            enemyOffset1[i] = (random.nextFloat() - 0.5F) * (Gdx.graphics.getHeight() - 200);
            enemyOffset2[i] = (random.nextFloat() - 0.5F) * (Gdx.graphics.getHeight() - 200);
            enemyOffset3[i] = (random.nextFloat() - 0.5F) * (Gdx.graphics.getHeight() - 200);
            enemyX[i] = Gdx.graphics.getWidth() + i * distance;
            enemyCircle1[i] = new Circle();
            enemyCircle2[i] = new Circle();
            enemyCircle3[i] = new Circle();
        }
    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1) { // Oyun devam ediyor
            if (Gdx.input.justTouched()) {
                velocity = -10; // Zıplama hızı azaltıldı
            }

            for (int i = 0; i < numberOfEnemies; i++) {
                if (enemyX[i] < 0) {
                    enemyX[i] = enemyX[i] + numberOfEnemies * distance;
                    enemyOffset1[i] = (random.nextFloat() - 0.5F) * (Gdx.graphics.getHeight() - 200);
                    enemyOffset2[i] = (random.nextFloat() - 0.5F) * (Gdx.graphics.getHeight() - 200);
                    enemyOffset3[i] = (random.nextFloat() - 0.5F) * (Gdx.graphics.getHeight() - 200);
                } else {
                    enemyX[i] -= enemyVelocity;
                }

                batch.draw(bee1, enemyX[i], Gdx.graphics.getHeight() / 2 + enemyOffset1[i], Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 10);
                batch.draw(bee2, enemyX[i], Gdx.graphics.getHeight() / 2 + enemyOffset2[i], Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 10);
                batch.draw(bee3, enemyX[i], Gdx.graphics.getHeight() / 2 + enemyOffset3[i], Gdx.graphics.getWidth() / 15, Gdx.graphics.getHeight() / 10);

                // Düşman çemberlerini ayarla
                enemyCircle1[i].set(enemyX[i] + Gdx.graphics.getWidth() / 30, Gdx.graphics.getHeight() / 2 + enemyOffset1[i] + Gdx.graphics.getHeight() / 20, Gdx.graphics.getWidth() / 30);
                enemyCircle2[i].set(enemyX[i] + Gdx.graphics.getWidth() / 30, Gdx.graphics.getHeight() / 2 + enemyOffset2[i] + Gdx.graphics.getHeight() / 20, Gdx.graphics.getWidth() / 30);
                enemyCircle3[i].set(enemyX[i] + Gdx.graphics.getWidth() / 30, Gdx.graphics.getHeight() / 2 + enemyOffset3[i] + Gdx.graphics.getHeight() / 20, Gdx.graphics.getWidth() / 30);
            }

            // Kuş düşmanı geçtiyse skoru artır
            if (enemyX[scoredEnemy] < birdx) {
                score++;
                scoredEnemy++;
                if (scoredEnemy >= numberOfEnemies) {
                    scoredEnemy = 0;
                }
            }

            // Kuş hareketi
            if (birdy > 0) {
                velocity += gravity;
                birdy -= velocity;
            } else {
                gameState = 2; // Oyun bitti
            }
        } else if (gameState == 0) { // Başlangıç ekranı
            if (Gdx.input.justTouched()) {
                gameState = 1; // Oyun başlasın
            }
        } else if (gameState == 2) { // Oyun bitti
            font2.draw(batch, "GAME OVER", 100, Gdx.graphics.getHeight() / 2);
            if (Gdx.input.justTouched()) {
                resetGame();
                gameState = 1;
            }
        }

        // Kuşu çiz
        batch.draw(bird, birdx, birdy, Gdx.graphics.getWidth() / 17, Gdx.graphics.getHeight() / 10);

        // Skoru yazdır
        font.draw(batch, String.valueOf(score), 100, 200);

        batch.end();

        // Çarpışma kontrolü
        birdCircle.set(birdx + Gdx.graphics.getWidth() / 32, birdy + Gdx.graphics.getHeight() / 20, Gdx.graphics.getWidth() / 34);
        for (int i = 0; i < numberOfEnemies; i++) {
            if (Intersector.overlaps(birdCircle, enemyCircle1[i]) || Intersector.overlaps(birdCircle, enemyCircle2[i]) || Intersector.overlaps(birdCircle, enemyCircle3[i])) {
                gameState = 2; // Oyun bitti
            }
        }
    }
}
