package de.helicopter_vs_aliens.model.powerup;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.RectangularGameEntity;
import de.helicopter_vs_aliens.model.background.BackgroundObject;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.Colorations;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;
import static de.helicopter_vs_aliens.model.background.BackgroundObject.BG_SPEED;
import static de.helicopter_vs_aliens.model.powerup.PowerUpType.*;


public class PowerUp extends RectangularGameEntity
{
    private static final int
        SIZE = 30,
		POWERUP_STOP_POSITION = 1004;
		
	private static final double
		POWER_UP_WORTH_MULTIPLIER = 7.5;
		
	private int
		direction,
        worth;		    // nur für PowerUps vom Typ BONUS_INCOME; bestimmt, wie viel Geld der Spieler für das Einsammeln erhält
    
    private boolean
        wasCollected,   // = true: PowerUp kann in die LinkedList für inaktive PowerUps verschoben werden
        hasStopped,	    // nur Helios-Klasse; = true: PowerUp fällt zu Boden
		isInStatusBar;	// = true: PowerUp befindet sich in der Statusbar
    
    private PowerUpType
		type;
	
	private Point2D 
		speed = new Point2D.Float();	// Geschwindigkeit des PowerUps
    
    private Color
        surfaceColor,	// Farben des PowerUps, hängen vom Typ ab
		crossColor;

		
	public static void updateAll(EnumMap<CollectionSubgroupType, LinkedList<PowerUp>> powerUp, Helicopter helicopter)
	{
    	for(Iterator<PowerUp> i = powerUp.get(ACTIVE).iterator(); i.hasNext();)
		{
			PowerUp pu = i.next();
			pu.update(helicopter);			
			if(pu.wasCollected)
			{
				i.remove();
				Controller.getInstance().getGameEntityRecycler().store(pu);
				// powerUp.get(INACTIVE).add(pu);
			}
		}		
	}

	private void update(Helicopter helicopter)
	{		
		if(this.bounds.intersects(helicopter.bounds)){this.collect(helicopter);}		
		
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
				double new_y_speed = 0.20 * this.direction * this.speed.getX();
				this.speed.setLocation(0.25 * this.direction + this.speed.getX(), 
										helicopter.canImmobilizePowerUp()
											? Math.min(new_y_speed, 0.03*(this.bounds.getCenterY()-30))
											: new_y_speed);
			}	
			else if(this.bounds.getMaxY() < GROUND_Y )
			{
				this.speed.setLocation(this.speed.getX(), this.speed.getY() - 0.35);
			}
			else{this.speed.setLocation(0, 0);}
			
			this.bounds.setRect(
					this.bounds.getX() 
						- this.speed.getX()
						- (BackgroundObject.backgroundMoves ? BG_SPEED : 0),
					Math.min(this.bounds.getY() - this.speed.getY(), 
							GROUND_Y - this.bounds.getHeight()),
                    SIZE, SIZE);
			
			if(   (this.speed.getX() == 0 && this.bounds.getMaxX() < 0) 
			    ||(this.speed.getX() != 0 && this.bounds.getMaxY() < 0))
			{
				this.collect();
			}
			this.setPaintBounds();
		}		
	}
	
	private boolean hasReachedStopPosition()
	{
		return POWERUP_STOP_POSITION < (this.bounds.getX() - this.speed.getX() + 20);
	}
	
	private void make(double x, double y, PowerUpType powerUpType, int powerUpWorth, int powerUpDirection)
	{
		this.bounds.setRect(x, y, SIZE, SIZE);
		this.setPaintBounds(SIZE, SIZE);
		this.wasCollected = false;
		this.hasStopped = false;
		this.isInStatusBar = false;
		this.speed.setLocation(0, 0);
		this.direction = powerUpDirection;
		this.type = powerUpType;
        this.surfaceColor = this.type.getSurfaceColor();
        this.crossColor = this.type.getCrossColor();
		if(this.type == BONUS_INCOME)
		{
            this.worth = powerUpWorth;
		}
	}
    
    public void collect()
    {
        this.wasCollected = true;
    }
	
	private void collect(Helicopter helicopter)
	{
		collect();
		if(this.type.ordinal() > 3 || helicopter.powerUpTimer[this.type.ordinal()] ==  0)
		{
			Audio.play(Audio.powerAnnouncer[this.type.ordinal()]);
		}
		
		if(this.type == TRIPLE_DAMAGE)
		{
			Audio.play(Audio.shieldUp);
			if(!Events.isBossLevel()){
				Menu.updateCollectedPowerUps(helicopter, this);}
		}
		else if(this.type == INVINCIBLE)
		{
			Audio.play(Audio.teleport1);
			if(!Events.isBossLevel()){Menu.updateCollectedPowerUps(helicopter, this);}
		}
		else if(this.type == UNLIMITRED_ENERGY)
		{
			Audio.play(Audio.shieldUp);
			if(!Events.isBossLevel())
			{
				helicopter.boostEnergy();
				Menu.updateCollectedPowerUps(helicopter, this);
			}				
		}
		else if(this.type == BOOSTED_FIRE_RATE)
		{
			Audio.play(Audio.shieldUp);
			if(!Events.isBossLevel())
			{				
				helicopter.adjustFireRate(true);
				Menu.updateCollectedPowerUps(helicopter, this);
			}				
		}
		else if(this.type == REPARATION)
		{
			helicopter.useReparationPowerUp();
		}
		else if(this.type == BONUS_INCOME)
		{
			Audio.play(Audio.cash);
			Events.lastExtraBonus = 0;
			Menu.moneyDisplayTimer = Events.START;
			Events.lastBonus = helicopter.getBonusFactor() * this.worth;
			Events.money += Events.lastBonus;
			Events.overallEarnings += Events.lastBonus;
			Events.extraBonusCounter += Events.lastBonus;
		}
	}

	public void moveToStatusbar()
	{
		Menu.collectedPowerUp[this.type.ordinal()] = this;
		this.speed.setLocation(0, 0);
		this.isInStatusBar = true;
		this.wasCollected = false;
		this.bounds.setRect(100, 432, Menu.POWER_UP_SIZE, Menu.POWER_UP_SIZE);
		this.setPaintBounds(Menu.POWER_UP_SIZE, Menu.POWER_UP_SIZE);
	}

	public static void activate(Helicopter helicopter, EnumMap<CollectionSubgroupType, LinkedList<PowerUp>> powerUp, Enemy enemy,
								PowerUpType type, boolean isIntendedForStatusBar)
	{
		PowerUp pu = Controller.getInstance().getGameEntityRecycler().retrieve(PowerUp.class);
		
		/*
		Iterator<PowerUp> i = powerUp.get(INACTIVE).iterator();
		if(i.hasNext()){pu = i.next(); i.remove();}	
		else{pu = new PowerUp();}*/
		
		if(enemy != null)
		{
			pu.make(enemy.bounds.getX(), 
					enemy.bounds.getY(), 
					type,
					(int)(POWER_UP_WORTH_MULTIPLIER * enemy.type.getStrength()),
					helicopter.bounds.getX() > enemy.bounds.getX() 
					|| helicopter.canImmobilizePowerUp() ? -1 : 1 );
		}
		else
		{
		    pu.make(0, 0, type, 0, 0);
		}
		powerUp.get(ACTIVE).add(pu);
		if(isIntendedForStatusBar){pu.moveToStatusbar();}
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
	
	public boolean isInStatusBar()
	{
		return isInStatusBar;
	}
}