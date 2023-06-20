package de.helicopter_vs_aliens;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicInteger;

public class MainFx extends Application
{
    public static void main(String[] args)
    {
        Application.launch(MainFx.class);
    }
    
    @Override
    public void start(final Stage primaryStage)
    {
        AtomicInteger counter = new AtomicInteger();
        
        
        Canvas canvas = new Canvas (1024, 768);
        Scene scene = new Scene(new Pane(canvas));
        primaryStage.setScene(scene);
        primaryStage.show();
        
        //primaryStage.setFullScreen(true);
        //primaryStage.setResizable(false);
        //primaryStage.setOpacity(0.5);
        //primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        
        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
        // GraphicsAdapter graphicsAdapter = JavaFxAdapter.of(graphicsContext2D);
        
        
        new AnimationTimer()
        {
            @Override
            public void handle(long now)
            {
                // simulation.nextStep();
    
                
    
                
                /*
                super.paintComponent(graphicsContext2D);
                
                imagePainter.initializeImage();
                imagePainter.paint(simulation);
                Image image = imagePainter.getImage();
                graphicsAdapter.drawImage(image, SimulationFrame.IMAGE_BORDER_DISTANCE, SimulationFrame.IMAGE_BORDER_DISTANCE, null);
                lastPainted = System.currentTimeMillis();
                */
                
                int i = counter.incrementAndGet();
                
                graphicsContext2D.clearRect(0, 0, primaryStage.getWidth(), primaryStage.getHeight());
                
                
                graphicsContext2D.setFill(Color.RED);
                graphicsContext2D.fillOval(50 + (i%200), 50, 150, 250);
                
                // PaintManager.getInstance().paint(graphicsAdapter, simulation);
                
                
                graphicsContext2D.restore();
                
            }
        }.start();
    }
}
