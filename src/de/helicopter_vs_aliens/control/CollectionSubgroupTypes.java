package de.helicopter_vs_aliens.control;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;


public enum CollectionSubgroupTypes
{
    INACTIVE,
    ACTIVE,
    DESTROYED;


    private final static Set<CollectionSubgroupTypes>
        STANDARD_SUBGROUP_TYPES = Collections.unmodifiableSet(EnumSet.of(INACTIVE, ACTIVE));

    static Set<CollectionSubgroupTypes> getStandardSubgroupTypes()
    {
        return STANDARD_SUBGROUP_TYPES;
    }
}