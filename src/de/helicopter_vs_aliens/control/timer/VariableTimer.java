package de.helicopter_vs_aliens.control.timer;

public class VariableTimer extends Timer
{
    public VariableTimer(int duration)
    {
        super(duration);
    }

    public void start(int duration)
    {
        this.timeInterval = duration;
        super.start();
    }
}
