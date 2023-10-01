package de.helicopter_vs_aliens.util;

public class Stopwatch
{
    private static final boolean
        USE_NANO_TIME = true;

    private long
        startTime = 0;

    boolean
        started = false;


    public static Stopwatch make()
    {
        return new Stopwatch();
    }

    private Stopwatch(){}

    public void startClock()
    {
        startTime = USE_NANO_TIME ? System.nanoTime() : System.currentTimeMillis();
        started = true;
    }

    public void stopClock()
    {
        if(started)
        {
            long stopTime = USE_NANO_TIME ? System.nanoTime() : System.currentTimeMillis();
            System.out.println("Zeit: " + (stopTime - startTime));
            started = false;
        }
    }
}
