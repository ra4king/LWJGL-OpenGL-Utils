package com.ra4king.opengl.util;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;

/**
 * @author Roi Atalla
 */
public class Stopwatch {
	private Stopwatch() {}
	
	private static HashMap<String,TimePeriod> allTimePeriods = new HashMap<>();
	
	private static List<TimePeriod> orderedTimePeriods = new ArrayList<>();
	
	private static Deque<TimePeriod> stackTimePeriods = new ArrayDeque<>();
	
	/**
	 * Starts timer with name specified by periodName. If there was already a timer running, it is pushed onto a stack.
	 * 
	 * Throws IllegalStateException if a timer with the same name hasn't been stop()-ed or end()-ed.
	 * 
	 * @param periodName Name of the timer to be created
	 */
	public static void start(String periodName) {
		TimePeriod timePeriod;
		
		if(allTimePeriods.containsKey(periodName)) {
			timePeriod = allTimePeriods.get(periodName);
			
			if(timePeriod.lastStartTime != 0)
				throw new IllegalStateException("Timer for '" + periodName + "' already started.");
		}
		else {
			timePeriod = new TimePeriod(periodName);
			allTimePeriods.put(periodName, timePeriod);
		}
		
		stackTimePeriods.push(timePeriod);
		
		timePeriod.lastStartTime = System.nanoTime();
	}
	
	/**
	 * Suspends the current timer, adding to the total time.
	 * 
	 * Throws IllegalStateException if there is no current timer.
	 */
	public static void suspend() {
		long now = System.nanoTime();
		
		if(stackTimePeriods.isEmpty())
			throw new IllegalStateException("No timer started");
		
		TimePeriod timePeriod = stackTimePeriods.getFirst();
		timePeriod.totalTime += now - timePeriod.lastStartTime;
	}
	
	/**
	 * Resumes the current timer
	 * 
	 * Throws IllegalStateException if there is no current timer.
	 */
	public static void resume() {
		if(stackTimePeriods.isEmpty())
			throw new IllegalStateException("No timer started");
		
		TimePeriod timePeriod = stackTimePeriods.getFirst();
		timePeriod.lastStartTime = System.nanoTime();
	}
	
	/**
	 * Ends the current timer without adding to its total time. Used when wanting to end the timer after calling suspend().
	 * 
	 * Throws IllegalStateException if there is no current timer.
	 */
	public static void end() {
		if(stackTimePeriods.isEmpty())
			throw new IllegalStateException("No timer started.");
		
		TimePeriod timePeriod = stackTimePeriods.pop();
		timePeriod.lastStartTime = 0;
		timePeriod.count++;
		
		endTimer(timePeriod);
	}
	
	/**
	 * Stops the current timer, adding to the total time.
	 * 
	 * Throws IllegalStateException if there is no current timer.
	 */
	public static void stop() {
		long now = System.nanoTime();
		
		if(stackTimePeriods.isEmpty())
			throw new IllegalStateException("No timer started.");
		
		TimePeriod timePeriod = stackTimePeriods.pop();
		timePeriod.totalTime += now - timePeriod.lastStartTime;
		timePeriod.count++;
		timePeriod.lastStartTime = 0;
		
		endTimer(timePeriod);
	}
	
	/**
	 * Stops the current timer, adding to the total time. Immediately prints.
	 * 
	 * Throws IllegalStateException if there is no current timer.
	 */
	public static void stopAndPrint(PrintStream writer) {
		TimePeriod timePeriod = null;
		if(!stackTimePeriods.isEmpty())
			timePeriod = stackTimePeriods.getFirst();
		
		stop();
		
		if(timePeriod != null) {
			print(writer, timePeriod);
			reset(timePeriod);
		}
	}
	
	private static void endTimer(TimePeriod timePeriod) {
		if(stackTimePeriods.isEmpty()) {
			if(!orderedTimePeriods.contains(timePeriod))
				orderedTimePeriods.add(timePeriod);
		}
		else {
			TimePeriod parent = stackTimePeriods.getFirst();
			if(!parent.children.contains(timePeriod))
				parent.children.add(timePeriod);
		}
	}
	
	public static void print(PrintStream writer) {
		print(writer, orderedTimePeriods, 0);
	}
	
	private static void print(PrintStream writer, List<TimePeriod> timePeriods, int offset) {
		for(TimePeriod timePeriod : timePeriods) {
			int childOffset;
			
			if(timePeriod.count > 0) {
				for(int a = 0; a < offset; a++)
					writer.print("  ");
				
				print(writer, timePeriod);
				
				childOffset = offset + 1;
			}
			else {
				// writer.printf("%s: never timed\n", timePeriod.name);
				
				childOffset = offset;
			}
			
			reset(timePeriod);
			
			if(!timePeriod.children.isEmpty()) {
				print(writer, timePeriod.children, childOffset);
			}
		}
	}
	
	private static void print(PrintStream writer, TimePeriod timePeriod) {
		writer.printf("%s: %.3f, %d, %.3f\n", timePeriod.name,
				timePeriod.totalTime / (1e6 * timePeriod.count), timePeriod.count, timePeriod.totalTime / 1e6);
	}
	
	private static void reset(TimePeriod timePeriod) {
		timePeriod.lastTotalTime = timePeriod.totalTime;
		timePeriod.totalTime = 0;
		
		timePeriod.lastCount = timePeriod.count;
		timePeriod.count = 0;
	}
	
	/**
	 * Gets the average time per frame
	 * 
	 * @param name The name of the time period
	 * @return The amount of time per frame, in seconds.
	 */
	public static double getTimePerFrame(String name) {
		TimePeriod period = allTimePeriods.get(name);
		
		if(period == null) {
			return 0.0;
		}
		
		return period.lastTotalTime / (1e6 * period.lastCount);
	}
	
	public static double getTotalFrames(String name) {
		TimePeriod period = allTimePeriods.get(name);
		
		if(period == null) {
			return 0.0;
		}
		
		return period.lastCount;
	}
	
	public static double getTotalTime(String name) {
		TimePeriod period = allTimePeriods.get(name);
		
		if(period == null) {
			return 0.0;
		}
		
		return period.lastTotalTime / 1e6;
	}
	
	private static class TimePeriod {
		String name;
		
		long lastStartTime;
		long totalTime;
		int count;
		
		long lastTotalTime = -1;
		int lastCount = -1;
		
		List<TimePeriod> children = new ArrayList<>();
		
		public TimePeriod(String name) {
			this.name = name;
		}
	}
}
