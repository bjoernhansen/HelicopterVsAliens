package de.helicopter_vs_aliens.platform_specific.awt;

public class BackgroundRepaintTimer
{
    private static final int
        DISABLED = -1;

    private int
        timer = 0;


    public void proceed()
    {
        if(timer > 1)
        {
            timer = DISABLED;
        }
        else
        {
            timer++;
        }
    }

    public boolean isActive()
    {
        return timer != DISABLED;
    }

    public void reset()
    {
        timer = 0;
    }
}
