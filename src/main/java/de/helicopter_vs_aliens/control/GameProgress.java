package de.helicopter_vs_aliens.control;

import de.helicopter_vs_aliens.control.entities.ActiveGameEntityManager;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.control.timer.Timer;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.gui.window.WindowManager;
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


public final class GameProgress
{
    private final FpsCalculator
        fpsCalculator;

    private int
        gameLoopCount = 0;

    private Helicopter
        helicopter = HelicopterType.getDefault()
                                   .makeInstance();

    private final Scenery
        scenery;

    private final GameRessourceProvider
        gameRessourceProvider;

    private Savegame
        saveGame;

    private final ActiveGameEntityManager
        activeGameEntityManager = ActiveGameEntityManager.getInstance();


    public GameProgress(GameRessourceProvider gameRessourceProvider, FpsCalculator fpsCalculator)
    {
        this.fpsCalculator = fpsCalculator;
        scenery = new Scenery(gameRessourceProvider);
        this.gameRessourceProvider = gameRessourceProvider;
    }

    void updateGame()
    {
        gameLoopCount++;
        Timer.countDownActiveTimers();
        if(WindowManager.window == GAME)
        {
            fpsCalculator.calculateFps();
            if(!Window.isMenuVisible)
            {
                Colorations.calculateVariableGameColors(gameLoopCount);
                scenery.update(gameRessourceProvider);
                Events.updateTimer();
                Window.updateDisplays(gameRessourceProvider);
                Enemy.updateAllDestroyed(gameRessourceProvider);
                Missile.updateAll(gameRessourceProvider);
                EnemyController.updateAllActive(gameRessourceProvider);
                EnemyMissile.updateAll(gameRessourceProvider);
                Events.checkForLevelUp(gameRessourceProvider);
                EnemyController.generateNewEnemies(gameRessourceProvider);
                helicopter.update(gameRessourceProvider);
                Explosion.updateAll(gameRessourceProvider);
                PowerUp.updateAll(gameRessourceProvider);
            }
        }
        else
        {
            Colorations.calculateVariableMenuColors(gameLoopCount);
            Window.update(gameRessourceProvider);
        }
    }

    public void init()
    {
        setSaveGame(Savegame.initialize());
        Window.initialize();
        Window.updateButtonLabels(helicopter);
        scenery.createInitialSceneryObjects();
    }

    public ActiveGameEntityManager getActiveGameEntityManager()
    {
        return activeGameEntityManager;
    }

    int getGameLoopCount ()
    {
        return gameLoopCount;
    }

    public void setHelicopter(Helicopter helicopter)
    {
        this.helicopter = helicopter;
        Window.dictionary.switchHelicopterTypeTo(helicopter.getType());
    }

    public Helicopter getHelicopter()
    {
        return helicopter;
    }



    public void setSaveGame(Savegame saveGame)
    {
        this.saveGame = saveGame;
    }

    public Scenery getScenery()
    {
        return scenery;
    }

    public Savegame getSaveGame()
    {
        return saveGame;
    }
}
