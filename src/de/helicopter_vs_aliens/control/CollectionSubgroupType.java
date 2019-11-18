package de.helicopter_vs_aliens.control;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;


public enum CollectionSubgroupType
{
    INACTIVE,
    ACTIVE,
    DESTROYED;


    private final static Set<CollectionSubgroupType>
        STANDARD_SUBGROUP_TYPES = Collections.unmodifiableSet(EnumSet.of(INACTIVE, ACTIVE));

    static Set<CollectionSubgroupType> getStandardSubgroupTypes()
    {
        return STANDARD_SUBGROUP_TYPES;
    }
}