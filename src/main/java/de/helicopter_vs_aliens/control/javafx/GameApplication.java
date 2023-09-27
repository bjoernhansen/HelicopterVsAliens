package de.helicopter_vs_aliens.control.javafx;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.GameProgress;
import de.helicopter_vs_aliens.control.events.EventFactory;
import de.helicopter_vs_aliens.control.events.SpecialKey;
import de.helicopter_vs_aliens.control.ressource_transfer.GuiStateProvider;
import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.graphics.JavaFxAdapter;
import de.helicopter_vs_aliens.util.Colorations;
import de.helicopter_vs_aliens.util.geometry.Dimension;
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

import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import static de.helicopter_vs_aliens.Main.VIRTUAL_DIMENSION;


public class GameApplication extends Application implements GuiStateProvider
{
    private static final Dimension
        DISPLAY_SHIFT = Dimension.newInstance(0, 0);

    private final Button
        button = new Button("Werkstatt");

    private final GameProgress
        gameProgress;

    private GraphicsAdapter
        graphicsAdapter;

    private GraphicsAdapter
        graphicsFxAdapter;

    private Image
        offImage;



    public GameApplication()
    {
        gameProgress = JavaFxController.gameProgress;
        gameProgress.setGuiStateProvider(this);
    }

    @Override
    public void start(final Stage primaryStage)
    {
        button.setOnAction(event -> primaryStage.close());
        button.setFocusTraversable(false);

        Canvas canvas = new Canvas(VIRTUAL_DIMENSION.width, VIRTUAL_DIMENSION.height);

        canvas.setOnMousePressed(e -> Events.mousePressed(EventFactory.makeMouseEvent(e), gameProgress));

        canvas.setOnMouseReleased(e -> Events.mouseReleased(EventFactory.makeMouseEvent(e), gameProgress.getHelicopter()));

        canvas.setOnMouseDragged(e -> Events.mouseMovedOrDragged(EventFactory.makeMouseEvent(e), gameProgress));

        canvas.setOnMouseMoved(e -> Events.mouseMovedOrDragged(EventFactory.makeMouseEvent(e), gameProgress));


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

        var canvasShift = Dimension.newInstance(
            (int) ((primaryStage.getWidth() - VIRTUAL_DIMENSION.width) / 2.0),
            (int) ((primaryStage.getHeight() - VIRTUAL_DIMENSION.height) / 2.0));


        double verticalAnchorDistance = canvasShift.getHeight() + VIRTUAL_DIMENSION.height - 10 - 25;
        AnchorPane.setTopAnchor(button, verticalAnchorDistance);
        double horizontalAnchorDistance = canvasShift.getWidth() + VIRTUAL_DIMENSION.width / 2.0 - 60;
        AnchorPane.setLeftAnchor(button, horizontalAnchorDistance);
        AnchorPane.setRightAnchor(button, horizontalAnchorDistance);

        AnchorPane.setLeftAnchor(canvas, (double) canvasShift.getWidth());
        AnchorPane.setTopAnchor(canvas, (double) canvasShift.getHeight());


        // primaryStage.setResizable(false);
        // primaryStage.setOpacity(0.5);


        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();

        graphicsFxAdapter = new JavaFxAdapter(graphicsContext2D);


        Audio.initialize();


        offImage = new BufferedImage((int) VIRTUAL_DIMENSION.getWidth(), (int) VIRTUAL_DIMENSION.getHeight(), BufferedImage.TYPE_INT_RGB);


        graphicsAdapter = Graphics2DAdapter.of(offImage);
        graphicsAdapter.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


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
    public void resetBackgroundRepaintTimer()
    {
        // not necessary
    }

    private void updateGame()
    {
        gameProgress.updateGame();
    }

    private void paintFrame(GraphicsAdapter graphicsAdapter)
    {
        GraphicsManager.getInstance()
                       .setGraphics(graphicsAdapter);
        gameProgress.getWindowManager()
                    .paintWindow(graphicsAdapter);
    }

    public static Dimension getDisplayShift()
    {
        return DISPLAY_SHIFT;
    }
}
