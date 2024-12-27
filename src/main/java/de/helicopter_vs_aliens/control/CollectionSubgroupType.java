package de.helicopter_vs_aliens.control;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;


public enum CollectionSubgroupType
{
    ACTIVE,
    DESTROYED;
    
    private static final Set<CollectionSubgroupType>
        STANDARD_SUBGROUP_TYPES = Collections.unmodifiableSet(EnumSet.of(ACTIVE));

    public static Set<CollectionSubgroupType> getStandardSubgroupTypes()
    {
        return STANDARD_SUBGROUP_TYPES;
    }
}