package de.helicopter_vs_aliens.graphics.painter.window;

import de.helicopter_vs_aliens.Main;
import de.helicopter_vs_aliens.control.Events;
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
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.scenery.SceneryLayer;
import de.helicopter_vs_aliens.model.scenery.SceneryObject;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.LinkedList;
import java.util.Optional;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;
import static de.helicopter_vs_aliens.control.CollectionSubgroupType.DESTROYED;
import static de.helicopter_vs_aliens.control.CollectionSubgroupType.INACTIVE;
import static de.helicopter_vs_aliens.control.TimeOfDay.NIGHT;
import static de.helicopter_vs_aliens.gui.window.Window.fontProvider;
import static de.helicopter_vs_aliens.model.RectangularGameEntity.GROUND_Y;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.HELIOS;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.ROTOR_SYSTEM;
import static de.helicopter_vs_aliens.util.dictionary.Language.ENGLISH;

public class GameWindowPainter extends WindowPainter
{
    private static final int
        HEALTH_BAR_LENGTH = 150,       // Länge des HitPoint-Balken des Helikopters
        ENEMY_HEALTH_BAR_WIDTH = 206;
    
    @Override
    public void paint(GraphicsAdapter graphicsAdapter, Window window)
    {
        paintBackground(graphicsAdapter);
        paintBackgroundDisplays(graphicsAdapter);
        Optional.ofNullable(Enemy.currentRock)
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
        controller.getScenery()
                  .paint(graphicsAdapter);
    }
    
    private void paintAllDestroyedEnemies(GraphicsAdapter graphicsAdapter)
    {
        controller.enemies.get(DESTROYED)
                          .forEach(enemy -> enemy.paint(graphicsAdapter));
    }
    
    private void paintAllMissiles(GraphicsAdapter graphicsAdapter)
    {
        controller.missiles.get(ACTIVE)
                           .forEach(missile -> missile.paint(graphicsAdapter));
    }
    
    private void paintAllActiveEnemies(GraphicsAdapter graphicsAdapter)
    {
        Helicopter helicopter = controller.getHelicopter();
        for (int i = 0; i < Enemy.currentNumberOfBarriers; i++)
        {
            Enemy.livingBarrier[i].paint(graphicsAdapter);
        }
        for (Enemy enemy : controller.enemies.get(ACTIVE))
        {
            if (enemy.isVisibleNonBarricadeVessel(helicopter.canDetectCloakedVessels()))
            {
                enemy.paint(graphicsAdapter);
            }
        }
    }
    
    private void paintAllEnemyMissiles(GraphicsAdapter graphicsAdapter)
    {
        controller.enemyMissiles.get(ACTIVE)
                                .forEach(enemyMissile -> enemyMissile.paint(graphicsAdapter));
    }
    
    private void paintAllExplosions(GraphicsAdapter graphicsAdapter)
    {
        controller.explosions.get(ACTIVE)
                             .forEach(explosion -> explosion.paint(graphicsAdapter));
    }
    
    private void paintAllPowerUps(GraphicsAdapter graphicsAdapter)
    {
        controller.powerUps.get(ACTIVE)
                           .forEach(powerUp -> powerUp.paint(graphicsAdapter));
    }
    
    private void paintForeground(GraphicsAdapter graphicsAdapter)
    {
        // der Boden
        graphicsAdapter.setPaint(Colorations.gradientGround[Events.timeOfDay.ordinal()]);
        graphicsAdapter.fillRect(0, GROUND_Y, Main.VIRTUAL_DIMENSION.width, 35);
        
        // Objekte vor dem Helikopter
        LinkedList<SceneryObject> activeSceneryObjects = controller.getScenery()
                                                                   .getSceneryObjects()
                                                                   .get(ACTIVE);
        SceneryLayer.getForegroundLayers()
                    .forEach(layer -> activeSceneryObjects.stream()
                                                          .filter(sceneryObject -> sceneryObject.getLayer() == layer)
                                                          .forEach(sceneryObject -> sceneryObject.paint(graphicsAdapter)));
    }
    
    private void paintForegroundDisplays(GraphicsAdapter graphicsAdapter)
    {
        if (showBossHealthBar())
        {
            paintBossHealthBar(graphicsAdapter);
        }
        paintHealthBar(graphicsAdapter, helicopter);
        paintCollectedPowerUps(graphicsAdapter);
        
        if (controller.isFpsDisplayVisible())
        {
            paintFpsDisplay(graphicsAdapter);
        }
        
        if (helicopter.isOnTheGround())
        {
            if (!Window.isMenuVisible && controller.isMouseCursorInWindow())
            {
                paintTimeDisplay(graphicsAdapter, Events.playingTime
                    + System.currentTimeMillis()
                    - Events.lastCurrentTime);
            } else
            {
                paintTimeDisplay(graphicsAdapter, Events.playingTime);
            }
        }
        
        if (Window.unlockedTimer > 0)
        {
            paintHelicopterDisplay(graphicsAdapter,
                Window.helicopterDummies.get(Window.unlockedType),
                unlockedDisplayPosition(Window.unlockedTimer),
                -50);
        }
    }
    
    private boolean showBossHealthBar()
    {
        return Enemy.currentMiniBoss != null || (Events.boss != null && Events.level < 51);
    }
    
    private void paintBossHealthBar(GraphicsAdapter graphicsAdapter)
    {
        if (Enemy.currentMiniBoss != null)
        {
            paintBossHealthBar(graphicsAdapter, Enemy.currentMiniBoss);
        } else
        {
            paintBossHealthBar(graphicsAdapter, Events.boss);
        }
    }
    
    private void paintBossHealthBar(GraphicsAdapter graphicsAdapter, Enemy boss)
    {
        graphicsAdapter.setColor(Colorations.hitPoints);
        graphicsAdapter.fillRect(813, 5, (ENEMY_HEALTH_BAR_WIDTH * boss.hitpoints) / boss.startingHitpoints, 10);
        if (Events.timeOfDay == NIGHT)
        {
            graphicsAdapter.setColor(Color.red);
        } else
        {
            graphicsAdapter.setColor(Colorations.red);
        }
        graphicsAdapter.fillRect(813 + (ENEMY_HEALTH_BAR_WIDTH * boss.hitpoints) / boss.startingHitpoints, 5, ENEMY_HEALTH_BAR_WIDTH - (ENEMY_HEALTH_BAR_WIDTH * boss.hitpoints) / boss.startingHitpoints, 10);
        if (Events.timeOfDay == NIGHT)
        {
            graphicsAdapter.setColor(Color.white);
        } else
        {
            graphicsAdapter.setColor(Color.black);
        }
        graphicsAdapter.drawRect(813, 5, ENEMY_HEALTH_BAR_WIDTH, 10);
    }
    
    private void paintHealthBar(GraphicsAdapter graphicsAdapter, Helicopter helicopter)
    {
        paintHealthBar(graphicsAdapter, helicopter, HEALTH_BAR_POSITION.x, HEALTH_BAR_POSITION.y, HEALTH_BAR_LENGTH, true);
    }
    
    private void paintCollectedPowerUps(GraphicsAdapter graphicsAdapter)
    {
        int j = 0;
        PowerUpPainter powerUpPainter = GraphicsManager.getInstance()
                                                       .getPainter(PowerUp.class);
        for (int i = 0; i < Window.MAXIMUM_COLLECTED_POWERUPS_COUNT; i++)
        {
            if (Window.collectedPowerUp[i] != null)
            {
                powerUpPainter.paint(graphicsAdapter, Window.collectedPowerUp[i], 166 + j * 28);
                j++;
            }
        }
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
        String outputString = String.format("%s: %s", Window.dictionary.playingTime(), Window.returnTimeDisplayText(time));
        // TODO besser lösen -> String abmessen
        graphicsAdapter.drawString(outputString, Window.language == ENGLISH ? 646 : 661, 450);
    }
    
    private void paintGui(GraphicsAdapter graphicsAdapter)
    {
        // Werkstatt-Button
        if (Events.isRestartWindowVisible)
        {
            boolean gameOver;
            gameOver = Events.money <= Events.repairFee(helicopter, helicopter.isDamaged) || Events.level >= 51;
            paintRestartWindow(graphicsAdapter, helicopter, gameOver);
        } else
        {
            if (helicopter.isOnTheGround())
            {
                Window.buttons.get(GroundButtonType.REPAIR_SHOP)
                              .paint(graphicsAdapter);
                Window.buttons.get(GroundButtonType.MAIN_MENU)
                              .paint(graphicsAdapter);
            }
            if (Window.isMenuVisible)
            {
                paintInGameMenu(graphicsAdapter);
            }
        }
    }
    
    private void paintRestartWindow(GraphicsAdapter graphicsAdapter,
                                    Helicopter helicopter,
                                    boolean gameOver)
    {
        if (!gameOver)
        {
            GraphicalEntities.paintFrame(graphicsAdapter, 363, 147, 256, 111, Colorations.golden);
        } else if (Events.level < 51 || helicopter.getType() == HELIOS)
        {
            GraphicalEntities.paintFrame(graphicsAdapter, 363, 112, 256, 146, Colorations.golden);
        } else if (Window.language == ENGLISH)
        {
            GraphicalEntities.paintFrame(graphicsAdapter, 363, 100, 256, 158, Colorations.golden);
        } else
        {
            GraphicalEntities.paintFrame(graphicsAdapter, 363, 64, 256, 194, Colorations.golden);
        }
        
        graphicsAdapter.setFont(fontProvider.getPlain(18));
        graphicsAdapter.setColor(Colorations.red);
        if (!gameOver)
        {
            graphicsAdapter.drawString((Window.language == ENGLISH ? "Your helicopter was" : "Ihr Helikopter wurde"), 410, 179);
            graphicsAdapter.drawString((Window.language == ENGLISH ? "severely damaged!" : "schwer beschädigt!"), 410, 197);
        } else if (Events.level < 51)
        {
            graphicsAdapter.drawString((Window.language == ENGLISH ? "Your helicopter was" : "Ihr Helikopter wurde"), 390, 137);
            graphicsAdapter.drawString((Window.language == ENGLISH ? "severely damaged!" : "schwer beschädigt!"), 390, 155);
            graphicsAdapter.drawString((Window.language == ENGLISH ? "Unfortunately, you " : "Leider reicht ihr Guthaben"), 390, 179);
            graphicsAdapter.drawString((Window.language == ENGLISH ? "cannot afford the repairs." : "nicht für eine Reparatur."), 390, 197);
        } else
        {
            if (helicopter.getType() == HELIOS)
            {
                graphicsAdapter.drawString((Window.language == ENGLISH ? "Congratulations!" : "Herzlichen Glückwunsch!"), 390, 137);
                graphicsAdapter.drawString((Window.language == ENGLISH ? "The attack was repulsed." : "Der Angriff wurde abgewehrt."), 390, 155);
                graphicsAdapter.drawString((Window.language == ENGLISH ? "Once again, mankind" : "Wieder einmal lebt die"), 390, 179);
                graphicsAdapter.drawString((Window.language == ENGLISH ? "lives in peace!" : "Menschheit in Frieden!"), 390, 197);
            } else
            {
                int i = Window.language == ENGLISH ? 0 : 36;
                
                graphicsAdapter.drawString((Window.language == ENGLISH ? "You won a great victory," : "Sie haben einen großen"), 390, 124 - i);
                graphicsAdapter.drawString((Window.language == ENGLISH ? "but the war isn't over yet." : "Sieg errungen, aber der"), 390, 142 - i);
                graphicsAdapter.drawString((Window.language == ENGLISH ? "Rumor has it only helios" : "Krieg ist noch nicht vorbei."), 390, 160 - i);
                graphicsAdapter.drawString((Window.language == ENGLISH ? "type helicopters can" : "Gerüchten zufolge können "), 390, 178 - i);
                graphicsAdapter.drawString((Window.language == ENGLISH ? "finally stop the invasion." : "nur Helikopter der Helios-"), 390, 196 - i);
                graphicsAdapter.drawString((Window.language == ENGLISH ? "" : "Klasse die Alien-Invasion"), 390, 214 - i);
                graphicsAdapter.drawString((Window.language == ENGLISH ? "" : "endgültig stoppen."), 390, 232 - i);
            }
        }
        Button newGameButton2 = Window.buttons.get(MainMenuButtonType.NEW_GAME_2);
        newGameButton2.setPrimaryLabel(Window.dictionary.messageAfterCrash(gameOver));
        newGameButton2.paint(graphicsAdapter);
    }
    
    private void paintInGameMenu(GraphicsAdapter graphicsAdapter)
    {
        GraphicalEntities.paintFrame(graphicsAdapter, 363, 77, 256, 231, Colorations.golden);
        graphicsAdapter.setColor(Colorations.red);
        graphicsAdapter.setFont(fontProvider.getPlain(25));
        graphicsAdapter.drawString(Window.dictionary.mainMenu(), 422 + (Window.language == ENGLISH ? 6 : 0), 106);
        
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
        if (helicopter.isOnTheGround() || Window.levelDisplayTimer.isActive())
        {
            paintLevelDisplay(graphicsAdapter);
        }
        if (Events.commendationTimer > 0)
        {
            paintPraiseDisplay(graphicsAdapter);
        }
        if (Window.moneyDisplayTimer != Timer.DISABLED
            || helicopter.isDamaged
            || (helicopter.isOnTheGround()
            && !Events.isRestartWindowVisible))
        {
            paintCreditDisplay(graphicsAdapter);
        }
        if (Window.specialInfoSelection != 0)
        {
            paintSpecialInfoDisplay(graphicsAdapter);
        }
    }
    
    private static void paintLevelDisplay(GraphicsAdapter graphicsAdapter)
    {
        if (Events.timeOfDay == NIGHT)
        {
            graphicsAdapter.setColor(Color.white);
        } else
        {
            graphicsAdapter.setColor(Color.black);
        }
        graphicsAdapter.setFont(fontProvider.getPlain(36));
        String levelString = "";
        if (Events.level < 51)
        {
            if (Events.level % 10 == 0)
            {
                levelString = "Boss Level " + (Events.level / 10);
            } else
            {
                levelString = "Level " + Events.level;
            }
        }
        FontMetrics fm = graphicsAdapter.getFontMetrics();
        int sw = fm.stringWidth(levelString);
        graphicsAdapter.drawString(levelString, (981 - sw) / 2, 55);
    }
    
    private static void paintPraiseDisplay(GraphicsAdapter graphicsAdapter)
    {
        if (Events.timeOfDay == NIGHT)
        {
            graphicsAdapter.setColor(Color.red);
        }
        // TODO wirklich 2 mal Color.RED? Funktion in Events zur Farbrückgabe verwenden
        else
        {
            graphicsAdapter.setColor(Color.red);
        }
        MultiKillType multiKillType = MultiKillType.getMultiKillType(Events.lastMultiKill);
        graphicsAdapter.setFont(fontProvider.getPlain(multiKillType.getTextSize()));
        FontMetrics fm = graphicsAdapter.getFontMetrics();
        int sw = fm.stringWidth(multiKillType.getDesignation());
        graphicsAdapter.drawString(multiKillType.getDesignation(), (981 - sw) / 2, 130);
    }
    
    private static void paintCreditDisplay(GraphicsAdapter graphicsAdapter)
    {
        if (Events.timeOfDay == NIGHT)
        {
            graphicsAdapter.setColor(Color.red);
        } else
        {
            graphicsAdapter.setColor(Colorations.red);
        }
        graphicsAdapter.setFont(fontProvider.getPlain(22));
        graphicsAdapter.drawString(String.format("%s: %d €", Window.dictionary.credit(), Events.money), 20, 35);
        if (Events.lastBonus > 0)
        {
            if (Events.timeOfDay == NIGHT)
            {
                graphicsAdapter.setColor(Colorations.MONEY_DISPLAY_NIGHT_RED);
            } else
            {
                graphicsAdapter.setColor(Colorations.darkerOrange);
            }
            if (Window.moneyDisplayTimer <= 23)
            {
                graphicsAdapter.setFont(new Font("Dialog", Font.PLAIN, Window.moneyDisplayTimer));
            }
            if (Window.moneyDisplayTimer > 23 && Window.moneyDisplayTimer < 77)
            {
                graphicsAdapter.setFont(fontProvider.getPlain(22));
            }
            if (Window.moneyDisplayTimer >= Window.BONUS_DISPLAY_TIME - 23)
            {
                graphicsAdapter.setFont(new Font("Dialog", Font.PLAIN, Window.BONUS_DISPLAY_TIME - Window.moneyDisplayTimer));
            }
            graphicsAdapter.drawString("+" + Events.lastBonus + " €", 20, 60);
            if (Events.lastExtraBonus > 0)
            {
                if (Events.timeOfDay == NIGHT)
                {
                    graphicsAdapter.setColor(Color.yellow);
                } else
                {
                    graphicsAdapter.setColor(Colorations.darkYellow);
                }
                graphicsAdapter.drawString("+" + Events.lastExtraBonus + " €", 20, 86);
            }
        }
    }
    
    private static void paintSpecialInfoDisplay(GraphicsAdapter graphicsAdapter)
    {
        graphicsAdapter.setColor(Colorations.red);
        graphicsAdapter.setFont(fontProvider.getPlain(22));
        String infoString = "";
        if (Window.specialInfoSelection == 1)
        {
            infoString = "Kills bis LevelUp: "
                + Events.killsAfterLevelUp
                + "/"
                + Events.numberOfKillsNecessaryForNextLevelUp();
        } else if (Window.specialInfoSelection == 2)
        {
            infoString = "Aktive PowerUps: "
                + controller.powerUps.get(ACTIVE)
                                     .size()
                + ";   Inaktive PowerUps: "
                + controller.getGameEntityRecycler()
                            .sizeOf(PowerUp.class);
        } else if (Window.specialInfoSelection == 3)
        {
            infoString = "Aktive Explosionen: "
                + controller.explosions.get(ACTIVE)
                                       .size()
                + ";   Inaktive Explosionen: "
                + controller.explosions.get(INACTIVE)
                                       .size();
        } else if (Window.specialInfoSelection == 4)
        {
            infoString = "Aktive Gegner: "
                + (controller.enemies.get(ACTIVE)
                                     .size() - Enemy.currentNumberOfBarriers) + " / " + (Enemy.maxNr)
                + ";   Zerstörte Gegner: "
                + controller.enemies.get(DESTROYED)
                                    .size()
                + ";   Hindernisse: "
                + Enemy.currentNumberOfBarriers + " / " + Enemy.maxBarrierNr
                + ";   Inaktive Gegner: "
                /*+ controller.enemies.get(INACTIVE)
                                    .size()*/;
        } else if (Window.specialInfoSelection == 5)
        {
            infoString = "Aktive Raketen: "
                + controller.missiles.get(ACTIVE)
                                     .size()
                + ";   Inaktive Raketen: "
                + controller.missiles.get(INACTIVE)
                                     .size();
        } else if (Window.specialInfoSelection == 6)
        {
            infoString = "Aktive gegnerische Geschosse: "
                + controller.enemyMissiles.get(ACTIVE)
                                          .size()
                + ";   Inaktive gegnerische Geschosse: "
                + controller.enemyMissiles.get(INACTIVE)
                                          .size();
        } else if (Window.specialInfoSelection == 7)
        {
            infoString = "Aktive Hintergrundobjekte: "
                + controller.getScenery()
                            .getSceneryObjects()
                            .get(ACTIVE)
                            .size()
                + ";   Inaktive Hintergrundobjekte: "
                + controller.getScenery().getSceneryObjects().get(INACTIVE)
                                           .size();
        } else if (Window.specialInfoSelection == 8)
        {
            infoString = "Speed level: "
                + helicopter.getUpgradeLevelOf(ROTOR_SYSTEM)
                + " +   Speed: " + helicopter.rotorSystem;
        } else if (Window.specialInfoSelection == 9)
        {
            infoString = "Bonus: " + Events.overallEarnings
                + "   Extra-Bonus: " + Events.extraBonusCounter;
        } else if (Window.specialInfoSelection == 10)
        {
            infoString = "Menü sichtbar: " + Window.isMenuVisible;
        } else if (Window.specialInfoSelection == 11)
        {
            int percentage = helicopter.numberOfEnemiesSeen > 0
                ? 100 * helicopter.numberOfEnemiesKilled / helicopter.numberOfEnemiesSeen
                : 0;
            infoString = (Window.language == ENGLISH
                ? "Defeated enemies: "
                : "Besiegte Gegner: ")
                + helicopter.numberOfEnemiesKilled
                + (Window.language == ENGLISH ? " of " : " von ")
                + helicopter.numberOfEnemiesSeen
                + " ("
                + percentage
                + "%)";
        } else if (Window.specialInfoSelection == 12)
        {
            int percentage = helicopter.missileCounter != 0
                ? 100 * helicopter.hitCounter / helicopter.missileCounter
                : 0;
            infoString = "Missile counter: "
                + helicopter.missileCounter
                + "; Hit counter: "
                + helicopter.hitCounter
                + "; Hit rate: "
                + percentage;
        } else if (Window.specialInfoSelection == 13)
        {
            infoString = Window.dictionary.typeName();
        } else if (Window.specialInfoSelection == 14)
        {
            infoString = helicopter.getTypeSpecificDebuggingOutput();
        } else if (Window.specialInfoSelection == 15)
        {
            infoString = String.format("Hitpoints: %.2f/%.2f; Energie: %.2f/%.2f",
                helicopter.getCurrentPlating(),
                helicopter.getMaximumPlating(),
                helicopter.getCurrentEnergy(),
                helicopter.getMaximumEnergy());
        }
        graphicsAdapter.drawString("Info: " + infoString, 20, 155);
    }
}
