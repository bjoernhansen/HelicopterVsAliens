package de.helicopter_vs_aliens.score;

import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public enum HighScoreType
{
    OVERALL(StartScreenMenuButtonType.BUTTON_2),
    PHOENIX(StartScreenMenuButtonType.BUTTON_3),
    ROCH(StartScreenMenuButtonType.BUTTON_4),
    OROCHI(StartScreenMenuButtonType.BUTTON_5),
    KAMAITACHI(StartScreenMenuButtonType.BUTTON_6),
    PEGASUS(StartScreenMenuButtonType.BUTTON_7),
    HELIOS(StartScreenMenuButtonType.BUTTON_8);
    
        
    private static final List<HighScoreType>
        VALUES = List.of(values());
    
    private static final Map<StartScreenMenuButtonType, HighScoreType>
        BUTTON_TO_HIGH_SCORE_TYPE_MAP = Arrays.stream(HighScoreType.values())
                                              .collect(Collectors.toUnmodifiableMap(HighScoreType::getAssociatedButtonType, Function.identity()));
    
    private static final List<HighScoreType>
        HIGH_SCORE_TYPES = List.copyOf(EnumSet.range(PHOENIX, HELIOS));
    
    private final StartScreenMenuButtonType
        associatedButtonType;
    
    
    HighScoreType(StartScreenMenuButtonType startScreenMenuButtonType)
    {
        this.associatedButtonType = startScreenMenuButtonType;
    }
    
    public static List<HighScoreType> getValues()
    {
        return VALUES;
    }
    
    public static HighScoreType of(StartScreenMenuButtonType startScreenMenuButtonType)
    {
        return BUTTON_TO_HIGH_SCORE_TYPE_MAP.get(startScreenMenuButtonType);
    }
    
    public static HighScoreType of(HelicopterType helicopterType)
    {
        return HIGH_SCORE_TYPES.get(helicopterType.ordinal());
    }
    
    private StartScreenMenuButtonType getAssociatedButtonType()
    {
        return associatedButtonType;
    }
}