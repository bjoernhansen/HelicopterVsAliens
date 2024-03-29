package de.helicopter_vs_aliens.control.entities;

import de.helicopter_vs_aliens.util.Calculations;

import java.util.List;


public final class GameEntityActivation
{
    private GameEntityActivation() throws Exception
    {
        throw new Exception();
    }
    
    private static final int
        STANDARD_PROBABILITY_REDUCTION_FACTOR = 1;
    
    private static final List<Integer>
        PROBABILITIES = List.of(100, 50, 34, 25, 20, 17, 15, 13, 12, 10, 10, 9, 8, 8, 7, 6);
    
    
    public static boolean isQuicklyApproved()
    {
        return isApproved(PROBABILITIES.size(), STANDARD_PROBABILITY_REDUCTION_FACTOR);
    }
    
    public static boolean isApproved(int numberOfMissingEntities)
    {
        return isApproved(numberOfMissingEntities, STANDARD_PROBABILITY_REDUCTION_FACTOR);
    }
    
    public static boolean isApproved(int numberOfMissingEntities, int probabilityReductionFactor)
    {
        int index = Calculations.constrainToRange(numberOfMissingEntities, 1, PROBABILITIES.size())-1;
        return Calculations.random(probabilityReductionFactor * PROBABILITIES.get(index)) == 0;
    }
}