package de.helicopter_vs_aliens;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import de.helicopter_vs_aliens.helicopter.Helicopter;
import de.helicopter_vs_aliens.enemy.Enemy;


public class EnemyMissile implements Constants, EnemyMissileTypes, MissileTypes
{      	
	public static final int 	
		DIAMETER = 10;		// Durchmesser der gegnerischen Geschosse
	
	private Point2D 
		location = new Point2D.Float(),
		speed    = new Point2D.Float(); // Geschwindigkeit der gegnerischen Geschosse
	
	private int
		rgb_color_value,	// aktueller Integer-Farbwert für die RGB-Rotkomponente der Geschoss-Farbe [0-255]
		diameter,			// Geschoss-Durchmesser
		type;				// Art des Geschoss
    
	private boolean 
		has_hit,			// = true: Hat den Helikoper getroffen und kann entsorgt werden
    	light_up_color; 	// = true: Farbe der gr�nen Geschosse wird heller, sonst dunkler
    
	private Color 	 
		variableGreen;  	// variable gr�ne Farbe der gegnerischen Geschosse  
    	
    		
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
		this.location.setLocation( this.location.getX() + this.speed.getX() - (BgObject.background_moves ? BG_SPEED : 0),
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
            this.rgb_color_value = Math.min(this.rgb_color_value + 25, 255);
        }
        else
        {
            this.rgb_color_value = Math.max(this.rgb_color_value - 25, 0); 
        }        
        if(this.type == DISCHARGER)
        {
        	this.variableGreen = new Color(this.rgb_color_value, 255, 0);
        }
        else
        {
        	this.variableGreen = new Color(255, this.rgb_color_value, (int)(0.65f * this.rgb_color_value));
        }  
        if(this.rgb_color_value == 0){this.light_up_color = true;}
        else if(this.rgb_color_value == 255){this.light_up_color = false;}
    }
    
    public void launch(Enemy enemy, int missile_type, double shooting_speed, Point2D shooting_direction)
    {
    	this.type = missile_type;   	
    	    	
    	if(enemy.model == BARRIER)
    	{
    		this.location.setLocation(enemy.bounds.getX() + (enemy.bounds.getWidth() -this.diameter)/2,
					  				  enemy.bounds.getY() + (enemy.bounds.getHeight()-this.diameter)/2);
    		this.speed.setLocation(	shooting_speed * shooting_direction.getX(), 
		 							shooting_speed * shooting_direction.getY());    	    	
    	}
    	else
    	{
    		this.speed.setLocation(	shooting_speed * (enemy.direction.x == -1 ? -1f : 1f), 0);  
    		    		
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
    
	

	static void 
	paint_all(Graphics2D g2d,
	                         ArrayList<LinkedList<EnemyMissile>> enemyMissile)
	{
		for(Iterator<EnemyMissile> i = enemyMissile.get(ACTIVE).iterator(); i.hasNext();)
		{
			EnemyMissile em = i.next();
			em.paint(g2d);
		}		
	}

	static void update_all(	ArrayList<LinkedList<EnemyMissile>> enemyMissile,
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