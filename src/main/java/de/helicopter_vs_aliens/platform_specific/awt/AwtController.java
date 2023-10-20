package de.helicopter_vs_aliens.platform_specific.awt;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.FrameSkipStatusType;
import de.helicopter_vs_aliens.control.ressource_transfer.GameProgress;
import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.util.Colorations;
import de.helicopter_vs_aliens.util.geometry.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;


public final class AwtController extends JPanel implements Controller, Runnable
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
        this.gameProgress = gameProgress;
        eventListener = new EventListener(gameProgress);
    }

    @Override
    public void start()
    {
        initialize();
        animator = new Thread(this);
        animator.start();
    }

    private void initialize()
    {
        Audio.initialize();
        initializeFrame();
        addListener();
        initAndAddLabel();

        originalDisplayMode = getDisplayMode();
        wholeScreenClip = createWholeScreenClip();
        offImage = createOffImage();
        graphicsAdapter = createGraphicsAdapterFrom(offImage);
    }

    private static DisplayMode getDisplayMode()
    {
        return GraphicsEnvironment.getLocalGraphicsEnvironment()
                                  .getDefaultScreenDevice()
                                  .getDisplayMode();
    }

    private void addListener()
    {
        addMouseListener(eventListener);
        addMouseMotionListener(eventListener);
    }

    private void initAndAddLabel()
    {
        setLayout(null);
        var htmlViewer = SwingBasedHtmlViewer.makeInstance(DISPLAY_SHIFT);
        Window.setHtmlViewer(htmlViewer);
        add(htmlViewer.getComponent());
    }

    private Rectangle2D createWholeScreenClip()
    {
        Dimension offDimension = Dimension.of(getSize());
        return new Rectangle2D.Double(0, 0, offDimension.getWidth(), offDimension.getHeight());
    }

    private GraphicsAdapter createGraphicsAdapterFrom(Image offImage)
    {
        GraphicsAdapter newGraphicsAdapter = Graphics2DAdapter.of(offImage);
        newGraphicsAdapter.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return newGraphicsAdapter;
    }

    private BufferedImage createOffImage()
    {
        return new BufferedImage(
            (int) Main.VIRTUAL_DIMENSION.getWidth(),
            (int) Main.VIRTUAL_DIMENSION.getHeight(),
            BufferedImage.TYPE_INT_RGB);
    }

    private void initializeFrame()
    {
        JFrame frame = new JFrame("Helicopter vs. Aliens");
        frame.setBackground(Color.black);
        frame.add("Center", this);
        frame.addKeyListener(eventListener);
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
        frame.setSize(WINDOW_SIZE.asAwtDimension());
        frame.setResizable(false);
        frame.setVisible(true);
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
        GraphicsAdapter localGraphicsAdapter = Graphics2DAdapter.of(g);
        if(graphicsAdapter != null)
        {
            if(frameSkipStatus == FrameSkipStatusType.INACTIVE)
            {
                frameSkipStatus = FrameSkipStatusType.DISABLED;
            }
            else
            {
                if(gameProgress.getBackgroundRepaintTimer()
                               .isActive())
                {
                    clearBackground(localGraphicsAdapter);
                }
                localGraphicsAdapter.drawImage(
                    offImage,
                    DISPLAY_SHIFT.getWidth(),
                    DISPLAY_SHIFT.getHeight());
            }

            if(gameProgress.isMouseCursorInWindow())
            {
                gameProgress.updateGame();
            }

            if(frameSkipStatus == FrameSkipStatusType.ACTIVE)
            {
                frameSkipStatus = FrameSkipStatusType.INACTIVE;
            }
            else
            {
                graphicsAdapter.setColor(Colorations.bg);
                graphicsAdapter.fillRectangle(wholeScreenClip);
                paintFrame(graphicsAdapter);
            }
        }
    }

    private void clearBackground(GraphicsAdapter graphicsAdapter)
    {
        gameProgress.getBackgroundRepaintTimer()
                    .proceed();
        graphicsAdapter.setColor(Color.black);
        graphicsAdapter.fillRect(0,
            0,
            originalDisplayMode.getWidth(),
            originalDisplayMode.getHeight());
    }

    // TODO dieselbe Methode ist auch in GameApplication
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
