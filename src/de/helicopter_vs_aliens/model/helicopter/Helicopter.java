package de.helicopter_vs_aliens.model.helicopter;

import de.helicopter_vs_aliens.audio.Audio;
import de.helicopter_vs_aliens.control.CollectionSubgroupType;
import de.helicopter_vs_aliens.control.Controller;
import de.helicopter_vs_aliens.control.EnemyController;
import de.helicopter_vs_aliens.control.Events;
import de.helicopter_vs_aliens.control.GameRessourceProvider;
import de.helicopter_vs_aliens.gui.PriceLevel;
import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.gui.window.WindowManager;
import de.helicopter_vs_aliens.model.RectangularGameEntity;
import de.helicopter_vs_aliens.model.enemy.Enemy;
import de.helicopter_vs_aliens.model.explosion.Explosion;
import de.helicopter_vs_aliens.model.explosion.ExplosionType;
import de.helicopter_vs_aliens.model.helicopter.components.Battery;
import de.helicopter_vs_aliens.model.helicopter.components.PowerUpController;
import de.helicopter_vs_aliens.model.missile.Missile;
import de.helicopter_vs_aliens.model.powerup.PowerUp;
import de.helicopter_vs_aliens.model.powerup.PowerUpType;
import de.helicopter_vs_aliens.score.Savegame;
import de.helicopter_vs_aliens.score.ScoreScreenTimes;
import de.helicopter_vs_aliens.util.Calculations;
import de.helicopter_vs_aliens.util.Colorations;

import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import static de.helicopter_vs_aliens.control.CollectionSubgroupType.ACTIVE;
import static de.helicopter_vs_aliens.control.CollectionSubgroupType.INACTIVE;
import static de.helicopter_vs_aliens.control.TimeOfDay.NIGHT;
import static de.helicopter_vs_aliens.gui.PriceLevel.EXTORTIONATE;
import static de.helicopter_vs_aliens.gui.WindowType.GAME;
import static de.helicopter_vs_aliens.model.enemy.EnemyType.KABOOM;
import static de.helicopter_vs_aliens.model.explosion.ExplosionType.ORDINARY;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.OROCHI;
import static de.helicopter_vs_aliens.model.helicopter.HelicopterType.ROCH;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.ENERGY_ABILITY;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.FIREPOWER;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.FIRE_RATE;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.MISSILE_DRIVE;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.PLATING;
import static de.helicopter_vs_aliens.model.helicopter.StandardUpgradeType.ROTOR_SYSTEM;


public abstract class Helicopter extends RectangularGameEntity
{
	// TODO alles public fields genau prüfen, ob sie public sein müssen, und wenn ja mit Setter-Methoden arbeiten, sonst private
	public static final int
		// TODO einstellen auf 60 Frames per Second
		POWER_UP_DURATION = 930,                // Zeit [frames] welche ein eingesammeltes PowerUp aktiv bleibt
		NO_COLLISION_DAMAGE_TIME = 20,        // Zeitrate, mit der Helicopter Schaden durch Kollisionen mit Gegnern nehmen kann
		INVULNERABILITY_DAMAGE_REDUCTION = 80,    // %-Wert der Schadensreduzierung bei Unverwundbarkeit
		STANDARD_SPECIAL_COSTS = 125000,
		CHEAP_SPECIAL_COSTS = 10000;
	
	public static final int
		POWER_UP_FADE_TIME = POWER_UP_DURATION / 4;
	
	public static final double
		FOCAL_PNT_X_LEFT = 39,
		FOCAL_PNT_X_RIGHT = 83,
		FOCAL_PNT_Y_EXP = 44,
		FOCAL_PNT_Y_POS = 56;
	
	static final int
		GOLIATH_PLATING_STRENGTH = 2,
		STANDARD_GOLIATH_COSTS = 75000,
		NO_COLLISION_HEIGHT = 6;
	
	static final float
		ENEMY_MISSILE_DAMAGE_FACTOR = 0.5f,
		STANDARD_MISSILE_DAMAGE_FACTOR = 1.0f;
	
	private static final int
		RECENT_DAMAGE_TIME = 50,        // Zeitrate in der die Lebenspunktleiste nach Kollisionen blinkt
		SLOW_TIME = 100,
		FIRE_RATE_POWERUP_LEVEL = 3,    // so vielen zusätzlichen Upgrades der Feuerrate entspricht die temporäre Steigerung der Feuerrate durch das entsprechende PowerUp
		STATIC_CHARGE_ENERGY_DRAIN = 45,              // Energieabzug für den Helikopter bei Treffer
		STANDARD_PLATING_STRENGTH = 1,
		SLOW_ROTATIONAL_SPEED = 7,
		FAST_ROTATIONAL_SPEED = 12,
		DAY_BONUS_FACTOR = 60,
		NIGHT_BONUS_FACTOR = 90,
		SPOTLIGHT_COSTS = 35000;
	
	private static final float
		NOSEDIVE_SPEED = 12f,            // Geschwindigkeit des Helikopters bei Absturz
		INVULNERABILITY_PROTECTION_FACTOR = 1.0f - INVULNERABILITY_DAMAGE_REDUCTION / 100.0f,
		STANDARD_PROTECTION_FACTOR = 1.0f,
		STANDARD_BASE_PROTECTION_FACTOR = 1.0f,
		PLATING_MULTIPLIER = 1.3f;
	
	public static final Point
		HELICOPTER_MENU_PAINT_POS = new Point(692, 360);
	
	private static final Dimension
		HELICOPTER_SIZE = new Dimension(122, 69);
	
	private static final Rectangle
		INITIAL_BOUNDS = new Rectangle(	150,
										GROUND_Y
											- HELICOPTER_SIZE.height
											- NO_COLLISION_HEIGHT,
										HELICOPTER_SIZE.width,
										HELICOPTER_SIZE.height);
	
	public int
		missileDrive,                        // Geschwindigkeit [Pixel pro Frame] der Raketen
		currentBaseFirepower,                // aktuelle Feuerkraft unter Berücksichtigung des Upgrade-Levels und des eventuell erforschten Jumbo-Raketen-Spezial-Upgrades
		platingDurabilityFactor = STANDARD_PLATING_STRENGTH,    // SpezialUpgrade; = 2, wenn erforscht, sonst = 1; Faktor, der die Standardpanzerung erhöht
		numberOfCannons = 1,                // Anzahl der Kanonen; mögliche Werte: 1, 2 und 3
		recentDamageTimer;                    // aktiv, wenn Helicopter kürzlich Schaden genommen hat; für Animation der HitPoint-Leiste
	
	// für die Spielstatistik
	
	
	private final Map<StandardUpgradeType, Integer>
		levelsOfStandardUpgrades = new EnumMap<>(StandardUpgradeType.class);  // Upgrade-Level aller 6 StandardUpgrades
	
	public ScoreScreenTimes
		scoreScreenTimes = new ScoreScreenTimes();    // Zeit, die bis zum Besiegen jedes einzelnen der 5 Boss-Gegner vergangen ist
	
	public float
		rotorSystem;                        // legt die aktuelle Geschwindigkeit des Helikopters fest
	
	public int
		rotorPosition;                        // Stellung des Helikopter-Hauptrotors für alle Klassen; genutzt für die StartScreen-Animation
	
	float
		spellCosts;                            // Energiekosten für die Nutzung des Energie-Upgrades
	
	public boolean
		hasSpotlights,                        // = true: Helikopter hat Scheinwerfer
		hasPiercingWarheads,                // = true: Helikopter-Raketen werden mit Durchstoß-Sprengköpfen bestückt
		isActive,                            // = false: Helikopter ist nicht in Bewegung und kann auch nicht starten, Raketen abschießen, etc. (vor dem ersten Start oder nach Absturz = false)
		isDamaged,                            // = true: Helikopter hat einen Totalschaden erlitten
	// TODO kann eventuell genutzt werden, um Malen des Helicopters und Drehen des Propellers zu trennen
	isRotorSystemActive,                // = true: Propeller dreht sich / Helikopter fliegt
		isContinuousFireEnabled,            // = true: Dauerfeuer aktiv
		isMovingLeft,
		isPlayedWithCheats = true;            // = true: Spielstand kann in die Highscore übernommen werden, da keine cheats angewendet wurden
	
	public final Point
		destination = new Point();                // dorthin fliegt der Helikopter
	
	// TODO noch Phoenix auslagern
	public final Point
		priorTeleportLocation = new Point();    // nur für Phönix-Klasse: Aufenthaltsort vor Teleportation
	
	public boolean
		isSearchingForTeleportDestination;        // = true: es wird gerade der Zielort der Teleportation ausgewählt
	
	public int
		// nur für Phönix- und Kamaitachi-Klasse
		// TODO auslagern in Phönix- und Kamaitachi-Klasse
		bonusKills,                            // Anzahl der Kills, für den aktuellen MultiKill-Award
		bonusKillsMoney,                    // Gesamtverdienst am Abschuss aller Gegner innerhalb des aktuellen MultiKill-Awards ohne Bonus
		bonusKillsTimer;                    // reguliert die Zeit, innerhalb welcher Kills für den MultiKill-Award berücksichtigt werden
	
	final Battery
		battery = Battery.createFor(this.getType());
	
	final PowerUpController
		powerUpController = new PowerUpController(this);
	
	public final Point2D
		location = new Point2D.Float();            // exakter Aufenthaltsort
	
	final Point2D
		nextLocation = new Point2D.Float();
	
	public Enemy
		tractor;            // Referenz auf den Gegner, der den Helikopter mit einem Traktorstrahl festhält
	
	private int
		fireRateTimer,    // reguliert die Zeit [frames], die mind. vergehen muss, bis wieder geschossen werden kann
		timeBetweenTwoShots,// Zeit [frames], die mindestens verstreichen muss, bis wieder geschossen werden kann
		slowedTimer;        // reguliert die Verlangsamung des Helicopters durch gegnerische Geschosse
	
	private float
		speed,                // aktuelle Geschwindigkeit des Helikopters
		currentPlating;        // aktuelle Panzerung (immer <= maximale Panzerung)
	
	private boolean
		isCrashing;            // Helikopter befindet sich im Sturzflug

	public Helicopter()
	{
		this.paintBounds.setSize(HELICOPTER_SIZE);
		this.powerUpController.turnOfAllBoosters();
	}
	
	public void update(GameRessourceProvider gameRessourceProvider)
	{
		this.updateTimer();
		if (this.canRegenerateEnergy())
		{
			this.battery.recharge();
		}
		this.evaluateFire(gameRessourceProvider);
		this.move(gameRessourceProvider);
	}
	
	public boolean hasSpotlightsTurnedOn()
	{
		return this.hasSpotlights
			&& Events.timeOfDay == NIGHT
			&& WindowManager.window == GAME;
	}
	
	void updateTimer()
	{
		if (this.recentDamageTimer > 0)
		{
			this.recentDamageTimer--;
		}
		if (this.slowedTimer > 0)
		{
			this.slowedTimer--;
		}
		this.powerUpController.evaluatePowerUpActivationStates();
	}
	
	private void evaluateFire(GameRessourceProvider gameRessourceProvider)
	{
		if (this.isReadyForShooting())
		{
			this.shoot(gameRessourceProvider);
		}
		this.fireRateTimer++;
	}
	
	private boolean isReadyForShooting()
	{
		return this.isContinuousFireEnabled
			&& !this.isDamaged
			&& !this.isOnTheGround()
			&& this.fireRateTimer >= this.timeBetweenTwoShots;
	}
	
	void shoot(GameRessourceProvider gameRessourceProvider)
	{
		// TODO Code Duplizierungen auflösen
		if (this.hasPiercingWarheads)
		{
			Audio.play(Audio.launch2);
		} else
		{
			Audio.play(Audio.launch1);
		}
		this.fireRateTimer = 0;
		gameRessourceProvider.getGameStatisticsCalculator().incrementMissileCounterBy(this.numberOfCannons);
		
		boolean stunningMissile = isShootingStunningMissile();
		Missile sister = null;
		
		Map<CollectionSubgroupType, LinkedList<Missile>> missiles = gameRessourceProvider.getMissiles();
		if (this.numberOfCannons >= 1)
		{
			Iterator<Missile> iterator = missiles.get(INACTIVE)
										  .iterator();
			Missile missile;
			if (iterator.hasNext())
			{
				missile = iterator.next();
				iterator.remove();
			} else
			{
				missile = new Missile();
			}
			if (this.getType() == ROCH || this.getType() == OROCHI)
			{
				missile.sister[0] = null;
				missile.sister[1] = null;
				sister = missile;
			}
			missiles.get(ACTIVE)
					.add(missile);
			missile.launch(this, stunningMissile, 56);
		}
		if (this.numberOfCannons >= 2)
		{
			Iterator<Missile> iterator = missiles.get(INACTIVE)
										  .iterator();
			Missile missile;
			if (iterator.hasNext())
			{
				missile = iterator.next();
				iterator.remove();
			} else
			{
				missile = new Missile();
			}
			// TODO warum immer true
			if (sister != null && sister.sister != null &&
				(this.getType() == ROCH || this.getType() == OROCHI))
			{
				missile.sister[0] = sister;
				missile.sister[1] = null;
				sister.sister[0] = missile;
				sister = missile;
			}
			missiles.get(ACTIVE)
					.add(missile);
			missile.launch(this, stunningMissile, 28);
		}
		if (this.numberOfCannons >= 3)
		{
			Iterator<Missile> iterator = missiles.get(INACTIVE)
										  .iterator();
			Missile missile;
			if (iterator.hasNext())
			{
				missile = iterator.next();
				iterator.remove();
			} else
			{
				missile = new Missile();
			}
			// TODO warum immer true
			if (sister != null && sister.sister != null &&
				(this.getType() == ROCH || this.getType() == OROCHI))
			{
				missile.sister[0] = sister.sister[0];
				missile.sister[1] = sister;
				sister.sister[0].sister[1] = missile;
				sister.sister[1] = missile;
			}
			missiles.get(ACTIVE)
					.add(missile);
			missile.launch(this, stunningMissile, 42);
		}
	}
	
	private void move(GameRessourceProvider gameRessourceProvider)
	{
		if (this.isOnTheGround())
		{
			this.isRotorSystemActive = false;
		}
		
		float
			nextX = (float) this.location.getX(),
			nextY = (float) this.location.getY();
		
		if (this.isCrashing)
		{
			nextY += NOSEDIVE_SPEED;
		} else if (this.isActive && this.tractor == null)
		{
			this.speed = (this.slowedTimer > 0) ? 1.5f : this.rotorSystem;
			float fraction = (float) (this.speed / this.location.distance(this.destination.x, this.destination.y));
			
			if (fraction < 1)
			{
				if (!(this.getMaxY() + NO_COLLISION_HEIGHT >= GROUND_Y
					&& this.destination.y >= GROUND_Y))
				{
					nextX += (float) (fraction * (this.destination.x - this.location.getX()) - 1);
				}
				nextY += (float) (fraction * (this.destination.y - this.location.getY()));
			} else
			{
				nextX = this.destination.x;
				nextY = this.destination.y;
			}
		}
		
		boolean isInTheAir = this.location.getY() != 407d;
		float lastX = (float) this.location.getX();
		
		this.nextLocation.setLocation(nextX, nextY);
		this.correctAndSetCoordinates();
		
		if (EnemyController.currentNumberOfBarriers > 0 && !this.isDamaged)
		{
			for (int i = 0; i < EnemyController.currentNumberOfBarriers; i++)
			{
				Enemy enemy = EnemyController.livingBarrier[i];
				enemy.lastTouchedSite = enemy.touchedSite;
				if (this.isLocationAdaptionApproved(enemy))
				{
					this.adaptPosTo(enemy);
					this.correctAndSetCoordinates();
					if (enemy.isStaticallyCharged())
					{
						enemy.startStaticDischarge(gameRessourceProvider.getExplosions(), this);
					}
				} else
				{
					enemy.setUntouched();
				}
				if (enemy.isUntouched())
				{
					enemy.untouchedCounter++;
					if (enemy.untouchedCounter > 2)
					{
						enemy.untouchedCounter = 0;
						enemy.isTouchingHelicopter = false;
					}
				} else
				{
					enemy.untouchedCounter = 0;
				}
			}
			for (int i = 0; i < EnemyController.currentNumberOfBarriers; i++)
			{
				EnemyController.livingBarrier[i].evaluatePosAdaption(this);
			}
		}
		
		if (this.isActive && this.tractor == null)
		{
			if (!this.isCrashing)
			{
				if (this.getMaxY() + NO_COLLISION_HEIGHT != GROUND_Y
					|| lastX != (float) this.location.getX())
				{
					this.isRotorSystemActive = true;
				}
				if (isInTheAir && !(this.location.getY() != 407d))
				{
					Audio.play(Audio.landing);
				}
			} else if (isInTheAir && this.location.getY() == 407d)
			{
				this.crashed(gameRessourceProvider.getExplosions());
			}
		}
		if (this.isRotorSystemActive)
		{
			this.rotatePropellerFast();
		}
		this.setPaintBounds();
	}
	
	boolean isShootingStunningMissile()
	{
		return false;
	}
	
	public boolean isLocationAdaptionApproved(Enemy enemy)
	{
		return enemy.isPushingHelicopter(this);
	}
	
	void adaptPosTo(Enemy enemy)
	{
		double
			x = this.getCenterX() - enemy.getCenterX(),
			y = this.getCenterY() - enemy.getCenterY(),
			pseudoAngle = (x / Calculations.ZERO_POINT.distance(x, y)),
			distance,
			localSpeed = enemy.hasUnresolvedIntersection ? this.speed : Double.MAX_VALUE;
		
		if (pseudoAngle > Calculations.ROOT05)
		{
			// Right
			// new pos x: enemy.getMaxX() + (this.moves_left ? 39 : 83)
			distance = enemy.getMaxX() + (this.isMovingLeft ? 39 : 83) - this.location.getX();
			this.nextLocation.setLocation(
				this.location.getX() + Math.min(distance, localSpeed),
				this.location.getY());
			enemy.setTouchedSiteToRight();
		} else if (pseudoAngle < -Calculations.ROOT05)
		{
			// Left
			// new pos x: enemy.x - this.getWidth() + (this.moves_left ? 39 : 83)
			distance = this.location.getX() - enemy.getX() + this.getWidth() - (this.isMovingLeft ? 39 : 83);
			this.nextLocation.setLocation(
				this.location.getX() - Math.min(distance, localSpeed),
				this.location.getY());
			enemy.setTouchedSiteToLeft();
		} else
		{
			if (this.getCenterY() > enemy.getCenterY())
			{
				// Bottom
				// new pos y: enemy.getMaxY() + 56
				distance = enemy.getMaxY() + 56 - this.location.getY();
				this.nextLocation.setLocation(
					this.location.getX(),
					this.location.getY() + Math.min(distance, localSpeed));
				enemy.setTouchedSiteToBottom();
			} else
			{
				// Top
				// new pos y: enemy.getY() - this.getHeight() + 56
				distance = this.location.getY() - enemy.getY() + this.getHeight() - 56;
				this.nextLocation.setLocation(
					this.location.getX(),
					this.location.getY() - Math.min(distance, localSpeed));
				enemy.setTouchedSiteToTop();
			}
			if (this.tractor != null)
			{
				this.stopTractor();
			}
		}
	}
	
	void correctAndSetCoordinates()
	{
		this.location.setLocation
						 (
							 Math.max(40, Math.min(1024, this.nextLocation.getX())),
							 Math.max(32, Math.min(407, this.nextLocation.getY()))
						 );
		this.setBounds();
	}
	
	// TODO in Methoden auslagern
	void setBounds()
	{
		setBounds(this.location.getX()
				- (this.isMovingLeft
				? FOCAL_PNT_X_LEFT
				: FOCAL_PNT_X_RIGHT),
				this.location.getY() - FOCAL_PNT_Y_POS,
				this.getWidth(),
				this.getHeight());
	}
	
	public void initializeForNewGame()
	{
		for (StandardUpgradeType standardUpgradeType : StandardUpgradeType.getValues())
		{
			this.setUpgradeLevelOf(standardUpgradeType, this.getType()
															.getInitialUpgradeLevelFor(standardUpgradeType));
		}
		restorePlating();
		this.battery.restore();
		generalInitialization();
	}
	
	public void initializeFromSavegame(Savegame savegame)
	{
		this.restoreLastGameState(savegame);
		generalInitialization();
	}
	
	void generalInitialization()
	{
		this.setSpellCosts();
		this.fireRateTimer = this.timeBetweenTwoShots;
		this.placeAtStartpos();
		this.prepareForMission();
	}
	
	private void restoreLastGameState(Savegame savegame)
	{
		for (StandardUpgradeType standardUpgradeType : StandardUpgradeType.getValues())
		{
			Integer upgradeLevel = savegame.levelsOfStandardUpgrades.get(standardUpgradeType);
			this.setUpgradeLevelOf(standardUpgradeType, upgradeLevel);
		}
		this.hasSpotlights = savegame.spotlight;
		this.platingDurabilityFactor = savegame.platingDurabilityFactor;
		this.hasPiercingWarheads = savegame.hasPiercingWarheads;
		this.numberOfCannons = savegame.numberOfCannons;
		this.currentPlating = savegame.currentPlating;
		
		this.battery.upgradeTo(this.getUpgradeLevelOf(ENERGY_ABILITY));
		this.battery.setCurrentCharge(savegame.currentEnergy);
		
		if (savegame.hasFifthSpecial)
		{
			this.obtainFifthSpecial();
		}
		
		Controller.getInstance().getGameStatisticsCalculator().restoreFrom(savegame);
		
		this.isPlayedWithCheats = savegame.wasCreatedThroughCheating;

		this.scoreScreenTimes = savegame.scoreScreenTimes;
	}
	
	public void reset()
	{
		// TODO ggf. muss einiges nicht mehr resettet werden, da immer ein neuer Helicopter erzeugt wird
		this.partialReset();
		this.placeAtStartpos();
		this.isDamaged = false;
		this.isPlayedWithCheats = false;
		Controller.getInstance().getGameStatisticsCalculator().resetCounterForHighscore();
		this.resetSpecialUpgrades();
		this.scoreScreenTimes.clear();
	}
	
	public void resetStateGeneral()
	{
		this.inactivate();
		this.isCrashing = false;
		this.slowedTimer = 0;
		this.recentDamageTimer = 0;
		this.powerUpController.reset();
		this.resetRotorPosition();
		this.fireRateTimer = this.timeBetweenTwoShots;
	}
	
	private void resetSpecialUpgrades()
	{
		this.hasSpotlights = false;
		this.platingDurabilityFactor = STANDARD_PLATING_STRENGTH;
		this.hasPiercingWarheads = false;
		this.numberOfCannons = 1;
		this.resetFifthSpecial();
	}
	
	abstract void resetFifthSpecial();
	
	public void repair()
	{
		Audio.play(Audio.cash);
		Controller.getInstance().getGameStatisticsCalculator().incrementNumberOfRepairs();
		this.isDamaged = false;
		this.isCrashing = false;
		this.restorePlating();
		this.setRelativePlatingDisplayColor();
	}
	
	public void obtainAllUpgrades()
	{
		for (StandardUpgradeType standardUpgradeType : StandardUpgradeType.getValues())
		{
			this.maximizeUpgrade(standardUpgradeType);
		}
		this.platingDurabilityFactor = GOLIATH_PLATING_STRENGTH;
		this.hasPiercingWarheads = true;
		this.getMaximumNumberOfCannons();
		makeAdjustmentsForCheatedUpgrades();
	}
	
	private void maximizeUpgrade(StandardUpgradeType standardUpgradeType)
	{
		this.setUpgradeLevelOf(standardUpgradeType, this.getType()
														.getMaximumUpgradeLevelFor(standardUpgradeType));
	}
	
	void getMaximumNumberOfCannons()
	{
		this.numberOfCannons = 2;
	}
	
	private void makeAdjustmentsForCheatedUpgrades()
	{
		this.restorePlating();
		this.battery.restore();
		this.isDamaged = false;
		Window.updateRepairShopButtons(this);
		this.isPlayedWithCheats = true;
	}
	
	public void obtainSomeUpgrades()
	{
		this.hasSpotlights = true;
		this.obtainFifthSpecial();
		for (StandardUpgradeType standardUpgradeType : StandardUpgradeType.getValues())
		{
			if (this.getUpgradeLevelOf(standardUpgradeType) < EXTORTIONATE.getMaximumUpgradeLevel())
			{
				this.setUpgradeLevelOf(standardUpgradeType, EXTORTIONATE.getMaximumUpgradeLevel());
			}
		}
		makeAdjustmentsForCheatedUpgrades();
	}
	
	public boolean hasSomeUpgrades()
	{
		for (StandardUpgradeType standardUpgradeType : StandardUpgradeType.getValues())
		{
			if (this.getUpgradeLevelOf(standardUpgradeType) < EXTORTIONATE.getMaximumUpgradeLevel())
			{
				return false;
			}
		}
		if (!this.hasSpotlights)
		{
			return false;
		} else return this.hasFifthSpecial();
	}
	
	abstract public boolean hasFifthSpecial();
	
	abstract public void obtainFifthSpecial();
	
	private boolean hasAllSpecialUpgrades()
	{
		return this.hasSpotlights
			&& this.hasGoliathPlating()
			&& this.hasPiercingWarheads
			&& this.hasAllCannons()
			&& hasFifthSpecial();
	}
	
	public boolean hasGoliathPlating()
	{
		return this.platingDurabilityFactor == GOLIATH_PLATING_STRENGTH;
	}
	
	public boolean hasAllCannons()
	{
		return this.numberOfCannons == 2;
	}
	
	public boolean hasAllUpgrades()
	{
		for (StandardUpgradeType standardUpgradeType : StandardUpgradeType.getValues())
		{
			if (!this.hasMaximumUpgradeLevelFor(standardUpgradeType))
			{
				return false;
			}
		}
		return hasAllSpecialUpgrades();
	}
	
	public void rotatePropellerSlow()
	{
		this.rotatePropeller(SLOW_ROTATIONAL_SPEED);
	}
	
	public void rotatePropellerFast()
	{
		this.rotatePropeller(FAST_ROTATIONAL_SPEED);
	}
	
	private void rotatePropeller(int rotationalSpeed)
	{
		this.rotorPosition = (this.rotorPosition + rotationalSpeed) % 360;
	}
	
	public void placeAtStartpos()
	{
		this.isMovingLeft = false;
		setBounds(INITIAL_BOUNDS);
		this.location.setLocation(this.getX() + FOCAL_PNT_X_RIGHT,
			INITIAL_BOUNDS.y + FOCAL_PNT_Y_POS);
		this.setPaintBounds();
	}
	
	public void stopTractor()
	{
		Audio.tractorBeam.stop();
		this.tractor.stopTractor();
		this.tractor = null;
	}
	
	public void crash()
	{
		this.isDamaged = true;
		this.isRotorSystemActive = false;
		this.battery.discharge();
		this.destination.setLocation(this.getX() + 40, 520);
		if (this.tractor != null)
		{
			this.stopTractor();
		}
		Controller.getInstance().getGameStatisticsCalculator().incrementNumberOfCrashes();
		if (this.location.getY() == 407d)
		{
			this.crashed(Controller.getInstance().getExplosions());
		} else
		{
			this.isCrashing = true;
		}
	}
	
	private void crashed(Map<CollectionSubgroupType, LinkedList<Explosion>> explosion)
	{
		this.isActive = false;
		this.powerUpController.startDecayOfAllActivePowerUps();
		if (Events.level < 51 && explosion != null)
		{
			Audio.play(Audio.explosion3);
			Explosion.start(explosion,
				this,
				(int) (this.getX()
					+ (this.isMovingLeft
					? FOCAL_PNT_X_LEFT
					: FOCAL_PNT_X_RIGHT)),
				(int) (this.getY() + FOCAL_PNT_Y_EXP),
				ORDINARY,
				false);
		}
		Events.isRestartWindowVisible = true;
		this.isCrashing = false;
	}
	
	public void takeMissileDamage()
	{
		this.currentPlating = Math.max(this.currentPlating - this.getProtectionFactor() * ENEMY_MISSILE_DAMAGE_FACTOR, 0f);
		this.startRecentDamageTimer();
		if (this.isDestinedToCrash())
		{
			this.crash();
		}
	}
	
	public boolean hasDestroyedPlating()
	{
		return this.currentPlating <= 0;
	}
	
	void startRecentDamageTimer()
	{
		this.recentDamageTimer = RECENT_DAMAGE_TIME;
	}
	
	private void updateRotorSystem()
	{
		rotorSystem = this.getSpeed();
	}
	
	private void updateMissileDrive()
	{
		this.missileDrive = this.getMissileDrive();
	}
	
	void setSpellCosts()
	{
		// TODO sollte für die Battery gesetzt werden, hier und automatisch, Helicopter braucht spellCost evtl. nicht mehr
		this.spellCosts = this.getType()
							  .getSpellCosts();
	}

	public void setRelativePlatingDisplayColor()
	{
		Colorations.plating = Colorations.percentColor(this.getRelativePlating());
	}
	

	

	
	public void activate()
	{
		this.isActive = true;
		this.isRotorSystemActive = true;
	}
	
	public void inactivate()
	{
		this.isActive = false;
		this.isRotorSystemActive = false;
	}
	
	public void adjustFireRate()
	{
		adjustFireRate(false);
	}
	
	public void adjustFireRate(boolean poweredUp)
	{
	    // TODO überprüfen ob man direkt hier hasBoostedFireRate() nutzen kann und somit Parameter wegfallen kann
		this.timeBetweenTwoShots = this.getFireRate(poweredUp);
	}

	public int calculateSumOfFireRateBooster(boolean poweredUp)
	{
		return this.getUpgradeLevelOf(StandardUpgradeType.FIRE_RATE)
				+ (poweredUp ? FIRE_RATE_POWERUP_LEVEL : 0);
	}

	abstract public void updateUnlockedHelicopters();

	public void useReparationPowerUp()
	{
		Audio.play(Audio.cash);
		if(!this.hasMaximumPlating())
		{
			this.currentPlating
				= Math.min(
                    this.getMaximumPlating(),
					this.currentPlating + Math.max(1, this.missingPlating()/2));
		}
	}
    
    public boolean hasMaximumPlating()
    {
        return currentPlating >= this.getMaximumPlating();
    }
    
    public float kaboomDamage()
	{
		return Math.max(4, 2*this.currentPlating/3);
	}
    
    
    private void restorePlating()
    {
        this.currentPlating = this.getMaximumPlating();
    }
	
	public float getMaximumPlating()
	{
		return this.platingDurabilityFactor * this.getBasePlating();
	}
    
    public float getBasePlating()
    {
        return getBasePlating(this.getPlatingLevel());
    }
    
    private float getBasePlating(int platingLevel)
    {
        return PLATING_MULTIPLIER * PLATING.getMagnitude(platingLevel);
    }
    
    private int getPlatingLevel()
    {
        return this.getUpgradeLevelOf(PLATING);
    }
    
    private void updatePlating()
    {
        this.currentPlating += this.getLastPlatingDurabilityIncrease();
        this.setRelativePlatingDisplayColor();
    }
    
    public float getLastPlatingDurabilityIncrease()
    {
        return this.platingDurabilityFactor * (this.getBasePlating() - this.getPreviousBasePlating());
    }
    
    private float getPreviousBasePlating()
    {
        int previousPlatingLevel = this.getPlatingLevel() - 1;
        return this.getBasePlating(previousPlatingLevel);
    }
    
    public float getCurrentPlating()
    {
        return currentPlating;
    }
    
	public void receiveStaticCharge(float degree)
	{
		if(!this.isInvincible())
		{
            this.slowDown();
		    if(!this.hasUnlimitedEnergy())
            {
                float energyConsumption = degree * this.getStaticChargeEnergyDrain();
                this.battery.drain(energyConsumption);
            }
		}
	}
    
    void slowDown()
    {
        this.slowedTimer = SLOW_TIME;
    }
    
    float getStaticChargeEnergyDrain()
    {
        return STATIC_CHARGE_ENERGY_DRAIN;
    }
    
    public boolean canCollideWith(Enemy enemy)
	{
		return this.basicCollisionRequirementsSatisfied(enemy) && enemy.canCollide();
	}

	public boolean basicCollisionRequirementsSatisfied(Enemy enemy)
	{
		return !this.isDamaged
				&& enemy.isOnScreen()
				&& enemy.intersects(this.getBounds());
	}
	
	public float getProtectionFactor()
	{
		return this.isInvincible()
				? INVULNERABILITY_PROTECTION_FACTOR
				: STANDARD_PROTECTION_FACTOR;
	}
	
    public void becomesCenterOf(Explosion exp)
	{
		exp.ellipse.setFrameFromCenter(
			this.getX() + (this.isMovingLeft ? FOCAL_PNT_X_LEFT : FOCAL_PNT_X_RIGHT),
			this.getY() + FOCAL_PNT_Y_EXP,
			this.getX() + (this.isMovingLeft ? FOCAL_PNT_X_LEFT : FOCAL_PNT_X_RIGHT),
			this.getY() + FOCAL_PNT_Y_EXP);
	}
 
	public boolean isOnTheGround()
	{
		return this.getMaxY() + NO_COLLISION_HEIGHT == GROUND_Y;
	}

	public void turnAround()
	{
		this.isMovingLeft = !this.isMovingLeft;
		this.setBounds();
	}
 
	public void beAffectedByCollisionWith(Enemy enemy,
										  GameRessourceProvider gameRessourceProvider,
										  boolean playCollisionSound)
	{
		this.startRecentDamageEffect(enemy);
		if(playCollisionSound)
		{
			Audio.play(enemy.type == KABOOM
					? Audio.explosion4
					: this.getCollisionAudio());
		}
		this.slowedTimer = 2;
		this.currentPlating = Math.max(0, this.currentPlating - enemy.collisionDamage(this));
	}
    
    void startRecentDamageEffect(Enemy enemy)
    {
        this.startRecentDamageTimer();
    }
    
    AudioClip getCollisionAudio()
    {
        return Audio.explosion1;
    }
    
    public boolean hasPerformedTeleportKill()
	{
		return this.bonusKillsTimer > 0;
	}

	public abstract HelicopterType getType();
	
	public void installGoliathPlating()
	{
		this.platingDurabilityFactor = GOLIATH_PLATING_STRENGTH;
		this.currentPlating += this.getBasePlating();
		this.setRelativePlatingDisplayColor();
	}
	
	public int getGoliathCosts()
	{
		return STANDARD_GOLIATH_COSTS;
	}
	
	public int getPiercingWarheadsCosts()
	{
		return STANDARD_SPECIAL_COSTS;
	}
	
	public void installPiercingWarheads()
	{
		this.hasPiercingWarheads = true;
	}

	public boolean canBeStoppedByTractorBeam() {
		return this.tractor == null;
	}
	
	public float getMissileDamageFactor()
	{
		return STANDARD_MISSILE_DAMAGE_FACTOR;
	}

	public ExplosionType getCurrentExplosionTypeOfMissiles(boolean stunningMissile)
	{
		return ORDINARY;
	}
	
	public boolean canImmobilizePowerUp()
	{
		return false;
	}
	
	public boolean canDetectCloakedVessels()
	{
		return false;
	}
	
	public void setCurrentBaseFirepower()
	{
		this.currentBaseFirepower = (int)(this.getMissileDamageFactor() * this.getFirepower());
	}
	
	public boolean isFifthSpecialOnMaximumStrength()
	{
		return true;
	}
	
	public boolean canBeHit()
	{
		return true;
	}
	
	public void initMenuEffect(int i)
	{
		Audio.playSpecialSound(this.getType());
	}
	
	public void updateMenuEffect()
	{
		this.rotatePropellerSlow();
		if(Window.effectTimer[this.getType().ordinal()] == 1)
		{
			this.stopMenuEffect();
		}
	}
	
	abstract public void stopMenuEffect();
    
    public boolean isTakingKaboomDamageFrom(Enemy enemy)
    {
        return enemy.isKaboomDamageDealer();
    }
    
    public float getBaseDamage()
    {
        return this.currentBaseFirepower;
    }
    
    public void rightMouseButtonReleaseAction(MouseEvent mouseEvent){}
    
    public boolean canObtainCollisionReward()
    {
        return false;
    }
    
    public int getBonusFactor()
    {
        return this.hasSpotlights ? NIGHT_BONUS_FACTOR : DAY_BONUS_FACTOR;
    }
    
    public void resetRotorPosition()
    {
        this.rotorPosition = 0;
    }
    
    public float getBaseProtectionFactor(boolean canExplode)
    {
        return STANDARD_BASE_PROTECTION_FACTOR;
    }
    
    public String getTypeSpecificDebuggingOutput()
	{
		return "";
	}
	
	public abstract void resetStateTypeSpecific();
    
    public void prepareForMission()
    {
        this.resetRotorPosition();
    }
    
    public boolean deservesMantisReward(long missileLaunchingTime)
    {
        return false;
    }

	public void receiveRewardFor(Enemy enemy, Missile missile, boolean beamKill)
	{
		Events.updateFinance(enemy, this);
		this.typeSpecificRewards(enemy, missile, beamKill);
		Window.moneyDisplayTimer = Events.START;
	}

	public void typeSpecificRewards(Enemy enemy, Missile missile, boolean beamKill) {}
    
    public void levelUpEffect(int previousLevel){}
    
    public PriceLevel getPriceLevelFor(StandardUpgradeType standardUpgradeType)
    {
        return this.getType().getPriceLevelFor(standardUpgradeType);
    }
	   
    private float getSpeed()
    {
        return ROTOR_SYSTEM.getMagnitude(this.getUpgradeLevelOf(ROTOR_SYSTEM));
    }
    
    private int getMissileDrive()
    {
        return (int)MISSILE_DRIVE.getMagnitude(this.getUpgradeLevelOf(StandardUpgradeType.MISSILE_DRIVE));
    }
    
    private int getFirepower()
    {
        return (int)FIREPOWER.getMagnitude(this.getUpgradeLevelOf(FIREPOWER));
    }
    
    public int getEmpDamage()
    {
        return (int)FIREPOWER.getMagnitude(this.getUpgradeLevelOf(ENERGY_ABILITY));
    }
    
    private int getFireRate(boolean poweredUp)
    {
        return (int)FIRE_RATE.getMagnitude(calculateSumOfFireRateBooster(poweredUp));
    }
    
    boolean canRegenerateEnergy()
    {
        return !this.isDamaged;
    }
	
	public float getCurrentEnergy()
	{
		return this.battery.getCurrentCharge();
	}
	
	public float getMaximumEnergy()
	{
		return this.battery.getCapacity();
	}
	
	public float getRelativeEnergy()
	{
		return this.battery.getStateOfCharge();
	}
	
	public void restoreEnergy()
	{
		this.battery.restore();
	}
		  
    float getRegenerationRate()
    {
        return this.battery.getRegenerationRate();
    }
   
    protected void consumeSpellCosts()
    {
        this.battery.drain(this.getEffectiveSpellCosts());
    }
    
    protected float getEffectiveSpellCosts()
    {
        return this.hasUnlimitedEnergy() ? 0.0f : this.spellCosts;
    }
	   
    public boolean isEnergyAbilityActivatable()
    {
        return this.hasEnoughEnergyForAbility();
    }
    
    public boolean hasEnoughEnergyForAbility()
    {
        return this.battery.getCurrentCharge() >= this.spellCosts || this.hasUnlimitedEnergy();
    }
	
	public void boostEnergy()
	{
		this.battery.boostCharge();
	}
    
    public void updateEnergyAbility()
    {
        this.battery.upgradeTo(this.getUpgradeLevelOf(ENERGY_ABILITY));
    }
    
    public void tryToUseEnergyAbility(Controller controller)
    {
        if(this.isEnergyAbilityActivatable())
        {
            useEnergyAbility(controller);
        }
    }
    
    public abstract void useEnergyAbility(Controller controller);
    
    public int getUpgradeLevelOf(StandardUpgradeType standardUpgradeType)
	{
		return this.levelsOfStandardUpgrades.get(standardUpgradeType);
	}
    
    public void upgrade(StandardUpgradeType standardUpgradeType)
    {
        int currentLevelOfUpgrade = this.getUpgradeLevelOf(standardUpgradeType);
        this.setUpgradeLevelOf(standardUpgradeType, currentLevelOfUpgrade + 1);
    }
	
    public void setUpgradeLevelOf(StandardUpgradeType standardUpgradeType, Integer upgradeLevel)
    {
        this.levelsOfStandardUpgrades.put(standardUpgradeType, upgradeLevel);
        
        switch (standardUpgradeType)
        {
            case ROTOR_SYSTEM:
                this.updateRotorSystem();
                break;
            case MISSILE_DRIVE:
                this.updateMissileDrive();
                break;
            case PLATING:
                this.updatePlating();
                break;
            case FIREPOWER:
                this.setCurrentBaseFirepower();
                break;
            case FIRE_RATE:
                this.adjustFireRate();
                break;
            case ENERGY_ABILITY:
                this.updateEnergyAbility();
                break;
        }
    }
		
    public boolean hasMaximumUpgradeLevelFor(StandardUpgradeType standardUpgradeType)
	{
		return this.getUpgradeLevelOf(standardUpgradeType) >= this.getType().getMaximumUpgradeLevelFor(standardUpgradeType);
	}
	
	public int getUpgradeCostFor(StandardUpgradeType standardUpgradeType)
	{
		int upgradeLevel = this.getUpgradeLevelOf(standardUpgradeType);
		PriceLevel priceLevel = this.getPriceLevelFor(standardUpgradeType);
		int baseUpgradeCosts = priceLevel.getBaseUpgradeCosts(upgradeLevel);
        int additionalUpgradeCosts = this.getAdditionalCosts(standardUpgradeType, upgradeLevel);
		
		return baseUpgradeCosts + additionalUpgradeCosts;
	}
    
    private int getAdditionalCosts(StandardUpgradeType standardUpgradeType, int upgradeLevel)
    {
        return this.getType().getAdditionalCosts(standardUpgradeType, upgradeLevel);
    }
    
    public float missingPlating()
    {
        return this.getMaximumPlating() - this.getCurrentPlating();
    }
    
    public void destroyPlating()
    {
        this.currentPlating = 0f;
    }
    
    public float getRelativePlating()
    {
        return this.getCurrentPlating() / this.getMaximumPlating();
    }

    public boolean isDestinedToCrash()
    {
        return this.hasDestroyedPlating() && !this.isDamaged;
    }
    
    public boolean hasTimeRecordingMissiles()
    {
    	return false;
    }
	
	public boolean hasKillCountingMissiles()
	{
		return false;
	}
    
    public void inactivate(Map<CollectionSubgroupType, LinkedList<Missile>> missiles, Missile missile)
    {
        missiles.get(INACTIVE).add(missile);
    }

	public int getFifthSpecialCosts()
	{
		return CHEAP_SPECIAL_COSTS;
	}

	public int getSpotlightCosts()
	{
		return SPOTLIGHT_COSTS;
	}
    
    public Map<StandardUpgradeType, Integer> getLevelsOfStandardUpgrades()
    {
        return new EnumMap<>(this.levelsOfStandardUpgrades);
    }
    
    public boolean isCountingAsFairPlayedHelicopter()
    {
        return !this.isPlayedWithCheats || Events.IS_SAVE_GAME_SAVED_ANYWAY;
    }
	
	public Color getPrimaryHullColor()
	{
		return this.hasGoliathPlating()
			? this.getType().getPlatedPrimaryHullColor()
			: this.getType().getStandardPrimaryHullColor();
	}
	
	public Color getSecondaryHullColor()
	{
		return this.hasGoliathPlating()
			? this.getType().getPlatedSecondaryHullColor()
			: this.getType().getStandardSecondaryHullColor();
	}
	
	public int getLastCannonCost()
	{
		return STANDARD_SPECIAL_COSTS;
	}
	
	
	// Method for interacting with PowerUpController class
	public void startDecayOfAllCurrentBooster()
	{
		powerUpController.startDecayOfAllActivePowerUps();
	}
	
	public boolean isUnacceptablyBoostedForBossLevel()
	{
		return powerUpController.isAnyPowerUpForbiddenAtBossLevelActive();
	}
	
	public boolean isBoosted(PowerUpType powerUpType)
	{
		return powerUpController.isPowerUpActive(powerUpType);
	}
	
	public boolean hasTripleDamage()
	{
		return isBoosted(PowerUpType.TRIPLE_DAMAGE);
	}
	
	public boolean isInvincible()
	{
		return isBoosted(PowerUpType.INVINCIBLE);
	}
	
	public boolean hasUnlimitedEnergy()
	{
		return isBoosted(PowerUpType.UNLIMITED_ENERGY);
	}
	
	public boolean hasBoostedFireRate()
	{
		return isBoosted(PowerUpType.BOOSTED_FIRE_RATE);
	}
	
	public void turnOfInvincibility()
	{
		powerUpController.turnOfInvinciblePowerUp();
	}
	
	public void gainTripleDamagePermanently()
	{
		powerUpController.activateTripleDamagePowerUpPermanently();
	}
	
	public void gainInvincibilityPermanently()
	{
		powerUpController.activateInvinciblePowerUpPermanently();
	}
	
	public void restartPowerUpTimer(PowerUpType powerUpType)
	{
		powerUpController.restartPowerUpTimer(powerUpType);
	}
	
	public void switchPowerUpActivationState(Map<CollectionSubgroupType, LinkedList<PowerUp>> powerUps, PowerUpType powerUpType)
	{
		powerUpController.switchPowerUpActivationState(powerUps, powerUpType);
	}
	
	public void partialReset()
	{
		resetStateGeneral();
		resetStateTypeSpecific();
	}
	
	public void typeSpecificActionOn(GameRessourceProvider gameRessourceProvider, Enemy enemy)
	{
	}
}