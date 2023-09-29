package de.helicopter_vs_aliens.control.ressource_transfer;

import de.helicopter_vs_aliens.gui.window.Window;
import de.helicopter_vs_aliens.gui.window.WindowManager;

import static de.helicopter_vs_aliens.gui.WindowType.GAME;


class FpsCalculator
{
    private int
        framesCount;

    private long
        fpsStartTime;

    private boolean
        isFpsDisplayVisible = false;


    void calculateFps()
    {
        if (isFpsDisplayVisible)
        {
            long timeDiff = System.currentTimeMillis() - fpsStartTime;
            framesCount++;
            if (timeDiff > 3000)
            {
                Window.fps = Math.round(1000f * framesCount / timeDiff);
                fpsStartTime = System.currentTimeMillis();
                framesCount = 0;
            }
        }
    }

    void switchFpsVisibleState()
    {
        if (WindowManager.window == GAME)
        {
            isFpsDisplayVisible = !isFpsDisplayVisible;
            if (isFpsDisplayVisible)
            {
                fpsStartTime = System.currentTimeMillis();
                framesCount = 0;
            }
        }
    }

    boolean isFpsDisplayVisible()
    {
        return isFpsDisplayVisible;
    }
}
