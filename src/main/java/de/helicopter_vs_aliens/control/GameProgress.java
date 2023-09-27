package de.helicopter_vs_aliens.control;

import de.helicopter_vs_aliens.control.entities.ActiveGameEntityManager;
import de.helicopter_vs_aliens.control.entities.GameEntityFactory;
import de.helicopter_vs_aliens.control.entities.GameEntitySupplier;
import de.helicopter_vs_aliens.graphics.GraphicsApiType;
import de.helicopter_vs_aliens.control.ressource_transfer.GameResources;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.control.ressource_transfer.GuiStateProvider;
import de.helicopter_vs_aliens.control.timer.Timer;
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

import static de.helicopter_vs_aliens.gui.WindowType.GAME;


public final class GameProgress implements GameRessourceProvider
{
    private final FpsCalculator
        fpsCalculator = new FpsCalculator();

    private int
        gameLoopCount = 0;

    // TODO ... eigentlich sollte Helicopter hier noch gar nicht initialisiert werden müssen, muss er aber -> Checken

    private final GraphicsApiType
        graphicsApiType;

    private Helicopter
        helicopter = HelicopterType.getDefault()
                                   .makeInstance();

    private final Scenery
        scenery;

    private Savegame
        saveGame;

    private final ActiveGameEntityManager
        activeGameEntityManager = ActiveGameEntityManager.getInstance();

    private final WindowManager
        windowManager = new WindowManager();

    private final GameEntitySupplier
        gameEntitySupplier = new GameEntitySupplier();

    private final GameStatisticsCalculator
        gameStatisticsCalculator = new GameStatisticsCalculator();

    private GuiStateProvider
        guiStateProvider;




    public GameProgress(GraphicsApiType graphicsApiType)
    {
        GameResources.setGameResources(this);
        this.graphicsApiType = graphicsApiType;
        scenery = new Scenery(this);
    }

    public void init()
    {
        setSaveGame(Savegame.initialize());
        Window.initialize();
        Window.updateButtonLabels(helicopter);
        scenery.createInitialSceneryObjects();
    }

    public void updateGame()
    {
        gameLoopCount++;
        Timer.countDownActiveTimers();
        if(WindowManager.window == GAME)
        {
            fpsCalculator.calculateFps();
            if(!Window.isMenuVisible)
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
                helicopter.update(this);
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

    @Override
    public ActiveGameEntityManager getActiveGameEntityManager()
    {
        return activeGameEntityManager;
    }

    @Override
    public GuiStateProvider getGuiStateProvider()
    {
        return guiStateProvider;
    }

    public void setGuiStateProvider(GuiStateProvider guiStateProvider)
    {
        this.guiStateProvider = guiStateProvider;
    }

    @Override
    public <T extends GameEntity> T getNewGameEntityInstance(GameEntityFactory<T> factory)
    {
        return getGameEntitySupplier().retrieve(factory);
    }

    @Override
    public boolean isFpsDisplayVisible()
    {
        return fpsCalculator.isFpsDisplayVisible();
    }

    @Override
    public void switchFpsVisibleState()
    {
        fpsCalculator.switchFpsVisibleState();
    }

    @Override
    public int getGameLoopCount()
    {
        return gameLoopCount;
    }

    @Override
    public void setHelicopter(Helicopter helicopter)
    {
        this.helicopter = helicopter;
        // TODO nicht mehr nötig, wenn Helicopter über den InstanceSupplier bezogen wird (wie Enemy)
        helicopter.setGameRessourceProvider(this);
        Window.dictionary.switchHelicopterTypeTo(helicopter.getType());
    }

    @Override
    public Helicopter getHelicopter()
    {
        return helicopter;
    }

    public void setSaveGame(Savegame saveGame)
    {
        this.saveGame = saveGame;
    }

    @Override
    public GameStatisticsCalculator getGameStatisticsCalculator()
    {
        return gameStatisticsCalculator;
    }

    @Override
    public Scenery getScenery()
    {
        return scenery;
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

    public WindowManager getWindowManager()
    {
        return windowManager;
    }

    public GraphicsApiType getGraphicsApiType()
    {
        return graphicsApiType;
    }
}
