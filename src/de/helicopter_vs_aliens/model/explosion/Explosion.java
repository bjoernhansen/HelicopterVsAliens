package de.helicopter_vs_aliens.model.explosion;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;

import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.GameEntity;
import de.helicopter_vs_aliens.model.background.BackgroundObject;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.enemy.Enemy;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;
import static de.helicopter_vs_aliens.control.CollectionSubgroupType.INACTIVE;
import static de.helicopter_vs_aliens.gui.Menu.*;
import static de.helicopter_vs_aliens.gui.WindowType.STARTSCREEN;
import static de.helicopter_vs_aliens.model.background.BackgroundObject.BG_SPEED;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.*;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.ENERGY_ABILITY;

public class Explosion extends GameEntity
{
    public int 
    	time,				// vergangene Zeit [frames] seit Starten der Explosion
		maxTime,			// maximale Dauer einer Explosion / eines EMP
    	kills,				// nur für Pegasus-Klasse: mit diesem EMP vernichtete Gegner
    	earnedMoney;		// nur für Pegasus-Klasse: dabei (kills, s.o.) verdientes Geld
  
	public Ellipse2D 
		ellipse = new Ellipse2D.Float();	// Einflussbereich der Explosion
	
	private Enemy
		source;
	
    private int
		maxRadius,	// maximaler Explosionsradius
   		broadness;	// breite des animierten Explosionsringes

    
    private float[]
    	progress = new float[2];	// reguliert das Fortschreiten der Explosionsanimation       
    
    private Color 
    	color;	// Typ- und zeitabhängige (time, s.o.) Farbe der Explosion
    
    private Point2D 
		center = new Point2D.Float();			// Zentrum der Exploson

	private ExplosionTypes
		type;		// Standard, Plasma, EMP, etc.


    private Explosion(){}    
    
    private Explosion(int x, int y)
    {
    	this.center.setLocation(x, y); 	
		this.time = 0;
		this.type = EMP;	
		this.maxTime = 25;
		this.maxRadius = 45;
		this.broadness = 36;	    
		this.source = null;
    }    
    
    public static void paintAll(Graphics2D g2d,
								EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion)
	{
		for(Explosion exp : explosion.get(ACTIVE))
		{
			exp.paint(g2d);
		}		
	}
    
	public static void updateAll(Helicopter helicopter,
								 EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion)
	{
    	for(Iterator<Explosion> i = explosion.get(ACTIVE).iterator(); i.hasNext();)
		{
			Explosion exp = i.next();
			exp.update();
			
			if(exp.time >= exp.maxTime)
			{
				i.remove();
				if(exp.type == EMP)
				{
					helicopter.empWave = null;
					if(exp.kills > 1)
					{
						Events.extraReward(exp.kills, exp.earnedMoney, 0.35f, 0.5f, 2.85f); // 0.5f, 0.5f, 3.0f
					}
				}
				explosion.get(INACTIVE).add(exp);
	        }
		}		
	}

    public static Explosion createStartscreenExplosion(int i)
    {
		return new Explosion(
				149
				+ START_SCREEN_OFFSET_X
				+ i * HELICOPTER_DISTANCE,
				310
				+ START_SCREEN_HELICOPTER_OFFSET_Y);
    }

    @Override
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
        this.ellipse.setFrameFromCenter(this.center.getX(), this.center.getY(), this.center.getX() - (this.progress[1] * this.maxRadius), this.center.getY() - (this.progress[1] * this.maxRadius));
        g2d.draw(this.ellipse);
        g2d.setStroke(new BasicStroke(1));
    }
    
    public void update()
	{
    	this.time += 1;
		float t = (float)this.time/this.maxTime;
		this.progress[0] = t;
		this.progress[1] = t * t * t - 3 * t * t + 3 * t;
		if(BackgroundObject.backgroundMoves)
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
    
     
    public static void start(EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion,
                             Helicopter helicopter,
                             double x, double y,
                             ExplosionTypes explosionType,
                             boolean extraDamage)
    {
    	start(explosion, helicopter, x, y, explosionType, extraDamage, null);
    }        
	public static void start(EnumMap<CollectionSubgroupType, LinkedList<Explosion>> explosion,
							 Helicopter helicopter,
							 double x, double y,
							 ExplosionTypes explosionType,
							 boolean extraDamage,
							 Enemy source)
    {
    	Iterator<Explosion> i = explosion.get(INACTIVE).iterator();
		Explosion exp;
		if(i.hasNext()){exp = i.next(); i.remove();}	
		else{exp = new Explosion();}
		exp.center.setLocation(x, y);
		exp.time = 0;
		// kann wahrscheinlich in den EMP spezifischen bereich verschoben werden
		helicopter.becomesCenterOf(exp);
		exp.type = explosionType;
		if(source != null){exp.source = source;}	
		else exp.source = null;
		if(explosionType != EMP)
		{
			exp.maxTime = 35;
			exp.maxRadius = 65 + (explosionType == JUMBO  || explosionType == PHASE_SHIFT  ? 20 : 0) + (extraDamage ? 20 : 0);
	    	exp.broadness =  50 + (explosionType == JUMBO  || explosionType == PHASE_SHIFT  ? 25 : 0) + (extraDamage ? 25 : 0);
		}		
		// EMP-Shockwave
		else
		{			
			if(window == STARTSCREEN)
			{
				exp.maxTime = 20;
				exp.maxRadius = 50;
				exp.broadness = 36;	    	
			}
			else
			{
				int level = helicopter.getUpgradeLevelOf(ENERGY_ABILITY);
				exp.maxTime = 20 + level;
				exp.maxRadius = 75 + (int)(19 + 3f * level * level);
				exp.broadness = 30 + 3 * (level);
			}			  	
	    	helicopter.empWave = exp;
	    	exp.earnedMoney = 0;
	    	exp.kills = 0;
		}			
		explosion.get(ACTIVE).add(exp);
    }
}
