package de.helicopter_vs_aliens.model.enemy;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public enum EnemyTypes
{
    // Standard-Gegner
    TINY,
    SMALL,
    RUNABOUT,
    FREIGHTER,
    BATCHWISE,
    SINUS,
    DODGER,
    CHAOS,
    CALLBACK,
    SHOOTER,
    CLOAK,
    BOLT,
    CARRIER,
    YELLOW,
    AMBUSH,
    LOOPING,
    CAPTURER,
    TELEPORTER,
    
    // Boss-Gegner
    BOSS_1,
    BOSS_2,
    BOSS_2_SERVANT,
    BOSS_3,
    BOSS_4_SERVANT,
    BOSS_4,
    FINAL_BOSS,
    SMALL_SHIELD_MAKER,
    BIG_SHIELD_MAKER,
    BODYGUARD,
    HEALER,
    PROTECTOR,
    
    // Hindernisse
    ROCK,
    BARRIER_0,
    BARRIER_1,
    BARRIER_2,
    BARRIER_3,
    BARRIER_4,
    BARRIER_5,
    BARRIER_6,
    BARRIER_7,
    
    KABOOM;
    
    
    private final static Set<EnemyTypes>
        BOSS_TYPES = Collections.unmodifiableSet(EnumSet.range(BOSS_1, PROTECTOR)),
        STANDARD_TYPES = Collections.unmodifiableSet(EnumSet.range(TINY, TELEPORTER)),
        FINAL_BOSS_SERVANT_TYPES = Collections.unmodifiableSet(EnumSet.range(SMALL_SHIELD_MAKER, PROTECTOR)),
        BARRIER = Collections.unmodifiableSet(EnumSet.range(BARRIER_0, BARRIER_7)),
        CLOAKABLE_AS_MINI_BOSS_TYPES = Collections.unmodifiableSet(EnumSet.range(BOLT, TELEPORTER));
        
    public static Set<EnemyTypes> getFinalBossServantTypes()
    {
        return FINAL_BOSS_SERVANT_TYPES;
    }
    
    public static Set<EnemyTypes> getBarrierTypes()
    {
        return BARRIER;
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
}