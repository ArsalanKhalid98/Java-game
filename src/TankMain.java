import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class TankMain extends Application {

    private Pane root;

    private List<GameObjects> bullets = new ArrayList<>();
    private List<GameObjects> bullets2 = new ArrayList<>();

    private GameObjects player;
    private GameObjects enemy;
    private GameObjects wall1;
    private GameObjects wall2;
    private GameObjects wall3;
    private GameObjects wall4;

    double pistollader = 10;
    double pistolladerteller = pistollader;
    double pistolladertellerDelta = 1;

    double playerHP = 5;
    double enemyHP = 5;

    public BitSet keyboardBitSet = new BitSet();

    private void addInputControls(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            keyboardBitSet.set(event.getCode().ordinal(), true);
        });
        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            keyboardBitSet.set(event.getCode().ordinal(), false);
        });
    }

    private Parent createContent() {

        root = new Pane();
        root.setPrefSize(600,600);

        player = new Player();
        player.setVelocity(new Point2D(1,0));
        addGameObject(player,100,100);

        enemy = new Enemy();
        enemy.setVelocity(new Point2D(1,0));
        addGameObject(enemy,100,500);

        wall1 = new Wall();
        addGameObject(wall1,200,300);

        wall2 = new Wall();
        addGameObject(wall2,200,150);

        wall3 = new Wall();
        addGameObject(wall3,400,150);

        wall4 = new Wall();
        addGameObject(wall4,400,300);

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
    private void addBullet2(GameObjects bullet, double x, double y) {
        bullets2.add(bullet);
        addGameObject(bullet,x,y);
    }

    private void addGameObject(GameObjects object, double x, double y) {
        object.getView().setTranslateX(x);
        object.getView().setTranslateY(y);
        root.getChildren().add(object.getView());
    }

    private static class Player extends GameObjects {
        Player() {
            super(new ImageView("/tank1.png"));
        }
    }

    private static class Enemy extends GameObjects {
        Enemy() {
            super(new ImageView("/tank2.png"));
        }
    }

    private static class Bullet extends GameObjects {
        Bullet() {
            super(new Circle(5,5,5,Color.BROWN));
        }
    }

    private static class Wall extends GameObjects {
        Wall() {
            super(new Rectangle(20,70,Color.ORANGE));
        }
    }

    @Override
    public void start(Stage stage) throws Exception {

        stage.setScene(new Scene(createContent()));

        addInputControls(stage.getScene());

        stage.show();

    }

    private void onUpdate() {

        boolean isAPressed = keyboardBitSet.get(KeyCode.A.ordinal());
        boolean isDPressed = keyboardBitSet.get(KeyCode.D.ordinal());
        boolean isWPressed = keyboardBitSet.get(KeyCode.W.ordinal());
        boolean isLeftPressed = keyboardBitSet.get(KeyCode.LEFT.ordinal());
        boolean isRightPressed = keyboardBitSet.get(KeyCode.RIGHT.ordinal());
        boolean isUpPressed = keyboardBitSet.get(KeyCode.UP.ordinal());

        pistolladerteller += pistolladertellerDelta;
        if( pistolladerteller > pistollader) {
            pistolladerteller = pistollader;
        }

        boolean isPistolLadet = pistolladerteller >= pistollader;

        if (isUpPressed && isPistolLadet) {
            Bullet bullet = new Bullet();
            // Setter bullet velocity til 5 ganger så mye som player
            bullet.setVelocity(player.getVelocity().normalize().multiply(5));

            //Adder bulleten til gameworld og posisjonen er da samme som player
            addBullet(bullet, player.getView().getTranslateX(), player.getView().getTranslateY());
            pistolladerteller = 0;
        }
        if (isWPressed && isPistolLadet) {
            Bullet bullet2 = new Bullet();
            // Setter bullet velocity til 5 ganger så mye som enemy
            bullet2.setVelocity(enemy.getVelocity().normalize().multiply(5));

            //Adder bulleten til gameworld og posisjonen er da samme som enemy
            addBullet2(bullet2, enemy.getView().getTranslateX(), enemy.getView().getTranslateY());
            pistolladerteller = 0;
        }

        if (isLeftPressed && !isRightPressed) {
            player.rotateLeft();
        } else if ( !isLeftPressed && isRightPressed) {
            player.rotateRight();
        }

        if ( isAPressed && !isDPressed) {
            enemy.rotateLeft();
        } else if ( !isAPressed && isDPressed) {
            enemy.rotateRight();
        }

        for (int i = 0; i < bullets.size(); i++){
            if(bullets.get(i).isColliding(enemy)) {
                bullets.get(i).getView().setTranslateX(-100);
                bullets.get(i).getView().setTranslateY(-100);
                bullets.get(i).setVelocity(new Point2D(0,0));
                if (enemyHP != 0) {
                    enemyHP = enemyHP - 1;
                } else {
                    enemyHP = 5;
                    addGameObject(enemy, 500, 500);
                }

            }
        }

        for (int i = 0; i < bullets2.size(); i++){
            if(bullets2.get(i).isColliding(player)) {
                bullets2.get(i).getView().setTranslateX(-100);
                bullets2.get(i).getView().setTranslateY(-100);
                bullets2.get(i).setVelocity(new Point2D(0,0));
                if(playerHP != 0){
                    playerHP = playerHP - 1;
                } else {
                    playerHP = 5;
                    addGameObject(player,100,100);
                }
            }
        }

        bullets.forEach(GameObjects::update);
        bullets2.forEach(GameObjects::update);

        player.update();
        enemy.update();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
