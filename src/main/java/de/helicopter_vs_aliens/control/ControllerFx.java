package de.helicopter_vs_aliens.control;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.entities.ActiveGameEntityManager;
import de.helicopter_vs_aliens.control.entities.GameEntityFactory;
import de.helicopter_vs_aliens.control.entities.GameEntitySupplier;
import de.helicopter_vs_aliens.control.events.EventFactory;
import de.helicopter_vs_aliens.control.events.SpecialKey;
import de.helicopter_vs_aliens.control.ressource_transfer.GameResources;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.control.ressource_transfer.GuiStateProvider;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;

import static de.helicopter_vs_aliens.Main.VIRTUAL_DIMENSION;
import static de.helicopter_vs_aliens.gui.WindowType.GAME;


public class ControllerFx extends Application implements GameRessourceProvider, GuiStateProvider
{
    private final Button
            button = new Button("Werkstatt");

    private Savegame
            saveGame;

    private Helicopter
            helicopter = HelicopterType.getDefault()
                                       .makeInstance();

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


    /*private final GameProgress
        gameProgress;*/

    private GraphicsAdapter
            graphicsAdapter;

    private GraphicsAdapter
            graphicsFxAdapter;

    private Image
            offImage;

    private int
            framesCounter = 0;

    private final Dimension
            displayShift = new Dimension();


    public ControllerFx()
    {
        GameResources.setGameResources(this);
    }

    @Override
    public void start(final Stage primaryStage)
    {
        button.setOnAction(event -> primaryStage.close());
        button.setFocusTraversable(false);

        Canvas canvas = new Canvas(VIRTUAL_DIMENSION.width, VIRTUAL_DIMENSION.height);

        canvas.setOnMousePressed(e -> Events.mousePressed(EventFactory.makeMouseEvent(e), this));

        canvas.setOnMouseReleased(e -> Events.mouseReleased(EventFactory.makeMouseEvent(e), this.getHelicopter()));

        canvas.setOnMouseDragged(e -> Events.mouseMovedOrDragged(EventFactory.makeMouseEvent(e), this));

        canvas.setOnMouseMoved(e -> Events.mouseMovedOrDragged(EventFactory.makeMouseEvent(e), this));


        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY))); // Setze den Hintergrund auf Rot
        anchorPane.getChildren()
                  .add(canvas);
        anchorPane.getChildren()
                  .add(button);

        // Set the anchorPane to be focusable
        canvas.setFocusTraversable(true);
        canvas.requestFocus();


        anchorPane.setOnKeyPressed(this::keyEvent);
        // canvas.setOnKeyPressed(this::keyEvent);


        var scene = new Scene(anchorPane);
        primaryStage.setScene(scene);


        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setFullScreen(true);
        primaryStage.show();

        var canvasShift = new Dimension(
                (int) ((primaryStage.getWidth() - VIRTUAL_DIMENSION.width) / 2.0),
                (int) ((primaryStage.getHeight() - VIRTUAL_DIMENSION.height) / 2.0));


        double verticalAnchorDistance = canvasShift.getHeight() + VIRTUAL_DIMENSION.height - 10 - 25;
        AnchorPane.setTopAnchor(button, verticalAnchorDistance);
        double horizontalAnchorDistance = canvasShift.getWidth() + VIRTUAL_DIMENSION.width / 2.0 - 60;
        AnchorPane.setLeftAnchor(button, horizontalAnchorDistance);
        AnchorPane.setRightAnchor(button, horizontalAnchorDistance);

        AnchorPane.setLeftAnchor(canvas, canvasShift.getWidth());
        AnchorPane.setTopAnchor(canvas, canvasShift.getHeight());


        // primaryStage.setResizable(false);
        // primaryStage.setOpacity(0.5);


        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();

        graphicsFxAdapter = new JavaFxAdapter(graphicsContext2D);


        saveGame = Savegame.initialize();
        Audio.initialize();


        offImage = new BufferedImage((int) VIRTUAL_DIMENSION.getWidth(), (int) VIRTUAL_DIMENSION.getHeight(), BufferedImage.TYPE_INT_RGB);


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

    private void keyEvent(KeyEvent keyEvent)
    {
        de.helicopter_vs_aliens.control.events.KeyEvent javaFxEvent = EventFactory.makeKeyEvent(keyEvent);

        System.out.println("keyEvent - start!");
        System.out.println("equal to a: " + javaFxEvent.isKeyEqualTo('a'));
        System.out.println("isLetterKey: " + javaFxEvent.isLetterKey());
        System.out.println("isKeyAllowedForPlayerName: " + javaFxEvent.isKeyAllowedForPlayerName());
        System.out.println("equal to ESCAPE: " + javaFxEvent.isKeyEqualTo(SpecialKey.ESCAPE));
        System.out.println("getKey: " + javaFxEvent.getKey());
        System.out.println("keyEvent - ende!");
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
    public GuiStateProvider getGuiStateProvider()
    {
        return this;
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
    public void resetBackgroundRepaintTimer()
    {
        // TODO not necessary
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
    public int getGameLoopCount()
    {
        // TODO not necessary
        return 0;
    }

    @Override
    public Dimension getDisplayShift()
    {
        return displayShift;
    }

    @Override
    public void switchDisplayMode(de.helicopter_vs_aliens.gui.button.Button currentButton)
    {
        // TODO not necessary
    }

    @Override
    public void switchAntialiasingActivationState(de.helicopter_vs_aliens.gui.button.Button currentButton)
    {
        // TODO not necessary
    }

    @Override
    public boolean isFullScreen()
    {
        return false;
    }

    @Override
    public DisplayMode getCurrentDisplayMode()
    {
        return null;
    }

    @Override
    public void switchFpsVisibleState()
    {
        // TODO not necessary
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
        GraphicsManager.getInstance()
                       .setGraphics(graphicsAdapter);
        windowManager.paintWindow(graphicsAdapter);
    }
}
