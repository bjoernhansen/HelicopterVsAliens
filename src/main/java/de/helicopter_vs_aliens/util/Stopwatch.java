package de.helicopter_vs_aliens.util;

public class Stopwatch
{
    private final boolean
            useNanoTime;

    private long
            startTime = 0;

    private boolean
            started = false;


    public static Stopwatch withMillisecondPrecision()
    {
        return new Stopwatch(false);
    }

    public static Stopwatch withNanosecondPrecision()
    {
        return new Stopwatch(true);
    }

    private Stopwatch(boolean useNanoTime)
    {
        this.useNanoTime = useNanoTime;
    }

    public void startClock()
    {
        startTime = getCurrentTime();
        started = true;
    }

    public void stopClock()
    {
        if(started)
        {
            System.out.println("elapsed time: " + getElapsedTimeUntilNow());
            started = false;
        }
    }

    private long getElapsedTimeUntilNow()
    {
        return getCurrentTime() - startTime;
    }

    private long getCurrentTime()
    {
        return useNanoTime ? System.nanoTime() : System.currentTimeMillis();
    }
}
