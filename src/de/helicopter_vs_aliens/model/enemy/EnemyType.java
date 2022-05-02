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
import de.helicopter_vs_aliens.model.enemy.defaultEnemy.AmbushingEnemy;
import de.helicopter_vs_aliens.model.enemy.defaultEnemy.BatchwiseFlyingEnemy;
import de.helicopter_vs_aliens.model.enemy.defaultEnemy.CallbackEnemy;
import de.helicopter_vs_aliens.model.enemy.defaultEnemy.CapturingEnemy;
import de.helicopter_vs_aliens.model.enemy.defaultEnemy.Carrier;
import de.helicopter_vs_aliens.model.enemy.defaultEnemy.ChaoticallyFlyingEnemy;
import de.helicopter_vs_aliens.model.enemy.defaultEnemy.CloakedEnemy;
import de.helicopter_vs_aliens.model.enemy.defaultEnemy.CrazyEnemy;
import de.helicopter_vs_aliens.model.enemy.defaultEnemy.Dodger;
import de.helicopter_vs_aliens.model.enemy.defaultEnemy.EscapedSpeeder;
import de.helicopter_vs_aliens.model.enemy.defaultEnemy.Freighter;
import de.helicopter_vs_aliens.model.enemy.defaultEnemy.Kaboom;
import de.helicopter_vs_aliens.model.enemy.defaultEnemy.LonelySpeeder;
import de.helicopter_vs_aliens.model.enemy.defaultEnemy.LoopingEnemy;
import de.helicopter_vs_aliens.model.enemy.defaultEnemy.Rock;
import de.helicopter_vs_aliens.model.enemy.defaultEnemy.Runabout;
import de.helicopter_vs_aliens.model.enemy.defaultEnemy.Shooter;
import de.helicopter_vs_aliens.model.enemy.defaultEnemy.SinusoidallyFlyingEnemy;
import de.helicopter_vs_aliens.model.enemy.defaultEnemy.SmallCruiser;
import de.helicopter_vs_aliens.model.enemy.defaultEnemy.TeleportingEnemy;
import de.helicopter_vs_aliens.model.enemy.defaultEnemy.TinyVessel;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public enum EnemyType implements GameEntityFactory<Enemy>
{
    // Standard-Gegner
    TINY(1, TinyVessel::new, TinyVessel.class), //Level 1
    SMALL(2, SmallCruiser::new, SmallCruiser.class), // Level 3
    RUNABOUT(2, Runabout::new, Runabout.class), // level 5
    FREIGHTER(4, Freighter::new, Freighter.class), // Level 7
    BATCHWISE(6, BatchwiseFlyingEnemy::new, BatchwiseFlyingEnemy.class), // Level 11
    SINUS(6, SinusoidallyFlyingEnemy::new, SinusoidallyFlyingEnemy.class), // Level 13
    DODGER(9, Dodger::new, Dodger.class), // Level 16
    CHAOTIC(11, ChaoticallyFlyingEnemy::new, ChaoticallyFlyingEnemy.class), // Level 21
    CALLBACK(10, CallbackEnemy::new, CallbackEnemy.class), // Level 24
    SHOOTER(12, Shooter::new, Shooter.class), // Level 26
    CLOAK(16, CloakedEnemy::new, CloakedEnemy.class), // Level 31
    LONELY_SPEEDER(14, LonelySpeeder::new, LonelySpeeder.class), // Level 35
    CARRIER(19, Carrier::new, Carrier.class), // Level 36
    CRAZY(22, CrazyEnemy::new, CrazyEnemy.class), // Level 37
    AMBUSH(30, AmbushingEnemy::new, AmbushingEnemy.class), // Level 41
    LOOPING(30, LoopingEnemy::new, LoopingEnemy.class), // Level 43
    CAPTURING(30, CapturingEnemy::new, CapturingEnemy.class), // Level 45
    TELEPORTING(35, TeleportingEnemy::new, TeleportingEnemy.class), // Level 46
    
    // Boss-Gegner
    BOSS_1(75, FirstBoss::new, FirstBoss.class),
    BOSS_2(100, SecondBoss::new, SecondBoss.class),
    BOSS_2_SERVANT(5, SecondBossServant::new, SecondBossServant.class),
    BOSS_3(500, ThirdBoss::new, ThirdBoss.class),
    BOSS_4_SERVANT(1, FourthBossServant::new, FourthBossServant.class),
    BOSS_4(1250, FourthBoss::new, FourthBoss.class),
    FINAL_BOSS(5000, FinalBoss::new, FinalBoss.class),
    SMALL_SHIELD_MAKER(55, SmallShieldMaker::new, SmallShieldMaker.class),
    BIG_SHIELD_MAKER(80, BigShieldMaker::new, BigShieldMaker.class),
    BODYGUARD(150, Bodyguard::new, Bodyguard.class),
    HEALER(65, Healer::new, Healer.class),
    PROTECTOR(25, Protector::new, Protector.class),
    
    // Hindernis (Rock-Enemy)
    ROCK(0, Rock::new, Rock.class),
    
    // Hindernisse (Barrier)
    SMALL_BARRIER(4, SmallBarrier::new, SmallBarrier.class), // Level 2
    BIG_BARRIER(4, BigBarrier::new, BigBarrier.class), // Level 6
    STUNNING_BARRIER(5, StunningBarrier::new, StunningBarrier.class), // Level 12
    PUSHING_BARRIER(6, PushingBarrier::new, PushingBarrier.class), // Level 15
    SHOOTING_BARRIER(7, ShootingBarrier::new, ShootingBarrier.class), // Level 18
    BURROWING_BARRIER(9, BurrowingBarrier::new, BurrowingBarrier.class), // Level 32
    SHIELDING_BARRIER(4, ShieldingBarrier::new, ShieldingBarrier.class), // Level 42
    // TODO CLOAKED_BARRIER  überarbeiten, wie oft soll er wiederkommen? manchmal schießt er nicht,
    CLOAKED_BARRIER(15, CloakedBarrier::new, CloakedBarrier.class), // Level 44
    
    // sonstige Gegner
    KABOOM(0, Kaboom::new, Kaboom.class),
    
    ESCAPED_SPEEDER(14, EscapedSpeeder::new, EscapedSpeeder.class);
    
    
    private static final List<EnemyType>
        VALUES = List.of(values());
    
    private final static Set<EnemyType>
        BOSS_TYPES = Collections.unmodifiableSet(EnumSet.range(BOSS_1, PROTECTOR)),
        DEFAULT_TYPES = Collections.unmodifiableSet(EnumSet.range(TINY, TELEPORTING)),
        FINAL_BOSS_SERVANT_TYPES = Collections.unmodifiableSet(EnumSet.range(SMALL_SHIELD_MAKER, PROTECTOR)),
        BARRIERS = Collections.unmodifiableSet(EnumSet.range(SMALL_BARRIER, CLOAKED_BARRIER)),
        CLOAKABLE_AS_MINI_BOSS_TYPES = Collections.unmodifiableSet(EnumSet.range(LONELY_SPEEDER, TELEPORTING));
    
        
    private final int
        strength; // Stärke des Gegners; bestimmt die Höhe der Belohnung bei Abschuss
    
    private final Supplier<? extends Enemy>
        instanceSupplier;
    
    private final Class<? extends Enemy>
        enemyClass;
    
    EnemyType(int strength, Supplier<? extends Enemy> instanceSupplier, Class<? extends Enemy> enemyClass)
    {
        this.strength = strength;
        this.instanceSupplier = instanceSupplier;
        this.enemyClass = enemyClass;
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
    
    boolean isSuitableMiniBoss()
    {
        return DEFAULT_TYPES.contains(this) && !(this == TINY);
    }
    
    boolean isCloakableAsMiniBoss()
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
}