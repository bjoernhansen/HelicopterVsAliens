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
	
	public double
		speed;			// Geschwindigkeit der Rakete
	
	public boolean
		extraDamage;	// = true: Rakete wurde abgeschossen während beim Helicopter das Extra-Feuerkraft-PowerUp aktiv ist
	
	public boolean
		dangerous;		// = true: kann den Helicopter beschädigen
	
	public boolean
		bounced;		// = true: ist an unverwundbaren Gegner abgeprallt
	
	public final Map<Integer, Enemy>
		hits = new HashMap<> ();	// HashMap zur Speicherung, welche Gegner bereits von der Rakete getroffen wurden (jede Rakete kann jeden Gegner nur einmal treffen)
	
	public ExplosionType
		typeOfExplosion;
	
	private boolean
		flying;					// = true: Rakete fliegt; wird gleich false gesetzt, wenn Rakete den sichtbaren Bildschirmbereich verlässt oder trifft
	
	
	// nur für Roch- und Orochi-Klasse relevant
	private int
		kills;
	
	public int
		earnedMoney;	// mit dieser Rakete durch Gegner-Vernichtung verdientes Geld
	
	private final Missile []
		sister = new Missile [2];	// nur für Roch- und Orochi Klasse: Schwesterraketen (werden gleichzeitig abgefeuert)
	
	private int
		sisterKills;			// nur Orochi Klasse: Kills der (gleichzeitig abgefeuerten) Schwesterrakete(n)
	
	private int
		nrOfHittingSisters;	// Anzahl der Schwesterraketen, die wenigstens einen Gegner vernichtet haben
	
	
	// nur für Roch- und Orochi-Klasse relevant
	private long
		launchingTime; 		// nur für Phönix-Klasse relevant
	
	
	public void launch(Helicopter helicopter, int y)
	{
		speed = helicopter.missileDrive * (helicopter.isMovingLeft ? -1 : 1);
		extraDamage = helicopter.hasTripleDamage();
		typeOfExplosion = helicopter.getCurrentExplosionTypeOfMissiles();
		setBounds(helicopter, y);
		setDamageEffect(helicopter.getBaseDamage());
	}
	
	public void reset()
	{
		dangerous = false;
		bounced = false;
		flying = true;
		hits.clear();
	}
	
	public void setBackTimeRecorder()
	{
		launchingTime = System.currentTimeMillis();
	}
	
	public void setBackKillCounter()
	{
		kills = 0;
		earnedMoney = 0;
		sisterKills = 0;
		nrOfHittingSisters = 0;
		sister[0] = null;
		sister[1] = null;
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
	
	// TODO code duplizierungen zwischen inactivateForRoch und inactivateForOrochi auflösen
	// TODO gemeinsame Methoden schaffen, code insgesamt refactorn und vereinfachenb, ggf. Logik in eigene Klasse auslagern
	public void inactivateForRoch()
	{
		if(sister[0] == null && sister[1] == null)
		{
			if(numberOfClusterKills() > 1)
			{
				Events.extraReward(numberOfClusterKills(), earnedMoney, 0.5f, 0.75f, 3.0f);
			}
		}
		else if(numberOfClusterKills() > 0)
		{
			for(int j = 0; true; j++)
			{
				if(sister[j] != null)
				{
					sister[j].earnedMoney += earnedMoney;
					sister[j].sisterKills += numberOfClusterKills();
					sister[j].nrOfHittingSisters += getNonFailedShots();
					break;
				}
			}
		}
		for(int j = 0; j < 2; j++)
		{
			if(sister[j] != null)
			{
				if(sister[j].sister[0] == this)
				{
					sister[j].sister[0] = null;
				}
				else if(sister[j].sister[1] == this)
				{
					sister[j].sister[1] = null;
				}
				else
				{
					// TODO allenfalls Exception werfen, aber wahrscheinlich unnötig
					assert false;
				}
			}
		}
	}
	
	public void inactivateForOrochi()
	{
		if(sister[0] == null && sister[1] == null)
		{
			if(numberOfClusterKills() > 1)
			{
				int nonFailedShots = getNonFailedShots();
				if(nonFailedShots == 1)
				{
					Events.extraReward(numberOfClusterKills(), earnedMoney, 0.25f, 0.0f, 0.25f);
				}
				if(nonFailedShots == 2)
				{
					Events.extraReward(numberOfClusterKills(), earnedMoney, 1.5f, 0.0f, 1.5f);
				}
				else if(nonFailedShots == 3)
				{
					Events.extraReward(numberOfClusterKills(), earnedMoney, 4f, 0.0f, 4f);
				}
				else
				{
					assert false;
				}
			}
		}
		else if(numberOfClusterKills() > 0)
		{
			for(int j = 0; true; j++)
			{
				if(sister[j] != null)
				{
					sister[j].earnedMoney += earnedMoney;
					sister[j].sisterKills += numberOfClusterKills();
					sister[j].nrOfHittingSisters += getNonFailedShots();
					break;
				}
			}
		}
		for(int j = 0; j < 2; j++)
		{
			if(sister[j] != null)
			{
				if(sister[j].sister[0] == this)
				{
					sister[j].sister[0] = null;
				}
				else if(sister[j].sister[1] == this)
				{
					sister[j].sister[1] = null;
				}
				else
				{
					// TODO allenfalls Exception werfen, aber wahrscheinlich unnötig
					assert false;
				}
			}
		}
	}
	
	
	
	public void joinClusterWith(Missile missile)
	{
		addSister(missile);
		missile.addSister(this);
	}
	
	private void addSister(Missile missile)
	{
		if(sister[0] == null)
		{
			sister[0] = missile;
		}
		else if(sister[1] == null)
		{
			sister[1] = missile;
		}
		else {
			throw new UnsupportedOperationException("Adding of more than 2 sisters is not supported!");
		}
	}
}