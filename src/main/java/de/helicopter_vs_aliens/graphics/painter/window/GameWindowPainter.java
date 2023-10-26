package de.helicopter_vs_aliens.graphics.painter.window;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.EnemyController;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.GameStatisticsCalculator;
import de.helicopter_vs_aliens.control.LevelManager;
import de.helicopter_vs_aliens.control.entities.GameEntityFactory;
import de.helicopter_vs_aliens.control.timer.Timer;
import de.helicopter_vs_aliens.graphics.GraphicalEntities;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.graphics.GraphicsManager;
import de.helicopter_vs_aliens.graphics.painter.PowerUpPainter;
import de.helicopter_vs_aliens.gui.MultiKillType;
import de.helicopter_vs_aliens.gui.button.Button;
import de.helicopter_vs_aliens.gui.button.GroundButtonType;
import de.helicopter_vs_aliens.gui.button.MainMenuButtonType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.enemy.EnemyType;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.powerup.PowerUpType;
import de.helicopter_vs_aliens.model.scenery.SceneryLayer;
import de.helicopter_vs_aliens.model.scenery.SceneryObject;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static de.helicopter_vs_aliens.control.TimeOfDay.NIGHT;
import static de.helicopter_vs_aliens.gui.window.Window.fontProvider;
import static de.helicopter_vs_aliens.model.RectangularGameEntity.GROUND_Y;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.HELIOS;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.ROTOR_SYSTEM;


public class GameWindowPainter extends WindowPainter
{
    private static final int
        HEALTH_BAR_LENGTH = 150;
    
    private static final int
        ENEMY_HEALTH_BAR_WIDTH = 206;
    
    public static final int
        IN_GAME_MENU_LEFT = 363;
    
    public static final int
        IN_GAME_MENU_WIDTH = 256;
    
    
    @Override
    public void paint(GraphicsAdapter graphicsAdapter, Window window)
    {
        paintBackground(graphicsAdapter);
        paintBackgroundDisplays(graphicsAdapter);
        Optional.ofNullable(EnemyController.currentRock)
                .ifPresent(enemy -> enemy.paint(graphicsAdapter));
        paintAllDestroyedEnemies(graphicsAdapter);
        paintAllMissiles(graphicsAdapter);
        paintAllActiveEnemies(graphicsAdapter);
        paintAllEnemyMissiles(graphicsAdapter);
        helicopter.paint(graphicsAdapter);
        paintAllExplosions(graphicsAdapter);
        paintAllPowerUps(graphicsAdapter);
        paintForeground(graphicsAdapter);
        super.paint(graphicsAdapter, window);
        paintForegroundDisplays(graphicsAdapter);
        paintGui(graphicsAdapter);
    }
    
    private void paintBackground(GraphicsAdapter graphicsAdapter)
    {
        gameRessourceProvider.getScenery()
                             .paint(graphicsAdapter);
    }
    
    private void paintAllDestroyedEnemies(GraphicsAdapter graphicsAdapter)
    {
        gameRessourceProvider.getActiveGameEntityManager()
                             .getEnemies()
                             .get(CollectionSubgroupType.DESTROYED)
                             .forEach(enemy -> enemy.paint(graphicsAdapter));
    }
    
    private void paintAllMissiles(GraphicsAdapter graphicsAdapter)
    {
        gameRessourceProvider.getActiveGameEntityManager()
                             .getMissiles()
                             .get(CollectionSubgroupType.ACTIVE)
                             .forEach(missile -> missile.paint(graphicsAdapter));
    }
    
    private void paintAllActiveEnemies(GraphicsAdapter graphicsAdapter)
    {
        for(int i = 0; i < EnemyController.currentNumberOfBarriers; i++)
        {
            EnemyController.livingBarrier[i].paint(graphicsAdapter);
        }
        for(Enemy enemy : gameRessourceProvider.getActiveGameEntityManager()
                                               .getEnemies()
                                               .get(CollectionSubgroupType.ACTIVE))
        {
            if(enemy.isVisibleNonBarricadeVessel())
            {
                enemy.paint(graphicsAdapter);
            }
        }
    }
    
    private void paintAllEnemyMissiles(GraphicsAdapter graphicsAdapter)
    {
        gameRessourceProvider.getActiveGameEntityManager()
                             .getEnemyMissiles()
                             .get(CollectionSubgroupType.ACTIVE)
                             .forEach(enemyMissile -> enemyMissile.paint(graphicsAdapter));
    }
    
    private void paintAllExplosions(GraphicsAdapter graphicsAdapter)
    {
        gameRessourceProvider.getActiveGameEntityManager()
                             .getExplosions()
                             .get(CollectionSubgroupType.ACTIVE)
                             .forEach(explosion -> explosion.paint(graphicsAdapter));
    }
    
    private void paintAllPowerUps(GraphicsAdapter graphicsAdapter)
    {
        gameRessourceProvider.getActiveGameEntityManager()
                             .getPowerUps()
                             .get(CollectionSubgroupType.ACTIVE)
                             .forEach(powerUp -> powerUp.paint(graphicsAdapter));
    }
    
    private void paintForeground(GraphicsAdapter graphicsAdapter)
    {
        // der Boden
        graphicsAdapter.setPaint(Colorations.gradientGround[Events.timeOfDay.ordinal()]);
        graphicsAdapter.fillRect(0, GROUND_Y, Main.VIRTUAL_DIMENSION.width, 35);
        
        // Objekte vor dem Helikopter
        Queue<SceneryObject> activeSceneryObjects = gameRessourceProvider.getActiveGameEntityManager()
                                                                         .getSceneryObjects()
                                                                         .get(CollectionSubgroupType.ACTIVE);
        SceneryLayer.getForegroundLayers()
                    .forEach(layer -> activeSceneryObjects.stream()
                                                          .filter(sceneryObject -> sceneryObject.getLayer() == layer)
                                                          .forEach(sceneryObject -> sceneryObject.paint(graphicsAdapter)));
    }
    
    private void paintForegroundDisplays(GraphicsAdapter graphicsAdapter)
    {
        if(showBossHealthBar())
        {
            paintBossHealthBar(graphicsAdapter);
        }
        paintHealthBar(graphicsAdapter, helicopter);
        paintCollectedPowerUps(graphicsAdapter);
        
        if(gameRessourceProvider.isFpsDisplayVisible())
        {
            paintFpsDisplay(graphicsAdapter);
        }
        
        if(helicopter.isOnTheGround())
        {
            if(!Window.isMenuVisible && gameRessourceProvider.isMouseCursorInWindow())
            {
                paintTimeDisplay(graphicsAdapter, Events.playingTime
                    + System.currentTimeMillis()
                    - Events.lastCurrentTime);
            }
            else
            {
                paintTimeDisplay(graphicsAdapter, Events.playingTime);
            }
        }
        
        if(Window.unlockedTimer > 0)
        {
            paintHelicopterDisplay(graphicsAdapter,
                                   Window.helicopterDummies.get(Window.unlockedType),
                                   unlockedDisplayPosition(Window.unlockedTimer),
                                   -50);
        }
    }
    
    private boolean showBossHealthBar()
    {
        return EnemyController.currentMiniBoss != null || (Events.boss != null && Events.level < 51);
    }
    
    private void paintBossHealthBar(GraphicsAdapter graphicsAdapter)
    {
        if(EnemyController.currentMiniBoss != null)
        {
            paintBossHealthBar(graphicsAdapter, EnemyController.currentMiniBoss);
        }
        else
        {
            paintBossHealthBar(graphicsAdapter, Events.boss);
        }
    }
    
    private void paintBossHealthBar(GraphicsAdapter graphicsAdapter, Enemy boss)
    {
        graphicsAdapter.setColor(Colorations.hitPoints);
        graphicsAdapter.fillRect(813, 5, (ENEMY_HEALTH_BAR_WIDTH * boss.getHitPoints()) / boss.startingHitPoints, 10);
        if(Events.timeOfDay == NIGHT)
        {
            graphicsAdapter.setColor(Color.red);
        }
        else
        {
            graphicsAdapter.setColor(Colorations.red);
        }
        graphicsAdapter.fillRect(813 + (ENEMY_HEALTH_BAR_WIDTH * boss.getHitPoints()) / boss.startingHitPoints,
                                 5,
                                 ENEMY_HEALTH_BAR_WIDTH - (ENEMY_HEALTH_BAR_WIDTH * boss.getHitPoints()) / boss.startingHitPoints,
                                 10);
        if(Events.timeOfDay == NIGHT)
        {
            graphicsAdapter.setColor(Color.white);
        }
        else
        {
            graphicsAdapter.setColor(Color.black);
        }
        graphicsAdapter.drawRect(813, 5, ENEMY_HEALTH_BAR_WIDTH, 10);
    }
    
    private void paintHealthBar(GraphicsAdapter graphicsAdapter, Helicopter helicopter)
    {
        paintHealthBar(graphicsAdapter,
                       helicopter,
                       HEALTH_BAR_POSITION.x,
                       HEALTH_BAR_POSITION.y,
                       HEALTH_BAR_LENGTH,
                       true);
    }
    
    private void paintCollectedPowerUps(GraphicsAdapter graphicsAdapter)
    {
        PowerUpPainter powerUpPainter = GraphicsManager.getInstance()
                                                       .getPainter(PowerUp.class);
        AtomicInteger statusBarPositionIndex = new AtomicInteger();
        PowerUpType.getStatusBarPowerUpTypes()
                   .stream()
                   .filter(Window.collectedPowerUps::containsKey)
                   .forEach(powerUpType -> {
                       int x = 166 + statusBarPositionIndex.get() * 28;
                       powerUpPainter.paint(graphicsAdapter, Window.collectedPowerUps.get(powerUpType), x);
                       statusBarPositionIndex.incrementAndGet();
                   });
    }
    
    private void paintFpsDisplay(GraphicsAdapter graphicsAdapter)
    {
        graphicsAdapter.setColor(Color.white);
        graphicsAdapter.setFont(fontProvider.getPlain(18));
        graphicsAdapter.drawString("FPS: " + (Window.fps == 0 ? Window.dictionary.pleaseWait() : Window.fps), 292, 449);
    }
    
    private void paintTimeDisplay(GraphicsAdapter graphicsAdapter, long time)
    {
        graphicsAdapter.setColor(Color.white);
        graphicsAdapter.setFont(fontProvider.getPlain(18));
        String outputString = String.format("%s: %s",
                                            Window.dictionary.playingTime(),
                                            Window.returnTimeDisplayText(time));
        // TODO besser lösen -> String abmessen
        graphicsAdapter.drawString(outputString, Window.language.getTimeDisplayPositionX(), 450);
    }
    
    private void paintGui(GraphicsAdapter graphicsAdapter)
    {
        // Werkstatt-Button
        if(Events.isRestartWindowVisible)
        {
            boolean gameOver = Events.money <= Events.repairFee(helicopter, helicopter.isDamaged) || Events.level >= 51;
            
            if(gameOver)
            {
                paintGameOverPopupWindow(graphicsAdapter);
            }
            else
            {
                paintDefaultRestartPopupWindow(graphicsAdapter);
            }
        }
        else
        {
            if(helicopter.isOnTheGround())
            {
                Window.buttons.get(GroundButtonType.REPAIR_SHOP)
                              .paint(graphicsAdapter);
                Window.buttons.get(GroundButtonType.MAIN_MENU)
                              .paint(graphicsAdapter);
            }
            if(Window.isMenuVisible)
            {
                paintInGameMenu(graphicsAdapter);
            }
        }
    }
    
    private void paintGameOverPopupWindow(GraphicsAdapter graphicsAdapter)
    {
        if(Events.level < 51 || helicopter.getType() == HELIOS)
        {
            paintInGamePopupWindow(graphicsAdapter, PopupWindowType.TOTAL_CRASH_OR_FINAL_VICTORY);
        }
        else
        {
            paintInGamePopupWindow(graphicsAdapter, PopupWindowType.TEMPORARILY_VICTORY);
        }
        
        setFontForRestartWindow(graphicsAdapter);
        
        if(Events.level < 51)
        {
            List<String> crashMessages = Window.dictionary.getCrashMessages();
            graphicsAdapter.drawString(crashMessages.get(0), 390, 137);
            graphicsAdapter.drawString(crashMessages.get(1), 390, 155);
            graphicsAdapter.drawString(crashMessages.get(2), 390, 179);
            graphicsAdapter.drawString(crashMessages.get(3), 390, 197);
        }
        else if(helicopter.getType() == HELIOS)
        {
            List<String> victoryMessages = Window.dictionary.getHeliosVictoryMessages();
            graphicsAdapter.drawString(victoryMessages.get(0), 390, 137);
            graphicsAdapter.drawString(victoryMessages.get(1), 390, 155);
            graphicsAdapter.drawString(victoryMessages.get(2), 390, 179);
            graphicsAdapter.drawString(victoryMessages.get(3), 390, 197);
        }
        else
        {
            List<String> victoryMessages = Window.dictionary.getDefaultVictoryMessages();
            int shiftY = Window.language.getVictoryMessageShiftY();
            for(int i = 0; i < 7; i++)
            {
                graphicsAdapter.drawString(victoryMessages.get(i), 390, 124 + i * 18 - shiftY);
            }
        }
        
        paintSecondNewGameButton(graphicsAdapter, true);
    }
    
    private void paintDefaultRestartPopupWindow(GraphicsAdapter graphicsAdapter)
    {
        paintInGamePopupWindow(graphicsAdapter, PopupWindowType.REPAIRABLE_CRASH);
        
        setFontForRestartWindow(graphicsAdapter);
        
        List<String> crashMessages = Window.dictionary.getCrashMessages();
        graphicsAdapter.drawString(crashMessages.get(0), 410, 179);
        graphicsAdapter.drawString(crashMessages.get(1), 410, 197);
        
        paintSecondNewGameButton(graphicsAdapter, false);
    }
    
    private static void paintInGamePopupWindow(GraphicsAdapter graphicsAdapter, PopupWindowType popupWindowType)
    {
        VerticalBoundaries verticalBoundaries = popupWindowType.getVerticalBoundaries();
        
        GraphicalEntities.paintFrame(graphicsAdapter,
                                     IN_GAME_MENU_LEFT, verticalBoundaries.top(),
                                     IN_GAME_MENU_WIDTH, verticalBoundaries.height(),
                                     Colorations.golden);
    }
    
    private static void paintSecondNewGameButton(GraphicsAdapter graphicsAdapter, boolean gameOver)
    {
        Button newGameButton2 = Window.buttons.get(MainMenuButtonType.NEW_GAME_2);
        newGameButton2.setPrimaryLabel(Window.dictionary.messageAfterCrash(gameOver));
        newGameButton2.paint(graphicsAdapter);
    }
    
    private static void setFontForRestartWindow(GraphicsAdapter graphicsAdapter)
    {
        graphicsAdapter.setFont(fontProvider.getPlain(18));
        graphicsAdapter.setColor(Colorations.red);
    }
    
    
    private void paintInGameMenu(GraphicsAdapter graphicsAdapter)
    {
        paintInGamePopupWindow(graphicsAdapter, PopupWindowType.DEFAULT);
        graphicsAdapter.setColor(Colorations.red);
        graphicsAdapter.setFont(fontProvider.getPlain(25));
        graphicsAdapter.drawString(Window.dictionary.mainMenu(), 422 + Window.language.getMainMenuHeadlineShiftX(), 106);
        
        MainMenuButtonType.getValues()
                          .forEach(buttonType -> Window.buttons.get(buttonType)
                                                               .paint(graphicsAdapter));
    }
    
    /**
     * Background Display
     **/
    private void paintBackgroundDisplays(GraphicsAdapter graphicsAdapter)
    {
        updateDependencies();
        if(helicopter.isOnTheGround() || Window.levelDisplayTimer.isActive())
        {
            paintLevelDisplay(graphicsAdapter);
        }
        if(Events.commendationTimer > 0)
        {
            paintPraiseDisplay(graphicsAdapter);
        }
        if(Window.moneyDisplayTimer != Timer.DISABLED
            || helicopter.isDamaged
            || (helicopter.isOnTheGround()
            && !Events.isRestartWindowVisible))
        {
            paintCreditDisplay(graphicsAdapter);
        }
        if(Window.specialInfoSelection != 0)
        {
            paintSpecialInfoDisplay(graphicsAdapter);
        }
    }
    
    private static void paintLevelDisplay(GraphicsAdapter graphicsAdapter)
    {
        if(Events.timeOfDay == NIGHT)
        {
            graphicsAdapter.setColor(Color.white);
        }
        else
        {
            graphicsAdapter.setColor(Color.black);
        }
        graphicsAdapter.setFont(fontProvider.getPlain(36));
        String levelString = "";
        if(Events.level < 51)
        {
            if(Events.level % 10 == 0)
            {
                levelString = "Boss Level " + (Events.level / 10);
            }
            else
            {
                levelString = "Level " + Events.level;
            }
        }
        int stringWidth = graphicsAdapter.getStringWidth(levelString);
        graphicsAdapter.drawString(levelString, (981 - stringWidth) / 2, 55);
    }
    
    private static void paintPraiseDisplay(GraphicsAdapter graphicsAdapter)
    {
        if(Events.timeOfDay == NIGHT)
        {
            graphicsAdapter.setColor(Color.red);
        }
        else
        {
            graphicsAdapter.setColor(Colorations.red);
        }
        MultiKillType multiKillType = MultiKillType.getMultiKillType(Events.lastMultiKill);
        graphicsAdapter.setFont(fontProvider.getPlain(multiKillType.getTextSize()));
        int stringWidth = graphicsAdapter.getStringWidth(multiKillType.getDesignation());
        graphicsAdapter.drawString(multiKillType.getDesignation(), (981 - stringWidth) / 2, 130);
    }
    
    private static void paintCreditDisplay(GraphicsAdapter graphicsAdapter)
    {
        if(Events.timeOfDay == NIGHT)
        {
            graphicsAdapter.setColor(Color.red);
        }
        else
        {
            graphicsAdapter.setColor(Colorations.red);
        }
        graphicsAdapter.setFont(fontProvider.getPlain(22));
        graphicsAdapter.drawString(String.format("%s: %s", Window.dictionary.credit(), Events.getMoneyWithCurrency()),
                                   20,
                                   35);
        if(Events.lastBonus > 0)
        {
            if(Events.timeOfDay == NIGHT)
            {
                graphicsAdapter.setColor(Colorations.MONEY_DISPLAY_NIGHT_RED);
            }
            else
            {
                graphicsAdapter.setColor(Colorations.darkerOrange);
            }
            if(Window.moneyDisplayTimer <= 23)
            {
                graphicsAdapter.setFont(fontProvider.getPlain(Window.moneyDisplayTimer));
            }
            if(Window.moneyDisplayTimer > 23 && Window.moneyDisplayTimer < 77)
            {
                graphicsAdapter.setFont(fontProvider.getPlain(22));
            }
            if(Window.moneyDisplayTimer >= Window.BONUS_DISPLAY_TIME - 23)
            {
                graphicsAdapter.setFont(fontProvider.getPlain(Window.BONUS_DISPLAY_TIME - Window.moneyDisplayTimer));
            }
            graphicsAdapter.drawString("+" + Events.getLastBonusWithCurrency(), 20, 60);
            if(Events.lastExtraBonus > 0)
            {
                if(Events.timeOfDay == NIGHT)
                {
                    graphicsAdapter.setColor(Color.yellow);
                }
                else
                {
                    graphicsAdapter.setColor(Colorations.darkYellow);
                }
                graphicsAdapter.drawString("+" + Events.getLastExtraBonusWithCurrency(), 20, 86);
            }
        }
    }
    
    private static void paintSpecialInfoDisplay(GraphicsAdapter graphicsAdapter)
    {
        GameStatisticsCalculator gameStatisticsCalculator = gameRessourceProvider.getGameStatisticsCalculator();
        graphicsAdapter.setColor(Colorations.red);
        graphicsAdapter.setFont(fontProvider.getPlain(22));
        String infoString = "";
        if(Window.specialInfoSelection == 1)
        {
            infoString = "Kills bis LevelUp: "
                + Events.killsAfterLevelUp
                + "/"
                + Events.numberOfKillsNecessaryForNextLevelUp();
        }
        else if(Window.specialInfoSelection == 2)
        {
            infoString = "Aktive PowerUps: "
                + gameRessourceProvider.getActiveGameEntityManager()
                                       .getPowerUps()
                                       .get(CollectionSubgroupType.ACTIVE)
                                       .size()
                + ";   Inaktive PowerUps: "
                + gameRessourceProvider.getGameEntitySupplier()
                                       .sizeOf(PowerUp.class);
        }
        else if(Window.specialInfoSelection == 3)
        {
            infoString = "Aktive Explosionen: "
                + gameRessourceProvider.getActiveGameEntityManager()
                                       .getExplosions()
                                       .get(CollectionSubgroupType.ACTIVE)
                                       .size()
                + ";   Inaktive Explosionen: "
                + gameRessourceProvider.getActiveGameEntityManager()
                                       .getExplosions()
                                       .get(CollectionSubgroupType.INACTIVE)
                                       .size();
        }
        else if(Window.specialInfoSelection == 4)
        {
            infoString = "Aktive Gegner: "
                + (gameRessourceProvider.getActiveGameEntityManager()
                                        .getEnemies()
                                        .get(CollectionSubgroupType.ACTIVE)
                                        .size() - EnemyController.currentNumberOfBarriers) + " / " + (LevelManager.maxNr)
                + ";   Zerst\u00F6rte Gegner: "
                + gameRessourceProvider.getActiveGameEntityManager()
                                       .getEnemies()
                                       .get(CollectionSubgroupType.DESTROYED)
                                       .size()
                + ";   Hindernisse: "
                + EnemyController.currentNumberOfBarriers + " / " + LevelManager.maxBarrierNr
                + ";   Inaktive Gegner: "
                + countInactiveNonBarrierEnemies();
        }
        else if(Window.specialInfoSelection == 5)
        {
            infoString = "Aktive Raketen: "
                + gameRessourceProvider.getActiveGameEntityManager()
                                       .getMissiles()
                                       .get(CollectionSubgroupType.ACTIVE)
                                       .size()
                + ";   Inaktive Raketen: "
                + gameRessourceProvider.getActiveGameEntityManager()
                                       .getMissiles()
                                       .get(CollectionSubgroupType.INACTIVE)
                                       .size();
        }
        else if(Window.specialInfoSelection == 6)
        {
            infoString = "Aktive gegnerische Geschosse: "
                + gameRessourceProvider.getActiveGameEntityManager()
                                       .getEnemyMissiles()
                                       .get(CollectionSubgroupType.ACTIVE)
                                       .size()
                + ";   Inaktive gegnerische Geschosse: "
                + gameRessourceProvider.getActiveGameEntityManager()
                                       .getEnemyMissiles()
                                       .get(CollectionSubgroupType.INACTIVE)
                                       .size();
        }
        else if(Window.specialInfoSelection == 7)
        {
            infoString = "Aktive Hintergrundobjekte: "
                + gameRessourceProvider.getActiveGameEntityManager()
                                       .getSceneryObjects()
                                       .get(CollectionSubgroupType.ACTIVE)
                                       .size()
                + ";   Inaktive Hintergrundobjekte: "
                + gameRessourceProvider.getActiveGameEntityManager()
                                       .getSceneryObjects()
                                       .get(CollectionSubgroupType.INACTIVE)
                                       .size();
        }
        else if(Window.specialInfoSelection == 8)
        {
            infoString = "Speed level: "
                + helicopter.getUpgradeLevelOf(ROTOR_SYSTEM)
                + " +   Speed: " + helicopter.rotorSystem;
        }
        else if(Window.specialInfoSelection == 9)
        {
            infoString = "Bonus: " + Events.overallEarnings
                + "   Extra-Bonus: " + Events.extraBonusCounter;
        }
        else if(Window.specialInfoSelection == 10)
        {
            infoString = "Men\u00FC sichtbar: " + Window.isMenuVisible;
        }
        else if(Window.specialInfoSelection == 11)
        {
            infoString = Window.dictionary.defeatedEnemies() + ": "
                + gameStatisticsCalculator.getNumberOfEnemiesKilled()
                + " " + Window.dictionary.prepositionOf() + " "
                + gameStatisticsCalculator.getNumberOfEnemiesSeen()
                + " ("
                + gameStatisticsCalculator.getKillRate()
                + "%)";
        }
        else if(Window.specialInfoSelection == 12)
        {
            int percentage = gameStatisticsCalculator.getMissileHitRate();
            infoString = "Missile counter: "
                + gameStatisticsCalculator.getMissileCounter()
                + "; Hit counter: "
                + gameStatisticsCalculator.getHitCounter()
                + "; Hit rate: "
                + percentage;
        }
        else if(Window.specialInfoSelection == 13)
        {
            infoString = Window.dictionary.typeName();
        }
        else if(Window.specialInfoSelection == 14)
        {
            infoString = helicopter.getTypeSpecificDebuggingOutput();
        }
        else if(Window.specialInfoSelection == 15)
        {
            infoString = String.format("Hit points: %.2f/%.2f; Energie: %.2f/%.2f",
                                       helicopter.getCurrentPlating(),
                                       helicopter.getMaximumPlating(),
                                       helicopter.getCurrentEnergy(),
                                       helicopter.getMaximumEnergy());
        }
        graphicsAdapter.drawString("Info: " + infoString, 20, 155);
    }
    
    private static int countInactiveNonBarrierEnemies()
    {
        return EnemyType.getValues()
                        .stream()
                        .filter(Predicate.not(EnemyType.getBarrierTypes()::contains))
                        .map(GameEntityFactory::getCorrespondingClass)
                        .map(gameRessourceProvider.getGameEntitySupplier()::sizeOf)
                        .mapToInt(Integer::intValue)
                        .sum();
    }
}
