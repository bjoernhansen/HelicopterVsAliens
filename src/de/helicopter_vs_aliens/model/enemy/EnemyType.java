package de.helicopter_vs_aliens.model.enemy;

import de.helicopter_vs_aliens.control.entities.GameEntityFactory;
import de.helicopter_vs_aliens.model.enemy.barrier.BigBarrier;
import de.helicopter_vs_aliens.model.enemy.barrier.BurrowingBarrier;
import de.helicopter_vs_aliens.model.enemy.barrier.CloakedBarrier;
import de.helicopter_vs_aliens.model.enemy.boss.Protector;
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
    TINY(TinyVessel::new, TinyVessel.class, 1, 2), //Level 1
    SMALL(SmallCruiser::new, SmallCruiser.class, 2, 3), // Level 3
    RUNABOUT(Runabout::new, Runabout.class, 2, 2), // level 5
    FREIGHTER(Freighter::new, Freighter.class, 4, 25), // Level 7
    BATCHWISE(BatchwiseFlyingEnemy::new, BatchwiseFlyingEnemy.class, 6, 16), // Level 11
    SINUS(SinusoidallyFlyingEnemy::new, SinusoidallyFlyingEnemy.class, 6, 6), // Level 13
    DODGER(Dodger::new, Dodger.class, 9, 24), // Level 16
    CHAOTIC(ChaoticallyFlyingEnemy::new, ChaoticallyFlyingEnemy.class, 11, 22), // Level 21
    CALLBACK(CallbackEnemy::new, CallbackEnemy.class, 10, 30), // Level 24
    SHOOTER(Shooter::new, Shooter.class, 12, 60), // Level 26
    CLOAK(CloakedEnemy::new, CloakedEnemy.class, 16, 100), // Level 31
    LONELY_SPEEDER(LonelySpeeder::new, LonelySpeeder.class, 14, 26), // Level 35
    CARRIER(Carrier::new, Carrier.class, 19, 450), // Level 36
    CRAZY(CrazyEnemy::new, CrazyEnemy.class, 22, 140), // Level 37
    AMBUSH(AmbushingEnemy::new, AmbushingEnemy.class, 30, 150), // Level 41
    LOOPING(LoopingEnemy::new, LoopingEnemy.class, 30, 330), // Level 43
    CAPTURING(CapturingEnemy::new, CapturingEnemy.class, 30, 520), // Level 45
    TELEPORTING(TeleportingEnemy::new, TeleportingEnemy.class, 35, 500), // Level 46
    
    // Boss-Gegner
    BOSS_1(FirstBoss::new, FirstBoss.class, 75, 225),
    BOSS_2(SecondBoss::new, SecondBoss.class, 100, 500),
    BOSS_2_SERVANT(SecondBossServant::new, SecondBossServant.class, 5, 15),
    BOSS_3(ThirdBoss::new, ThirdBoss.class, 500, 1750),
    BOSS_4_SERVANT(FourthBossServant::new, FourthBossServant.class, 1, 100),
    BOSS_4(FourthBoss::new, FourthBoss.class, 1250, 10000),
    FINAL_BOSS(FinalBoss::new, FinalBoss.class, 5000, 25000),
    SMALL_SHIELD_MAKER(SmallShieldMaker::new, SmallShieldMaker.class, 55, 3000),
    BIG_SHIELD_MAKER(BigShieldMaker::new, BigShieldMaker.class, 80, 4250),
    BODYGUARD(Bodyguard::new, Bodyguard.class, 150, 7500),
    HEALER(Healer::new, Healer.class, 65, 3500),
    PROTECTOR(Protector::new, Protector.class, 25, Integer.MAX_VALUE),
    
    // Hindernisse (Barrier)
    SMALL_BARRIER(SmallBarrier::new, SmallBarrier.class, 4, Integer.MAX_VALUE), // Level 2
    BIG_BARRIER(BigBarrier::new, BigBarrier.class, 4, Integer.MAX_VALUE), // Level 6
    STUNNING_BARRIER(StunningBarrier::new, StunningBarrier.class, 5, Integer.MAX_VALUE), // Level 12
    PUSHING_BARRIER(PushingBarrier::new, PushingBarrier.class, 6, Integer.MAX_VALUE), // Level 15
    SHOOTING_BARRIER(ShootingBarrier::new, ShootingBarrier.class, 7, Integer.MAX_VALUE), // Level 18
    BURROWING_BARRIER(BurrowingBarrier::new, BurrowingBarrier.class, 9, Integer.MAX_VALUE), // Level 32
    SHIELDING_BARRIER(ShieldingBarrier::new, ShieldingBarrier.class, 4, Integer.MAX_VALUE), // Level 42
    CLOAKED_BARRIER(CloakedBarrier::new, CloakedBarrier.class, 15, Integer.MAX_VALUE), // Level 44
    
    // Hindernis (Rock-Enemy)
    ROCK(Rock::new, Rock.class, 0, 2),
    
    // sonstige Gegner
    KABOOM(Kaboom::new, Kaboom.class, 0, Integer.MAX_VALUE),
    
    ESCAPED_SPEEDER(EscapedSpeeder::new, EscapedSpeeder.class, 14, 26);
    
    
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
    
    private final int
        strength; // Stärke des Gegners; bestimmt die Höhe der Belohnung bei Abschuss
    
    private final int
        hitPoints;
    
    EnemyType(Supplier<? extends Enemy> instanceSupplier, Class<? extends Enemy> enemyClass, int strength, int hitPoints)
    {
        this.instanceSupplier = instanceSupplier;
        this.enemyClass = enemyClass;
        this.strength = strength;
        this.hitPoints = hitPoints;
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
}