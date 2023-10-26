package de.helicopter_vs_aliens.graphics.painter.window;

import de.helicopter_vs_aliens.control.BossLevel;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.GameStatisticsCalculator;
import de.helicopter_vs_aliens.control.ressource_transfer.GameResources;
import de.helicopter_vs_aliens.graphics.GraphicalEntities;
import de.helicopter_vs_aliens.graphics.GraphicsAdapter;
import de.helicopter_vs_aliens.gui.button.StartScreenSubCancelButtonType;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;

import static de.helicopter_vs_aliens.control.Events.MAXIMUM_LEVEL;
import static de.helicopter_vs_aliens.gui.window.Window.fontProvider;

public class ScoreScreenWindowPainter extends WindowPainter
{
    private static final int
        SPACE_BETWEEN_ROWS = 30,
        X_POS_1 = 351,
        X_POS_2 = 633,
        Y_POS = 129;

    @Override
    public void paint(GraphicsAdapter graphicsAdapter, Window window)
    {
        super.paint(graphicsAdapter, window);
        paintScoreScreen(graphicsAdapter, helicopter);
    }

    private static void paintScoreScreen(GraphicsAdapter graphicsAdapter, Helicopter helicopter)
    {
        // Helikopter-Anzeige
        paintHelicopterDisplay(graphicsAdapter, helicopter, 0, 5);

        graphicsAdapter.setPaint(Colorations.gradientVariableWhite);
        graphicsAdapter.setFont(fontProvider.getPlain(60));
        String temporaryString = Window.dictionary.gameStatistics();
        graphicsAdapter.drawString((temporaryString), (981 - graphicsAdapter.getStringWidth(temporaryString)) / 2, 65);

        GraphicalEntities.paintFrame(graphicsAdapter, 619, 90, 376, 298);
        GraphicalEntities.paintFrameLine(graphicsAdapter, 621, 140, 372);
        GraphicalEntities.paintFrameLine(graphicsAdapter, 621, 249, 372);
        GraphicalEntities.paintFrame(graphicsAdapter, 297, 90, 250, 200);
        
        Window.buttons.get(StartScreenSubCancelButtonType.CANCEL).paint(graphicsAdapter);
        
        Color color;
        String missionText;
        Long totalPlayingTime = helicopter.scoreScreenTimes.getTotalPlayingTime();
        
        if(Events.level > MAXIMUM_LEVEL)
        {
            color = Color.green;
            missionText = Window.dictionary.missionCompletedIn(totalPlayingTime);
        }
        else
        {
            color = Color.red;
            missionText = Window.dictionary.missionFailedIn(totalPlayingTime);
        }
        
        graphicsAdapter.setColor(color);
        graphicsAdapter.drawString(missionText, X_POS_2, Y_POS - 9);
        
        graphicsAdapter.setColor(Color.white);
        graphicsAdapter.drawString(Window.dictionary.playingTimePerBoss() + ": ", X_POS_1 - 20, Y_POS - 9);

        BossLevel.getValues()
                 .forEach(bossLevel -> {
                     String boss = Window.dictionary.boss();
                     if(bossLevel.completed())
                     {
                         graphicsAdapter.setColor(Color.green);
                         graphicsAdapter.drawString(Window.dictionary.minutes(helicopter.scoreScreenTimes.get(bossLevel)) + " (" + boss + " " + bossLevel.getBossNr() + ")", X_POS_1, Y_POS - 9 + SPACE_BETWEEN_ROWS * bossLevel.getBossNr());
                     }
                     else
                     {
                         graphicsAdapter.setColor(Color.red);
                         graphicsAdapter.drawString(Window.dictionary.undefeated() + " (" + boss + " " + bossLevel.getBossNr() + ")", X_POS_1, Y_POS - 9 + SPACE_BETWEEN_ROWS * bossLevel.getBossNr());
                     }
                 });

        graphicsAdapter.setColor(Color.white);
        // TODO hier dictionary einsetzen und loop erzeugen
        GameStatisticsCalculator gameStatisticsCalculator = GameResources.getProvider()
                                                                         .getGameStatisticsCalculator();
        graphicsAdapter.drawString((Window.dictionary.crashLandings() + ": ")
                + gameStatisticsCalculator.getNumberOfCrashes(),
            X_POS_2, Y_POS + SPACE_BETWEEN_ROWS + 11);
        graphicsAdapter.drawString(Window.dictionary.repairs() + ": "
                + gameStatisticsCalculator.getNumberOfRepairs(),
            X_POS_2, Y_POS + SPACE_BETWEEN_ROWS * 2 + 11);
        graphicsAdapter.drawString(Window.dictionary.overallEarnings() + ": "
                + Events.getLastOverallEarningsWithCurrency(),
            X_POS_2, Y_POS + SPACE_BETWEEN_ROWS * 3 + 11);

        int percentage;
        String outputString;
        
        graphicsAdapter.setColor(Colorations.scoreScreen[0]);
        percentage = Events.bonusIncomePercentage();
        outputString = Window.dictionary.additionalIncomeDueToExtraBoni() + ": +" + percentage + "%";
        graphicsAdapter.drawString(outputString, X_POS_2, Y_POS + SPACE_BETWEEN_ROWS * 5);
        
        graphicsAdapter.setColor(Colorations.scoreScreen[1]);
        percentage = gameStatisticsCalculator.getKillRate();
        outputString = Window.dictionary.defeatedEnemies() + ": " + gameStatisticsCalculator.getNumberOfEnemiesKilled() + " " + Window.dictionary.prepositionOf() + " " + gameStatisticsCalculator.getNumberOfEnemiesSeen() + " (" + percentage + "%";
        graphicsAdapter.drawString(outputString + ")", X_POS_2, Y_POS + SPACE_BETWEEN_ROWS * 6);
        
        graphicsAdapter.setColor(Colorations.scoreScreen[2]);
        percentage = gameStatisticsCalculator.getMiniBossKillRate();
        outputString = Window.dictionary.defeatedMiniBosses() + ": " + gameStatisticsCalculator.getNumberOfMiniBossKilled() + " " + Window.dictionary.prepositionOf() + " " + gameStatisticsCalculator.getNumberOfMiniBossSeen() + " (" + percentage + "%)";
        graphicsAdapter.drawString(outputString, X_POS_2, Y_POS + SPACE_BETWEEN_ROWS * 7);
        
        graphicsAdapter.setColor(Colorations.scoreScreen[3]);
        percentage = gameStatisticsCalculator.getMissileHitRate();
        outputString = Window.dictionary.hitRate() + ": " + percentage + "%";
        graphicsAdapter.drawString(outputString, X_POS_2, Y_POS + SPACE_BETWEEN_ROWS * 8);
    }
}
