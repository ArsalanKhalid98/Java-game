


// Må endre en del av koden så den ikke er lik som forskjellige tutorials på nett ofc, men det fikser vi.


import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class TankMain extends Application {

    private Pane root;

    private List<GameObjects> bullets = new ArrayList<>();
    private List <GameObjects> enemies = new ArrayList<>();

    private GameObjects player;

    private Parent createContent() {

        root = new Pane();
        root.setPrefSize(600,600);

        player = new Player();
        player.setVelocity(new Point2D(1,0));
        addGameObject(player,300,300);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onUpdate();
            }
        };
        timer.start();

        return root;
    }

    private void addBullet(GameObjects bullet, double x, double y) {
        bullets.add(bullet);
        addGameObject(bullet,x,y);
    }

    private void addEnemy(GameObjects enemy, double x, double y) {
        enemies.add(enemy);
        addGameObject(enemy,x,y);
    }

    private void addGameObject(GameObjects object, double x, double y) {
        object.getView().setTranslateX(x);
        object.getView().setTranslateY(y);
        root.getChildren().add(object.getView());
    }

    private void onUpdate() {
        for (GameObjects bullet : bullets) {
            for (GameObjects enemy : enemies) {
                if (bullet.isColliding(enemy)) {
                    bullet.setAlive(false);
                    enemy.setAlive(false);

                    //remover bullet og enemy når de blir hit av bullet
                    root.getChildren().removeAll(bullet.getView(), enemy.getView());
                }
            }
        }

        bullets.removeIf(GameObjects::isDead);
        enemies.removeIf(GameObjects::isDead);

        bullets.forEach(GameObjects::update);
        enemies.forEach(GameObjects::update);


        player.update();

        //Slett denne for å fjerne Røde dotter
        if (Math.random() < 0.02) {
            // istedet for 600, så skriver man root.getPrefWidth,
            addEnemy(new Enemy(), Math.random() * root.getPrefWidth(), Math.random() * root.getPrefHeight());
        }
    }


    private static class Player extends GameObjects {
        Player() {
            super(new ImageView("/anansi.png"));
        }
    }

    private static class Enemy extends GameObjects {
        Enemy() {
            super(new Circle(15,15,15,Color.RED));
        }
    }

    private static class Bullet extends GameObjects {
        Bullet() {
            super(new Circle(5,5,5,Color.BROWN));
        }
    }

    @Override
    public void start(Stage stage) throws Exception {

        stage.setScene(new Scene(createContent()));
        stage.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                player.rotateLeft();
            }

            else if (e.getCode() == KeyCode.RIGHT) {
                player.rotateRight();
            }

            else if (e.getCode() == KeyCode.SPACE) {
                Bullet bullet = new Bullet();
                // Setter bullet velocity til 5 ganger så mye som player
                bullet.setVelocity(player.getVelocity().normalize().multiply(5));
                //Adder bulleten til gameworld og posisjonen er da samme som player
                addBullet(bullet, player.getView().getTranslateX(), player.getView().getTranslateY());
            }
        });

        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}