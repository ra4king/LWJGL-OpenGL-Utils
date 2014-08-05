package com.ra4king.opengl.util;

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
	public static void stopAndPrint() {
		TimePeriod timePeriod = null;
		if(!stackTimePeriods.isEmpty())
			timePeriod = stackTimePeriods.getFirst();
		
		stop();
		
		if(timePeriod != null)
			print(timePeriod);
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
	
	public static void print() {
		print(orderedTimePeriods, 0);
	}
	
	private static void print(List<TimePeriod> timePeriods, int offset) {
		for(TimePeriod timePeriod : timePeriods) {
			int childOffset;
			
			if(timePeriod.count > 0) {
				for(int a = 0; a < offset; a++)
					System.out.print("  ");
				
				print(timePeriod);
				
				childOffset = offset + 1;
			}
			else {
				// System.out.printf("%s: never timed\n", timePeriod.name);
				
				childOffset = offset;
			}
			
			if(!timePeriod.children.isEmpty()) {
				print(timePeriod.children, childOffset);
			}
		}
	}
	
	private static void print(TimePeriod timePeriod) {
		System.out.printf("%s: %.3f, %d, %.3f\n", timePeriod.name,
				timePeriod.totalTime / (1e6 * timePeriod.count), timePeriod.count, timePeriod.totalTime / 1e6);
		
		timePeriod.totalTime = 0;
		timePeriod.count = 0;
	}
	
	private static class TimePeriod {
		String name;
		
		long lastStartTime;
		long totalTime;
		int count;
		
		List<TimePeriod> children = new ArrayList<>();
		
		public TimePeriod(String name) {
			this.name = name;
		}
	}
}
