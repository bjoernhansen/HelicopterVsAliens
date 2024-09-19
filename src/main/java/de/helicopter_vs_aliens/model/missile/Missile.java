package de.helicopter_vs_aliens.model.missile;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.entities.GroupTypeOwner;
import de.helicopter_vs_aliens.control.entities.PaintableEntityGroupType;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.RectangularPaintableEntity;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.ExplosionType;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.model.scenery.SceneryObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.TIT;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BOSS_2_SERVANT;


public class Missile extends RectangularPaintableEntity implements GroupTypeOwner
{
	private static final float
		STANDARD_DAMAGE_FACTOR = 1.0f;
	
	private static final float
		POWERUP_DAMAGE_FACTOR = 3.0f;	// Faktor, um den sich die Schadenswirkung von Raketen erhöht, wenn das Bonus-Damage-PowerUp eingesammelt wurde
	
	private static final float
		SHIFT_DAMAGE_FACTOR = 8.9f;		// Pegasus-Klasse: Faktor, um den sich die Schadenswirkung einer Rakete erhöht, wenn diese abgeschossen wird, während der Interphasen-Generator aktiviert ist

	private int
		damageEffect;
	
	private int
		kills;
	
	public int
		earnedMoney;	// mit dieser Rakete durch Gegner-Vernichtung verdientes Geld
	
	public double
		speed;			// Geschwindigkeit der Rakete
	
	public boolean
		extraDamage;	// = true: Rakete wurde abgeschossen während beim Helicopter das Extra-Feuerkraft-PowerUp aktiv ist
	
	public boolean
		dangerous;		// = true: kann den Helicopter beschädigen
	
	public boolean
		bounced;		// = true: ist an unverwundbaren Gegner abgeprallt
	
	public final Missile []
		sister = new Missile [2];	// nur für Roch- und Orochi Klasse: Schwesterraketen (werden gleichzeitig abgefeuert)
	
	public final Map<Integer, Enemy>
		hits = new HashMap<> ();	// HashMap zur Speicherung, welche Gegner bereits von der Rakete getroffen wurden (jede Rakete kann jeden Gegner nur einmal treffen)

	public ExplosionType
		typeOfExplosion;

	public int
		sisterKills;			// nur Orochi Klasse: Kills der (gleichzeitig abgefeuerten) Schwesterrakete(n)
	
	public int
		nrOfHittingSisters;	// Anzahl der Schwesterraketen, die wenigstens einen Gegner vernichtet haben
	
	private long
		launchingTime; 		// nur für Phönix-Klasse relevant
	
	private boolean
		flying;					// = true: Rakete fliegt; wird gleich false gesetzt, wenn Rakete den sichtbaren Bildschirmbereich verlässt oder trifft
	

	public void launch(Helicopter helicopter, int y)
	{
		speed = helicopter.missileDrive * (helicopter.isMovingLeft ? -1 : 1);
		dangerous = false;
		bounced = false;
		flying = true;
		extraDamage = helicopter.hasTripleDamage();
		typeOfExplosion = helicopter.getCurrentExplosionTypeOfMissiles();
		setBounds(helicopter, y);
		setDamageEffect(helicopter.getBaseDamage());
		hits.clear();
		
		if(helicopter.hasKillCountingMissiles())
		{
			kills = 0;
			earnedMoney = 0;
			sisterKills = 0;
			nrOfHittingSisters = 0;
		}
		else if(helicopter.hasTimeRecordingMissiles())
		{
			launchingTime = System.currentTimeMillis();
		}
	}
	
	// TODO in Methoden auslagern
	private void setBounds(Helicopter helicopter, int y)
	{
		setBounds(helicopter.location.getX()
								- (helicopter.isMovingLeft
									? (typeOfExplosion == ExplosionType.JUMBO ? 30 : 20)
									: 0),
							helicopter.getY() + y,
							typeOfExplosion == ExplosionType.JUMBO  ? 30 : 20,
							typeOfExplosion == ExplosionType.JUMBO ? 6 : 4);
		setPaintBounds((int)getWidth(),
							  (int)getHeight());
	}
	
	private void setDamageEffect(float baseDamage)
	{
		damageEffect = (int) (	baseDamage
							* (typeOfExplosion == ExplosionType.PHASE_SHIFT ? SHIFT_DAMAGE_FACTOR : STANDARD_DAMAGE_FACTOR)
							* (extraDamage ? POWERUP_DAMAGE_FACTOR : 1));
	}
	
	public static void updateAll(GameRessourceProvider gameRessourceProvider)
	{
		for(Iterator<Missile> i = gameRessourceProvider.getActivePaintableEntityManager()
													   .getMissiles().get(CollectionSubgroupType.ACTIVE).iterator(); i.hasNext();)
		{
			Missile missile = i.next();
			missile.update(gameRessourceProvider, i);
		}
	}

	private void update(GameRessourceProvider gameRessourceProvider, Iterator<Missile> i)
	{
		Helicopter helicopter = gameRessourceProvider.getHelicopter();
		setX(getX()
					+ speed
					+ (Scenery.backgroundMoves ? - SceneryObject.BG_SPEED : 0));
				
		if(getX() > 1175 || getX() + 20 < 0)
		{
			flying = false;
		}
		else if(canHit(helicopter))
		{
			hit(helicopter);
		}
		checkIfMissileHitEnemy(gameRessourceProvider);
		if(!flying)
		{
			i.remove();
			helicopter.inactivate(this);
		}
		setPaintBounds();
	}
	
	private boolean canHit(Helicopter helicopter)
	{
		return dangerous && intersects(helicopter);
	}
	
	private void hit(Helicopter helicopter)
	{
		Audio.play(Audio.explosion2);
		dangerous = false;
		if(!helicopter.hasPiercingWarheads)
		{
			flying = false;
		}
		helicopter.takeMissileDamage();
	}
	
	private void checkIfMissileHitEnemy(GameRessourceProvider gameRessourceProvider)
	{
		Helicopter helicopter = gameRessourceProvider.getHelicopter();
		for(Enemy enemy : gameRessourceProvider.getActivePaintableEntityManager()
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
				else if (!bounced
					&& enemy.teleportTimer < 1
					&& enemy.getType() != BOSS_2_SERVANT)
				{
					Audio.play(Audio.rebound);
					speed = -Math.signum(speed) * StandardUpgradeType.MISSILE_DRIVE.getMagnitude(1);
					dangerous = true;
					bounced = true;
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
					
					if (helicopter.deservesMantisReward(launchingTime))
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
					flying = false;
					break;
				}
			}
			if (couldHit(enemy) && enemy.isReadyToDodge())
			{
				enemy.dodge(this);
			}
		}
	}
	
	public boolean intersects(Enemy enemy)
	{
		int intersectLineX
			= (int)(getX()
					+ (speed < 0 ? 0 : getWidth())
					+ (speed < 0
						? enemy.getWidth()/7.5
						: -(enemy.getModel() == TIT
							? enemy.getWidth()/3.0
							: enemy.getWidth()/7.5)));
		
		return enemy.intersectsLine(	intersectLineX,
									getMinY(),
									intersectLineX,
									getMaxY());
	}

	private boolean couldHit(Enemy enemy)
	{
		return 	  (speed > 0
				   && enemy.intersects(	getX(),
					   							getY()-1,
					   							20 * speed,
					   							getWidth()+2))
				||(speed < 0
				   && enemy.intersects(	getMaxX() + 20 * speed,
												getY()-1,
												-20 * speed,
												getWidth()+2));
	}
	
	public boolean isFlyingRight()
	{
		return speed > 0;
	}
	
	public boolean isFlyingLeft()
	{
		return speed < 0;
	}
    
    public boolean hasGreatExplosivePower()
    {
		return typeOfExplosion.isBigExplosion() || extraDamage;
    }
	
	@Override
	public PaintableEntityGroupType getGroupType()
	{
		return PaintableEntityGroupType.MISSILE;
	}
    
    public boolean isStunning()
    {
		return typeOfExplosion == ExplosionType.STUNNING;
    }
	
	public boolean atLeastOneSisterHasQualifiedForFirstCreditOn(Enemy enemy)
	{
		return hasFirstSisterQualifiedForFirstCreditOn(enemy)
			|| hasSecondSisterQualifiedForFirstCreditOn(enemy);
	}
	
	public boolean hasFirstSisterQualifiedForFirstCreditOn(Enemy enemy)
	{
		return sister[0] != null && sister[0].hasQualifiedForFirstCreditOn(enemy);
	}
	
	public boolean hasSecondSisterQualifiedForFirstCreditOn(Enemy enemy)
	{
		return sister[1] != null && sister[1].hasQualifiedForFirstCreditOn(enemy);
	}
	
	public boolean hasQualifiedForFirstCreditOn(Enemy enemy)
	{
		return intersects(enemy) && !hasKilled();
	}
	
	public void creditFirstSister()
	{
		sister[0].credit();
	}
	
	public void creditSecondSister()
	{
		sister[1].credit();
	}
	
	private void credit()
	{
		kills++;
		earnedMoney += Events.lastBonus;
	}
	
	public boolean hasKilled()
	{
		return kills > 0;
	}
	
	public void creditItselfOrSisterOn(Enemy enemy, boolean hasPiercingWarheads)
	{
		if(hasKilled()
			&& hasPiercingWarheads
			&& atLeastOneSisterHasQualifiedForFirstCreditOn(enemy))
		{
			if(hasFirstSisterQualifiedForFirstCreditOn(enemy))
			{
				creditFirstSister();
			}
			else if(hasSecondSisterQualifiedForFirstCreditOn(enemy))
			{
				creditSecondSister();
			}
		}
		else
		{
			credit();
		}
	}
	
	public int getDamageEffect()
	{
		return damageEffect;
	}
	
	public int numberOfClusterKills()
	{
		return kills + sisterKills;
	}
	
	public int getNonFailedShots()
	{
		return (hasKilled() ? 1 : 0) + nrOfHittingSisters;
	}
}