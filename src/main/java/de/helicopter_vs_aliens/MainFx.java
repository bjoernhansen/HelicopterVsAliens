package de.helicopter_vs_aliens;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.EnemyController;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.GameStatisticsCalculator;
import de.helicopter_vs_aliens.control.entities.ActiveGameEntityManager;
import de.helicopter_vs_aliens.control.entities.GameEntityFactory;
import de.helicopter_vs_aliens.control.entities.GameEntitySupplier;
import de.helicopter_vs_aliens.control.ressource_transfer.GameResources;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.control.timer.Timer;
import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.graphics.JavaFxAdapter;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.gui.window.WindowManager;
import de.helicopter_vs_aliens.model.GameEntity;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;
import de.helicopter_vs_aliens.model.missile.EnemyMissile;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.model.scenery.SceneryObject;
import de.helicopter_vs_aliens.score.Savegame;
import de.helicopter_vs_aliens.util.Colorations;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Queue;

import static de.helicopter_vs_aliens.gui.WindowType.GAME;


public class MainFx extends Application implements GameRessourceProvider
{
    public static final Dimension
        VIRTUAL_DIMENSION = new Dimension(1024, 461);

    private final Button
        button = new Button("Werkstatt");

    private Savegame
        saveGame;

    private Helicopter
        helicopter = HelicopterType.getDefault().makeInstance();

    private final Scenery
        scenery = new Scenery(this);

    private final WindowManager
        windowManager = new WindowManager();

    private final GameEntitySupplier
        gameEntitySupplier = new GameEntitySupplier();

    private final GameStatisticsCalculator
        gameStatisticsCalculator = GameStatisticsCalculator.getInstance();

    private final ActiveGameEntityManager
        activeGameEntityManager = ActiveGameEntityManager.getInstance();

    private GraphicsAdapter
        graphicsAdapter;

    private GraphicsAdapter
        graphicsFxAdapter;

    private Image
        offImage;

    private int
        framesCounter = 0;


    public MainFx()
    {
        GameResources.setGameResources(this);
    }

    @Override
    public void start(final Stage primaryStage)
    {
        button.setOnAction(event -> {}/*primaryStage.close()*/);
        Canvas canvas = new Canvas(VIRTUAL_DIMENSION.width, VIRTUAL_DIMENSION.height);

        canvas.setOnMouseMoved(this::mouseMoveEvent);
        canvas.setOnMouseDragged(this::mouseMoveEvent);
        canvas.setOnMousePressed(this::mouseClickEvent);
        canvas.setOnMouseReleased(this::mouseClickEvent);

/*
        @Override
        public void keyPressed	 (KeyEvent e){Events.keyTyped(e, this);}

        @Override
        public void mousePressed (java.awt.event.MouseEvent e){Events.mousePressed(e, this);}

        @Override
        public void mouseReleased(java.awt.event.MouseEvent e){Events.mouseReleased(e, helicopter);}

        @Override
        public void mouseDragged (java.awt.event.MouseEvent e){Events.mouseMovedOrDragged(e, helicopter);}

        @Override
        public void mouseMoved   (java.awt.event.MouseEvent e){Events.mouseMovedOrDragged(e, helicopter);}

              */

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY))); // Setze den Hintergrund auf Rot
        anchorPane.getChildren().add(canvas);
        anchorPane.getChildren().add(button);

        anchorPane.setOnKeyPressed(this::keyEvent);


        var scene = new Scene(anchorPane);
        primaryStage.setScene(scene);


        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setFullScreen(true);
        primaryStage.show();


        Main.displayShift = new Dimension(
            (int) ((primaryStage.getWidth() - VIRTUAL_DIMENSION.width) / 2.0),
            (int) ((primaryStage.getHeight() - VIRTUAL_DIMENSION.height) / 2.0));

        double verticalAnchorDistance = Main.displayShift.getHeight() + VIRTUAL_DIMENSION.height - 10 - 25;
        AnchorPane.setTopAnchor(button, verticalAnchorDistance);
        double horizontalAnchorDistance = Main.displayShift.getWidth() + VIRTUAL_DIMENSION.width / 2.0 - 60;
        AnchorPane.setLeftAnchor(button, horizontalAnchorDistance);
        AnchorPane.setRightAnchor(button, horizontalAnchorDistance);

        AnchorPane.setLeftAnchor(canvas, Main.displayShift.getWidth());
        AnchorPane.setTopAnchor(canvas, Main.displayShift.getHeight());


        System.out.println(primaryStage.getWidth());
        System.out.println(primaryStage.getHeight());

        // primaryStage.setResizable(false);
        // primaryStage.setOpacity(0.5);


        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();

        graphicsFxAdapter = new JavaFxAdapter(graphicsContext2D);


        saveGame = Savegame.initialize();
        Audio.initialize();


        offImage = new BufferedImage((int) Main.VIRTUAL_DIMENSION.getWidth(), (int) Main.VIRTUAL_DIMENSION.getHeight(), BufferedImage.TYPE_INT_RGB);


        graphicsAdapter = Graphics2DAdapter.of(offImage);
        graphicsAdapter.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        Window.initialize();
        Window.updateButtonLabels(helicopter);
        scenery.createInitialSceneryObjects();
        Audio.refreshBackgroundMusic();


        new AnimationTimer()
        {
            @Override
            public void handle(long now)
            {
                graphicsFxAdapter.drawImage(offImage, 0, 0);

                updateGame();

                graphicsAdapter.setColor(Colorations.bg);
                graphicsAdapter.fillRect(0, 0, VIRTUAL_DIMENSION.width, VIRTUAL_DIMENSION.height);
                paintFrame(graphicsAdapter);
            }
        }.start();
    }

    private void mouseClickEvent(MouseEvent event)
    {
        System.out.println("mouseClickEvent");
        System.out.println(event.getEventType());
        System.out.println(event.getButton());
        System.out.println(event.getX() + " " + event.getY());
    }

    private void mouseMoveEvent(MouseEvent event)
    {
        System.out.println("mouseMoveEvent");
        System.out.println(event.getEventType());
        System.out.println(event.getButton());
        System.out.println(event.getX() + " " + event.getY());
    }

    private void keyEvent(KeyEvent event)
    {
        System.out.println("keyEvent!");
        System.out.println(event.getCharacter());
        System.out.println(event.getEventType());
        System.out.println(event.getCode());
        System.out.println(event.getText());
    }

    public static void main(String[] args)
    {
        Application.launch(MainFx.class);
    }


    @Override
    public Map<CollectionSubgroupType, Queue<Enemy>> getEnemies()
    {
        return activeGameEntityManager.getEnemies();
    }

    @Override
    public Map<CollectionSubgroupType, Queue<Missile>> getMissiles()
    {
        return activeGameEntityManager.getMissiles();
    }

    @Override
    public Map<CollectionSubgroupType, Queue<Explosion>> getExplosions()
    {
        return activeGameEntityManager.getExplosions();
    }

    @Override
    public Map<CollectionSubgroupType, Queue<SceneryObject>> getSceneryObjects()
    {
        return activeGameEntityManager.getSceneryObjects();
    }

    @Override
    public Map<CollectionSubgroupType, Queue<EnemyMissile>> getEnemyMissiles()
    {
        return activeGameEntityManager.getEnemyMissiles();
    }

    @Override
    public Map<CollectionSubgroupType, Queue<PowerUp>> getPowerUps()
    {
        return activeGameEntityManager.getPowerUps();
    }

    @Override
    public GameStatisticsCalculator getGameStatisticsCalculator()
    {
        return gameStatisticsCalculator;
    }

    @Override
    public Scenery getScenery()
    {
        return this.scenery;
    }

    @Override
    public Savegame getSaveGame()
    {
        return saveGame;
    }

    @Override
    public GameEntitySupplier getGameEntitySupplier()
    {
        return gameEntitySupplier;
    }

    @Override
    public ActiveGameEntityManager getActiveGameEntityManager()
    {
        return activeGameEntityManager;
    }

    @Override
    public <T extends GameEntity> T getNewGameEntityInstance(GameEntityFactory<T> factory)
    {
        return gameEntitySupplier.retrieve(factory);
    }

    @Override
    public boolean isMouseCursorInWindow()
    {
        // TODO not necessary
        return true;
    }

    @Override
    public boolean isFpsDisplayVisible()
    {
        // TODO not necessary
        return true;
    }

    @Override
    public boolean isAntialiasingActivated()
    {
        // TODO not necessary
        return false;
    }

    @Override
    public int getFramesCounter()
    {
        // TODO not necessary
        return 0;
    }

    @Override
    public Helicopter getHelicopter()
    {
        return helicopter;
    }

    @Override
    public void setHelicopter(Helicopter helicopter)
    {
        this.helicopter = helicopter;
        Window.dictionary.switchHelicopterTypeTo(helicopter.getType());
    }

    private void updateGame()
    {
        this.framesCounter++;
        Timer.countDownActiveTimers();
        if (WindowManager.window == GAME)
        {
            // TODO long duplicated code fragment -> common use
            if (!Window.isMenuVisible)
            {
                Colorations.calculateVariableGameColors(framesCounter);

                scenery.update(this);
                Events.updateTimer();
                Window.updateDisplays(this);
                Enemy.updateAllDestroyed(this);
                Missile.updateAll(this);
                EnemyController.updateAllActive(this);
                EnemyMissile.updateAll(this);
                Events.checkForLevelUp(this);
                EnemyController.generateNewEnemies(this);
                this.helicopter.update(this);
                Explosion.updateAll(this);
                PowerUp.updateAll(this);
            }
        }
        else
        {
            Colorations.calculateVariableMenuColors(framesCounter);
            Window.update(this);
        }
    }

    private void paintFrame(GraphicsAdapter graphicsAdapter)
    {
        GraphicsManager.getInstance().setGraphics(graphicsAdapter);
        windowManager.paintWindow(graphicsAdapter);
    }
}