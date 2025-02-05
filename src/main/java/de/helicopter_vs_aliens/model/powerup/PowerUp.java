package de.helicopter_vs_aliens.model.powerup;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.entities.ManageablePaintable;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.model.RectangularPaintableEntity;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.model.scenery.SceneryObject;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.Color;
import java.awt.geom.Point2D;


public class PowerUp extends RectangularPaintableEntity implements ManageablePaintable
{
    private static final int
        SIZE = 30;
	
	private static final int
		POWERUP_STOP_POSITION = 1004;
		
	private int
		direction;
	
	private int
		worth;		    // nur für PowerUps vom Typ BONUS_INCOME; bestimmt, wie viel Geld der Spieler für das Einsammeln erhält
    
    private boolean
        wasCollected;
	
	private boolean
		isFlying;
	
	private boolean
		isOutsideOfStatusBar;	// = true: PowerUp befindet sich in der Statusbar
    
    private PowerUpType
		type;
	
	private final Point2D
		speed = new Point2D.Float();	// Geschwindigkeit des PowerUps
    
    private Color
        surfaceColor;
	
	private Color
		crossColor;

		
	public static void updateAll(GameRessourceProvider gameRessourceProvider)
	{
		var paintableController = gameRessourceProvider.getManageablePaintableService();
		paintableController.forEachActiveEntity(ManageablePaintableGroupType.POWER_UP, PowerUp::update);
		paintableController.removeIf(ManageablePaintableGroupType.POWER_UP, PowerUp::wasCollected);
	}

	private void update()
	{
		Helicopter helicopter = getGameRessourceProvider().getHelicopter();
		if(intersects(helicopter))
		{
			collect(helicopter);
		}
		if(isFlying
		   && helicopter.canImmobilizePowerUp()
		   && hasReachedStopPosition())
		{
			stop();
		}
		
		if(isOutsideOfStatusBar)
		{
			if(isFlying)
			{
				double newSpeedY = 0.20 * direction * speed.getX();
				speed.setLocation(0.25 * direction + speed.getX(),
										helicopter.canImmobilizePowerUp()
											? Math.min(newSpeedY, 0.03*(getCenterY()-30))
											: newSpeedY);
			}	
			else if(isAboveGround())
			{
				speed.setLocation(speed.getX(), speed.getY() - 0.35);
			}
			else{
				speed.setLocation(0, 0);}
			
			// TODO unverständlich, in Methoden auslagern
			setBounds(
					getX()
						- speed.getX()
						- (Scenery.isBackgroundMoving ? SceneryObject.BG_SPEED : 0),
					Math.min(getY() - speed.getY(),
							 GROUND_Y - getHeight()),
                    SIZE, SIZE);
			
			if(   (speed.getX() == 0 && getMaxX() < 0)
			    ||(speed.getX() != 0 && getMaxY() < 0))
			{
				setCollected();
			}
			setPaintBounds();
		}		
	}
	
	private boolean isAboveGround()
	{
		return getMaxY() < GROUND_Y;
	}
	
	private boolean hasReachedStopPosition()
	{
		return POWERUP_STOP_POSITION < (getX() - speed.getX() + 20);
	}
	
	public void initialize()
	{
		initialize(0, 0, 0, 0);
	}
	
	private void initialize(Enemy enemy, int powerUpDirection)
	{
		initialize( enemy.getX(),
					     enemy.getY(),
					     enemy.getBounty(),
					     powerUpDirection );
	}
	
	private void initialize(double x, double y, int powerUpWorth, int powerUpDirection)
	{
		setBounds(x, y, SIZE, SIZE);
		setPaintBounds(SIZE, SIZE);
		wasCollected = false;
		isFlying = true;
		isOutsideOfStatusBar = true;
		speed.setLocation(0, 0);
		direction = powerUpDirection;
        surfaceColor = type.getSurfaceColor();
        crossColor = type.getCrossColor();
		if(type == PowerUpType.BONUS_INCOME)
		{
            worth = powerUpWorth;
		}
	}
    
    public void setCollected()
    {
        wasCollected = true;
    }
	
	private void collect(Helicopter helicopter)
	{
		setCollected();
		if(!PowerUpType.getStatusBarPowerUpTypes().contains(type) || !helicopter.isBoosted(type))
		{
			Audio.play(Audio.powerAnnouncer[type.ordinal()]);
		}
		
		if(type == PowerUpType.TRIPLE_DAMAGE)
		{
			Audio.play(Audio.shieldUp);
			if(!Events.isCurrentLevelBossLevel()){
				Window.updateCollectedPowerUps(helicopter, this);}
		}
		else if(type == PowerUpType.INVINCIBLE)
		{
			Audio.play(Audio.teleport1);
			if(!Events.isCurrentLevelBossLevel()){
				Window.updateCollectedPowerUps(helicopter, this);}
		}
		else if(type == PowerUpType.UNLIMITED_ENERGY)
		{
			Audio.play(Audio.shieldUp);
			if(!Events.isCurrentLevelBossLevel())
			{
				helicopter.boostEnergy();
				Window.updateCollectedPowerUps(helicopter, this);
			}				
		}
		else if(type == PowerUpType.BOOSTED_FIRE_RATE)
		{
			Audio.play(Audio.shieldUp);
			if(!Events.isCurrentLevelBossLevel())
			{				
				helicopter.adjustFireRate(true);
				Window.updateCollectedPowerUps(helicopter, this);
			}				
		}
		else if(type == PowerUpType.REPARATION)
		{
			helicopter.useReparationPowerUp();
		}
		else if(type == PowerUpType.BONUS_INCOME)
		{
			Audio.play(Audio.cash);
			Events.lastExtraBonus = 0;
			Window.moneyDisplayTimer = Events.START;
			Events.lastBonus = helicopter.getBonusFactor() * worth;
			Events.money += Events.lastBonus;
			Events.overallEarnings += Events.lastBonus;
			Events.extraBonusCounter += Events.lastBonus;
		}
	}

	public void moveToStatusbar()
	{
		Window.collectedPowerUps.put(type, this);
		speed.setLocation(0, 0);
		isOutsideOfStatusBar = false;
		wasCollected = false;
		setBounds(100, 432, Window.POWER_UP_SIZE, Window.POWER_UP_SIZE);
		setPaintBounds(Window.POWER_UP_SIZE, Window.POWER_UP_SIZE);
	}
	
	public static void activateInstance(GameRessourceProvider gameRessourceProvider, Enemy enemy)
	{
		PowerUpType powerUpType = enemy.getTypeOfRandomlyDroppedPowerUp();
		int powerUpDirection = getPowerUpDirection(gameRessourceProvider.getHelicopter(), enemy);
		PowerUp powerUp = getInstance(gameRessourceProvider, powerUpType);
		powerUp.initialize(enemy, powerUpDirection);
	}

	public static PowerUp getInstance(GameRessourceProvider gameRessourceProvider, PowerUpType powerUpType)
	{
		PowerUp powerUp = gameRessourceProvider.getManageablePaintableService()
											   .activateEntity(powerUpType);
		powerUp.setType(powerUpType);
		return powerUp;
	}
	
	public void setType(PowerUpType type)
	{
		this.type = type;
	}
	
	private static int getPowerUpDirection(Helicopter helicopter, Enemy enemy)
	{
		return helicopter.getX() > enemy.getX() || helicopter.canImmobilizePowerUp() ? -1 : 1;
	}
	
	private void stop()
	{
		isFlying = false;
		speed.setLocation(0, 0);
	}
    
    public PowerUpType getType()
    {
        return type;
    }
    
    public void setOpaque()
    {
        surfaceColor = Colorations.setOpaque(surfaceColor);
        crossColor = Colorations.setOpaque(crossColor);
    }
    
    public void setAlpha(int alpha)
    {
        surfaceColor = Colorations.setAlpha(surfaceColor, alpha);
        crossColor = Colorations.setAlpha(crossColor, alpha);
    }
    
    public Color getSurfaceColor()
    {
        return surfaceColor;
    }
    
    public Color getCrossColor()
    {
        return crossColor;
    }
	
	@Override
	public ManageablePaintableGroupType getGroupType()
	{
		return ManageablePaintableGroupType.POWER_UP;
	}
	
	public boolean wasCollected()
	{
		return wasCollected;
	}
}