package de.helicopter_vs_aliens.gui.button;

public interface ButtonSpecifier
{
    ButtonCategory getCategory();
    
    int getX();
    
    int getY();
    
    String getLabel();
    
    String getSecondLabel();
}