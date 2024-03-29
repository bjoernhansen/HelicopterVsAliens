package de.helicopter_vs_aliens.model.missile;

import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.explosion.ExplosionType;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.model.scenery.SceneryObject;
import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.entities.GameEntityGroupType;
import de.helicopter_vs_aliens.control.entities.GroupTypeOwner;
import de.helicopter_vs_aliens.model.RectangularGameEntity;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;

import java.util.HashMap;
import java.util.Iterator;

import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.TIT;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BOSS_2_SERVANT;


public class Missile extends RectangularGameEntity implements GroupTypeOwner
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

	public ExplosionType
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
		this.extraDamage = helicopter.hasTripleDamage();

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
	
	// TODO in Methoden auslagern
	private void setBounds(Helicopter helicopter, int y)
	{
		setBounds(helicopter.location.getX()
								- (helicopter.isMovingLeft
									? (this.typeOfExplosion == ExplosionType.JUMBO ? 30 : 20)
									: 0), 
							helicopter.getY() + y,
							this.typeOfExplosion == ExplosionType.JUMBO  ? 30 : 20,
							this.typeOfExplosion == ExplosionType.JUMBO ? 6 : 4);
		this.setPaintBounds((int)this.getWidth(),
							  (int)this.getHeight());
	}
	
	private void setDmg(float baseDamage)
	{
		this.dmg = 	(int) (	baseDamage
							* (this.typeOfExplosion == ExplosionType.PHASE_SHIFT ? SHIFT_DAMAGE_FACTOR : STANDARD_DAMAGE_FACTOR)
							* (this.extraDamage ? POWERUP_DAMAGE_FACTOR : 1));
	}
	
	public static void updateAll(GameRessourceProvider gameRessourceProvider)
	{
		for(Iterator<Missile> i = gameRessourceProvider.getActiveGameEntityManager()
													   .getMissiles().get(CollectionSubgroupType.ACTIVE).iterator(); i.hasNext();)
		{
			Missile missile = i.next();
			missile.update(gameRessourceProvider, i);
		}
	}

	private void update(GameRessourceProvider gameRessourceProvider, Iterator<Missile> i)
	{
		Helicopter helicopter = gameRessourceProvider.getHelicopter();
		this.setX(this.getX()
					+ this.speed
					+ (Scenery.backgroundMoves ? - SceneryObject.BG_SPEED : 0));
				
		if(this.getX() > 1175 || this.getX() + 20 < 0)
		{
			this.flying = false;
		}
		else if(this.canHit(helicopter))
		{
			this.hit(helicopter);
		}
		this.checkIfMissileHitEnemy(gameRessourceProvider);
		if(!this.flying)
		{
			i.remove();
			helicopter.inactivate(gameRessourceProvider.getActiveGameEntityManager()
													   .getMissiles(), this);
		}
		this.setPaintBounds();
	}
	
	private boolean canHit(Helicopter helicopter)
	{
		return this.dangerous && this.intersects(helicopter);
	}
	
	private void hit(Helicopter helicopter)
	{
		Audio.play(Audio.explosion2);
		this.dangerous = false;
		if(!helicopter.hasPiercingWarheads)
		{
			this.flying = false;
		}
		helicopter.takeMissileDamage();
	}
	
	private void checkIfMissileHitEnemy(GameRessourceProvider gameRessourceProvider)
	{
		Helicopter helicopter = gameRessourceProvider.getHelicopter();
		for(Enemy enemy : gameRessourceProvider.getActiveGameEntityManager()
											   .getEnemies().get(CollectionSubgroupType.ACTIVE))
		{
			if (enemy.isHittable(this))
			{
				if (enemy.teleportTimer == 0
					&& !enemy.isStunned()
					&& enemy.empSlowedTimer == 0)
				{
					enemy.teleport();
				}
				else if (!enemy.isInvincible())
				{
					enemy.hitByMissile(gameRessourceProvider, this);
				}
				else if (!this.bounced
					&& enemy.teleportTimer < 1
					&& !(enemy.getType() == BOSS_2_SERVANT))
				{
					Audio.play(Audio.rebound);
					this.speed = -Math.signum(this.speed) * StandardUpgradeType.MISSILE_DRIVE.getMagnitude(1);
					this.dangerous = true;
					this.bounced = true;
				}
				
				if (enemy.hasHPsLeft())
				{
					if (!enemy.isStunned())
					{
						enemy.reactToHit(this);
					}
				}
				else
				{
					enemy.dieByMissile(gameRessourceProvider, this);
					
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
			if (this.couldHit(enemy) && enemy.isReadyToDodge())
			{
				enemy.dodge(this);
			}
		}	
	}
	
	public boolean intersects(Enemy e)
	{
		int intersectLineX
			= (int)(this.getX()
					+ (this.speed < 0 ? 0 : this.getWidth())
					+ (this.speed < 0 
						? e.getWidth()/7.5
						: -(e.getModel() == TIT
							? e.getWidth()/3.0
							: e.getWidth()/7.5)));
		
		return e.intersectsLine(	intersectLineX,
									this.getMinY(),
									intersectLineX,
									this.getMaxY());
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
				   && enemy.intersects(	this.getX(),
					   							this.getY()-1,
					   							20 * this.speed, 
					   							this.getWidth()+2))
				||(this.speed < 0 
				   && enemy.intersects(	this.getMaxX() + 20 * this.speed,
												this.getY()-1,
												-20 * this.speed,
												this.getWidth()+2));
	}
	
	public boolean isFlyingRight()
	{
		return this.speed > 0;
	}
	
	public boolean isFlyingLeft()
	{
		return this.speed < 0;
	}
    
    public boolean hasGreatExplosivePower()
    {
		return this.typeOfExplosion.isBigExplosion() || this.extraDamage;
    }
	
	@Override
	public GameEntityGroupType getGroupType()
	{
		return GameEntityGroupType.MISSILE;
	}
    
    public boolean isStunning()
    {
		return typeOfExplosion == ExplosionType.STUNNING;
    }
}