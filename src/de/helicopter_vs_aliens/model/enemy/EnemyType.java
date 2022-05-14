package de.helicopter_vs_aliens.model.enemy;

import de.helicopter_vs_aliens.control.entities.GameEntityFactory;
import de.helicopter_vs_aliens.model.enemy.barrier.BigBarrier;
import de.helicopter_vs_aliens.model.enemy.barrier.DiggerBarrier;
import de.helicopter_vs_aliens.model.enemy.barrier.CloakedBarrier;
import de.helicopter_vs_aliens.model.enemy.barrier.Protector;
import de.helicopter_vs_aliens.model.enemy.barrier.PushingBarrier;
import de.helicopter_vs_aliens.model.enemy.barrier.ShieldingBarrier;
import de.helicopter_vs_aliens.model.enemy.barrier.ShootingBarrier;
import de.helicopter_vs_aliens.model.enemy.barrier.SmallBarrier;
import de.helicopter_vs_aliens.model.enemy.barrier.StunningBarrier;
import de.helicopter_vs_aliens.model.enemy.boss.BigShieldMaker;
import de.helicopter_vs_aliens.model.enemy.boss.Bodyguard;
import de.helicopter_vs_aliens.model.enemy.boss.FinalBoss;
import de.helicopter_vs_aliens.model.enemy.boss.FirstBoss;
import de.helicopter_vs_aliens.model.enemy.boss.FourthBoss;
import de.helicopter_vs_aliens.model.enemy.boss.FourthBossServant;
import de.helicopter_vs_aliens.model.enemy.boss.Healer;
import de.helicopter_vs_aliens.model.enemy.boss.SecondBoss;
import de.helicopter_vs_aliens.model.enemy.boss.SecondBossServant;
import de.helicopter_vs_aliens.model.enemy.boss.SmallShieldMaker;
import de.helicopter_vs_aliens.model.enemy.boss.ThirdBoss;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.AmbushingEnemy;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.BatchwiseFlyingEnemy;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.CallbackEnemy;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.CapturingEnemy;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.Carrier;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.ChaoticallyFlyingEnemy;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.CloakedEnemy;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.CrazyEnemy;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.Dodger;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.EscapedSpeeder;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.Freighter;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.Kaboom;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.LonelySpeeder;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.LoopingEnemy;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.Rock;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.Runabout;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.Shooter;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.SinusoidallyFlyingEnemy;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.SmallCruiser;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.TeleportingEnemy;
import de.helicopter_vs_aliens.model.enemy.basicEnemy.TinyVessel;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public enum EnemyType implements GameEntityFactory<Enemy>
{
    // Standard-Gegner
    TINY(TinyVessel::new, TinyVessel.class, EnemyModelType.TIT, 1, 2, 110), //Level 1
    SMALL(SmallCruiser::new, SmallCruiser.class, EnemyModelType.TIT, 2, 3, 125), // Level 3
    RUNABOUT(Runabout::new, Runabout.class, EnemyModelType.TIT,  2, 2, 100), // level 5
    FREIGHTER(Freighter::new, Freighter.class, EnemyModelType.CARGO,  4, 25, 145), // Level 7
    BATCHWISE(BatchwiseFlyingEnemy::new, BatchwiseFlyingEnemy.class, EnemyModelType.TIT,  6, 16, 130), // Level 11
    SINUS(SinusoidallyFlyingEnemy::new, SinusoidallyFlyingEnemy.class, EnemyModelType.TIT, 6, 6, 110), // Level 13
    DODGER(Dodger::new, Dodger.class, EnemyModelType.TIT,  9, 24, 170), // Level 16
    CHAOTIC(ChaoticallyFlyingEnemy::new, ChaoticallyFlyingEnemy.class, EnemyModelType.TIT,  11, 22, 125), // Level 21
    CALLBACK(CallbackEnemy::new, CallbackEnemy.class, EnemyModelType.TIT,  10, 30, 95), // Level 24
    SHOOTER(Shooter::new, Shooter.class, EnemyModelType.CARGO,  12, 60, 80), // Level 26
    CLOAK(CloakedEnemy::new, CloakedEnemy.class, EnemyModelType.CARGO,  16, 100, 85), // Level 31
    LONELY_SPEEDER(LonelySpeeder::new, LonelySpeeder.class, EnemyModelType.TIT,  14, 26, 70), // Level 35
    CARRIER(Carrier::new, Carrier.class, EnemyModelType.CARGO,  19, 450, 165), // Level 36
    CRAZY(CrazyEnemy::new, CrazyEnemy.class, EnemyModelType.TIT,  22, 140, 115), // Level 37
    AMBUSH(AmbushingEnemy::new, AmbushingEnemy.class, EnemyModelType.TIT,  30, 150, 95), // Level 41
    LOOPING(LoopingEnemy::new, LoopingEnemy.class, EnemyModelType.TIT,  30, 330, 105), // Level 43
    CAPTURING(CapturingEnemy::new, CapturingEnemy.class, EnemyModelType.TIT,  30, 520, 115), // Level 45
    TELEPORTING(TeleportingEnemy::new, TeleportingEnemy.class, EnemyModelType.CARGO,  35, 500, 130), // Level 46
    
    // Boss-Gegner
    BOSS_1(FirstBoss::new, FirstBoss.class, EnemyModelType.TIT,  75, 225, 275),
    BOSS_2(SecondBoss::new, SecondBoss.class, EnemyModelType.CARGO,  100, 500, 250),
    BOSS_2_SERVANT(SecondBossServant::new, SecondBossServant.class, EnemyModelType.TIT,  5, 15, 65),
    BOSS_3(ThirdBoss::new, ThirdBoss.class, EnemyModelType.TIT,  500, 1750, 250),
    BOSS_4(FourthBoss::new, FourthBoss.class, EnemyModelType.TIT,  1250, 10000, 250),
    BOSS_4_SERVANT(FourthBossServant::new, FourthBossServant.class, EnemyModelType.TIT,  1, 100, 85),
    FINAL_BOSS(FinalBoss::new, FinalBoss.class, EnemyModelType.TIT,  5000, 25000, 450),
    SMALL_SHIELD_MAKER(SmallShieldMaker::new, SmallShieldMaker.class, EnemyModelType.TIT,  55, 3000, 125),
    BIG_SHIELD_MAKER(BigShieldMaker::new, BigShieldMaker.class, EnemyModelType.TIT,  80, 4250, 145),
    BODYGUARD(Bodyguard::new, Bodyguard.class, EnemyModelType.TIT,  150, 7500, 225),
    HEALER(Healer::new, Healer.class, EnemyModelType.CARGO,  65, 3500, 115),
    PROTECTOR(Protector::new, Protector.class, EnemyModelType.BARRIER, 25, Integer.MAX_VALUE, 90),
    
    // Hindernisse (Barrier)
    SMALL_BARRIER(SmallBarrier::new, SmallBarrier.class, EnemyModelType.BARRIER, 4, Integer.MAX_VALUE, 65), // Level 2
    BIG_BARRIER(BigBarrier::new, BigBarrier.class, EnemyModelType.BARRIER, 4, Integer.MAX_VALUE, 150), // Level 6
    STUNNING_BARRIER(StunningBarrier::new, StunningBarrier.class, EnemyModelType.BARRIER, 5, Integer.MAX_VALUE, 65), // Level 12
    PUSHING_BARRIER(PushingBarrier::new, PushingBarrier.class, EnemyModelType.BARRIER, 6, Integer.MAX_VALUE, 105), // Level 15
    SHOOTING_BARRIER(ShootingBarrier::new, ShootingBarrier.class, EnemyModelType.BARRIER, 7, Integer.MAX_VALUE, 85), // Level 18
    BURROWING_BARRIER(DiggerBarrier::new, DiggerBarrier.class, EnemyModelType.BARRIER, 9, Integer.MAX_VALUE, 80), // Level 32
    SHIELDING_BARRIER(ShieldingBarrier::new, ShieldingBarrier.class, EnemyModelType.BARRIER, 4, Integer.MAX_VALUE, 80), // Level 42
    CLOAKED_BARRIER(CloakedBarrier::new, CloakedBarrier.class, EnemyModelType.BARRIER, 15, Integer.MAX_VALUE, 100), // Level 44
    
    // Hindernis (Rock-Enemy)
    ROCK(Rock::new, Rock.class, EnemyModelType.CARGO, 0, 2, 300),
    
    // sonstige Gegner
    KABOOM(Kaboom::new, Kaboom.class, EnemyModelType.TIT,0, Short.MAX_VALUE, 120),
    
    ESCAPED_SPEEDER(EscapedSpeeder::new, EscapedSpeeder.class, EnemyModelType.TIT, 14, 26, 70);
    
    
    private static final List<EnemyType>
        VALUES = List.of(values());
    
    private final static Set<EnemyType>
        BOSS_TYPES = Collections.unmodifiableSet(EnumSet.range(BOSS_1, PROTECTOR)),
        DEFAULT_TYPES = Collections.unmodifiableSet(EnumSet.range(TINY, TELEPORTING)),
        FINAL_BOSS_SERVANT_TYPES = Collections.unmodifiableSet(EnumSet.range(SMALL_SHIELD_MAKER, PROTECTOR)),
        BARRIERS = Collections.unmodifiableSet(EnumSet.range(SMALL_BARRIER, CLOAKED_BARRIER)),
        CLOAKABLE_AS_MINI_BOSS_TYPES = Collections.unmodifiableSet(EnumSet.range(LONELY_SPEEDER, TELEPORTING));
    
    private static final float
        BOUNTY_MULTIPLIER = 7.5f;
    
    
    private final Supplier<? extends Enemy>
        instanceSupplier;
    
    private final Class<? extends Enemy>
        enemyClass;
    
    private final EnemyModelType
        model;
    
    private final int
        strength; // Stärke des Gegners; bestimmt die Höhe der Belohnung bei Abschuss
    
    private final int
        hitPoints;
    
    private final int
        width;
    
    EnemyType(Supplier<? extends Enemy> instanceSupplier, Class<? extends Enemy> enemyClass, EnemyModelType enemyModelType, int strength, int hitPoints, int width)
    {
        this.instanceSupplier = instanceSupplier;
        this.enemyClass = enemyClass;
        this.model = enemyModelType;
        this.strength = strength;
        this.hitPoints = hitPoints;
        this.width = width;
    }
    
    static Set<EnemyType> getFinalBossServantTypes()
    {
        return FINAL_BOSS_SERVANT_TYPES;
    }
    
    static Set<EnemyType> getBarrierTypes()
    {
        return BARRIERS;
    }
    
    public boolean isMajorBoss()
    {
        return BOSS_TYPES.contains(this);
    }
    
    public boolean isFinalBossServant()
    {
        return FINAL_BOSS_SERVANT_TYPES.contains(this);
    }
    
    boolean isMinorServant()
    {
        return this == BOSS_2_SERVANT || this == BOSS_4_SERVANT;
    }
    
    boolean isServant()
    {
        return	this.isMinorServant() || this.isFinalBossServant();
    }
    
    boolean isMainBoss()
    {
        return this.isMajorBoss() && !this.isServant();
    }
    
    public boolean isShieldMaker()
    {
        return this  == SMALL_SHIELD_MAKER || this == BIG_SHIELD_MAKER;
    }
    
    public boolean isSuitableMiniBoss()
    {
        return DEFAULT_TYPES.contains(this) && !(this == TINY);
    }
    
    public boolean isCloakableAsMiniBoss()
    {
        return CLOAKABLE_AS_MINI_BOSS_TYPES.contains(this);
    }
    
    public int getStrength()
    {
        return this.strength;
    }
    
    public static List<EnemyType> getValues()
    {
        return VALUES;
    }
    
    @Override
    public Enemy makeInstance()
    {
        Enemy enemy = instanceSupplier.get();
        enemy.type = this;
        return enemy;
    }
    
    @Override
    public Class<? extends Enemy> getCorrespondingClass()
    {
        return enemyClass;
    }
    
    public int getBounty()
    {
        return (int)(BOUNTY_MULTIPLIER * this.getStrength());
    }
    
    public int getHitPoints()
    {
        return hitPoints;
    }
    
    public int getWidth()
    {
        return width;
    }
    
    public EnemyModelType getModel()
    {
        return model;
    }
}