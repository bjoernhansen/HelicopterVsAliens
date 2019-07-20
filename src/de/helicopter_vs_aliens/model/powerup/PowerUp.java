package de.helicopter_vs_aliens.model.powerup;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.background.BackgroundObject;
import de.helicopter_vs_aliens.gui.Menu;
import de.helicopter_vs_aliens.model.MovingObject;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.util.MyColor;
import de.helicopter_vs_aliens.util.MyMath;

import static de.helicopter_vs_aliens.model.background.BackgroundObject.BG_SPEED;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeTypes.ENERGY_ABILITY;
import static de.helicopter_vs_aliens.model.powerup.PowerUpTypes.*;


public class PowerUp extends MovingObject
{
	private static final int
		GAME_SIZE = 30;
	public static final int MENU_SIZE = 23;
	
	private int
		direction;
	
	public boolean
		collected;        // = true: PowerUp kann in die LinkedList für inaktive PowerUps verschoben werden
		private boolean stopped;		// nur Helios-Klasse; =true: PowerUp fällt zu Boden
	
	private int 
		worth;			// nur für PowerUps vom Typ 5; bestimmt, wie viel Geld der Spieler für das Einsammeln erhält
	
	private boolean
		inStatusBar;	// = true: PowerUp befindet sich in der Statusbar
	
	public PowerUpTypes
		type;
	
	private Point2D 
		speed = new Point2D.Float();	// Geschwindigkeit des PowerUps
	
	public Color
		surface,		// Farben des PowerUps, hängen vom Typ ab
		cross;							

		
	public static void
	updateAll(ArrayList<LinkedList<PowerUp>> powerUp,
			  Helicopter helicopter)
	{
    	for(Iterator<PowerUp> i = powerUp.get(ACTIVE).iterator(); i.hasNext();)
		{
			PowerUp pu = i.next();
			pu.update(helicopter);			
			if(pu.collected)
			{
				i.remove();
				powerUp.get(INACTIVE).add(pu);
			}
		}		
	}

	public static void
	paintAll(Graphics2D g2d,
			 ArrayList<LinkedList<PowerUp>> powerUp)
	{
		for(PowerUp pu : powerUp.get(ACTIVE))
		{
			if (!pu.inStatusBar)
			{
				pu.paint(g2d);
			}
		}	
	}

	private void paint(Graphics2D g2d){paint(g2d, this.paintBounds.x);}
    
    public void paint(Graphics2D g2d, int x)
	{
		paint(	g2d,
				x, 
				this.paintBounds.y,
				this.paintBounds.width,
				this.paintBounds.height,
				this.surface, this.cross);		
	}
    
	public static void paint(Graphics2D g2d, int x, int y, int width, int height, Color surface, Color cross)
	{		
		g2d.setPaint(surface);
		g2d.fillRoundRect(x, y, width, height, 12, 12);
		g2d.setStroke(new BasicStroke(width/5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
		g2d.setPaint(cross);
		g2d.drawLine(x + width/2, y + height/5, x + width/2, y + (4 * height)/5);
		g2d.drawLine(x + width/5, y + height/2, x + (4 * width)/5, y + height/2);		
		g2d.setStroke(new BasicStroke(1));
		g2d.setPaint(MyColor.dimColor(surface, 0.75f));
		g2d.drawRoundRect(x, y, width, height, 12, 12);		
	}
	
	private void update(Helicopter helicopter)
	{		
		if(this.bounds.intersects(helicopter.bounds)){this.collect(helicopter);}		
		
		if(!this.stopped 
		   && helicopter.hasPowerUpImmobilizer
		   && (this.bounds.getX() - this.speed.getX() + 20 > 1004 ))
		{
			this.stop();
		}
		
		if(!this.inStatusBar)
		{
			if(!this.stopped)
			{
				double new_y_speed = 0.20 * this.direction * this.speed.getX();
				this.speed.setLocation(0.25 * this.direction + this.speed.getX(), 
										helicopter.hasPowerUpImmobilizer
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
						- (BackgroundObject.background_moves ? BG_SPEED : 0),
					Math.min(this.bounds.getY() - this.speed.getY(), 
							GROUND_Y - this.bounds.getHeight()), 
					GAME_SIZE, GAME_SIZE);
			
			if(   (this.speed.getX() == 0 && this.bounds.getMaxX() < 0) 
			    ||(this.speed.getX() != 0 && this.bounds.getMaxY() < 0))
			{
				this.collected = true;
			}
			this.setPaintBounds();
		}		
	}
	
	private void make(double x, double y, PowerUpTypes powerUp_type, int powerUp_worth, int powerUp_direction)
	{
		this.bounds.setRect(x, y, GAME_SIZE, GAME_SIZE);
		this.setPaintBounds(GAME_SIZE, GAME_SIZE);
		this.collected = false;
		this.stopped = false;
		this.inStatusBar = false;
		this.speed.setLocation(0, 0);
		this.direction = powerUp_direction;
		this.type = powerUp_type;		
		if(this.type == TRIPLE_DMG)
		{
			this.surface = Color.magenta;
			this.cross = Color.black;
		}
		else if(this.type == INVINCIBLE)
		{
			this.surface = Color.green;
			this.cross = Color.yellow;
		}
		else if(this.type == UNLIMITRED_ENERGY)
		{
			this.surface = Color.blue;
			this.cross = Color.cyan;
		}
		else if(this.type == BOOSTED_FIRE_RATE)
		{
			this.surface = Color.red;
			this.cross = Color.orange;
		}
		else if(this.type == REPARATION)
		{
			this.surface = Color.white;
			this.cross = Color.red;
		}
		else
		{
			this.surface = Color.orange;
			this.cross = MyColor.golden;
			this.worth = powerUp_worth;
		} 
	}
	
	private void collect(Helicopter helicopter)
	{
		this.collected = true;
		
		if(this.type.ordinal() > 3 || helicopter.powerUpTimer[this.type.ordinal()] ==  0)
		{
			Audio.play(Audio.pu_announcer[this.type.ordinal()]);
		}
		
		if(this.type == TRIPLE_DMG)
		{
			Audio.play(Audio.shield_up);
			if(!Events.isBossLevel()){
				Menu.update_collected_powerUps(helicopter, this);}
		}
		else if(this.type == INVINCIBLE)
		{
			Audio.play(Audio.teleport1);
			if(!Events.isBossLevel()){Menu.update_collected_powerUps(helicopter, this);}
		}
		else if(this.type == UNLIMITRED_ENERGY)
		{
			Audio.play(Audio.shield_up);	
			if(!Events.isBossLevel())
			{
				helicopter.energy 
					= Math.min(MyMath.energy(helicopter.levelOfUpgrade[ENERGY_ABILITY.ordinal()]),
											 helicopter.energy 
											 	+ Math.max(10, 
											 			   2*(MyMath.energy(helicopter.levelOfUpgrade[5])
											 				 - helicopter.energy)/3));
				Menu.update_collected_powerUps(helicopter, this);
			}				
		}
		else if(this.type == BOOSTED_FIRE_RATE)
		{
			Audio.play(Audio.shield_up);
			if(!Events.isBossLevel())
			{				
				helicopter.adjustFireRate(true);
				Menu.update_collected_powerUps(helicopter, this);
			}				
		}
		else if(this.type == REPARATION)
		{
			helicopter.use_reparation_PU();
		}
		else if(this.type == BONUS_INCOME)
		{
			Audio.play(Audio.cash);
			Events.last_extra_bonus = 0;
			Menu.moneyDisplayTimer = START;
			Events.lastBonus = (int)(1.5f*(helicopter.spotlight ? Events.NIGHT_BONUS_FACTOR : Events.DAY_BONUS_FACTOR) * this.worth);
			Events.money += Events.lastBonus;
			Events.overallEarnings += Events.lastBonus;
			Events.extraBonusCounter += Events.lastBonus;
		}
	}
	
	public void moveToStatusbar()
	{
		Menu.collected_PowerUp[this.type.ordinal()] = this;
		this.speed.setLocation(0, 0);
		this.inStatusBar = true;
		this.collected = false;
		this.bounds.setRect(100, 432, MENU_SIZE, MENU_SIZE);	
		this.setPaintBounds(MENU_SIZE, MENU_SIZE);
	}

	public static void activate(Helicopter helicopter, ArrayList<LinkedList<PowerUp>> powerUp, Enemy enemy, 
	                     PowerUpTypes type, boolean to_status_bar)
	{
		Iterator<PowerUp> i = powerUp.get(INACTIVE).iterator();
		PowerUp pu;					
		if(i.hasNext()){pu = i.next(); i.remove();}	
		else{pu = new PowerUp();}	
		if(enemy != null)
		{
			pu.make(enemy.bounds.getX(), 
					enemy.bounds.getY(), 
					type,
					enemy.isMiniBoss
						? (int)(1.25f * enemy.strength) 
						: 5 * enemy.strength, 
					helicopter.bounds.getX() > enemy.bounds.getX() 
					|| helicopter.hasPowerUpImmobilizer ? -1 : 1 );
		}
		else{pu.make(0, 0, type, 0, 0);}
		powerUp.get(ACTIVE).add(pu);
		if(to_status_bar){pu.moveToStatusbar();}		
	}
	
	private void stop()
	{
		this.stopped = true;
		this.speed.setLocation(0, 0);
	}
}
