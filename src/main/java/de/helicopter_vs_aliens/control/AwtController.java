package de.helicopter_vs_aliens.control;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.ressource_transfer.GuiStateProvider;
import de.helicopter_vs_aliens.control.timer.Timer;
import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.gui.Label;
import de.helicopter_vs_aliens.gui.button.Button;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.util.Colorations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Optional;


public final class AwtController extends JPanel implements Controller, Runnable, GuiStateProvider
{
    private static final int
        BACKGROUND_PAINT_DISABLED = -1;

    private static final Dimension
        STANDARD_RESOLUTION = new Dimension(1280, 720);

    private static final Dimension
        WINDOW_SIZE = new Dimension(STANDARD_RESOLUTION.width + 6,
        STANDARD_RESOLUTION.height + 29);

    private static final DisplayMode
        standardDisplayMode = new DisplayMode(
        STANDARD_RESOLUTION.width,
        STANDARD_RESOLUTION.height,
        32, 60);

    private static final long
        DELAY = 16;

    private boolean
        isFullScreen = true;

    private transient DisplayMode
        currentDisplayMode;

    private Dimension
        displayShift = new Dimension();

    private boolean
        isAntialiasingActivated = true;

    private int
        backgroundRepaintTimer = 0;

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

    private transient GraphicsDevice
        device;

    private JFrame
        frame;

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

    private void init()
    {
        frame = new JFrame("Helicopter vs. Aliens");
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

        device = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                    .getDefaultScreenDevice();
        if(!device.isFullScreenSupported())
        {
            isFullScreen = false;
        }
        originalDisplayMode = device.getDisplayMode();


        frame.setUndecorated(true);
        device.setFullScreenWindow(frame);
        activateDisplayMode();
        switchDisplayMode(null);

        frame.setVisible(true);


        Audio.initialize();

        Dimension offDimension = getSize();
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
        // TODO kontrollieren warum hier ein Feld versteckt wird
        GraphicsAdapter graphicsAdapter = Graphics2DAdapter.of(g);
        if(this.graphicsAdapter != null)
        {
            if(this.frameSkipStatus == FrameSkipStatusType.INACTIVE)
            {
                this.frameSkipStatus = FrameSkipStatusType.DISABLED;
            }
            else
            {
                if(this.backgroundRepaintTimer != BACKGROUND_PAINT_DISABLED)
                {
                    this.clearBackground(graphicsAdapter);
                }
                graphicsAdapter.drawImage(this.offImage,
                    displayShift.width,
                    displayShift.height);
            }

            if(isFullScreen || isMouseCursorInWindow())
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
        if(backgroundRepaintTimer > 1)
        {
            backgroundRepaintTimer = Timer.DISABLED;
        }
        else
        {
            backgroundRepaintTimer++;
        }
        graphicsAdapter.setColor(Color.black);
        graphicsAdapter.fillRect(0,
            0,
            currentDisplayMode.getWidth(),
            currentDisplayMode.getHeight());
    }


    private void paintFrame(GraphicsAdapter graphicsAdapter)
    {
        GraphicsManager.getInstance()
                       .setGraphics(graphicsAdapter);
        gameProgress.getWindowManager()
                    .paintWindow(graphicsAdapter);
    }

    @Override
    public boolean isAntialiasingActivated()
    {
        return isAntialiasingActivated;
    }

    @Override
    public int getGameLoopCount()
    {
        return gameProgress.getGameLoopCount();
    }

    @Override
    public void resetBackgroundRepaintTimer()
    {
        backgroundRepaintTimer = 0;
    }

    @Override
    public boolean isMouseCursorInWindow()
    {
        return eventListener.isMouseCursorInWindow();
    }


    @Override
    public void switchAntialiasingActivationState(Button currentButton)
    {
        isAntialiasingActivated = !isAntialiasingActivated;
        graphicsAdapter.setRenderingHint(RenderingHints.KEY_ANTIALIASING, isAntialiasingActivated
            ? RenderingHints.VALUE_ANTIALIAS_ON
            : RenderingHints.VALUE_ANTIALIAS_OFF);
        Window.dictionary.updateAntialiasing();
        currentButton.setPrimaryLabel(Window.dictionary.antialiasing());
    }

    @Override
    public void switchDisplayMode(Button currentButton)
    {
        isFullScreen = !isFullScreen;

        Window.dictionary.updateDisplayMode();
        Optional.ofNullable(currentButton)
                .ifPresent(button -> button.setPrimaryLabel(Window.dictionary.oppositeDisplayMode()));

        frame.dispose();
        frame.setUndecorated(isFullScreen);

        if(isFullScreen)
        {
            device.setFullScreenWindow(frame);
            activateDisplayMode();
        }
        else
        {
            device.setFullScreenWindow(null);
            if(currentButton != null)
            {
                Window.adaptToWindowMode(displayShift);
            }
            frame.setSize(WINDOW_SIZE);
            frame.setLocation((int) ((originalDisplayMode.getWidth()
                    - WINDOW_SIZE.getWidth()) / 2),
                (int) ((originalDisplayMode.getHeight()
                    - WINDOW_SIZE.getHeight()) / 2));
            frame.setVisible(true);
        }
    }

    private void activateDisplayMode()
    {
        currentDisplayMode = Window.hasOriginalResolution
            ? originalDisplayMode
            : standardDisplayMode;
        device.setDisplayMode(currentDisplayMode);

        displayShift = new Dimension((currentDisplayMode.getWidth() - Main.VIRTUAL_DIMENSION.width) / 2,
            (currentDisplayMode.getHeight() - Main.VIRTUAL_DIMENSION.height) / 2);
        if(Window.label == null)
        {
            Window.label = new Label();
        }
        else
        {
            Window.label.setBounds(
                displayShift.width + 42,
                displayShift.height + 83,
                940,
                240);
        }
        resetBackgroundRepaintTimer();
    }

    @Override
    public boolean isFullScreen()
    {
        return isFullScreen;
    }

    @Override
    public Dimension getDisplayShift()
    {
        return displayShift;
    }

    @Override
    public DisplayMode getCurrentDisplayMode()
    {
        return currentDisplayMode;
    }
}
