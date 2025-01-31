package de.helicopter_vs_aliens.model.missile;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.entities.ManageablePaintable;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableController;
import de.helicopter_vs_aliens.control.entities.ManageablePaintableGroupType;
import de.helicopter_vs_aliens.control.ressource_transfer.GameRessourceProvider;
import de.helicopter_vs_aliens.model.RectangularPaintableEntity;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.ExplosionType;
import de.helicopter_vs_aliens.model.helicopter.Helicopter;
import de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType;
import de.helicopter_vs_aliens.model.scenery.Scenery;
import de.helicopter_vs_aliens.model.scenery.SceneryObject;

import java.util.Collection;
import java.util.HashSet;

import static de.helicopter_vs_aliens.model.enemy.EnemyModelType.TIT;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.BOSS_2_SERVANT;


public class Missile extends RectangularPaintableEntity implements ManageablePaintable
{
	private static final float
		STANDARD_DAMAGE_FACTOR = 1.0f;
	
	private static final float
		POWERUP_DAMAGE_FACTOR = 3.0f;	// Faktor, um den sich die Schadenswirkung von Raketen erhöht, wenn das Bonus-Damage-PowerUp eingesammelt wurde
	
	private static final float
		SHIFT_DAMAGE_FACTOR = 8.9f;		// Pegasus-Klasse: Faktor, um den sich die Schadenswirkung einer Rakete erhöht, wenn diese abgeschossen wird, während der Interphasen-Generator aktiviert ist

	
	private int
		damageEffect;
	
	private double
		speed;			// Geschwindigkeit der Rakete
	
	private boolean
		inflictsExtraDamage;	// = true: Rakete wurde abgeschossen während beim Helicopter das Extra-Feuerkraft-PowerUp aktiv ist
	
	private boolean
		dangerous;		// = true: kann den Helicopter beschädigen
	
	private boolean
		bounced;		// = true: ist an unverwundbaren Gegner abgeprallt
	
	private final Collection<Enemy>
		hits = new HashSet<>();	// HashMap zur Speicherung, welche Gegner bereits von der Rakete getroffen wurden (jede Rakete kann jeden Gegner nur einmal treffen)
	
	private ExplosionType
		typeOfExplosion;
	
	private boolean
		isFlying = true;					// = true: Rakete fliegt; wird gleich false gesetzt, wenn Rakete den sichtbaren Bildschirmbereich verlässt oder trifft
	
	// nur für Roch- und Orochi-Klasse relevant
	private final MissileClusterManager
		missileClusterManager = new MissileClusterManager(this);
	
	// nur für Phönix-Klasse relevant
	private long
		launchingTime; 		// nur für Phönix-Klasse relevant
	
	
	Missile(){}
	
	public void launch(Helicopter helicopter, int y)
	{
		speed = helicopter.missileDrive * (helicopter.isMovingLeft ? -1 : 1);
		inflictsExtraDamage = helicopter.hasTripleDamage();
		typeOfExplosion = helicopter.getCurrentExplosionTypeOfMissiles();
		setBounds(helicopter, y);
		setDamageEffect(helicopter.getBaseDamage());
	}
	
	public void reset()
	{
		dangerous = false;
		bounced = false;
		isFlying = true;
		hits.clear();
	}
	
	public void setBackTimeRecorder()
	{
		launchingTime = System.currentTimeMillis();
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
							* (inflictsExtraDamage ? POWERUP_DAMAGE_FACTOR : 1));
	}
	
	// TODO gibt es immer "updateAll"? --> wenn ja Zusammenführen!
	public static void updateAll(GameRessourceProvider gameRessourceProvider)
		// TODO Fehler finden und dann verwenden anstelle von updateAll
	{
		ManageablePaintableController manageablePaintableController = gameRessourceProvider.getManageablePaintableController();
		// TODO der PaintableEntityController könnte ggf. eine Hilfsklasse zurückgeben, die dann bereits typ spezifisch ist, so müsste nicht jedes mal wieder der Group-Type übergeben werden
		manageablePaintableController.forEachActiveEntity(ManageablePaintableGroupType.MISSILE,
														  missile -> ((Missile)missile).update());
		manageablePaintableController.forEachActiveEntityIf(ManageablePaintableGroupType.MISSILE,
															missile -> !((Missile)missile).isFlying,
															missile -> ((Missile)missile).inactivate());
		manageablePaintableController.removeIf(ManageablePaintableGroupType.MISSILE,
											   missile -> !((Missile)missile).isFlying);
	}
	
	// TODO wenn alle ManagablePaintables eine Update-Methode haben, dann könnte diese Methode teil des Interfaces werden und dann könnte in der KLasse ManagePaintableController das sehr elegant gelöst werden
	private void update()
	{
		double newX = getX() + speed + (Scenery.isBackgroundMoving ? -SceneryObject.BG_SPEED : 0);
		setX(newX);
		if(getX() > 1175 || getX() + 20 < 0)
		{
			isFlying = false;
		}
		else if(canHit(getHelicopter()))
		{
			hit(getHelicopter());
		}
		checkIfMissileHitEnemy();
		setPaintBounds();
	}
	
	private void inactivate()
	{
		Helicopter helicopter = getGameRessourceProvider().getHelicopter();
		if(helicopter.hasKillCountingMissiles())
		{
			Grantable reward = helicopter.getMultipleHitsExtraReward(this);
			missileClusterManager.inactivateWith(reward);
		}
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
			isFlying = false;
		}
		helicopter.takeMissileDamage();
	}
	
	private void checkIfMissileHitEnemy()
	{
		for(Enemy enemy : getGameRessourceProvider().getManageablePaintableController()
											   .getIntactEnemies())
		{
			if (enemy.isHittable(this))
			{
				Helicopter helicopter = getHelicopter();
				if (enemy.teleportTimer == 0
					&& !enemy.isStunned()
					&& enemy.empSlowedTimer == 0)
				{
					enemy.teleport();
				}
				else if (!enemy.isInvincible())
				{
					enemy.hitByMissile(this);
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
					enemy.dieByMissile( this);
					
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
					isFlying = false;
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
		return typeOfExplosion.isBigExplosion() || inflictsExtraDamage;
    }
	
	@Override
	public ManageablePaintableGroupType getGroupType()
	{
		return ManageablePaintableGroupType.MISSILE;
	}
    
    public boolean isStunning()
    {
		return typeOfExplosion == ExplosionType.STUNNING;
    }
	
	public int getDamageEffect()
	{
		return damageEffect;
	}
	
	public void setBackStatistics()
	{
		missileClusterManager.reset();
	}
	
	public void creditItselfOrCompanionOn(Enemy enemy, boolean hasPiercingWarheads)
	{
		missileClusterManager.assignKillToSelfOrCompanion(enemy, hasPiercingWarheads);
	}
	
	public void grantExtraRewardForNonFailedShots()
	{
		missileClusterManager.grantExtraRewardForSuccessfulShots();
	}
	
	public void grantExtraRewardForMultipleKillsWithSingleShot()
	{
		missileClusterManager.grantExtraRewardForMultipleKillsWithSingleShot();
	}
	
	public void joinClusterWith(Missile missile)
	{
		missileClusterManager.joinClusterWith(missile.missileClusterManager);
	}
	
	public boolean inflictsExtraDamage()
	{
		return inflictsExtraDamage;
	}
	
	public void rememberHitting(Enemy enemy)
	{
		hits.add(enemy);
	}
	
	public void forgetHitting(Enemy enemy)
	{
		hits.remove(enemy);
	}
	
	public boolean hasHit(Enemy enemy)
	{
		return hits.contains(enemy);
	}
	
	public double getSpeed()
	{
		return speed;
	}
	
	public ExplosionType getTypeOfExplosion()
	{
		return typeOfExplosion;
	}
}