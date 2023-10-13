package de.helicopter_vs_aliens.platform_specific.javafx;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.ressource_transfer.GameProgress;
import de.helicopter_vs_aliens.control.events.EventFactory;
import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.graphics.JavaFxAdapter;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.util.Colorations;
import de.helicopter_vs_aliens.util.geometry.Dimension;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import static de.helicopter_vs_aliens.Main.VIRTUAL_DIMENSION;


public class GameApplication extends Application
{
    private static final Dimension
        DISPLAY_SHIFT = Dimension.newInstance(0, 0);

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
    }

    @Override
    public void start(final Stage primaryStage)
    // TODO Refactoring dieser Methode
    {
        Audio.initialize();

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY))); // Setze den Hintergrund auf Rot

        var scene = new Scene(anchorPane);
        primaryStage.setScene(scene);

        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setFullScreen(true);
        primaryStage.show();

        final double scalingFactor = primaryStage.getWidth() / VIRTUAL_DIMENSION.width;
        gameProgress.setScalingFactor(scalingFactor);

        Dimension scaledDimension = Dimension.newInstance(
                                        (int) (scalingFactor * VIRTUAL_DIMENSION.width),
                                        (int) (scalingFactor * VIRTUAL_DIMENSION.height));

        Canvas canvas = new Canvas(scaledDimension.getWidth(), scaledDimension.getHeight());

        canvas.setOnMousePressed(e -> Events.mousePressed(EventFactory.makeMouseEvent(e), gameProgress, scalingFactor));
        canvas.setOnMouseReleased(e -> Events.mouseReleased(EventFactory.makeMouseEvent(e), gameProgress.getHelicopter(), scalingFactor));
        canvas.setOnMouseDragged(e -> Events.mouseMovedOrDragged(EventFactory.makeMouseEvent(e), gameProgress, scalingFactor));
        canvas.setOnMouseMoved(e -> Events.mouseMovedOrDragged(EventFactory.makeMouseEvent(e), gameProgress, scalingFactor));
        anchorPane.getChildren()
                  .add(canvas);

        // Set the anchorPane to be focusable
        canvas.setFocusTraversable(true);
        canvas.requestFocus();

        anchorPane.setOnKeyPressed(e -> Events.keyTyped(EventFactory.makeKeyEvent(e), gameProgress));

        var canvasShift = Dimension.newInstance(
            (int) ((primaryStage.getWidth() - scaledDimension.getWidth()) / 2.0),
            (int) ((primaryStage.getHeight() - scaledDimension.getHeight()) / 2.0));

        var htmlViewer = JavaFxBasedHtmlViewer.makeInstance(canvasShift, scalingFactor);
        Window.setHtmlViewer(htmlViewer);
        WebView webView = htmlViewer.getComponent();

        anchorPane.getChildren()
                  .add(webView);

        AnchorPane.setLeftAnchor(webView, webView.getLayoutX());
        AnchorPane.setTopAnchor(webView, webView.getLayoutY());

        AnchorPane.setLeftAnchor(canvas, (double) canvasShift.getWidth());
        AnchorPane.setTopAnchor(canvas, (double) canvasShift.getHeight());

        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
        graphicsFxAdapter = new JavaFxAdapter(graphicsContext2D);
        offImage = new BufferedImage((int) VIRTUAL_DIMENSION.getWidth(), (int) VIRTUAL_DIMENSION.getHeight(), BufferedImage.TYPE_INT_RGB);
        graphicsAdapter = Graphics2DAdapter.of(offImage);
        graphicsAdapter.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        new AnimationTimer()
        {
            @Override
            public void handle(long now)
            {
                graphicsFxAdapter.drawImage(offImage, DISPLAY_SHIFT, scaledDimension);
                updateGame();
                graphicsAdapter.setColor(Colorations.bg);
                graphicsAdapter.fillRect(0, 0, VIRTUAL_DIMENSION.width, VIRTUAL_DIMENSION.height);
                paintFrame(graphicsAdapter);
            }
        }.start();
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
