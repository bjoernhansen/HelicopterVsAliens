package de.helicopter_vs_aliens.model.missile;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import de.helicopter_vs_aliens.*;
import de.helicopter_vs_aliens.model.Explosion;
import de.helicopter_vs_aliens.model.background.BackgroundObject;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.enemy.Enemy;

import static de.helicopter_vs_aliens.model.enemy.EnemyModelTypes.*;
import static de.helicopter_vs_aliens.model.missile.EnemyMissileTypes.BUSTER;
import static de.helicopter_vs_aliens.model.missile.EnemyMissileTypes.DISCHARGER;


public class EnemyMissile implements Constants, MissileTypes
{      	
	public static final int 	
		DIAMETER = 10;		// Durchmesser der gegnerischen Geschosse
	
	private Point2D 
		location = new Point2D.Float(),
		speed    = new Point2D.Float(); // Geschwindigkeit der gegnerischen Geschosse
	
	private int
		rgbColorValue,	// aktueller Integer-Farbwert für die RGB-Rotkomponente der Geschoss-Farbe [0-255]
		diameter;			// Geschoss-Durchmesser
    
	private boolean 
		has_hit,			// = true: Hat den Helikoper getroffen und kann entsorgt werden
    	light_up_color; 	// = true: Farbe der grünen Geschosse wird heller, sonst dunkler
    
	private Color 	 
		variableGreen;  	// variable grüne Farbe der gegnerischen Geschosse
    	
    private EnemyMissileTypes
		type;				// Art des Geschoss
	
	
    private void paint(Graphics2D g2d)
    {
        g2d.setColor(this.variableGreen);
        g2d.fillOval((int)this.location.getX(), 
        			 (int)this.location.getY(), 
        			      this.diameter, 
        			      this.diameter);   
        g2d.setColor(this.type == BUSTER ? Color.orange : Color.white);
        g2d.drawOval((int)this.location.getX(),
        		     (int)this.location.getY(),
        		          this.diameter, 
        		          this.diameter);
    }
    
    private void update(Helicopter helicopter)
    {    		
    	this.determine_color();	
		this.location.setLocation( this.location.getX() + this.speed.getX() - (BackgroundObject.background_moves ? BG_SPEED : 0),
								   this.location.getY() + this.speed.getY() );	
		if(	helicopter.interphase_generator_timer <= helicopter.shift_time && 
		helicopter.bounds.intersectsLine( this.location.getX() + this.diameter/2, 
										  this.location.getY(), 
										  this.location.getX() + this.diameter/2, 
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
    		helicopter.take_missile_damage();    		
    		Explosion.start(Controller.getInstance().explosion,
    		helicopter, 
    		(int)(helicopter.bounds.getX() 
    				+ (helicopter.is_moving_left 
    					? Helicopter.FOCAL_PNT_X_LEFT 
    					: Helicopter.FOCAL_PNT_X_RIGHT)), 
    		(int)(helicopter.bounds.getY() + Helicopter.FOCAL_PNT_Y_EXP), 
    		STANDARD, 
    		false);
    	}
    	else 
    	{
    		Audio.play(Audio.explosion5);
    		helicopter.receive_static_charged(1.0f);    		
    	}
        this.has_hit = true;
    }
    
    private void determine_color()
    {
        if(this.light_up_color)
        {
            this.rgbColorValue = Math.min(this.rgbColorValue + 25, 255);
        }
        else
        {
            this.rgbColorValue = Math.max(this.rgbColorValue - 25, 0);
        }        
        if(this.type == DISCHARGER)
        {
        	this.variableGreen = new Color(this.rgbColorValue, 255, 0);
        }
        else
        {
        	this.variableGreen = new Color(255, this.rgbColorValue, (int)(0.65f * this.rgbColorValue));
        }  
        if(this.rgbColorValue == 0){this.light_up_color = true;}
        else if(this.rgbColorValue == 255){this.light_up_color = false;}
    }
    
    public void launch(Enemy enemy, EnemyMissileTypes missileType, double shootingSpeed, Point2D shootingDirection)
    {
    	this.type = missileType;
    	    	
    	if(enemy.model == BARRIER)
    	{
    		this.location.setLocation(enemy.bounds.getX() + (enemy.bounds.getWidth() -this.diameter)/2,
					  				  enemy.bounds.getY() + (enemy.bounds.getHeight()-this.diameter)/2);
    		this.speed.setLocation(	shootingSpeed * shootingDirection.getX(),
		 							shootingSpeed * shootingDirection.getY());
    	}
    	else
    	{
    		this.speed.setLocation(	shootingSpeed * (enemy.direction.x == -1 ? -1f : 1f), 0);
    		    		
    		if(enemy.model == TIT)
    		{
    			this.location.setLocation(enemy.bounds.getX() + (enemy.direction.x == -1 ? 0 : enemy.bounds.getWidth()),
    									  enemy.bounds.getY() );
    		}
	    	else if(enemy.model == CARGO)
	    	{
	    		this.location.setLocation(enemy.bounds.getX() + (enemy.direction.x == -1 ? 0 : enemy.bounds.getWidth()),
	    								  enemy.bounds.getY() + (enemy.bounds.getHeight()-this.diameter)/2);
	    	}
    	}    	
    	this.diameter = ((this.type == DISCHARGER) ? DIAMETER : (DIAMETER + 2));
		this.has_hit = false;
		this.light_up_color = true;
    }
    
	

	public static void
	paintAll(Graphics2D g2d,
			 ArrayList<LinkedList<EnemyMissile>> enemyMissile)
	{
		for(Iterator<EnemyMissile> i = enemyMissile.get(ACTIVE).iterator(); i.hasNext();)
		{
			EnemyMissile em = i.next();
			em.paint(g2d);
		}		
	}

	public static void updateAll(ArrayList<LinkedList<EnemyMissile>> enemyMissile,
								 Helicopter helicopter)
	{
		for(Iterator<EnemyMissile> i = enemyMissile.get(ACTIVE).iterator(); i.hasNext();)
		{
			EnemyMissile em = i.next();	    			
			em.update(helicopter);	
			if(    em.location.getX() + 80 < 0 
				|| em.location.getX() > 1050 
				|| em.location.getY() + 20 < 0 
				|| em.location.getY() > 515 
				|| em.has_hit)
			{
				i.remove();					
				enemyMissile.get(INACTIVE).add(em);
			}
		}		
	}
}    