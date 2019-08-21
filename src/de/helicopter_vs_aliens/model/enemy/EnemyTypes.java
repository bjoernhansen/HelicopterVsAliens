package de.helicopter_vs_aliens.model.enemy;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum EnemyTypes
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
    BOLT(14),
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
    
    // Hindernisse
    ROCK(0),
    BARRIER_0(4),
    BARRIER_1(4),
    BARRIER_2(5),
    BARRIER_3(6),
    BARRIER_4(7),
    BARRIER_5(9),
    BARRIER_6(4),
    BARRIER_7(15), // TODO überarbeiten, wie oft soll er wiederkommen? manchmal schießt er nicht,
    
    KABOOM(0);
    
    
    private final static Set<EnemyTypes>
        BOSS_TYPES = Collections.unmodifiableSet(EnumSet.range(BOSS_1, PROTECTOR)),
        STANDARD_TYPES = Collections.unmodifiableSet(EnumSet.range(TINY, TELEPORTER)),
        FINAL_BOSS_SERVANT_TYPES = Collections.unmodifiableSet(EnumSet.range(SMALL_SHIELD_MAKER, PROTECTOR)),
        BARRIERS = Collections.unmodifiableSet(EnumSet.range(BARRIER_0, BARRIER_7)),
        CLOAKABLE_AS_MINI_BOSS_TYPES = Collections.unmodifiableSet(EnumSet.range(BOLT, TELEPORTER));
      
    private final int strength; // Stärke des Gegner, bestimmmt die Höhe der Belohnung bei Abschuss
    
    
    EnemyTypes(int strength)
    {
        this.strength = strength;
    }
    
    static Set<EnemyTypes> getFinalBossServantTypes()
    {
        return FINAL_BOSS_SERVANT_TYPES;
    }
    
    static Set<EnemyTypes> getBarrierTypes()
    {
        return BARRIERS;
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
}