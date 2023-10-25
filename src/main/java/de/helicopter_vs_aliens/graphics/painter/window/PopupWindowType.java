package de.helicopter_vs_aliens.graphics.painter.window;

import de.helicopter_vs_aliens.gui.window.Window;


public enum PopupWindowType
{
    DEFAULT(new VerticalBoundaries(77, 231)),
    
    REPAIRABLE_CRASH(new VerticalBoundaries(147, 111)),
    
    TEMPORARILY_VICTORY(null)
    {
        @Override
        public VerticalBoundaries getVerticalBoundaries()
        {
            return Window.language.getVerticalBoundariesOfPopupWindowForTemporarilyVictory();
        }
    },
    
    TOTAL_CRASH_OR_FINAL_VICTORY(new VerticalBoundaries(112,146));
    
    
    private final VerticalBoundaries
        verticalBoundaries;
    
    
    PopupWindowType(VerticalBoundaries verticalBoundaries)
    {
        this.verticalBoundaries = verticalBoundaries;
    }
    
    public VerticalBoundaries getVerticalBoundaries()
    {
        return verticalBoundaries;
    }
}
