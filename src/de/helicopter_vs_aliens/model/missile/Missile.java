package de.helicopter_vs_aliens.model.missile;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupTypes;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.MovingObject;
import de.helicopter_vs_aliens.model.background.BackgroundObject;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.ExplosionTypes;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.util.MyColor;
import de.helicopter_vs_aliens.util.MyMath;

import java.awt.*;
import java.util.*;

import static de.helicopter_vs_aliens.control.CollectionSubgroupTypes.ACTIVE;
import static de.helicopter_vs_aliens.model.background.BackgroundObject.BG_SPEED;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelTypes.TIT;
import static de.helicopter_vs_aliens.model.enemy.EnemyTypes.BOSS_2_SERVANT;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.JUMBO;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.PHASE_SHIFT;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterTypes.*;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeTypes.ENERGY_ABILITY;


public class Missile extends MovingObject
{	
	private static final float
		POWERUP_DAMAGE_FACTOR = 3f,				// Faktor, um den sich die Schadenswirkung von Raketen erhöht, wenn das Bonus-Damage-PowerUp eingesammelt wurde
		SHIFT_DAMAGE_FACTOR = 8.9f;				// Pegasus-Klasse: Faktor, um den sich die Schadenswirkung einer Rakete erhöht, wenn diese abgeschossen wird, während der Interphasen-Generator aktiviert ist

	public int
		dmg,			// Schaden, den die Rakete beim Gegner anrichtet, wenn sie trifft
		kills, 			// nur für Roch- und Orochi Klasse: mit dieser Rakete vernichtete Gegner
		earnedMoney;	// mit dieser Rakete durch Gegner-Vernichtung verdientes Geld
	
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

	public ExplosionTypes
		typeOfExplosion;

	private int
		sisterKills,			// nur Orochi Klasse: Kills der (gleichzeitig abgefeuerten) Schwesterrakete(n)
		nrOfHittingSisters;	// Anzahl der Schwesterraketen, die wenigstens einen Gegner vernichtet haben
	
	private long
		launchingTime; 		// nur für Phönix-Klasse relevant
	
	private boolean 
		flying;					// = true: Rakete fliegt; wird gleich false gesetzt, wenn Rakete den sichtbaren Bildschirmbereich verlässt oder trifft
	

	public void launch(Helicopter helicopter, boolean stunningMissile, int y)
	{
		this.speed = helicopter.missileDrive * (helicopter.isMovingLeft ? -1 : 1);
		this.dangerous = false;
		this.bounced = false;
		this.flying = true;	
		this.extraDamage = helicopter.hasTripleDmg();

		this.typeOfExplosion = helicopter.getCurrentExplosionTypeOfMissiles(stunningMissile);

		this.setBounds(helicopter, y);
		this.setDmg(helicopter);
		this.hits.clear();
		
		if(helicopter.getType() == ROCH || helicopter.getType() == OROCHI)
		{
			this.kills = 0;
			this.earnedMoney = 0;
			this.sisterKills = 0;
			this.nrOfHittingSisters = 0;
		}
		else if(helicopter.getType() == PHOENIX)
		{
			this.launchingTime = System.currentTimeMillis();
		}		
	}
	
	private void setBounds(Helicopter helicopter, int y)
	{
		this.bounds.setRect(helicopter.location.getX() 
								- (helicopter.isMovingLeft
									? (this.typeOfExplosion == JUMBO ? 30 : 20)
									: 0), 
							helicopter.bounds.getY() + y, 
							this.typeOfExplosion == JUMBO  ? 30 : 20,
							this.typeOfExplosion == JUMBO ? 6 : 4);
		this.setPaintBounds((int)this.bounds.getWidth(),
							  (int)this.bounds.getHeight());
	}
	
	private void setX(double x)
	{
		this.bounds.setRect(x,
							this.bounds.getY(),
							this.bounds.getWidth(),
							this.bounds.getHeight());	
	}
	
	private void setDmg(Helicopter helicopter)
	{
		this.dmg = 	(int)(helicopter.currentBaseFirepower
				* ((helicopter.plasmaActivationTimer == 0) ? 1 : MyMath.plasmaDamageFactor(helicopter.levelOfUpgrade[ENERGY_ABILITY.ordinal()]))
				* (this.typeOfExplosion == PHASE_SHIFT ? SHIFT_DAMAGE_FACTOR : 1)
				* (this.extraDamage ? POWERUP_DAMAGE_FACTOR : 1));
	}

	public static void paintAllMissiles(Graphics2D g2d, Controller controller)
	{
		for(Missile m : controller.missiles.get(ACTIVE))
		{
			m.paint(g2d);
		}		
	}

	public static void updateAll(Controller controller, Helicopter helicopter)
	{
		for(Iterator<Missile> i = controller.missiles.get(ACTIVE).iterator(); i.hasNext();)
		{
			Missile m = i.next();
			m.update(controller, i, helicopter);
		}
	}

	private void update(Controller controller, Iterator<Missile> i, Helicopter helicopter)
	{		
		this.setX(this.bounds.getX()
					+ this.speed
					+ (BackgroundObject.backgroundMoves ? - BG_SPEED : 0));
				
		if(this.bounds.getX() > 1175 || this.bounds.getX() + 20 < 0){this.flying = false;}
		else{this.checkForHitHelicopter(helicopter);}
		this.checkForHitEnemys(controller, helicopter);
		if(!this.flying)
		{
			i.remove();	
			this.inactivate(controller.missiles, helicopter);
		}
		this.setPaintBounds();
	}
	
	private void checkForHitHelicopter(Helicopter helicopter)
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
			helicopter.takeMissileDamage();
		}
	}		
	
	private void checkForHitEnemys(Controller controller,
								   Helicopter helicopter)
	{
		for(Enemy enemy : controller.enemies.get(ACTIVE))
		{
			if (enemy.isHitable(this))
			{
				if (enemy.teleportTimer == 0
					&& enemy.stunningTimer == 0
					&& enemy.empSlowedTimer == 0)
				{
					enemy.teleport();
				} else if (!enemy.isInvincible())
				{
					enemy.hitByMissile(helicopter, this, controller.explosions);
				} else if (!this.bounced
					&& enemy.teleportTimer < 1
					&& !(enemy.type == BOSS_2_SERVANT))
				{
					Audio.play(Audio.rebound);
					this.speed = -Math.signum(this.speed)
						* MyMath.missileDrive(1);
					this.dangerous = true;
					this.bounced = true;
				}
				
				if (enemy.hasHPsLeft())
				{
					if (enemy.stunningTimer == 0)
					{
						enemy.reactToHit(helicopter, this);
					}
				} else
				{
					enemy.die(controller, helicopter, this, false);
					if (helicopter.getType() == PHOENIX
						&& helicopter.bonusKillsTimer > 0
						&& this.launchingTime > helicopter.pastTeleportTime)
					{
						Events.extraReward(1,
							enemy.getEffectiveStrength()
								* (helicopter.hasSpotlights
								? Events.NIGHT_BONUS_FACTOR
								: Events.DAY_BONUS_FACTOR),
							1.25f,
							0f,
							1.25f);
					}
				}
				if (!helicopter.hasPiercingWarheads
					&& !enemy.isInvincible())
				{
					this.flying = false;
					break;
				}
			}
			if (this.couldHit(enemy) && enemy.isReadyToDodge(helicopter))
			{
				enemy.dodge();
			}
		}	
	}
	
	public boolean intersects(Enemy e)
	{
		int intersectLineX
			= (int)(this.bounds.getX()
					+ (this.speed < 0 ? 0 : this.bounds.getWidth()) 
					+ (this.speed < 0 
						? 2*e.bounds.getWidth()/15 
						: -(e.model == TIT 
							? e.bounds.getWidth()/3 
							: 2*e.bounds.getWidth()/15)));		
		
		return e.bounds.intersectsLine(	intersectLineX,
										this.bounds.getY(),
										intersectLineX,
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
	
	private void inactivate(EnumMap<CollectionSubgroupTypes, LinkedList<Missile>> missile, Helicopter helicopter)
	{							
		if(helicopter.getType() == ROCH || helicopter.getType() == OROCHI)
		{
			if(this.sister[0] == null && this.sister[1] == null)
			{							
				if(this.kills + this.sisterKills > 1)
				{
					if(helicopter.getType() == ROCH)
					{										
						Events.extraReward(this.kills + this.sisterKills, this.earnedMoney, 0.5f, 0.75f, 3.0f);
					}
					else if(helicopter.getType() == OROCHI)
					{
						int nonFailedShots = (this.kills > 0 ? 1 : 0) + this.nrOfHittingSisters;
						if(nonFailedShots == 1)
						{										
							Events.extraReward(this.kills + this.sisterKills, this.earnedMoney, 0.25f, 0.0f, 0.25f);
						}
						if(nonFailedShots == 2)
						{										
							Events.extraReward(this.kills + this.sisterKills, this.earnedMoney, 1.5f, 0.0f, 1.5f);
						}
						else if(nonFailedShots == 3)
						{										
							Events.extraReward(this.kills + this.sisterKills, this.earnedMoney, 4f, 0.0f, 4f);
						}
						else assert false;
					}	
				}
			}
			else if(this.kills + this.sisterKills > 0)
			{
				for(int j = 0; true; j++)
				{
					if(this.sister[j] != null)
					{
						this.sister[j].earnedMoney += this.earnedMoney;
						this.sister[j].sisterKills += this.kills + this.sisterKills;
						this.sister[j].nrOfHittingSisters += ((this.kills > 0 ? 1 : 0) + this.nrOfHittingSisters);
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
		missile.get(CollectionSubgroupTypes.INACTIVE).add(this);
	}
	
	public static boolean canTakeCredit(Missile missile, Enemy enemy)
	{		
		return missile != null 
			   && missile.intersects(enemy);
	}
	
	public void credit()
	{
		this.kills++;
		this.earnedMoney += Events.lastBonus;
	}

	private boolean couldHit(Enemy enemy)
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