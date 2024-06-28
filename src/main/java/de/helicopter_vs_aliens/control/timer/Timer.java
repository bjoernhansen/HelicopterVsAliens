package de.helicopter_vs_aliens.control.timer;

// TODO Timer-Klasse überall verwenden, wo integer-Counter verwendet werden
// TODO es muss festgelegt werden können, wann ein Timer heruntergezählt wird, jeder Timer braucht somit eine eigene
// Bedingung (eine Methode) welche bei jedem Timer abgefragt wird. Zwei Ansätze sind denkbar: 1. übergeben von Lambda
// Ausdruck an Methode
// oder verschiedene Unterklassen
// TODO bei Fensterwechsel Bedingung, die bestimmt, ob Timer wegfällt
public class Timer
{
    // TODO DISABLED und EXPIRED später wieder löschen, wenn nicht mehr nötig
    public static final int
        DISABLED = -1;
    
    private static final int
        EXPIRED = 0;
    
    private final static TimerManager
        timerManager = TimerManager.getInstance();
    
    
    private int
        timeLeft = EXPIRED;
    
    private boolean
        isActive = false;
    
    int
        timeInterval;
    
    
    public Timer(int duration)
    {
        timeInterval = duration;
    }
    
    public int getTimeLeft()
    {
        return timeLeft;
    }
    
    public boolean isActive()
    {
        return isActive;
    }
    
    public boolean hasExpired()
    {
        return timeLeft <= EXPIRED && isActive;
    }
    
    public void start()
    {
        timeLeft = timeInterval;
        isActive = true;
        timerManager.addTimer(this);
    }
    
    public void reset()
    {
        timeLeft = EXPIRED;
        isActive = false;
        timerManager.removeTimer(this);
    }
    
    void countDown()
    {
        timeLeft = Math.max(0, timeLeft - 1);
    }
    
    void disable()
    {
        isActive = false;
    }
}