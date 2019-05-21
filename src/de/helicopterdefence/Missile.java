package de.helicopterdefence;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import enemy.BossTypes;
import enemy.Enemy;

import static de.helicopterdefence.HelicopterTypes.PHOENIX_;


public class Missile extends MovingObject implements DamageFactors, MissileTypes, BossTypes
{	
	public int
		type,
		dmg,			// Schaden, den die Rakete beim Gegner anrichtet, wenn sie trifft
		kills, 			// nur für Roch- und Orochi Klasse: mit dieser Rakete vernichtete Gegner
		earned_money;	// mit dieser Rakete durch Gegner-Vernichtung verdientes Geld
	
	public double 
		speed;			// Geschwindigkeit der Rakete
	
	public boolean
		extra_dmg,		// = true: Rakete wurde abgeschossen während beim Helicopter das Extra-Feuerkraft-PowerUp aktiv ist
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
	
	
	void launch(Helicopter helicopter, boolean stunning_missile, int y)
	{
		this.speed = helicopter.missile_drive * (helicopter.is_moving_left ? -1 : 1);	
		this.dangerous = false;
		this.bounced = false;
		this.flying = true;	
		this.extra_dmg = helicopter.has_triple_dmg();
		if(!stunning_missile)
		{
			if(helicopter.plasma_activation_timer > 0){this.type = PLASMA;}
			else if(helicopter.jumbo_missiles > 2){this.type = JUMBO;}
			else{this.type = STANDARD;}									
		}								
		else if(helicopter.type == OROCHI){this.type = STUNNING;}
		else{this.type = PHASE_SHIFT;}
		this.setBounds(helicopter, y);
		this.set_dmg(helicopter);
		this.hits.clear();
		
		if(helicopter.type == ROCH || helicopter.type == OROCHI)
		{
			this.kills = 0;
			this.earned_money = 0;
			this.sister_kills = 0;
			this.nr_of_hitting_sisters = 0;			
		}
		else if(helicopter.helicopterType == PHOENIX_)
		{
			this.launching_time = System.currentTimeMillis();
		}		
	}
	
	private void setBounds(Helicopter helicopter, int y)
	{
		this.bounds.setRect(helicopter.location.getX() 
								- (helicopter.is_moving_left 
									? (this.type == JUMBO ? 30 : 20) 
									: 0), 
							helicopter.bounds.getY() + y, 
							this.type == JUMBO  ? 30 : 20, 
							this.type == JUMBO ? 6 : 4);
		this.set_paint_bounds((int)this.bounds.getWidth(),
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
		this.dmg = 	(int)(helicopter.current_firepower 
				* (helicopter.nr_of_cannons == 3 ? OROCHI_XTRA_DMG_FACTOR : 1)
				* ((helicopter.plasma_activation_timer == 0) ? 1 : MyMath.plasma_dmg_factor(helicopter.level_of_upgrade[ENERGY_ABILITY]))
				* (this.type == PHASE_SHIFT ? SHIFT_DAMAGE_FACTOR : 1)
				* (this.extra_dmg ? POWERUP_DAMAGE_FACTOR : 1));
	}

	static void paint_all_missiles(Graphics2D g2d, HelicopterDefence hd)
	{
		for(Iterator<Missile> i = hd.missile.get(ACTIVE).iterator(); i.hasNext();)
		{
			Missile m = i.next();
			m.paint(g2d);
		}		
	}

	static void update_all(HelicopterDefence hd, Helicopter helicopter)
	{
		for(Iterator<Missile> i = hd.missile.get(ACTIVE).iterator(); i.hasNext();)
		{
			Missile m = i.next();
			m.update(hd, i, helicopter);			
		}
	}

	private void update(HelicopterDefence hd, Iterator<Missile> i, Helicopter helicopter)
	{		
		this.set_x(this.bounds.getX()							
					+ this.speed
					+ (BgObject.background_moves ? -BG_SPEED : 0));
				
		if(this.bounds.getX() > 1175 || this.bounds.getX() + 20 < 0){this.flying = false;}
		else{this.check_for_hit_helicopter(helicopter);}
		this.check_for_hit_enemys(hd, helicopter);
		if(!this.flying)
		{
			i.remove();	
			this.inactivate(hd.missile, helicopter);				
		}
		this.set_paint_bounds();
	}
	
	private void check_for_hit_helicopter(Helicopter helicopter)
	{
		if(	this.dangerous 
			&& helicopter.bounds.intersects(this.bounds))
		{
			Audio.play(Audio.explosion2);
			this.dangerous = false;
			if(!helicopter.has_piercing_warheads)
			{
				this.flying = false;
			}			
			helicopter.take_missile_damage();			
		}
	}		
	
	private void check_for_hit_enemys(HelicopterDefence hd, 
	                                  Helicopter helicopter)
	{
		for(Iterator<Enemy> i = hd.enemy.get(ACTIVE).iterator(); i.hasNext();)
		{
			Enemy e = i.next();
			if(e.is_hitable(this))
			{									  
				if(	   e.teleport_timer   == READY
					&& e.stunning_timer   == READY
					&& e.emp_slowed_timer == READY)
				{
					e.teleport();
				}
				else if(!e.is_invincible())
				{				
					e.hit_by_missile(helicopter, this, hd.explosion);
				}
				else if(!this.bounced 
						&& e.teleport_timer < 1 
						&& !(e.type == BOSS_2_SERVANT))
				{
					Audio.play(Audio.rebound);
					this.speed = -Math.signum(this.speed)
							      *MyMath.missile_drive(1);
					this.dangerous = true;
					this.bounced = true;
				}
				
				if(e.has_HPs_left())
				{
					if(e.stunning_timer == READY)
					{
						e.react_to_hit(helicopter, this);
					}
				}
				else
				{						
					e.die(hd, helicopter, this, false);
					if(	helicopter.helicopterType == PHOENIX_
						&& helicopter.bonus_kills_timer > 0 
						&& this.launching_time > helicopter.past_teleport_time)
					{
						Events.extra_reward(1, 
											e.strength
											*(helicopter.spotlight 
												? Events.NIGHT_BONUS_FACTOR 
												: Events.DAY_BONUS_FACTOR),
											1.25f, 
											0f, 
											1.25f);
					}
				}
				if( !helicopter.has_piercing_warheads 
				    && !e.is_invincible())
				{
					this.flying = false;	
					break;
				}				
			}			
			if(this.could_hit(e) && e.is_ready_to_dodge(helicopter)){e.dodge();}
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
		g2d.fillRect(this.paint_bounds.x
							+(this.speed >= 0 ? 0 : this.paint_bounds.width + 3), 
					 this.paint_bounds.y-2, 					 	         
					  2, 
					 this.paint_bounds.height+4);
		
		g2d.fillRect(this.paint_bounds.x
							+ (this.speed >= 0 ? 2 : this.paint_bounds.width + 1),
					 this.paint_bounds.y-1,
					  2,
					 this.paint_bounds.height+2);
		
		g2d.fillRect(this.paint_bounds.x
							+(this.speed >= 0 ? 4 : 1),
					 (this.paint_bounds.y), 
					 (this.paint_bounds.width),
					 (this.paint_bounds.height));
					
		g2d.setColor(MyColor.pink);
		g2d.fillRect(this.paint_bounds.x+(this.speed >= 0 ? 2 : 0),
					 this.paint_bounds.y+1,
					 this.paint_bounds.width+3,
					 this.paint_bounds.height-2);
		
		g2d.setColor(Color.yellow);
		g2d.fillRect(this.paint_bounds.x
						+(this.speed >= 0 ? -3 : this.paint_bounds.width+5), 
					 (this.paint_bounds.y),
					  3,
					 (this.paint_bounds.height));		
		
		g2d.setColor(MyColor.translucentWhite);
		g2d.fillRect((int)(this.get_paintMaxX()
						*(this.speed >= 0 ? -this.speed/5 : 1) 
						+(this.speed >= 0 ? -6 : 11)),
				     (this.paint_bounds.y),				     
				     (int)(0.2*Math.abs(this.speed)*this.paint_bounds.width), 				     
				     (this.paint_bounds.height));		
	}
	
	private void inactivate(ArrayList<LinkedList<Missile>> missile, Helicopter helicopter)
	{							
		if(helicopter.type == ROCH || helicopter.type == OROCHI)
		{
			if(this.sister[0] == null && this.sister[1] == null)
			{							
				if(this.kills + this.sister_kills > 1)
				{
					if(helicopter.type == ROCH)
					{										
						Events.extra_reward(this.kills + this.sister_kills, this.earned_money, 0.5f, 0.75f, 3.0f);
					}
					else if(helicopter.type == OROCHI)
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
				for(int j = 0; j < 2; j++)
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
		this.earned_money += Events.last_bonus;
	}

	public boolean could_hit(Enemy enemy)
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