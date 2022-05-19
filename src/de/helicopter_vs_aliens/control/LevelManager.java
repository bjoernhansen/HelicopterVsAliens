package de.helicopter_vs_aliens.control;

import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.enemy.EnemyType;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;

public class LevelManager
{
    public static int
        maxNr;// bestimmt wie viele Standard-Gegner gleichzeitig erscheinen können
    
    public static EnemyType
        nextBossEnemyType;		 	// bestimmt, welche Boss-Typ erstellt wird
    
    public static int
        // TODO selection ist kein guter Bezeichner
        selection;            // bestimmt welche Typen von Gegnern zufällig erscheinen können
    
    public static int
        maxBarrierNr;            // bestimmt wie viele Hindernis-Gegner gleichzeitig erscheinen können
    public static int
        selectionBarrier;    // bestimmt den Typ der Hindernis-Gegner
    public static boolean
        wasEnemyCreationPaused =  false;// = false: es werden keine neuen Gegner erzeugt, bis die Anzahl aktiver Gegner auf 0 fällt
    
    public static void adaptToFirstLevel()
    {
        maxNr = 2;
        nextBossEnemyType = null;
        selection = 3;
        maxBarrierNr = 0;
        selectionBarrier = 1;
    }
    
    public static void adaptToLevel(Helicopter helicopter, int level, boolean isRealLevelUp)
    {
        if(level == 1)
        {
            adaptToFirstLevel();
        }
        else if(level == 2){
            maxNr = 3;}
        else if(level == 3){
            selection = 6;}
        else if(level == 4){
            selection = 10; maxBarrierNr = 1;}
        else if(level == 5){
            selection = 15;}
        else if(level == 6)
        {
            wasEnemyCreationPaused = false;
            maxNr = 3;
            nextBossEnemyType = null;
            selection = 25;
            maxBarrierNr = 1;
            selectionBarrier = 2;
            
        }
        else if(level == 7){
            selection = 30; maxBarrierNr = 2;}
        else if(level == 8){
            maxNr = 4;}
        else if(level == 9)
        {
            selection = 35;
            maxNr = 3;
            maxBarrierNr = 3;
        }
        else if(level == 10)
        {
            wasEnemyCreationPaused = true;
            nextBossEnemyType = EnemyType.BOSS_1;
            selection = 0;
            helicopter.startDecayOfAllCurrentBooster();
        }
        else if(level == 11)
        {
            maxNr = 3;
            nextBossEnemyType = null;
            selection = 75;
            maxBarrierNr = 1;
            selectionBarrier = 2;
            
            if(	 helicopter.isCountingAsFairPlayedHelicopter()
                 && !Events.recordTimeManager.hasAnyBossBeenKilledBefore())
            {
                Window.unlock(HelicopterType.HELIOS);
            }
            if(isRealLevelUp){Events.determineHighscoreTimes(helicopter);}
        }
        else if(level == 12){
            selectionBarrier = 3;}
        else if(level == 13){
            maxBarrierNr = 2; selection = 105;}
        else if(level == 14){
            selection = 135;}
        else if(level == 15){
            selectionBarrier = 4;}
        else if(level == 16)
        {
            wasEnemyCreationPaused = false;
            maxNr = 4;
            nextBossEnemyType = null;
            selection = 155;
            maxBarrierNr = 2;
            selectionBarrier = 4;
        }
        else if(level == 17){
            selection = 175;}
        else if(level == 18){
            selectionBarrier = 5;}
        else if(level == 19){
            maxBarrierNr = 3;}
        else if(level == 20)
        {
            wasEnemyCreationPaused = true;
            nextBossEnemyType = EnemyType.BOSS_2;
            selection = 0;
            helicopter.startDecayOfAllCurrentBooster();
            if(helicopter.isCountingAsFairPlayedHelicopter() && !helicopter.getType().hasReachedLevel20())
            {
                Events.helicoptersThatReachedLevel20.add(helicopter.getType());
                helicopter.updateUnlockedHelicopters();
            }
        }
        else if(level == 21)
        {
            maxNr = 3;
            nextBossEnemyType = null;
            selection = 400;
            maxBarrierNr = 2;
            selectionBarrier = 5;
            
            if(isRealLevelUp){Events.determineHighscoreTimes(helicopter);}
        }
        else if(level == 22){
            selection = 485;}
        else if(level == 23){
            selection = 570;}
        else if(level == 24){
            maxNr = 4;}
        else if(level == 25){
            selection = 660;}
        else if(level == 26)
        {
            wasEnemyCreationPaused = false;
            maxNr = 4;
            nextBossEnemyType = null;
            selection = 735;
            maxBarrierNr = 2;
            selectionBarrier = 5;
        }
        else if(level == 27){
            selection = 835;}
        else if(level == 28){
            maxNr = 5;}
        else if(level == 29){
            maxNr = 4; maxBarrierNr = 3;}
        else if(level == 30)
        {
            wasEnemyCreationPaused = true;
            nextBossEnemyType = EnemyType.BOSS_3;
            selection = 0;
            helicopter.startDecayOfAllCurrentBooster();
        }
        else if(level == 31)
        {
            maxNr = 3;
            nextBossEnemyType = null;
            selection = 1670;
            maxBarrierNr = 2;
            selectionBarrier = 5;
            
            if(isRealLevelUp){Events.determineHighscoreTimes(helicopter);}
        }
        else if(level == 32){
            selectionBarrier = 6;}
        else if(level == 33){
            selection = 2175;}
        else if(level == 34){
            maxNr = 4;}
        else if(level == 35){
            selection = 3180;}
        else if(level == 36)
        {
            wasEnemyCreationPaused = false;
            maxNr = 4;
            nextBossEnemyType = null;
            selection = 4185;
            maxBarrierNr = 2;
            selectionBarrier = 6;
        }
        else if(level == 37){
            selection = 5525;}
        else if(level == 38){
            maxNr = 5;}
        else if(level == 39){
            maxNr = 4; maxBarrierNr = 3;}
        else if(level == 40)
        {
            wasEnemyCreationPaused = true;
            nextBossEnemyType = EnemyType.BOSS_4;
            selection = 0;
            helicopter.startDecayOfAllCurrentBooster();
        }
        else if(level == 41)
        {
            maxNr = 3;
            nextBossEnemyType = null;
            selection = 15235;
            maxBarrierNr = 2;
            selectionBarrier = 6;
            
            if(isRealLevelUp){Events.determineHighscoreTimes(helicopter);}
        }
        else if(level == 42){
            selectionBarrier = 7; maxNr = 4;}
        else if(level == 43){
            selection = 20760;}
        else if(level == 44){
            selectionBarrier = 8; maxNr = 5;}
        else if(level == 45){
            selection = 26285;}
        else if(level == 46)
        {
            wasEnemyCreationPaused = false;
            maxNr = 5;
            nextBossEnemyType = null;
            selection = 31810;
            maxBarrierNr = 2 ;
            selectionBarrier = 8;
        }
        else if(level == 47){
            maxNr = 6;}
        else if(level == 48){
            maxBarrierNr = 3;}
        else if(level == 49){
            maxNr = 7;}
        else if(level == Events.MAXIMUM_LEVEL)
        {
            wasEnemyCreationPaused = true;
            nextBossEnemyType = EnemyType.FINAL_BOSS;
            selection = 0;
            helicopter.startDecayOfAllCurrentBooster();
        }
    }
}
