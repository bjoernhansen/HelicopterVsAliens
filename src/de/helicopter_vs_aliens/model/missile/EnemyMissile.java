package de.helicopter_vs_aliens.model.missile;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.GameRessourceProvider;
import de.helicopter_vs_aliens.model.GameEntity;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.scenery.Scenery;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Iterator;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;
import static de.helicopter_vs_aliens.control.CollectionSubgroupType.INACTIVE;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.BARRIER;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.CARGO;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.TIT;
import static de.helicopter_vs_aliens.model.explosion.ExplosionType.ORDINARY;
import static de.helicopter_vs_aliens.model.missile.EnemyMissileType.BUSTER;
import static de.helicopter_vs_aliens.model.missile.EnemyMissileType.DISCHARGER;
import static de.helicopter_vs_aliens.model.scenery.SceneryObject.BG_SPEED;


public class EnemyMissile extends GameEntity
{      	
	public static final int 	
		DIAMETER = 10;		// Durchmesser der gegnerischen Geschosse
	
	private final Point2D
		location = new Point2D.Float(),
		speed    = new Point2D.Float(); // Geschwindigkeit der gegnerischen Geschosse
	
	private int
		rgbColorValue,	// aktueller Integer-Farbwert für die RGB-Rotkomponente der Geschoss-Farbe [0-255]
		diameter;		// Geschoss-Durchmesser
    
	private boolean
		hasHit,			// = true: Hat den Helikopter getroffen und kann entsorgt werden
    	lightUpColor; 	// = true: Farbe der grünen Geschosse wird heller, sonst dunkler
    
	private Color
        variableColor;  // variable grüne Farbe der gegnerischen Geschosse
    	
    private EnemyMissileType
		type;				// Art des Geschosses
	
    
    private void update(Helicopter helicopter)
    {    		
    	this.determineColor();
		this.location.setLocation( this.location.getX() + this.speed.getX() - (Scenery.backgroundMoves ? BG_SPEED : 0),
								   this.location.getY() + this.speed.getY() );	
		if(	helicopter.canBeHit()
			&& helicopter.intersectsLine( 	this.location.getX() + this.diameter/2f,
											this.location.getY(),
										   	this.location.getX() + this.diameter/2f,
											this.location.getY() + this.diameter))
        {
			this.hit(helicopter);
		}		
    }
    
    private void hit(Helicopter helicopter)
    {    	
    	if(this.type == BUSTER)
    	{
    		Audio.play(Audio.explosion2);
    		helicopter.takeMissileDamage();
    		Explosion.start(Controller.getInstance().getExplosions(),
    						helicopter,
							(int)(helicopter.getX()
									+ (helicopter.isMovingLeft
										? Helicopter.FOCAL_PNT_X_LEFT
										: Helicopter.FOCAL_PNT_X_RIGHT)),
							(int)(helicopter.getY() + Helicopter.FOCAL_PNT_Y_EXP),
                    ORDINARY,
							false);
    	}
    	else 
    	{
    		Audio.play(Audio.explosion5);
    		helicopter.receiveStaticCharge(1.0f);
    	}
        this.hasHit = true;
    }
    
    private void determineColor()
    {
        if(this.lightUpColor)
        {
            this.rgbColorValue = Math.min(this.rgbColorValue + 25, 255);
        }
        else
        {
            this.rgbColorValue = Math.max(this.rgbColorValue - 25, 0);
        }        
        if(this.type == DISCHARGER)
        {
        	this.variableColor = new Color(this.rgbColorValue, 255, 0);
        }
        else
        {
        	this.variableColor = new Color(255, this.rgbColorValue, (int)(0.65f * this.rgbColorValue));
        }  
        if(this.rgbColorValue == 0){this.lightUpColor = true;}
        else if(this.rgbColorValue == 255){this.lightUpColor = false;}
    }
    
    public void launch(Enemy enemy, EnemyMissileType missileType, double shootingSpeed, Point2D shootingDirection)
    {
    	this.type = missileType;
    	    	
    	if(enemy.getModel() == BARRIER)
    	{
    		this.location.setLocation(enemy.getX() + (enemy.getWidth() -this.diameter)/2,
					  				  enemy.getY() + (enemy.getHeight()-this.diameter)/2);
    		this.speed.setLocation(	shootingSpeed * shootingDirection.getX(),
		 							shootingSpeed * shootingDirection.getY());
    	}
    	else
    	{
    		this.speed.setLocation(	shootingSpeed * (enemy.isFlyingLeft() ? -1f : 1f), 0);
    		    		
    		if(enemy.getModel() == TIT)
    		{
    			this.location.setLocation(enemy.getX() + (enemy.isFlyingLeft() ? 0 : enemy.getWidth()),
    									  enemy.getY() );
    		}
	    	else if(enemy.getModel() == CARGO)
	    	{
	    		this.location.setLocation(enemy.getX() + (enemy.isFlyingLeft() ? 0 : enemy.getWidth()),
	    								  enemy.getY() + (enemy.getHeight()-this.diameter)/2);
	    	}
    	}    	
    	this.diameter = ((this.type == DISCHARGER) ? DIAMETER : (DIAMETER + 2));
		this.hasHit = false;
		this.lightUpColor = true;
    }
	
	public static void updateAll(GameRessourceProvider gameRessourceProvider)
	{
		for(Iterator<EnemyMissile> i = gameRessourceProvider.getEnemyMissiles().get(ACTIVE).iterator(); i.hasNext();)
		{
			EnemyMissile em = i.next();	    			
			em.update(gameRessourceProvider.getHelicopter());
			if(    em.location.getX() + 80 < 0 
				|| em.location.getX() > 1050 
				|| em.location.getY() + 20 < 0 
				|| em.location.getY() > 515 
				|| em.hasHit)
			{
				i.remove();					
				gameRessourceProvider.getEnemyMissiles().get(INACTIVE).add(em);
			}
		}		
	}
	
	public Point2D getLocation()
	{
		return location;
	}
	
	public int getDiameter()
	{
		return diameter;
	}
	
	public Color getVariableColor()
	{
		return variableColor;
	}
	
	public EnemyMissileType getType()
	{
		return type;
	}
}