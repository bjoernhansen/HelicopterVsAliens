package de.helicopter_vs_aliens.control;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.entities.ActiveGameEntityManager;
import de.helicopter_vs_aliens.control.entities.GameEntityFactory;
import de.helicopter_vs_aliens.control.entities.GameEntitySupplier;
import de.helicopter_vs_aliens.control.events.EventFactory;
import de.helicopter_vs_aliens.control.ressource_transfer.GameResources;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.control.timer.Timer;
import de.helicopter_vs_aliens.graphics.Graphics2DAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
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

import javax.swing.*;


import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import static de.helicopter_vs_aliens.gui.WindowType.GAME;


public final class Controller extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener,
    WindowListener, GameRessourceProvider
{
    private static final boolean
        STOP_GAME_WHEN_MOUSE_OUTSIDE_WINDOW = true;

    private static final int
        BACKGROUND_PAINT_DISABLED = -1;

    private static final long
        DELAY = 16;

    private static Controller
        instance;

    private boolean
        isAntialiasingActivated = true,
        isMouseCursorInWindow = true,
        isFpsDisplayVisible = false;

    // TODO Wieso gibt es framesCounter und numberOfFrames?
    private int
        numberOfFrames,
        framesCounter = 0,
        backgroundRepaintTimer = 0;

    private long
        timeMillis,
        fpsStartTime;

    private FrameSkipStatusType
        frameSkipStatus = FrameSkipStatusType.DISABLED;

    private Savegame
        saveGame;

    private Helicopter
        helicopter = HelicopterType.getDefault().makeInstance();

    private GraphicsAdapter
        graphicsAdapter;

    private Thread
        animator;

    private Image
        offImage;

    private Rectangle2D
        wholeScreenClip;

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


    private Controller()
    {
    }

    public void init()
    {
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
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

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

    public void shutDown()
    {
        System.exit(0);
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
                    Main.displayShift.width,
                    Main.displayShift.height);
            }

            if (Main.isFullScreen || this.isMouseCursorInWindow)
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
            Main.currentDisplayMode.getWidth(),
            Main.currentDisplayMode.getHeight());
    }

    private void updateGame()
    {
        this.framesCounter++;
        Timer.countDownActiveTimers();
        if (WindowManager.window == GAME)
        {
            calculateFps();
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

    private void calculateFps()
    {
        if (isFpsDisplayVisible)
        {
            long timeDiff = System.currentTimeMillis() - fpsStartTime;
            numberOfFrames++;
            if (timeDiff > 3000)
            {
                Window.fps = Math.round(1000f * numberOfFrames / timeDiff);
                fpsStartTime = System.currentTimeMillis();
                numberOfFrames = 0;
            }
        }
    }

    public void switchFpsVisibleState()
    {
        if (WindowManager.window == GAME)
        {
            isFpsDisplayVisible = !isFpsDisplayVisible;
            if (isFpsDisplayVisible)
            {
                fpsStartTime = System.currentTimeMillis();
                numberOfFrames = 0;
            }
        }
    }

    // Behandlung von Fenster-, Tastatur- und Mausereignisse
    @Override
    public void keyPressed(KeyEvent keyEvent)
    {
        Events.keyTyped(EventFactory.makeKeyEvent(keyEvent), this);
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent)
    {
        Events.mousePressed(EventFactory.makeMouseEvent(mouseEvent), this);
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent)
    {
        Events.mouseReleased(EventFactory.makeMouseEvent(mouseEvent), helicopter);
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent)
    {
        Events.mouseMovedOrDragged(EventFactory.makeMouseEvent(mouseEvent), helicopter);
    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent)
    {
        Events.mouseMovedOrDragged(EventFactory.makeMouseEvent(mouseEvent), helicopter);
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent)
    {
        if (WindowManager.window == GAME && STOP_GAME_WHEN_MOUSE_OUTSIDE_WINDOW)
        {
            this.isMouseCursorInWindow = true;
            Events.lastCurrentTime = System.currentTimeMillis();
        }
        this.backgroundRepaintTimer = 0;
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent)
    {
        if (WindowManager.window == GAME && STOP_GAME_WHEN_MOUSE_OUTSIDE_WINDOW)
        {
            this.isMouseCursorInWindow = false;
            Events.playingTime += System.currentTimeMillis() - Events.lastCurrentTime;
        }
    }

    // ungenutzte Listener-Methoden
    @Override
    public void keyTyped(KeyEvent keyEvent)
    {
    }

    @Override
    public void keyReleased(KeyEvent keyEvent)
    {
    }

    @Override
    public void windowDeiconified(WindowEvent windowEvent)
    {
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent)
    {
    }

    @Override
    public void windowOpened(WindowEvent windowEvent)
    {
    }

    @Override
    public void windowClosing(WindowEvent windowEvent)
    {
    }

    @Override
    public void windowClosed(WindowEvent windowEvent)
    {
    }

    @Override
    public void windowIconified(WindowEvent windowEvent)
    {
    }

    @Override
    public void windowActivated(WindowEvent windowEvent)
    {
    }

    @Override
    public void windowDeactivated(WindowEvent windowEvent)
    {
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

    // TODO Aufrufe kontrollieren und ggf. ersetzen durch intern genutzte Variable plus "Dependency Injection"
    public static Controller getInstance()
    {
        if (Optional.ofNullable(instance).isEmpty())
        {
            instance = new Controller();
            GameResources.setGameResources(instance);
        }
        return instance;
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

    public void switchAntialiasingActivationState(Button currentButton)
    {
        isAntialiasingActivated = !isAntialiasingActivated;
        graphicsAdapter.setRenderingHint(RenderingHints.KEY_ANTIALIASING, getInstance().isAntialiasingActivated()
            ? RenderingHints.VALUE_ANTIALIAS_ON
            : RenderingHints.VALUE_ANTIALIAS_OFF);
        Window.dictionary.updateAntialiasing();
        currentButton.setPrimaryLabel(Window.dictionary.antialiasing());
    }

    @Override
    public int getFramesCounter()
    {
        return framesCounter;
    }

    @Override
    public boolean isMouseCursorInWindow()
    {
        return isMouseCursorInWindow;
    }

    @Override
    public boolean isFpsDisplayVisible()
    {
        return isFpsDisplayVisible;
    }

    public void resetBackgroundRepaintTimer()
    {
        backgroundRepaintTimer = 0;
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
}