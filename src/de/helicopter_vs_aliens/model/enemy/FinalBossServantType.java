package de.helicopter_vs_aliens.model.enemy;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum FinalBossServantType
{
    SMALL_SHIELD_MAKER(
        EnemyType.SMALL_SHIELD_MAKER,
        0,
        0.013f,
        175),
    
    BIG_SHIELD_MAKER(
        EnemyType.BIG_SHIELD_MAKER,
        1,
        0.013f,
        175),
    
    BODYGUARD(
        EnemyType.BODYGUARD,
        2,
        0.007f,
        900),
    
    HEALER(
        EnemyType.HEALER,
        3,
        0.01f,
        250),
    
    PROTECTOR(
        EnemyType.PROTECTOR,
        4,
        0.04f,
        90);
    
    private static final List<FinalBossServantType>
        VALUES = List.of(values());
    
    private static final Set<FinalBossServantType>
        SHIELD_MAKER = Collections.unmodifiableSet(EnumSet.of(SMALL_SHIELD_MAKER, BIG_SHIELD_MAKER));
    
    private static final Map<EnemyType, FinalBossServantType>
        SERVANT_TYPE_MAP = VALUES.stream()
                                 .collect(Collectors.toUnmodifiableMap(FinalBossServantType::getEnemyType, Function.identity()));
    
    private final EnemyType
        enemyType;
    
    private final int
        id;
    
    private final float
        returnProbability;
    
    private final int
        minimumTimeBeforeRecreation;
    
    
    FinalBossServantType(EnemyType enemyType, int id, float returnProbability, int minimumTimeBeforeRecreation)
    {
        this.enemyType = enemyType;
        this.id = id;
        this.returnProbability = returnProbability;
        this.minimumTimeBeforeRecreation = minimumTimeBeforeRecreation;
    }
    
    public static List<FinalBossServantType> getValues()
    {
        return VALUES;
    }
    
    public static Optional<FinalBossServantType> of(EnemyType enemyType)
    {
        return Optional.ofNullable(SERVANT_TYPE_MAP.get(enemyType));
    }
    
    public static Set<FinalBossServantType> getShieldMaker()
    {
        return SHIELD_MAKER;
    }
    
    public EnemyType getEnemyType()
    {
        return enemyType;
    }
    
    public int getId()
    {
        return id;
    }
    
    public float getReturnProbability()
    {
        return returnProbability;
    }
    
    public int getMinimumTimeBeforeRecreation()
    {
        return minimumTimeBeforeRecreation;
    }
}
