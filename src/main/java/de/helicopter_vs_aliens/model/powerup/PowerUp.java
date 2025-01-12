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
        SIZE = 30,
		POWERUP_STOP_POSITION = 1004;
		
	private int
		direction,
        worth;		    // nur für PowerUps vom Typ BONUS_INCOME; bestimmt, wie viel Geld der Spieler für das Einsammeln erhält
    
    private boolean
        wasCollected,   // = true: PowerUp kann in die LinkedList für inaktive PowerUps verschoben werden
        hasStopped,	    // nur Helios-Klasse; = true: PowerUp fällt zu Boden
		isInStatusBar;	// = true: PowerUp befindet sich in der Statusbar
    
    private PowerUpType
		type;
	
	private final Point2D
		speed = new Point2D.Float();	// Geschwindigkeit des PowerUps
    
    private Color
        surfaceColor,	// Farben des PowerUps, hängen vom Typ ab
		crossColor;

		
	public static void updateAll(GameRessourceProvider gameRessourceProvider)
	{
		var paintableController = gameRessourceProvider.getActiveManageablePaintableController();
		paintableController.forEachActiveEntity(ManageablePaintableGroupType.POWER_UP, powerUp -> ((PowerUp)powerUp).update());
		paintableController.removeIf(ManageablePaintableGroupType.POWER_UP, powerUp -> ((PowerUp)powerUp).wasCollected);
	}

	private void update()
	{
		Helicopter helicopter = getGameRessourceProvider().getHelicopter();
		if(this.intersects(helicopter))
		{
			this.collect(helicopter);
		}
		if(!this.hasStopped
		   && helicopter.canImmobilizePowerUp()
		   && this.hasReachedStopPosition())
		{
			this.stop();
		}
		
		if(!this.isInStatusBar)
		{
			if(!this.hasStopped)
			{
				double newSpeedY = 0.20 * this.direction * this.speed.getX();
				this.speed.setLocation(0.25 * this.direction + this.speed.getX(), 
										helicopter.canImmobilizePowerUp()
											? Math.min(newSpeedY, 0.03*(this.getCenterY()-30))
											: newSpeedY);
			}	
			else if(isAboveGround())
			{
				this.speed.setLocation(this.speed.getX(), this.speed.getY() - 0.35);
			}
			else{this.speed.setLocation(0, 0);}
			
			// TODO unverständlich, in Methoden auslagern
			setBounds(
					this.getX()
						- this.speed.getX()
						- (Scenery.backgroundMoves ? SceneryObject.BG_SPEED : 0),
					Math.min(this.getY() - this.speed.getY(),
							GROUND_Y - this.getHeight()),
                    SIZE, SIZE);
			
			if(   (this.speed.getX() == 0 && this.getMaxX() < 0)
			    ||(this.speed.getX() != 0 && this.getMaxY() < 0))
			{
				this.setCollected();
			}
			this.setPaintBounds();
		}		
	}
	
	private boolean isAboveGround()
	{
		return this.getMaxY() < GROUND_Y;
	}
	
	private boolean hasReachedStopPosition()
	{
		return POWERUP_STOP_POSITION < (this.getX() - this.speed.getX() + 20);
	}
	
	public void initialize()
	{
		this.initialize(0, 0, 0, 0);
	}
	
	private void initialize(Enemy enemy, int powerUpDirection)
	{
		this.initialize( enemy.getX(),
					     enemy.getY(),
					     enemy.getBounty(),
					     powerUpDirection );
	}
	
	private void initialize(double x, double y, int powerUpWorth, int powerUpDirection)
	{
		setBounds(x, y, SIZE, SIZE);
		this.setPaintBounds(SIZE, SIZE);
		this.wasCollected = false;
		this.hasStopped = false;
		this.isInStatusBar = false;
		this.speed.setLocation(0, 0);
		this.direction = powerUpDirection;
        this.surfaceColor = this.type.getSurfaceColor();
        this.crossColor = this.type.getCrossColor();
		if(this.type == PowerUpType.BONUS_INCOME)
		{
            this.worth = powerUpWorth;
		}
	}
    
    public void setCollected()
    {
        this.wasCollected = true;
    }
	
	private void collect(Helicopter helicopter)
	{
		setCollected();
		if(!PowerUpType.getStatusBarPowerUpTypes().contains(this.type) || !helicopter.isBoosted(this.type))
		{
			Audio.play(Audio.powerAnnouncer[this.type.ordinal()]);
		}
		
		if(this.type == PowerUpType.TRIPLE_DAMAGE)
		{
			Audio.play(Audio.shieldUp);
			if(!Events.isCurrentLevelBossLevel()){
				Window.updateCollectedPowerUps(helicopter, this);}
		}
		else if(this.type == PowerUpType.INVINCIBLE)
		{
			Audio.play(Audio.teleport1);
			if(!Events.isCurrentLevelBossLevel()){
				Window.updateCollectedPowerUps(helicopter, this);}
		}
		else if(this.type == PowerUpType.UNLIMITED_ENERGY)
		{
			Audio.play(Audio.shieldUp);
			if(!Events.isCurrentLevelBossLevel())
			{
				helicopter.boostEnergy();
				Window.updateCollectedPowerUps(helicopter, this);
			}				
		}
		else if(this.type == PowerUpType.BOOSTED_FIRE_RATE)
		{
			Audio.play(Audio.shieldUp);
			if(!Events.isCurrentLevelBossLevel())
			{				
				helicopter.adjustFireRate(true);
				Window.updateCollectedPowerUps(helicopter, this);
			}				
		}
		else if(this.type == PowerUpType.REPARATION)
		{
			helicopter.useReparationPowerUp();
		}
		else if(this.type == PowerUpType.BONUS_INCOME)
		{
			Audio.play(Audio.cash);
			Events.lastExtraBonus = 0;
			Window.moneyDisplayTimer = Events.START;
			Events.lastBonus = helicopter.getBonusFactor() * this.worth;
			Events.money += Events.lastBonus;
			Events.overallEarnings += Events.lastBonus;
			Events.extraBonusCounter += Events.lastBonus;
		}
	}

	public void moveToStatusbar()
	{
		Window.collectedPowerUps.put(this.type, this);
		this.speed.setLocation(0, 0);
		this.isInStatusBar = true;
		this.wasCollected = false;
		setBounds(100, 432, Window.POWER_UP_SIZE, Window.POWER_UP_SIZE);
		this.setPaintBounds(Window.POWER_UP_SIZE, Window.POWER_UP_SIZE);
	}
	
	public static void activateInstance(GameRessourceProvider gameRessourceProvider, Enemy enemy)
	{
		PowerUpType powerUpType = enemy.getTypeOfRandomlyDroppedPowerUp();
		int powerUpDirection = PowerUp.getPowerUpDirection(gameRessourceProvider.getHelicopter(), enemy);
		PowerUp powerUp = PowerUp.getInstance(gameRessourceProvider, powerUpType);
		powerUp.initialize(enemy, powerUpDirection);
	}

	public static PowerUp getInstance(GameRessourceProvider gameRessourceProvider, PowerUpType powerUpType)
	{
		PowerUp powerUp = gameRessourceProvider.getActiveManageablePaintableController()
											   .activatePaintableEntity(powerUpType);
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
		this.hasStopped = true;
		this.speed.setLocation(0, 0);
	}
    
    public PowerUpType getType()
    {
        return type;
    }
    
    public void setOpaque()
    {
        this.surfaceColor = Colorations.setOpaque(this.surfaceColor);
        this.crossColor = Colorations.setOpaque(this.crossColor);
    }
    
    public void setAlpha(int alpha)
    {
        this.surfaceColor = Colorations.setAlpha(this.surfaceColor, alpha);
        this.crossColor = Colorations.setAlpha(this.crossColor, alpha);
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
}