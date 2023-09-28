package de.helicopter_vs_aliens.control.awt;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.FrameSkipStatusType;
import de.helicopter_vs_aliens.control.GameProgress;
import de.helicopter_vs_aliens.control.ressource_transfer.GuiStateProvider;
import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.gui.Label;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.util.Colorations;
import de.helicopter_vs_aliens.util.geometry.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;


public final class AwtController extends JPanel implements Controller, Runnable, GuiStateProvider
{
    private static final long
        DELAY = 16;

    private static final Dimension
        STANDARD_RESOLUTION = Dimension.newInstance(1280, 720);

    private static final Dimension
        WINDOW_SIZE = Dimension.newInstance(
        STANDARD_RESOLUTION.getWidth() + 6,
        STANDARD_RESOLUTION.getHeight() + 29);

    private static final Dimension
        DISPLAY_SHIFT = Dimension.newInstance(
        (STANDARD_RESOLUTION.getWidth() - Main.VIRTUAL_DIMENSION.width - 10) / 2,
        (STANDARD_RESOLUTION.getHeight() - Main.VIRTUAL_DIMENSION.height - 10) / 2);


    private long
        timeMillis;

    private FrameSkipStatusType
        frameSkipStatus = FrameSkipStatusType.DISABLED;

    private transient GraphicsAdapter
        graphicsAdapter;

    private transient Thread
        animator;

    private transient Image
        offImage;

    private transient Rectangle2D
        wholeScreenClip;

    private transient DisplayMode
        originalDisplayMode;

    private final transient EventListener
        eventListener;

    private final transient GameProgress
        gameProgress;


    public AwtController(GameProgress gameProgress)
    {
        gameProgress.setGuiStateProvider(this);
        this.gameProgress = gameProgress;
        eventListener = new EventListener(gameProgress);
    }

    @Override
    public void start()
    {
        gameProgress.init();
        init();
        animator = new Thread(this);
        animator.start();
        Audio.refreshBackgroundMusic();
    }

    // TODO Refactoring dieser Methode
    private void init()
    {
        JFrame frame = new JFrame("Helicopter vs. Aliens");
        frame.setBackground(Color.black);
        frame.add("Center", this);

        frame.addKeyListener(eventListener);
        frame.setResizable(false);
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });

        this.setLayout(null);

        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                                   .getDefaultScreenDevice();

        originalDisplayMode = device.getDisplayMode();


        frame.setUndecorated(true);
        device.setFullScreenWindow(frame);
        device.setDisplayMode(originalDisplayMode);

        if(Window.label == null)
        {
            Window.label = new Label();
        }
        else
        {
            Window.label.setBounds(
                DISPLAY_SHIFT.getWidth() + 42,
                DISPLAY_SHIFT.getHeight() + 83,
                940,
                240);
        }
        gameProgress.resetBackgroundRepaintTimer();
        frame.dispose();
        frame.setUndecorated(false);
        device.setFullScreenWindow(null);
        frame.setSize(WINDOW_SIZE.getWidth(), WINDOW_SIZE.getHeight());
        frame.setLocation(
            (originalDisplayMode.getWidth() - WINDOW_SIZE.getWidth()) / 2,
            (originalDisplayMode.getHeight() - WINDOW_SIZE.getHeight()) / 2);
        frame.setVisible(true);


        Audio.initialize();

        Dimension offDimension = Dimension.of(getSize());
        wholeScreenClip = new Rectangle2D.Double(0, 0, offDimension.getWidth(), offDimension.getHeight());
        offImage = new BufferedImage((int) Main.VIRTUAL_DIMENSION.getWidth(), (int) Main.VIRTUAL_DIMENSION.getHeight(), BufferedImage.TYPE_INT_RGB);
        graphicsAdapter = Graphics2DAdapter.of(offImage);
        graphicsAdapter.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphicsAdapter.fillRectangle(wholeScreenClip);

        Shape offscreenClip = new Rectangle2D.Double(0,
            0,
            Main.VIRTUAL_DIMENSION.getWidth(),
            Main.VIRTUAL_DIMENSION.getHeight());
        graphicsAdapter.setClip(offscreenClip);

        add(Window.label);
        addMouseListener(eventListener);
        addMouseMotionListener(eventListener);
    }

    @Override
    public void run()
    {
        if(this.frameSkipStatus != FrameSkipStatusType.ACTIVE)
        {
            timeMillis = System.currentTimeMillis();
        }
        while(Thread.currentThread() == this.animator)
        {
            repaint();
            try
            {
                timeMillis += DELAY;
                long pauseTime = timeMillis - System.currentTimeMillis();
                if(pauseTime < 1)
                {
                    this.frameSkipStatus = FrameSkipStatusType.ACTIVE;
                }
                else
                {
                    Thread.sleep(pauseTime);
                }
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
                break;
            }
        }
    }

    // Berechnen und Zeichnen des Spiels
    @Override
    protected void paintComponent(Graphics g)
    {
        // TODO kontrollieren, warum hier ein Feld versteckt wird
        GraphicsAdapter graphicsAdapter = Graphics2DAdapter.of(g);
        if(this.graphicsAdapter != null)
        {
            if(this.frameSkipStatus == FrameSkipStatusType.INACTIVE)
            {
                this.frameSkipStatus = FrameSkipStatusType.DISABLED;
            }
            else
            {
                if(gameProgress.getBackgroundRepaintTimer().isActive())
                {
                    this.clearBackground(graphicsAdapter);
                }
                graphicsAdapter.drawImage(
                    this.offImage,
                    DISPLAY_SHIFT.getWidth(),
                    DISPLAY_SHIFT.getHeight());
            }

            if(gameProgress.isMouseCursorInWindow())
            {
                gameProgress.updateGame();
            }

            if(this.frameSkipStatus == FrameSkipStatusType.ACTIVE)
            {
                this.frameSkipStatus = FrameSkipStatusType.INACTIVE;
            }
            else
            {
                this.graphicsAdapter.setColor(Colorations.bg);
                this.graphicsAdapter.fillRectangle(this.wholeScreenClip);
                paintFrame(this.graphicsAdapter);
            }
        }
    }

    private void clearBackground(GraphicsAdapter graphicsAdapter)
    {
        gameProgress.getBackgroundRepaintTimer().proceed();
        graphicsAdapter.setColor(Color.black);
        graphicsAdapter.fillRect(0,
            0,
            originalDisplayMode.getWidth(),
            originalDisplayMode.getHeight());
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
