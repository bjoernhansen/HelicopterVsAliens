package de.helicopter_vs_aliens;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import de.helicopter_vs_aliens.helicopter.Helicopter;
import de.helicopter_vs_aliens.enemy.Enemy;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class Explosion implements Constants, MissileTypes
{
    public int 
    	time,				// vergangene Zeit [frames] seit Starten der Explosion
    	max_time,			// maximale Dauer einer Explosion / eines EMP 
    	kills,				// nur für Pegasus-Klasse: mit diesem EMP vernichtete Gegner
    	earned_money;		// nur für Pegasus-Klasse: dabei (kills, s.o.) verdientes Geld
  
	public Ellipse2D 
		ellipse = new Ellipse2D.Float();	// Einflussbereich der Explosion
	
	Enemy
		source;
	
    private int
    	max_radius,	// maximaler Explosionsradius 
   		broadness,	// breite des animierten Explosionsringes		
    	type;		// Standard, Plasma, EMP, etc.
    
    private float[]
    	progress = new float[2];	// reguliert das Fortschreiten der Explosionsanimation       
    
    private Color 
    	color;	// Typ- und zeitabh�ngige (time, s.o.) Farbe der Explosion
    
    private Point2D 
		center = new Point2D.Float();			// Zentrum der Exploson
	     
    private Explosion(){}    
    
    Explosion(int x, int y)
    {
    	this.center.setLocation(x, y); 	
		this.time = 0;
		this.type = EMP;	
		this.max_time = 25;
		this.max_radius = 45;
		this.broadness = 36;	    
		this.source = null;
    }    
    
    static void paint_all(Graphics2D g2d,
                          ArrayList<LinkedList<Explosion>> explosion)
	{
    	for(Iterator<Explosion> i = explosion.get(ACTIVE).iterator(); i.hasNext();)
		{
			Explosion exp = i.next();
			exp.paint(g2d);
		}		
	}
    
	static void update_all(Helicopter helicopter,
						   ArrayList<LinkedList<Explosion>> explosion)
	{
    	for(Iterator<Explosion> i = explosion.get(ACTIVE).iterator(); i.hasNext();)
		{
			Explosion exp = i.next();
			exp.update();
			
			if(exp.time >= exp.max_time)
			{
				i.remove();
				if(exp.type == EMP)
				{
					helicopter.emp_wave = null;
					if(exp.kills > 1)
					{
						Events.extra_reward(exp.kills, exp.earned_money, 0.35f, 0.5f, 2.85f); // 0.5f, 0.5f, 3.0f
					}
				}
				explosion.get(INACTIVE).add(exp);
	        }
		}		
	}
    
	public void paint(Graphics2D g2d)
    {    	
    	if(this.type == STUNNING || this.type == EMP)
		{
			this.color = new Color((int) (255 * (1 - this.progress[0])), (int) (255 * (1 - this.progress[0] * this.progress[1])), 255, (int) (255 * (1 - this.progress[0] * this.progress[1])));
		}
    	else if(this.type == PLASMA)
		{
			// Plasma    		
    		this.color = new Color((int)(242 * (1-this.progress[0]*this.progress[1])), (int) (255 * (1-this.progress[0]*this.progress[0]) /*(int)(255 * (1-factor3*factor3))*/), (int)(1.0 * 255 * (1-this.progress[1])), (int)(255 * (1-this.progress[0]*this.progress[1])));
		}
    	else
    	{    		
    		this.color = new Color(255, (int)(255 * (1-this.progress[1]*this.progress[1])), (int)(255 * (1-this.progress[1])), (int)(255 * (1-this.progress[0]*this.progress[1])));
    	}    	
    	g2d.setPaint(this.color);
    	g2d.setStroke(new BasicStroke((int)(1+(this.broadness-1)*(1-this.progress[0]))));
        this.ellipse.setFrameFromCenter(this.center.getX(), this.center.getY(), this.center.getX() - (this.progress[1] * this.max_radius), this.center.getY() - (this.progress[1] * this.max_radius));
        g2d.draw(this.ellipse);
        g2d.setStroke(new BasicStroke(1));
    }
    
    public void update()
	{
    	this.time += 1;
		float t = (float)this.time/this.max_time;
		this.progress[0] = t;
		this.progress[1] = t * t * t - 3 * t * t + 3 * t;
		if(BgObject.background_moves)
		{			
			this.center.setLocation(this.center.getX() - BG_SPEED, 
									this.center.getY());	
		}		
		if(this.source != null)
		{
			this.center.setLocation((int)this.source.bounds.getCenterX(), 
									(int)this.source.bounds.getCenterY());
		}
	}
    
     
    public static void start(ArrayList<LinkedList<Explosion>> explosion, 
				      Helicopter helicopter, 
			          double x, double y, 
			          int missile_type, 
			          boolean extra_dmg)
    {
    	start(explosion, helicopter, x, y, missile_type, extra_dmg, null);
    }        
	public static void start(ArrayList<LinkedList<Explosion>> explosion, 
                      Helicopter helicopter, 
                      double x, double y, 
                      int missile_type, 
                      boolean extra_dmg,
                      Enemy source)
    {
    	Iterator<Explosion> i = explosion.get(INACTIVE).iterator();
		Explosion exp;
		if(i.hasNext()){exp = i.next(); i.remove();}	
		else{exp = new Explosion();}
		exp.center.setLocation(x, y);
		exp.time = 0;
		// kann wahrscheinlich in den EMP spezifischen bereich verschoben werden
		helicopter.becomes_center_of(exp);
		exp.type = missile_type;	
		if(source != null){exp.source = source;}	
		else exp.source = null;
		if(missile_type != EMP)
		{
			exp.max_time = 35;
			exp.max_radius = 65 + (missile_type == JUMBO  || missile_type == PHASE_SHIFT  ? 20 : 0) + (extra_dmg ? 20 : 0);
	    	exp.broadness =  50 + (missile_type == JUMBO  || missile_type == PHASE_SHIFT  ? 25 : 0) + (extra_dmg ? 25 : 0); 
		}		
		// EMP-Shockwave
		else
		{			
			if(Events.window == STARTSCREEN)
			{
				exp.max_time = 20;
				exp.max_radius = 50;
				exp.broadness = 36;	    	
			}
			else
			{
				exp.max_time = 20 + helicopter.level_of_upgrade[ENERGY_ABILITY];
				exp.max_radius = 75 + (int)((19+3f*helicopter.level_of_upgrade[ENERGY_ABILITY]) * helicopter.level_of_upgrade[ENERGY_ABILITY]);
				exp.broadness = 30 + 3 * (helicopter.level_of_upgrade[ENERGY_ABILITY]);	  
			}			  	
	    	helicopter.emp_wave = exp;
	    	exp.earned_money = 0;
	    	exp.kills = 0;
		}			
		explosion.get(ACTIVE).add(exp);
    }
}
