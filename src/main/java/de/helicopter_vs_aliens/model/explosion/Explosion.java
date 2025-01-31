package de.helicopter_vs_aliens.model.explosion;

import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.entities.ManageablePaintable;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.gui.window.WindowManager;
import de.helicopter_vs_aliens.model.PaintableEntity;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.Pegasus;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;
import de.helicopter_vs_aliens.model.scenery.Scenery;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import static de.helicopter_vs_aliens.gui.WindowType.START_SCREEN;
import static de.helicopter_vs_aliens.model.scenery.SceneryObject.BG_SPEED;

public class Explosion extends PaintableEntity implements ManageablePaintable
{
    private int
		time;
		
	private int
		maxTime;
	
	private int
		kills;
	
	private int
		earnedMoney;		// nur für Pegasus-Klasse: dabei (kills, s.o.) verdientes Geld
  
	private final Ellipse2D
		ellipse = new Ellipse2D.Float();	// Einflussbereich der Explosion
	
	private Enemy
		source;
	
    private int
		maxRadius;
	
	private int
		broadness;	// breite des animierten Explosionsringes
	
    private final float[]
    	progress = new float[2];	// reguliert das Fortschreiten der Explosionsanimation       
    
    private Color 
    	color;	// Typ- und zeitabhängige (time, s.o.) Farbe der Explosion
    
    private final Point2D
		center = new Point2D.Float();			// Zentrum der Explosion

	private ExplosionType
		type;		// Standard, Plasma, EMP, etc.


    Explosion(){}
    
    Explosion(int x, int y)
    {
    	center.setLocation(x, y);
		time = 0;
		type = ExplosionType.EMP;
		maxTime = 25;
		maxRadius = 45;
		broadness = 36;
    }    
	
	boolean hasExpired()
	{
		return time >= maxTime;
	}
	
	void onExplosionEnd()
	{
		if(type == ExplosionType.EMP)
		{
			getGameRessourceProvider().getHelicopter().handleExplosionEnd();
			if(kills > 1)
			{
				// TODO Konstanten einführen und ggf. dorthin auslagern wo es passend ist
				Events.extraReward(kills, earnedMoney, 0.35f, 0.5f, 2.85f); // 0.5f, 0.5f, 3.0f
			}
		}
	}
    
    public void update()
	{
		updateProgress();
		updatePosition();
		updateColor();
	}
	
	private void updateProgress()
	{
		time += 1;
		float t = (float)time/ maxTime;
		progress[0] = t;
		progress[1] = t * t * t - 3 * t * t + 3 * t;
	}
	
	private void updatePosition()
	{
		if(Scenery.isBackgroundMoving)
		{
			center.setLocation(center.getX() - BG_SPEED,
									center.getY());
		}
		if(source != null)
		{
			center.setLocation((int)source.getCenterX(),
				(int)source.getCenterY());
		}
	}
	
	private void updateColor()
	{
		if(type == ExplosionType.STUNNING || type == ExplosionType.EMP)
		{
			color = new Color((int) (255 * (1 - progress[0])), (int) (255 * (1 - progress[0] * progress[1])), 255, (int) (255 * (1 - progress[0] * progress[1])));
		}
		else if(type == ExplosionType.PLASMA)
		{
			color = new Color((int)(242 * (1- progress[0]* progress[1])), (int) (255 * (1- progress[0]* progress[0]) /*(int)(255 * (1-factor3*factor3))*/), (int)(1.0 * 255 * (1- progress[1])), (int)(255 * (1- progress[0]* progress[1])));
		}
		else
		{
			color = new Color(255, (int)(255 * (1- progress[1]* progress[1])), (int)(255 * (1- progress[1])), (int)(255 * (1- progress[0]* progress[1])));
		}
	}
	
	void initialize(
		double x,
		double y,
		ExplosionType explosionType,
		boolean extraDamage,
		Enemy source)
	{
		center.setLocation(x, y);
		time = 0;
		// kann wahrscheinlich in den EMP spezifischen bereich verschoben werden
		Helicopter helicopter = getHelicopter();
		helicopter.becomesCenterOf(this);
		type = explosionType;
		this.source = source;
		if(explosionType != ExplosionType.EMP)
		{
			maxTime = 35;
			maxRadius = 65 + (explosionType == ExplosionType.JUMBO  || explosionType == ExplosionType.PHASE_SHIFT  ? 20 : 0) + (extraDamage ? 20 : 0);
	    	broadness =  50 + (explosionType == ExplosionType.JUMBO  || explosionType == ExplosionType.PHASE_SHIFT  ? 25 : 0) + (extraDamage ? 25 : 0);
		}
		else
		{
			// EMP-Shockwave
			if(WindowManager.window == START_SCREEN)
			{
				maxTime = 20;
				maxRadius = 50;
				broadness = 36;
			}
			else
			{
				int level = helicopter.getUpgradeLevelOf(StandardUpgradeType.ENERGY_ABILITY);
				maxTime = 20 + level;
				maxRadius = 75 + (int)(19 + 3f * level * level);
				broadness = 30 + 3 * (level);
			}
			((Pegasus)helicopter).empWave = this;
	    	earnedMoney = 0;
	    	kills = 0;
		}
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
	
	public int getTime()
	{
		return time;
	}
	
	public int getMaxTime()
	{
		return maxTime;
	}
	
	public Ellipse2D getEllipse()
	{
		return ellipse;
	}
	
	@Override
	public ManageablePaintableGroupType getGroupType()
	{
		return ManageablePaintableGroupType.EXPLOSION;
	}
	
	public void countKill()
	{
		kills++;
	}
	
	public void increaseRewardBy(int reward)
	{
		earnedMoney += reward;
	}
}
