package com.ra4king.opengl.util;

/**
 * @author Roi Atalla
 */
public class Timer {
	public static enum TimerType {
		LOOP,
		SINGLE,
		INFINITE
	}
	
	private TimerType type;
	private float secDuration;
	
	private boolean hasUpdated;
	private boolean isPaused;
	
	private float absPrevTime;
	private float secAccumTime;
	
	private long elapsedTime;
	
	public Timer() {
		this(TimerType.INFINITE, 1);
	}
	
	/**
	 * Initializes the Timer with a type and duration
	 * 
	 * @param type The Type of the timer can be <code>TimerType.LOOP</code>, <code>TimerType.SINGLE</code>, or <code>TimerType.INFINITY</code>
	 * @param duration The duration of the Timer specified as a <code>float</code> interpreted as seconds.
	 *        If the Type is is <code>TimerType.INFINITE</code>, this value is meaningless.
	 *        Otherwise, it must be a positive nonzero value.
	 */
	public Timer(TimerType type, float duration) {
		this.type = type;
		this.secDuration = duration;
		
		if(type != TimerType.INFINITE && duration <= 0)
			throw new IllegalArgumentException("duration cannot be less than or equal to 0");
	}
	
	public void reset() {
		hasUpdated = false;
		secAccumTime = 0;
	}
	
	/**
	 * Toggles pause/resume and returns the new state
	 * 
	 * @return <code>True</code> if paused, otherwise <code>false</code>
	 */
	public boolean togglePause() {
		isPaused = !isPaused;
		return isPaused;
	}
	
	public boolean isPaused() {
		return isPaused;
	}
	
	public void setPause(boolean pause) {
		isPaused = pause;
	}
	
	/**
	 * Updates the timer.
	 * 
	 * @param deltaTime Amount of time in nanoseconds since the last call to this method.
	 * @return <code>True</code> if the Timer is completed, otherwise <code>false</code> if the Timer is still in progress.
	 */
	public boolean update(long deltaTime) {
		elapsedTime += deltaTime;
		
		float currTime = elapsedTime / (float)1e9;
		
		if(!hasUpdated) {
			absPrevTime = currTime;
			hasUpdated = true;
		}
		
		if(isPaused) {
			absPrevTime = currTime;
			return false;
		}
		
		float delta = currTime - absPrevTime;
		secAccumTime += delta;
		
		absPrevTime = currTime;
		
		return type == TimerType.SINGLE && secAccumTime > secDuration;
	}
	
	/**
	 * Rewinds the Timer. Could also fast forward the Timer if given a negative value.
	 * 
	 * @param secRewind Amount of time in seconds.
	 */
	public void rewind(float secRewind) {
		secAccumTime -= secRewind;
		if(secAccumTime < 0)
			secAccumTime = 0;
	}
	
	/**
	 * Fast forward the Timer. Equivalent to <code>rewind(-secFastForward)</code>.
	 * 
	 * @param secFastForward Amount of time in seconds.
	 */
	public void fastForward(float secFastForward) {
		rewind(-secFastForward);
	}
	
	/**
	 * The Alpha is a floating-point value that represents the ratio of the time passed to the duration time.
	 * Equivalent to <code>getProgression() / timerDuration</code> (except for <code>TimerType.INFINITY</code>).
	 * 
	 * @return For <code>TimerType.SINGLE</code>, it returns a value between 0.0f and 1.0f (inclusive) where 0.0f is no time
	 *         passed and 1.0f is the Timer completed.
	 *         For <code>TimerType.LOOP</code>, it returns a value equivalent to <code>timePassed / timerDuration</code>.
	 *         For <code>TimerType.INFINITY</code>, it returns -1f.
	 */
	public float getAlpha() {
		switch(type) {
			case LOOP:
				return (secAccumTime % secDuration) / secDuration;
			case SINGLE:
				return Utils.clamp(secAccumTime / secDuration, 0, 1);
			case INFINITE:
			default:
				return -1;
		}
	}
	
	/**
	 * A floating-point value interpreted as seconds.
	 * 
	 * @return The amount of seconds passed since the start of the Timer.
	 */
	public float getProgression() {
		switch(type) {
			case LOOP:
				return secAccumTime % secDuration;
			case SINGLE:
				return Utils.clamp(secAccumTime, 0, secDuration);
			case INFINITE:
			default:
				return -1;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public float getTimeSinceStart() {
		return secAccumTime;
	}
}
