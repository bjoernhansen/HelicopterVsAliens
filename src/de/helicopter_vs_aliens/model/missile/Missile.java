package de.helicopter_vs_aliens.model.missile;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.background.BackgroundObject;
import de.helicopter_vs_aliens.model.MovingObject;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.enemy.BossTypes;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.util.MyColor;
import de.helicopter_vs_aliens.util.MyMath;

import static de.helicopter_vs_aliens.model.background.BackgroundObject.BG_SPEED;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelTypes.TIT;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.OROCHI;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.PHOENIX;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.ROCH;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeTypes.ENERGY_ABILITY;


public class Missile extends MovingObject implements MissileTypes, BossTypes
{	
	private static final float
		POWERUP_DAMAGE_FACTOR = 3f,				// Faktor, um den sich die Schadenswirkung von Raketen erhöht, wenn das Bonus-Damage-PowerUp eingesammelt wurde
		OROCHI_EXTRA_DAMAGE_FACTOR = 1.03f, 	// Orochi-Klasse: Faktor, um den sich die Schadenswirkung von Raketen erhöht wird
		SHIFT_DAMAGE_FACTOR = 8.9f;				// Pegasus-Klasse: Faktor, um den sich die Schadenswirkung einer Rakete erhöht, wenn diese abgeschossen wird, während der Interphasen-Generator aktiviert ist

	public int
		type,
		dmg,			// Schaden, den die Rakete beim Gegner anrichtet, wenn sie trifft
		kills, 			// nur für Roch- und Orochi Klasse: mit dieser Rakete vernichtete Gegner
		earned_money;	// mit dieser Rakete durch Gegner-Vernichtung verdientes Geld
	
	public double 
		speed;			// Geschwindigkeit der Rakete
	
	public boolean
		extraDamage,		// = true: Rakete wurde abgeschossen während beim Helicopter das Extra-Feuerkraft-PowerUp aktiv ist
		dangerous,		// = true: kann den Helicopter beschädigen
		bounced;		// = true: ist an unverwundbaren Gegner abgeprallt
	
	public Missile [] 
		sister = new Missile [2];	// nur für Roch- und Orochi Klasse: Schwesterraketen (werden gleichzeitig abgefeuert)
	
	public HashMap<Integer, Enemy> 
		hits = new HashMap<> ();	// HashMap zur Speicherung, welche Gegner bereits von der Rakete getroffen wurden (jede Rakete kann jeden Gegner nur einmal treffen)
	
	private int 
		sister_kills,			// nur Orochi Klasse: Kills der (gleichzeitig abgefeuerten) Schwesterrakete(n) 
		nr_of_hitting_sisters;	// Anzahl der Schwesterraketen, die wenigstens einen Gegner vernichtet haben
	
	private long 
		launching_time; 		// nur für Phönix-Klasse relevant
	
	private boolean 
		flying;					// = true: Rakete fliegt; wird gleich false gesetzt, wenn Rakete den sichtbaren Bildschirmbereich verlässt oder trifft
	
	
	public void launch(Helicopter helicopter, boolean stunning_missile, int y)
	{
		this.speed = helicopter.missileDrive * (helicopter.isMovingLeft ? -1 : 1);
		this.dangerous = false;
		this.bounced = false;
		this.flying = true;	
		this.extraDamage = helicopter.has_triple_dmg();
		if(!stunning_missile)
		{
			if(helicopter.plasmaActivationTimer > 0){this.type = PLASMA;}
			else if(helicopter.hasJumboMissiles()){this.type = JUMBO;}
			else{this.type = STANDARD;}									
		}								
		else if(helicopter.getType() == OROCHI){this.type = STUNNING;}
		else{this.type = PHASE_SHIFT;}
		this.setBounds(helicopter, y);
		this.set_dmg(helicopter);
		this.hits.clear();
		
		if(helicopter.getType() == ROCH || helicopter.getType() == OROCHI)
		{
			this.kills = 0;
			this.earned_money = 0;
			this.sister_kills = 0;
			this.nr_of_hitting_sisters = 0;			
		}
		else if(helicopter.getType() == PHOENIX)
		{
			this.launching_time = System.currentTimeMillis();
		}		
	}
	
	private void setBounds(Helicopter helicopter, int y)
	{
		this.bounds.setRect(helicopter.location.getX() 
								- (helicopter.isMovingLeft
									? (this.type == JUMBO ? 30 : 20) 
									: 0), 
							helicopter.bounds.getY() + y, 
							this.type == JUMBO  ? 30 : 20, 
							this.type == JUMBO ? 6 : 4);
		this.setPaintBounds((int)this.bounds.getWidth(),
							  (int)this.bounds.getHeight());
	}
	
	private void set_x(double x)
	{
		this.bounds.setRect(x,
							this.bounds.getY(),
							this.bounds.getWidth(),
							this.bounds.getHeight());	
	}
	
	private void set_dmg(Helicopter helicopter)
	{
		this.dmg = 	(int)(helicopter.currentFirepower
				* (helicopter.numberOfCannons == 3 ? OROCHI_EXTRA_DAMAGE_FACTOR : 1.0f)
				* ((helicopter.plasmaActivationTimer == 0) ? 1 : MyMath.plasma_dmg_factor(helicopter.levelOfUpgrade[ENERGY_ABILITY.ordinal()]))
				* (this.type == PHASE_SHIFT ? SHIFT_DAMAGE_FACTOR : 1)
				* (this.extraDamage ? POWERUP_DAMAGE_FACTOR : 1));
	}

	public static void paintAllMissiles(Graphics2D g2d, Controller controller)
	{
		for(Missile m : controller.missile.get(ACTIVE))
		{
			m.paint(g2d);
		}		
	}

	public static void updateAll(Controller controller, Helicopter helicopter)
	{
		for(Iterator<Missile> i = controller.missile.get(ACTIVE).iterator(); i.hasNext();)
		{
			Missile m = i.next();
			m.update(controller, i, helicopter);
		}
	}

	private void update(Controller controller, Iterator<Missile> i, Helicopter helicopter)
	{		
		this.set_x(this.bounds.getX()							
					+ this.speed
					+ (BackgroundObject.background_moves ? - BG_SPEED : 0));
				
		if(this.bounds.getX() > 1175 || this.bounds.getX() + 20 < 0){this.flying = false;}
		else{this.check_for_hit_helicopter(helicopter);}
		this.check_for_hit_enemys(controller, helicopter);
		if(!this.flying)
		{
			i.remove();	
			this.inactivate(controller.missile, helicopter);
		}
		this.setPaintBounds();
	}
	
	private void check_for_hit_helicopter(Helicopter helicopter)
	{
		if(	this.dangerous 
			&& helicopter.bounds.intersects(this.bounds))
		{
			Audio.play(Audio.explosion2);
			this.dangerous = false;
			if(!helicopter.hasPiercingWarheads)
			{
				this.flying = false;
			}			
			helicopter.take_missile_damage();			
		}
	}		
	
	private void check_for_hit_enemys(Controller controller,
                                      Helicopter helicopter)
	{
		for(Enemy e : controller.enemy.get(ACTIVE))
		{
			if (e.isHitable(this))
			{
				if (e.teleportTimer == READY
					&& e.stunningTimer == READY
					&& e.empSlowedTimer == READY)
				{
					e.teleport();
				} else if (!e.isInvincible())
				{
					e.hitByMissile(helicopter, this, controller.explosion);
				} else if (!this.bounced
					&& e.teleportTimer < 1
					&& !(e.type == BOSS_2_SERVANT))
				{
					Audio.play(Audio.rebound);
					this.speed = -Math.signum(this.speed)
						* MyMath.missile_drive(1);
					this.dangerous = true;
					this.bounced = true;
				}
				
				if (e.hasHPsLeft())
				{
					if (e.stunningTimer == READY)
					{
						e.reactToHit(helicopter, this);
					}
				} else
				{
					e.die(controller, helicopter, this, false);
					if (helicopter.getType() == PHOENIX
						&& helicopter.bonusKillsTimer > 0
						&& this.launching_time > helicopter.pastTeleportTime)
					{
						Events.extra_reward(1,
							e.strength
								* (helicopter.spotlight
								? Events.NIGHT_BONUS_FACTOR
								: Events.DAY_BONUS_FACTOR),
							1.25f,
							0f,
							1.25f);
					}
				}
				if (!helicopter.hasPiercingWarheads
					&& !e.isInvincible())
				{
					this.flying = false;
					break;
				}
			}
			if (this.could_hit(e) && e.isReadyToDodge(helicopter))
			{
				e.dodge();
			}
		}	
	}
	
	public boolean intersects(Enemy e)
	{
		int intersect_line_x 
			= (int)(this.bounds.getX()
					+ (this.speed < 0 ? 0 : this.bounds.getWidth()) 
					+ (this.speed < 0 
						? 2*e.bounds.getWidth()/15 
						: -(e.model == TIT 
							? e.bounds.getWidth()/3 
							: 2*e.bounds.getWidth()/15)));		
		
		return e.bounds.intersectsLine(	intersect_line_x, 
										this.bounds.getY(),
										intersect_line_x, 
										this.bounds.getMaxY());
	}

	private void paint(Graphics2D g2d)
	{		
		g2d.setColor(MyColor.red);
		g2d.fillRect(this.paintBounds.x
							+(this.speed >= 0 ? 0 : this.paintBounds.width + 3),
					 this.paintBounds.y-2,
					  2, 
					 this.paintBounds.height+4);
		
		g2d.fillRect(this.paintBounds.x
							+ (this.speed >= 0 ? 2 : this.paintBounds.width + 1),
					 this.paintBounds.y-1,
					  2,
					 this.paintBounds.height+2);
		
		g2d.fillRect(this.paintBounds.x
							+(this.speed >= 0 ? 4 : 1),
					 (this.paintBounds.y),
					 (this.paintBounds.width),
					 (this.paintBounds.height));
					
		g2d.setColor(MyColor.pink);
		g2d.fillRect(this.paintBounds.x+(this.speed >= 0 ? 2 : 0),
					 this.paintBounds.y+1,
					 this.paintBounds.width+3,
					 this.paintBounds.height-2);
		
		g2d.setColor(Color.yellow);
		g2d.fillRect(this.paintBounds.x
						+(this.speed >= 0 ? -3 : this.paintBounds.width+5),
					 (this.paintBounds.y),
					  3,
					 (this.paintBounds.height));
		
		g2d.setColor(MyColor.translucentWhite);
		g2d.fillRect((int)(this.getPaintMaxX()
						*(this.speed >= 0 ? -this.speed/5 : 1) 
						+(this.speed >= 0 ? -6 : 11)),
				     (this.paintBounds.y),
				     (int)(0.2*Math.abs(this.speed)*this.paintBounds.width),
				     (this.paintBounds.height));
	}
	
	private void inactivate(ArrayList<LinkedList<Missile>> missile, Helicopter helicopter)
	{							
		if(helicopter.getType() == ROCH || helicopter.getType() == OROCHI)
		{
			if(this.sister[0] == null && this.sister[1] == null)
			{							
				if(this.kills + this.sister_kills > 1)
				{
					if(helicopter.getType() == ROCH)
					{										
						Events.extra_reward(this.kills + this.sister_kills, this.earned_money, 0.5f, 0.75f, 3.0f);
					}
					else if(helicopter.getType() == OROCHI)
					{
						int non_failed_shots = (this.kills > 0 ? 1 : 0) + this.nr_of_hitting_sisters;
						if(non_failed_shots == 1)
						{										
							Events.extra_reward(this.kills + this.sister_kills, this.earned_money, 0.25f, 0.0f, 0.25f);
						}
						if(non_failed_shots == 2)
						{										
							Events.extra_reward(this.kills + this.sister_kills, this.earned_money, 1.5f, 0.0f, 1.5f);
						}
						else if(non_failed_shots == 3)
						{										
							Events.extra_reward(this.kills + this.sister_kills, this.earned_money, 4f, 0.0f, 4f);
						}
						else assert false;
					}	
				}
			}
			else if(this.kills + this.sister_kills > 0)
			{
				for(int j = 0; true; j++)
				{
					if(this.sister[j] != null)
					{
						this.sister[j].earned_money += this.earned_money;
						this.sister[j].sister_kills += this.kills + this.sister_kills;
						this.sister[j].nr_of_hitting_sisters += ((this.kills > 0 ? 1 : 0) + this.nr_of_hitting_sisters);
						break;
					}
				}							
			}
			for(int j = 0; j < 2; j++)
			{
				if(this.sister[j] != null)
				{
					if(this.sister[j].sister[0] == this){this.sister[j].sister[0] = null;}
					else if(this.sister[j].sister[1] == this){this.sister[j].sister[1] = null;}
					else assert false;
				}
			}									
		}					
		missile.get(INACTIVE).add(this);		
	}
	
	public static boolean can_take_credit(Missile missile, Enemy enemy)
	{		
		return missile != null 
			   && missile.intersects(enemy);
	}
	
	public void credit()
	{
		this.kills++;
		this.earned_money += Events.lastBonus;
	}

	private boolean could_hit(Enemy enemy)
	{		
		return 	  (this.speed > 0 
				   && enemy.bounds.intersects(	this.bounds.getX(), 
					   							this.bounds.getY()-1, 
					   							20 * this.speed, 
					   							this.bounds.getWidth()+2))
				||(this.speed < 0 
				   && enemy.bounds.intersects(	this.bounds.getMaxX() + 20 * this.speed, 
												this.bounds.getY()-1, 
												-20 * this.speed,
												this.bounds.getWidth()+2));
	}
}	