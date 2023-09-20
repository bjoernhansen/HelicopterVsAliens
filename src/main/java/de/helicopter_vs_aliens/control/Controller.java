package de.helicopter_vs_aliens.control;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.entities.ActiveGameEntityManager;
import de.helicopter_vs_aliens.control.entities.GameEntityFactory;
import de.helicopter_vs_aliens.control.entities.GameEntitySupplier;
import de.helicopter_vs_aliens.control.ressource_transfer.GameResources;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.control.timer.Timer;
import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.gui.Label;
import de.helicopter_vs_aliens.gui.button.Button;
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

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
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
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import static de.helicopter_vs_aliens.gui.WindowType.GAME;


public final class Controller extends JPanel implements Runnable, GameRessourceProvider
{
    public static final Dimension
        VIRTUAL_DIMENSION = new Dimension(1024, 461);

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


    private static Controller
        instance;


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

    private boolean
        isFpsDisplayVisible = false;

    private int
        framesCount;

    private int
        gameLoopCount = 0;

    private int
        backgroundRepaintTimer = 0;

    private long
        timeMillis;

    private long
        fpsStartTime;

    private FrameSkipStatusType
        frameSkipStatus = FrameSkipStatusType.DISABLED;

    private Savegame
        saveGame;

    private transient Helicopter
        helicopter = HelicopterType.getDefault().makeInstance();

    private transient GraphicsAdapter
        graphicsAdapter;

    private transient Thread
        animator;

    private transient Image
        offImage;

    private transient Rectangle2D
        wholeScreenClip;

    private final transient Scenery
        scenery = new Scenery(this);

    private final transient WindowManager
        windowManager = new WindowManager();

    private final transient GameEntitySupplier
        gameEntitySupplier = new GameEntitySupplier();

    private final transient GameStatisticsCalculator
        gameStatisticsCalculator = GameStatisticsCalculator.getInstance();

    private final transient ActiveGameEntityManager
        activeGameEntityManager = ActiveGameEntityManager.getInstance();


    private transient GraphicsDevice
        device;

    private JFrame
        frame;

    private transient DisplayMode
        originalDisplayMode;

    private final transient EventListener
        eventListener = new EventListener(this);


    private Controller()
    {
    }

    public static Controller getInstance()
    // TODO Aufrufe kontrollieren und ggf. ersetzen durch intern genutzte Variable plus "Dependency Injection"
    {
        if (Optional.ofNullable(instance).isEmpty())
        {
            instance = new Controller();
            GameResources.setGameResources(instance);
        }
        return instance;
    }

    public void init()
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

        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (!device.isFullScreenSupported())
        {
            isFullScreen = false;
        }
        originalDisplayMode = device.getDisplayMode();

        this.setSaveGame(Savegame.initialize());

        frame.setUndecorated(true);
        device.setFullScreenWindow(frame);
        activateDisplayMode();
        switchDisplayMode(null);

        frame.setVisible(true);


        Audio.initialize();

        Dimension offDimension = getSize();
        wholeScreenClip = new Rectangle2D.Double(0, 0, offDimension.getWidth(), offDimension.getHeight());
        offImage = new BufferedImage((int) VIRTUAL_DIMENSION.getWidth(), (int) VIRTUAL_DIMENSION.getHeight(), BufferedImage.TYPE_INT_RGB);
        graphicsAdapter = Graphics2DAdapter.of(offImage);
        graphicsAdapter.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphicsAdapter.fillRectangle(wholeScreenClip);

        Shape offscreenClip = new Rectangle2D.Double(0,
            0,
            VIRTUAL_DIMENSION.getWidth(),
            VIRTUAL_DIMENSION.getHeight());
        graphicsAdapter.setClip(offscreenClip);

        add(Window.label);
        // TODO herausfinden, wieso die Listener nicht klappen
        addMouseListener(eventListener);
        addMouseMotionListener(eventListener);

        Window.initialize();
        Window.updateButtonLabels(helicopter);
        scenery.createInitialSceneryObjects();
    }

    public void start()
    {
        this.animator = new Thread(this);
        this.animator.start();
        Audio.refreshBackgroundMusic();
    }

    @Override
    public void run()
    {
        if (this.frameSkipStatus != FrameSkipStatusType.ACTIVE)
        {
            timeMillis = System.currentTimeMillis();
        }
        while (Thread.currentThread() == this.animator)
        {
            repaint();
            try
            {
                timeMillis += DELAY;
                long pauseTime = timeMillis - System.currentTimeMillis();
                if (pauseTime < 1)
                {
                    this.frameSkipStatus = FrameSkipStatusType.ACTIVE;
                }
                else
                {
                    Thread.sleep(pauseTime);
                }
            }
            catch (InterruptedException e)
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
        if (this.graphicsAdapter != null)
        {
            if (this.frameSkipStatus == FrameSkipStatusType.INACTIVE)
            {
                this.frameSkipStatus = FrameSkipStatusType.DISABLED;
            }
            else
            {
                if (this.backgroundRepaintTimer != BACKGROUND_PAINT_DISABLED)
                {
                    this.clearBackground(graphicsAdapter);
                }
                graphicsAdapter.drawImage(this.offImage,
                    displayShift.width,
                    displayShift.height);
            }

            if (isFullScreen || isMouseCursorInWindow())
            {
                updateGame();
            }

            if (this.frameSkipStatus == FrameSkipStatusType.ACTIVE)
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
        if (backgroundRepaintTimer > 1)
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

    private void updateGame()
    {
        this.gameLoopCount++;
        Timer.countDownActiveTimers();
        if (WindowManager.window == GAME)
        {
            calculateFps();
            if (!Window.isMenuVisible)
            {
                Colorations.calculateVariableGameColors(gameLoopCount);

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
            Colorations.calculateVariableMenuColors(gameLoopCount);
            Window.update(this);
        }
    }

    private void paintFrame(GraphicsAdapter graphicsAdapter)
    {
        GraphicsManager.getInstance().setGraphics(graphicsAdapter);
        windowManager.paintWindow(graphicsAdapter);
    }

    private void calculateFps()
    {
        if (isFpsDisplayVisible)
        {
            long timeDiff = System.currentTimeMillis() - fpsStartTime;
            framesCount++;
            if (timeDiff > 3000)
            {
                Window.fps = Math.round(1000f * framesCount / timeDiff);
                fpsStartTime = System.currentTimeMillis();
                framesCount = 0;
            }
        }
    }

    @Override
    public void switchFpsVisibleState()
    {
        if (WindowManager.window == GAME)
        {
            isFpsDisplayVisible = !isFpsDisplayVisible;
            if (isFpsDisplayVisible)
            {
                fpsStartTime = System.currentTimeMillis();
                framesCount = 0;
            }
        }
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

    @Override
    public GameEntitySupplier getGameEntitySupplier()
    {
        return gameEntitySupplier;
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

    public void setSaveGame(Savegame saveGame)
    {
        this.saveGame = saveGame;
    }

    @Override
    public boolean isAntialiasingActivated()
    {
        return isAntialiasingActivated;
    }

    @Override
    public int getFramesCounter()
    {
        return gameLoopCount;
    }


    @Override
    public boolean isFpsDisplayVisible()
    {
        return isFpsDisplayVisible;
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
    public GameStatisticsCalculator getGameStatisticsCalculator()
    {
        return gameStatisticsCalculator;
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
    public <T extends GameEntity> T getNewGameEntityInstance(GameEntityFactory<T> factory)
    {
        return gameEntitySupplier.retrieve(factory);
    }

    @Override
    public ActiveGameEntityManager getActiveGameEntityManager()
    {
        return activeGameEntityManager;
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

        if (isFullScreen)
        {
            device.setFullScreenWindow(frame);
            activateDisplayMode();
        }
        else
        {
            device.setFullScreenWindow(null);
            if (currentButton != null)
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

        displayShift = new Dimension((currentDisplayMode.getWidth() - VIRTUAL_DIMENSION.width) / 2,
            (currentDisplayMode.getHeight() - VIRTUAL_DIMENSION.height) / 2);
        if (Window.label == null)
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
