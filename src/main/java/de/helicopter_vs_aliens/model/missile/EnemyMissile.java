package de.helicopter_vs_aliens.model.missile;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.entities.ManageablePaintable;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.PaintableEntity;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.ExplosionType;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.model.scenery.SceneryObject;

import java.awt.Color;
import java.awt.geom.Point2D;

import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.*;
import static de.helicopter_vs_aliens.model.missile.EnemyMissileType.BUSTER;
import static de.helicopter_vs_aliens.model.missile.EnemyMissileType.DISCHARGER;


public class EnemyMissile extends PaintableEntity implements ManageablePaintable
{      	
	public static final int
		BASE_DIAMETER = 10;		// Durchmesser der gegnerischen Geschosse
	private static final float
		STUNNING_MISSILE_ENERGY_CONSUMPTION_FACTOR = 1.0f;
	
	private final Point2D
		location = new Point2D.Float();
	
	private final Point2D
		speed    = new Point2D.Float(); // Geschwindigkeit der gegnerischen Geschosse
	
	private int
		rgbColorValue;
	
	private int
		diameter;		// Geschoss-Durchmesser
    
	private boolean
		hasHit;
	
	private boolean
		lightUpColor; 	// = true: Farbe der grünen Geschosse wird heller, sonst dunkler
    
	private Color
        variableColor;  // variable grüne Farbe der gegnerischen Geschosse
    	
    private EnemyMissileType
		type;				// Art des Geschosses
	
	
	EnemyMissile()
	{
	}
	
	private void update()
    {
		Helicopter helicopter = getGameRessourceProvider().getHelicopter();
    	determineColor();
		location.setLocation( location.getX() + speed.getX() - (Scenery.backgroundMoves ? SceneryObject.BG_SPEED : 0),
								   location.getY() + speed.getY() );
		if(	helicopter.canBeHit()
			&& helicopter.intersectsLine( 	location.getX() + diameter/2f,
											location.getY(),
										   	location.getX() + diameter/2f,
											location.getY() + diameter))
        {
			hit();
		}		
    }
    
    private void hit()
    {
		Helicopter helicopter = getHelicopter();
    	if(type == BUSTER)
    	{
    		Audio.play(Audio.explosion2);
    		helicopter.takeMissileDamage();
			getGameRessourceProvider().getExplosionController()
								 .start(
									 (int)(helicopter.getX()
										 + (helicopter.isMovingLeft
										 ? Helicopter.FOCAL_POINT_X_LEFT
										 : Helicopter.FOCAL_POINT_X_RIGHT)),
									 (int)(helicopter.getY() + Helicopter.FOCAL_POINT_Y_EXP),
									 ExplosionType.ORDINARY,
									 false);
    	}
    	else 
    	{
    		Audio.play(Audio.explosion5);
    		helicopter.receiveStaticCharge(STUNNING_MISSILE_ENERGY_CONSUMPTION_FACTOR);
    	}
        hasHit = true;
    }
    
    private void determineColor()
    {
        if(lightUpColor)
        {
            rgbColorValue = Math.min(rgbColorValue + 25, 255);
        }
        else
        {
            rgbColorValue = Math.max(rgbColorValue - 25, 0);
        }        
        if(type == DISCHARGER)
        {
        	variableColor = new Color(rgbColorValue, 255, 0);
        }
        else
        {
        	variableColor = new Color(255, rgbColorValue, (int)(0.65f * rgbColorValue));
        }  
        if(rgbColorValue == 0){
			lightUpColor = true;}
        else if(rgbColorValue == 255){
			lightUpColor = false;}
    }
    
    public void launch(Enemy enemy, EnemyMissileType missileType, double shootingSpeed, Point2D shootingDirection)
    {
    	type = missileType;
    	    	
    	if(enemy.getModel() == BARRIER)
    	{
    		location.setLocation(enemy.getX() + (enemy.getWidth() - diameter)/2,
					  				  enemy.getY() + (enemy.getHeight()- diameter)/2);
    		speed.setLocation(	shootingSpeed * shootingDirection.getX(),
		 							shootingSpeed * shootingDirection.getY());
    	}
    	else
    	{
    		speed.setLocation(	shootingSpeed * (enemy.isFlyingLeft() ? -1f : 1f), 0);
    		    		
    		if(enemy.getModel() == TIT)
    		{
    			location.setLocation(enemy.getX() + (enemy.isFlyingLeft() ? 0 : enemy.getWidth()), enemy.getY() );
    		}
	    	else if(enemy.getModel() == CARGO)
	    	{
	    		location.setLocation(enemy.getX() + (enemy.isFlyingLeft() ? 0 : enemy.getWidth()),
	    								  enemy.getY() + (enemy.getHeight()- diameter)/2);
	    	}
    	}    	
    	diameter = ((type == DISCHARGER) ? BASE_DIAMETER : (BASE_DIAMETER + 2));
		hasHit = false;
		lightUpColor = true;
    }
	
	// TODO auslagern in eigene Klasse und dann GameressourceProvider im Konstruktor übergeben
	public static void updateAll(GameRessourceProvider gameRessourceProvider)
	{
		var paintableController = gameRessourceProvider.getManageablePaintableController();
		paintableController.forEachActiveEntity(ManageablePaintableGroupType.ENEMY_MISSILE,
												enemyMissile -> ((EnemyMissile)enemyMissile).update());
		paintableController.removeIf(ManageablePaintableGroupType.ENEMY_MISSILE,
									 enemyMissile -> ((EnemyMissile)enemyMissile).isOutOfSight());
	}
	
	private boolean isOutOfSight()
	{
		return location.getX() + 80 < 0
			|| location.getX() > 1050
			|| location.getY() + 20 < 0
			|| location.getY() > 515
			|| hasHit;
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
	
	@Override
	public ManageablePaintableGroupType getGroupType()
	{
		return ManageablePaintableGroupType.ENEMY_MISSILE;
	}
}