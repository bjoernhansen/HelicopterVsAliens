package de.helicopter_vs_aliens.model.enemy;

import de.helicopter_vs_aliens.control.entities.GameEntityFactory;
import de.helicopter_vs_aliens.model.enemy.barrier.Barrier;
import de.helicopter_vs_aliens.model.enemy.boss.BossEnemy;
import de.helicopter_vs_aliens.model.enemy.defaultEnemy.DefaultEnemy;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public enum EnemyType implements GameEntityFactory<Enemy>
{
    // Standard-Gegner
    TINY(1),
    SMALL(2),
    RUNABOUT(2),
    FREIGHTER(4),
    BATCHWISE(6),
    SINUS(6),
    DODGER(9),
    CHAOS(11),
    CALLBACK(10),
    SHOOTER(12),
    CLOAK(16),
    LONELY_BOLT(14),
    CARRIER(19),
    YELLOW(22),
    AMBUSH(30),
    LOOPING(30),
    CAPTURER(30),
    TELEPORTER(35),
    
    // Boss-Gegner
    BOSS_1(75),
    BOSS_2(100),
    BOSS_2_SERVANT(5),
    BOSS_3(500),
    BOSS_4_SERVANT(1),
    BOSS_4(1250),
    FINAL_BOSS(5000),
    SMALL_SHIELD_MAKER(55),
    BIG_SHIELD_MAKER(80),
    BODYGUARD(150),
    HEALER(65),
    PROTECTOR(25),
    
    // Hindernis (Rock-Enemy)
    ROCK(0),
    
    // Hindernisse (Barrier)
    BARRIER_0(4),
    BARRIER_1(4),
    BARRIER_2(5),
    BARRIER_3(6),
    BARRIER_4(7),
    BARRIER_5(9),
    BARRIER_6(4),
    BARRIER_7(15), // TODO überarbeiten, wie oft soll er wiederkommen? manchmal schießt er nicht,
    
    // sonstige Gegner
    KABOOM(0),
    
    ESCAPED_BOLT(14);
    
    
    private static final List<EnemyType>
        VALUES = List.of(values());
    
    private final static Set<EnemyType>
        BOSS_TYPES = Collections.unmodifiableSet(EnumSet.range(BOSS_1, PROTECTOR)),
        STANDARD_TYPES = Collections.unmodifiableSet(EnumSet.range(TINY, TELEPORTER)),
        FINAL_BOSS_SERVANT_TYPES = Collections.unmodifiableSet(EnumSet.range(SMALL_SHIELD_MAKER, PROTECTOR)),
        BARRIERS = Collections.unmodifiableSet(EnumSet.range(BARRIER_0, BARRIER_7)),
        CLOAKABLE_AS_MINI_BOSS_TYPES = Collections.unmodifiableSet(EnumSet.range(LONELY_BOLT, TELEPORTER));
    
    
    static{
        STANDARD_TYPES.forEach(enemyType -> enemyType.instanceSupplier = DefaultEnemy::new);
        BARRIERS.forEach(enemyType -> enemyType.instanceSupplier = Barrier::new);
        BOSS_TYPES.forEach(enemyType -> enemyType.instanceSupplier = BossEnemy::new);
        ROCK.instanceSupplier = DefaultEnemy::new;
        KABOOM.instanceSupplier = DefaultEnemy::new;
    }
    
    private final int
        strength; // Stärke des Gegners; bestimmt die Höhe der Belohnung bei Abschuss
    
    private Supplier<? extends Enemy>
        instanceSupplier;
    
    
    EnemyType(int strength)
    {
        this.strength = strength;
    }
    
    static Set<EnemyType> getFinalBossServantTypes()
    {
        return FINAL_BOSS_SERVANT_TYPES;
    }
    
    static Set<EnemyType> getBarrierTypes()
    {
        return BARRIERS;
    }
    
    static Set<EnemyType> getBossTypes()
    {
        return BOSS_TYPES;
    }
    
    public boolean isMajorBoss()
    {
        return BOSS_TYPES.contains(this);
    }
    
    boolean isFinalBossServant()
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
    
    boolean isShieldMaker()
    {
        return this  == SMALL_SHIELD_MAKER || this == BIG_SHIELD_MAKER;
    }
    
    boolean isSuitableMiniBoss()
    {
        return STANDARD_TYPES.contains(this) && !(this == TINY);
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
    public Class<Enemy> getCorrespondingClass()
    {
        // TODO implementation
        return null;
    }
}