package de.helicopter_vs_aliens.model.powerup;

import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;


public class PowerUpController
{
    private final GameRessourceProvider gameRessourceProvider;
    
    public PowerUpController(GameRessourceProvider gameRessourceProvider) {
        this.gameRessourceProvider = gameRessourceProvider;
    }
    
    public static void activateInstance(GameRessourceProvider gameRessourceProvider, Enemy enemy)
    {
        PowerUpType powerUpType = enemy.getTypeOfRandomlyDroppedPowerUp();
        int powerUpDirection = getPowerUpDirection(gameRessourceProvider.getHelicopter(), enemy);
        PowerUp powerUp = getInstance(gameRessourceProvider, powerUpType);
        powerUp.initialize(enemy, powerUpDirection);
    }
    
    private static int getPowerUpDirection(Helicopter helicopter, Enemy enemy)
    {
        return helicopter.getX() > enemy.getX() || helicopter.canImmobilizePowerUp() ? -1 : 1;
    }
    
    public static PowerUp getInstance(GameRessourceProvider gameRessourceProvider, PowerUpType powerUpType)
    {
        PowerUp powerUp = gameRessourceProvider.getManageablePaintableService()
                                               .activateEntity(powerUpType);
        powerUp.setType(powerUpType);
        return powerUp;
    }
}
