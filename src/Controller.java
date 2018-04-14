import javafx.animation.AnimationTimer;
import javafx.fxml.*;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class Controller {

    private Stage stage;
    private Scene game, lobby;

    private Pane root, overLayer;
    private Label hpLabel, hpLabel2, score, finishLabel;
    private Button resumeplz;
    private AnimationTimer timer;

    private List<Bullet> bullets = new ArrayList<>();
    private List<Bullet> bullets2 = new ArrayList<>();
    private List<Wall> Walls = new ArrayList<>();

    private Player player;
    private Player enemy;

    private double lader = 10; //skudd per antall frames
    private double laderTeller = lader;
    private double laderTellerDelta = 1;

    private int scoreP = 0;
    private int scoreE = 0;

    private double scenewidth = 600;
    private double sceneheigth = 600;

    private BitSet keyboardBitSet = new BitSet();

    public void addInputControls(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            keyboardBitSet.set(event.getCode().ordinal(), true);
        });
        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            keyboardBitSet.set(event.getCode().ordinal(), false);
        });
    }
    public void newRound(){
        player.getView().setTranslateX(50); //flytter spiller tilbake til spawn
        player.getView().setTranslateY(50);
        enemy.getView().setTranslateX(500); //flytter spiller2 tilbake til spawn
        enemy.getView().setTranslateY(500);
        player.getView().setRotate(0);
        enemy.getView().setRotate(180);
        for (Bullet b : bullets){
            b.RemoveBullet(root);
        }
        for (Bullet b : bullets2){
            b.RemoveBullet(root);
        }
        bullets.clear();
        bullets2.clear();
        enemy.setHp(10);
        player.setHp(10);
    }
    public Parent createContent() {

        root = new Pane();
        overLayer = new Pane();

        root.setPrefSize(scenewidth,sceneheigth);

        player = new Player("tank1.png", 10,3, 50,50, root);
        player.setVelocity(new Point2D(0,0));

        enemy = new Player("tank2.png", 10,3, 500,500, root);
        enemy.setVelocity(new Point2D(0,0));
        enemy.getView().setRotate(180);

        Wall vegg = new Wall(25,100,Color.ORANGE,125,100, root);
        Walls.add(vegg);
        Wall vegg2 = new Wall(25,100,Color.ORANGE,450,100, root);
        Walls.add(vegg2);
        Wall vegg3 = new Wall(25,100,Color.ORANGE,125,375, root);
        Walls.add(vegg3);
        Wall vegg4 = new Wall(25,100,Color.ORANGE,450,375, root);
        Walls.add(vegg4);
        Wall vegg5 = new Wall(75,25,Color.ORANGE,150,100, root);
        Walls.add(vegg5);
        Wall vegg6 = new Wall(75,25,Color.ORANGE,375,100, root);
        Walls.add(vegg6);
        Wall vegg7 = new Wall(75,25,Color.ORANGE,150,450, root);
        Walls.add(vegg7);
        Wall vegg8 = new Wall(75,25,Color.ORANGE,375,450, root);
        Walls.add(vegg8);
        Wall vegg9 = new Wall(25,25,Color.ORANGE,scenewidth/2 - 25/2,sceneheigth/2 - 25/2, root);
        Walls.add(vegg9);

        root.getChildren().add(overLayer);

        hpLabel = new Label();
        hpLabel.setTextFill(Color.BLACK);
        overLayer.getChildren().add(hpLabel);

        hpLabel2 = new Label();
        hpLabel2.setTextFill(Color.BLACK);
        overLayer.getChildren().add(hpLabel2);

        finishLabel = new Label();
        finishLabel.setTextFill(Color.BLACK);
        overLayer.getChildren().add(finishLabel);

        score = new Label();
        score.setTextFill(Color.BLACK);
        overLayer.getChildren().add(score);

        resumeplz = new Button("RESUME PLEASE");
        overLayer.getChildren().add(resumeplz);

        return root;
    }
    public void stopContent() {
        timer.stop();
    }
    public void resumeContent() {
        timer.start();
    }
    public Button startButton;
    public void startGame() {
        stage = (Stage) startButton.getScene().getWindow();
        game = new Scene(createContent());
        stage.setScene(game);
        addInputControls(game);
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onUpdate();
            }
        };
        resumeContent();
    }
    public Button resumeButton;
    public void resumeGame() {
        System.out.println("Resuming game ...");
        stage.setScene(game);
        timer.start();
    }
    public Button loadButton;
    public void loadGame() {
        try {
            Save save = (Save) resourceManager.load("1.save");
            System.out.println("Loading game ...");
            scoreP = save.getScoreP();
            scoreE = save.getScoreE();
            newRound();
        } catch (Exception ex){
            if(ex.getMessage() != null)
                System.out.println("KAN IKKE LOADE!: " + ex.getMessage());
        }
    }
    public Button saveButton;
    public void saveGame() {
        Save save = new Save(scoreP,scoreE);
        try {
            resourceManager.save(save,"1.save");
            System.out.println("Saving game ...");
        } catch (Exception ex){
            System.out.println("FUNKER IKKE Å LAGRE " + ex.getMessage());
        }
    }
    public Button exitButton;
    public void exitGame() {
        stage.close();
    }
    private void onUpdate() {
        boolean isWPressed = keyboardBitSet.get(KeyCode.W.ordinal());
        boolean isSPressed = keyboardBitSet.get(KeyCode.S.ordinal());
        boolean isAPressed = keyboardBitSet.get(KeyCode.A.ordinal());
        boolean isDPressed = keyboardBitSet.get(KeyCode.D.ordinal());
        boolean isVPressed = keyboardBitSet.get(KeyCode.V.ordinal());
        boolean isUpPressed = keyboardBitSet.get(KeyCode.UP.ordinal());
        boolean isDownPressed = keyboardBitSet.get(KeyCode.DOWN.ordinal());
        boolean isLeftPressed = keyboardBitSet.get(KeyCode.LEFT.ordinal());
        boolean isRightPressed = keyboardBitSet.get(KeyCode.RIGHT.ordinal());
        boolean isPeriodPressed = keyboardBitSet.get(KeyCode.PERIOD.ordinal());

        double maxX = scenewidth - (player.getWidth() / 2);
        double minX = 0 - (player.getWidth() / 2);
        double maxY = sceneheigth -(player.getHeigth() / 2);
        double minY = 0 - (player.getHeigth() / 2);

        laderTeller += laderTellerDelta;
        if( laderTeller > lader) {
            laderTeller = lader;
        }
        boolean isPistolLadet = laderTeller >= lader;
        //Pause
        resumeplz.setOnAction(e -> {
            stopContent();
            lobby = exitButton.getScene();
            stage.setScene(lobby);
        });
        //skyting player2
        if (isPeriodPressed && isPistolLadet) {
            Bullet bullet2 = new Bullet(5,5,5,Color.RED,enemy.getX(),enemy.getY(), root,enemy);
            //Adder bulleten til gameworld og posisjonen er da samme som player
            bullets2.add(bullet2);
            //resetter pistolklokka
            laderTeller = 0;
        }
        //skyting player1
        if (isVPressed && isPistolLadet) {
            Bullet bullet = new Bullet(5,5,5,Color.RED,player.getX(),player.getY(), root, player);
            //Adder bulleten til gameworld
            bullets.add(bullet);
            //resetter pistolklokka
            laderTeller = 0;
        }

        if (isLeftPressed && !isRightPressed) {
            enemy.rotateLeft();
        } else if ( !isLeftPressed && isRightPressed) {
            enemy.rotateRight();
        }

        if ( isAPressed && !isDPressed) {
            player.rotateLeft();
        } else if ( !isAPressed && isDPressed) {
            player.rotateRight();
        }
        if(isWPressed){
            player.setVelocity(new Point2D(Math.cos(Math.toRadians(player.getRotate())), Math.sin(Math.toRadians(player.getRotate()))));
        }else{
            player.setVelocity(new Point2D(0,0));
        }

        if(isUpPressed){
            enemy.setVelocity(new Point2D(Math.cos(Math.toRadians(enemy.getView().getRotate())), Math.sin(Math.toRadians(enemy.getView().getRotate()))));
        } else  if(isDownPressed){
            enemy.setVelocity(new Point2D(-Math.cos(Math.toRadians(enemy.getView().getRotate())), -Math.sin(Math.toRadians(enemy.getView().getRotate()))));
        }else{
            enemy.setVelocity(new Point2D(0,0));
        }

        if(isWPressed){
            player.setVelocity(new Point2D(Math.cos(Math.toRadians(player.getRotate())), Math.sin(Math.toRadians(player.getRotate()))));
        } else if(isSPressed){
            player.setVelocity(new Point2D(-Math.cos(Math.toRadians(player.getView().getRotate())), -Math.sin(Math.toRadians(player.getView().getRotate()))));
        }else{
            player.setVelocity(new Point2D(0,0));
        }
        //behandler kulekollisjon med person og utkant
        for (int i = 0; i < bullets.size(); i++){
            if(bullets.get(i).isColliding(enemy)) {
                bullets.get(i).RemoveBullet(root);
                bullets.remove(i);
                if (enemy.getHp() != 1) {
                    enemy.setHp(enemy.getHp() - 1);
                } else if(enemy.getLifePoints() != 1){
                    enemy.setHp(10);
                    newRound();
                    enemy.setLifePoints(enemy.getLifePoints() - 1);
                    scoreP++;
                } else {
                    scoreP++;
                    root.getChildren().remove(enemy.getView());
                    enemy.setHp(enemy.getHp() - 1);
                    enemy.setLifePoints(enemy.getLifePoints() - 1);
                    finishLabel.setText("PLAYER 1 WON!");
                }
            } //sjekker om kulene treffer utkant av kartet
            else if (bullets.get(i).getView().getTranslateY() <= 0  || bullets.get(i).getView().getTranslateY() >= sceneheigth+25) {
                bullets.get(i).RemoveBullet(root);
                bullets.remove(i);
            } else if (bullets.get(i).getView().getTranslateX() <= 0  || bullets.get(i).getView().getTranslateX() >= scenewidth+25) {
                bullets.get(i).RemoveBullet(root);
                bullets.remove(i);
            } else {
                for(Wall j : Walls) {
                    if (bullets.get(i).isColliding(j)){
                        bullets.get(i).RemoveBullet(root);
                        bullets.remove(i);
                    }
                }
            }
        }
        //behandler kulekollisjon med person og utkant
        for (int i = 0; i < bullets2.size(); i++){
            if(bullets2.get(i).isColliding(player)) {
                bullets2.get(i).RemoveBullet(root);
                bullets2.remove(i);
                if(player.getHp() != 1){
                    player.setHp(player.getHp() - 1);
                } else if(player.getLifePoints() != 1) {
                    newRound();
                    player.setLifePoints(player.getLifePoints() - 1);
                    scoreE++;
                } else {
                    scoreE++;
                    root.getChildren().remove(player.getView());
                    player.setHp(player.getHp() - 1);
                    player.setLifePoints(player.getLifePoints() - 1);
                    finishLabel.setText("PLAYER 2 WON!");
                }
            } //sjekker om kulene treffer utkant av kartet
            else if (bullets2.get(i).getView().getTranslateY() <= 0  || bullets2.get(i).getView().getTranslateY() >= sceneheigth+25) {
                bullets2.get(i).RemoveBullet(root);
                bullets2.remove(i);
            } else if (bullets2.get(i).getView().getTranslateX() <= 0  || bullets2.get(i).getView().getTranslateX() >= scenewidth+25) {
                bullets2.get(i).RemoveBullet(root);
                bullets2.remove(i);
            } else {
                for(Wall w : Walls) {
                    if (bullets2.get(i).isColliding(w)){
                        bullets2.get(i).RemoveBullet(root);
                        bullets2.remove(i);
                    }
                }
            }
        }
        //kollisjon med veggene spiller 1
        for(Wall i : Walls) {
            if (player.getX() >= i.getMinX() - player.getWidth() && player.getX() <= i.getMinX() - player.getWidth() + 1 && player.getY() >= i.getMinY() - player.getWidth() && player.getY() <= i.getMaxY()) {
                player.getView().setTranslateX(i.getMinX() - player.getWidth());
            } else if (player.getX() >= i.getMaxX() - 1 && player.getX() <= i.getMaxX() && player.getY() >= i.getMinY() - player.getWidth() && player.getY() <= i.getMaxY()) {
                player.getView().setTranslateX(i.getMaxX());
            } else if (player.getX() >= i.getMinX() - player.getWidth() && player.getX() <= i.getMaxX() && player.getY() >= i.getMinY() - player.getWidth() && player.getY() <= i.getMinY() - player.getWidth() + 1) {
                player.getView().setTranslateY(i.getMinY() - player.getWidth());
            } else if (player.getX() >= i.getMinX() - player.getWidth() && player.getX() <= i.getMaxX() && player.getY() >= i.getMaxY() - 1 && player.getY() <= i.getMaxY()) {
                player.getView().setTranslateY(i.getMaxY());
            }
        }
        //kollisjon med veggene spiller 2
        for(Wall i : Walls) {
            if (enemy.getX() >= i.getMinX() - enemy.getWidth() && enemy.getX() <= i.getMinX() - enemy.getWidth() + 1 && enemy.getY() >= i.getMinY() - enemy.getWidth() && enemy.getY() <= i.getMaxY()) {
                enemy.getView().setTranslateX(i.getMinX() - enemy.getWidth());
            } else if (enemy.getX() >= i.getMaxX() - 1 && enemy.getX() <= i.getMaxX() && enemy.getY() >= i.getMinY() - enemy.getWidth() && enemy.getY() <= i.getMaxY()) {
                enemy.getView().setTranslateX(i.getMaxX());
            } else if (enemy.getX() >= i.getMinX() - enemy.getWidth() && enemy.getX() <= i.getMaxX() && enemy.getY() >= i.getMinY() - enemy.getWidth() && enemy.getY() <= i.getMinY() - enemy.getWidth() + 1) {
                enemy.getView().setTranslateY(i.getMinY() - enemy.getWidth());
            } else if (enemy.getX() >= i.getMinX() - enemy.getWidth() && enemy.getX() <= i.getMaxX() && enemy.getY() >= i.getMaxY() - 1 && enemy.getY() <= i.getMaxY()) {
                enemy.getView().setTranslateY(i.getMaxY());
            }
        }
        // går for langt til høyre eller venstre så kommer du ut på andre siden
        if(player.getX() >= maxX) {
            player.getView().setTranslateX(maxX);
        } else if (player.getX() <= minX) {
            player.getView().setTranslateX(minX);
        }
        // går for langt opp eller ned så kommer du ut på andre siden
        if(player.getY() >= maxY) {
            player.getView().setTranslateY(maxY);
        } else if (player.getY() <= minY) {
            player.getView().setTranslateY(minY);
        }
        // går for langt til høyre eller venstre så kommer du ut på andre siden
        if(enemy.getX() >= maxX) {
            enemy.getView().setTranslateX(maxX);
        } else if (enemy.getX() <= minX) {
            enemy.getView().setTranslateX(minX);
        }
        // går for langt opp eller ned så kommer du ut på andre siden
        if(enemy.getY() >= maxY) {
            enemy.getView().setTranslateY(maxY);
        } else if (enemy.getY() <= minY) {
            enemy.getView().setTranslateY(minY);
        }

        hpLabel.setText("PLAYER 1 HP: " + player.getHp());
        hpLabel.setTranslateX(10);
        hpLabel.setTranslateY(10);

        hpLabel2.setText("PLAYER 2 HP: " + enemy.getHp());
        hpLabel2.setTranslateX(scenewidth - hpLabel2.getBoundsInParent().getWidth()-10);
        hpLabel2.setTranslateY(10);

        finishLabel.setTranslateX(scenewidth/2 - finishLabel.getWidth()/2);
        finishLabel.setTranslateY(sceneheigth/2 - 75);
        finishLabel.setFont(new Font(40));

        score.setText(scoreP + " : " + scoreE);
        score.setTranslateX(scenewidth/2 - score.getWidth()/2);
        score.setTranslateY(10);
        score.setFont(new Font(20));

        //oppdaterer posisjon
        bullets.forEach(Bullet::update);
        bullets2.forEach(Bullet::update);
        player.update();
        enemy.update();
    }
}
