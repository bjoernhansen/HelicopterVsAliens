package de.helicopter_vs_aliens.model.explosion;

import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;
import de.helicopter_vs_aliens.control.entities.GameEntityGroupType;
import de.helicopter_vs_aliens.control.entities.GroupTypeOwner;
import de.helicopter_vs_aliens.gui.window.WindowManager;
import de.helicopter_vs_aliens.model.GameEntity;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.Pegasus;
import de.helicopter_vs_aliens.model.scenery.Scenery;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

import static de.helicopter_vs_aliens.gui.WindowType.START_SCREEN;
import static de.helicopter_vs_aliens.gui.window.Window.HELICOPTER_DISTANCE;
import static de.helicopter_vs_aliens.gui.window.Window.START_SCREEN_HELICOPTER_OFFSET_Y;
import static de.helicopter_vs_aliens.gui.window.Window.START_SCREEN_OFFSET_X;
import static de.helicopter_vs_aliens.model.scenery.SceneryObject.BG_SPEED;

public class Explosion extends GameEntity implements GroupTypeOwner
{
    public int 
    	time,				// vergangene Zeit [frames] seit Starten der Explosion
		maxTime,			// maximale Dauer einer Explosion / eines EMP
		// TODO was nur für die Pegasus-Klasse relevant ist, sollte nicht hier sein, eventuell EMP-Klasse, die von Explosion erbt
    	kills,				// nur für Pegasus-Klasse: mit diesem EMP vernichtete Gegner
    	earnedMoney;		// nur für Pegasus-Klasse: dabei (kills, s.o.) verdientes Geld
  
	public final Ellipse2D
		ellipse = new Ellipse2D.Float();	// Einflussbereich der Explosion
	
	private Enemy
		source;
	
    private int
		maxRadius,	// maximaler Explosionsradius
   		broadness;	// breite des animierten Explosionsringes
	
    private final float[]
    	progress = new float[2];	// reguliert das Fortschreiten der Explosionsanimation       
    
    private Color 
    	color;	// Typ- und zeitabhängige (time, s.o.) Farbe der Explosion
    
    private final Point2D
		center = new Point2D.Float();			// Zentrum der Explosion

	private ExplosionType
		type;		// Standard, Plasma, EMP, etc.


    private Explosion(){}    
    
    private Explosion(int x, int y)
    {
    	this.center.setLocation(x, y); 	
		this.time = 0;
		this.type = ExplosionType.EMP;
		this.maxTime = 25;
		this.maxRadius = 45;
		this.broadness = 36;	    
		this.source = null;
    }    
	   
	public static void updateAll(GameRessourceProvider gameRessourceProvider)
	{
    	for(Iterator<Explosion> i = gameRessourceProvider.getActiveGameEntityManager()
														 .getExplosions().get(CollectionSubgroupType.ACTIVE).iterator(); i.hasNext();)
		{
			Explosion exp = i.next();
			exp.update();
			
			if(exp.time >= exp.maxTime)
			{
				i.remove();
				// TODO irgendwie auslagern in Pegasus und ggf. auch Methode in Explosion
				if(exp.type == ExplosionType.EMP)
				{
					((Pegasus)gameRessourceProvider.getHelicopter()).empWave = null;
					if(exp.kills > 1)
					{
						Events.extraReward(exp.kills, exp.earnedMoney, 0.35f, 0.5f, 2.85f); // 0.5f, 0.5f, 3.0f
					}
				}
				gameRessourceProvider.getActiveGameEntityManager()
									 .getExplosions().get(CollectionSubgroupType.INACTIVE).add(exp);
	        }
		}		
	}

    public static Explosion createStartScreenExplosion(int i)
    {
		return new Explosion(
				149
				+ START_SCREEN_OFFSET_X
				+ i * HELICOPTER_DISTANCE,
				310
				+ START_SCREEN_HELICOPTER_OFFSET_Y);
    }
    
    public void update()
	{
		updateProgress();
		updatePosition();
		updateColor();
	}
	
	private void updateProgress()
	{
		this.time += 1;
		float t = (float)this.time/this.maxTime;
		this.progress[0] = t;
		this.progress[1] = t * t * t - 3 * t * t + 3 * t;
	}
	
	private void updatePosition()
	{
		if(Scenery.backgroundMoves)
		{
			this.center.setLocation(this.center.getX() - BG_SPEED,
				this.center.getY());
		}
		if(this.source != null)
		{
			this.center.setLocation((int)this.source.getCenterX(),
				(int)this.source.getCenterY());
		}
	}
	
	private void updateColor()
	{
		if(this.type == ExplosionType.STUNNING || this.type == ExplosionType.EMP)
		{
			this.color = new Color((int) (255 * (1 - this.progress[0])), (int) (255 * (1 - this.progress[0] * this.progress[1])), 255, (int) (255 * (1 - this.progress[0] * this.progress[1])));
		}
		else if(this.type == ExplosionType.PLASMA)
		{
			this.color = new Color((int)(242 * (1-this.progress[0]*this.progress[1])), (int) (255 * (1-this.progress[0]*this.progress[0]) /*(int)(255 * (1-factor3*factor3))*/), (int)(1.0 * 255 * (1-this.progress[1])), (int)(255 * (1-this.progress[0]*this.progress[1])));
		}
		else
		{
			this.color = new Color(255, (int)(255 * (1-this.progress[1]*this.progress[1])), (int)(255 * (1-this.progress[1])), (int)(255 * (1-this.progress[0]*this.progress[1])));
		}
	}
	
	public static void start(Map<CollectionSubgroupType, Queue<Explosion>> explosions,
							 Helicopter helicopter,
							 double x, double y,
							 ExplosionType explosionType,
							 boolean extraDamage)
    {
    	start(explosions, helicopter, x, y, explosionType, extraDamage, null);
    }
	
	public static void start(Map<CollectionSubgroupType, Queue<Explosion>> explosions,
							 Helicopter helicopter,
							 double x, double y,
							 ExplosionType explosionType,
							 boolean extraDamage,
							 Enemy source)
    {
    	Iterator<Explosion> iterator = explosions.get(CollectionSubgroupType.INACTIVE).iterator();
		Explosion exp;
		if(iterator.hasNext()){exp = iterator.next(); iterator.remove();}
		else{exp = new Explosion();}
		exp.center.setLocation(x, y);
		exp.time = 0;
		// kann wahrscheinlich in den EMP spezifischen bereich verschoben werden
		helicopter.becomesCenterOf(exp);
		exp.type = explosionType;
		exp.source = source;
		if(explosionType != ExplosionType.EMP)
		{
			exp.maxTime = 35;
			exp.maxRadius = 65 + (explosionType == ExplosionType.JUMBO  || explosionType == ExplosionType.PHASE_SHIFT  ? 20 : 0) + (extraDamage ? 20 : 0);
	    	exp.broadness =  50 + (explosionType == ExplosionType.JUMBO  || explosionType == ExplosionType.PHASE_SHIFT  ? 25 : 0) + (extraDamage ? 25 : 0);
		}
		else
		{
			// EMP-Shockwave
			if(WindowManager.window == START_SCREEN)
			{
				exp.maxTime = 20;
				exp.maxRadius = 50;
				exp.broadness = 36;	    	
			}
			else
			{
				int level = helicopter.getUpgradeLevelOf(StandardUpgradeType.ENERGY_ABILITY);
				exp.maxTime = 20 + level;
				exp.maxRadius = 75 + (int)(19 + 3f * level * level);
				exp.broadness = 30 + 3 * (level);
			}
			((Pegasus)helicopter).empWave = exp;
	    	exp.earnedMoney = 0;
	    	exp.kills = 0;
		}			
		explosions.get(CollectionSubgroupType.ACTIVE).add(exp);
    }
	
	public float[] getProgress()
	{
		return progress;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public int getMaxRadius()
	{
		return maxRadius;
	}
	
	public int getBroadness()
	{
		return broadness;
	}
	
	public Point2D getCenter()
	{
		return center;
	}
	
	@Override
	public GameEntityGroupType getGroupType()
	{
		return GameEntityGroupType.EXPLOSION;
	}
}
