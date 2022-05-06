package de.helicopter_vs_aliens.model.missile;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.model.RectangularGameEntity;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.ExplosionTypes;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;

import java.util.HashMap;
import java.util.Iterator;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;
import static de.helicopter_vs_aliens.model.scenery.SceneryObject.BG_SPEED;
import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.TIT;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BOSS_2_SERVANT;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.JUMBO;
import static de.helicopter_vs_aliens.model.explosion.ExplosionTypes.PHASE_SHIFT;


public class Missile extends RectangularGameEntity
{	
	private static final float
		STANDARD_DAMAGE_FACTOR = 1.0f,
		POWERUP_DAMAGE_FACTOR = 3.0f,	// Faktor, um den sich die Schadenswirkung von Raketen erhöht, wenn das Bonus-Damage-PowerUp eingesammelt wurde
		SHIFT_DAMAGE_FACTOR = 8.9f;		// Pegasus-Klasse: Faktor, um den sich die Schadenswirkung einer Rakete erhöht, wenn diese abgeschossen wird, während der Interphasen-Generator aktiviert ist

	public int
		dmg,			// Schaden, den die Rakete beim Gegner anrichtet, wenn sie trifft
		kills, 			// nur für Roch- und Orochi Klasse: mit dieser Rakete vernichtete Gegner
		earnedMoney;	// mit dieser Rakete durch Gegner-Vernichtung verdientes Geld
	
	public double 
		speed;			// Geschwindigkeit der Rakete
	
	public boolean
		extraDamage,	// = true: Rakete wurde abgeschossen während beim Helicopter das Extra-Feuerkraft-PowerUp aktiv ist
		dangerous,		// = true: kann den Helicopter beschädigen
		bounced;		// = true: ist an unverwundbaren Gegner abgeprallt
	
	public final Missile []
		sister = new Missile [2];	// nur für Roch- und Orochi Klasse: Schwesterraketen (werden gleichzeitig abgefeuert)
	
	public final HashMap<Integer, Enemy>
		hits = new HashMap<> ();	// HashMap zur Speicherung, welche Gegner bereits von der Rakete getroffen wurden (jede Rakete kann jeden Gegner nur einmal treffen)

	public ExplosionTypes
		typeOfExplosion;

	public int
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
		this.setDmg(helicopter.getBaseDamage());
		this.hits.clear();
		
		if(helicopter.hasKillCountingMissiles())
		{
			this.kills = 0;
			this.earnedMoney = 0;
			this.sisterKills = 0;
			this.nrOfHittingSisters = 0;
		}
		else if(helicopter.hasTimeRecordingMissiles())
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
							helicopter.getBounds().getY() + y,
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
	
	private void setDmg(float baseDamage)
	{
		this.dmg = 	(int) (	baseDamage
							* (this.typeOfExplosion == PHASE_SHIFT ? SHIFT_DAMAGE_FACTOR : STANDARD_DAMAGE_FACTOR)
							* (this.extraDamage ? POWERUP_DAMAGE_FACTOR : 1));
	}
	
	public static void updateAll(Controller controller, Helicopter helicopter)
	{
		for(Iterator<Missile> i = controller.missiles.get(ACTIVE).iterator(); i.hasNext();)
		{
			Missile missile = i.next();
			missile.update(controller, i, helicopter);
		}
	}

	private void update(Controller controller, Iterator<Missile> i, Helicopter helicopter)
	{		
		this.setX(this.bounds.getX()
					+ this.speed
					+ (Scenery.backgroundMoves ? - BG_SPEED : 0));
				
		if(this.bounds.getX() > 1175 || this.bounds.getX() + 20 < 0){this.flying = false;}
		else{this.checkForHitHelicopter(helicopter);}
		this.checkForHitEnemys(controller, helicopter);
		if(!this.flying)
		{
			i.remove();	
			helicopter.inactivate(controller.missiles, this);
		}
		this.setPaintBounds();
	}
	
	private void checkForHitHelicopter(Helicopter helicopter)
	{
		if(	this.dangerous 
			&& helicopter.getBounds().intersects(this.bounds))
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
			if (enemy.isHittable(this))
			{
				if (enemy.teleportTimer == 0
					&& enemy.stunningTimer == 0
					&& enemy.empSlowedTimer == 0)
				{
					enemy.teleport();
				}
				else if (!enemy.isInvincible())
				{
					enemy.hitByMissile(helicopter, this, controller.explosions);
				}
				else if (!this.bounced
					&& enemy.teleportTimer < 1
					&& !(enemy.type == BOSS_2_SERVANT))
				{
					Audio.play(Audio.rebound);
					this.speed = -Math.signum(this.speed) * StandardUpgradeType.MISSILE_DRIVE.getMagnitude(1);
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
					enemy.die(controller, helicopter, this);
					
					if (helicopter.deservesMantisReward(this.launchingTime))
					{
						Events.extraReward(1,
							enemy.getEffectiveStrength() * helicopter.getBonusFactor(),
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
						? 2*e.getBounds().getWidth()/15
						: -(e.model == TIT 
							? e.getBounds().getWidth()/3
							: 2*e.getBounds().getWidth()/15)));
		
		return e.getBounds().intersectsLine(	intersectLineX,
										this.bounds.getY(),
										intersectLineX,
										this.bounds.getMaxY());
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
				   && enemy.getBounds().intersects(	this.bounds.getX(),
					   							this.bounds.getY()-1, 
					   							20 * this.speed, 
					   							this.bounds.getWidth()+2))
				||(this.speed < 0 
				   && enemy.getBounds().intersects(	this.bounds.getMaxX() + 20 * this.speed,
												this.bounds.getY()-1, 
												-20 * this.speed,
												this.bounds.getWidth()+2));
	}
}