package de.helicopter_vs_aliens.score;

import de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType;
import de.helicopter_vs_aliens.model.helicopter.HelicopterType;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType.BUTTON_2;
import static de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType.BUTTON_3;
import static de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType.BUTTON_4;
import static de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType.BUTTON_5;
import static de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType.BUTTON_6;
import static de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType.BUTTON_7;
import static de.helicopter_vs_aliens.gui.button.StartScreenMenuButtonType.BUTTON_8;

public enum HighScoreType
{
    OVERALL(BUTTON_2),
    PHOENIX(BUTTON_3),
    ROCH(BUTTON_4),
    OROCHI(BUTTON_5),
    KAMAITACHI(BUTTON_6),
    PEGASUS(BUTTON_7),
    HELIOS(BUTTON_8);
    
        
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
    
    public static int valueCount(){
        return VALUES.size();
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