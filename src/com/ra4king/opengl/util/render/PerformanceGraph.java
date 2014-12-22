package com.ra4king.opengl.util.render;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;
import java.util.function.Supplier;

import org.lwjgl.BufferUtils;

import com.ra4king.opengl.util.ShaderProgram;
import com.ra4king.opengl.util.Utils;
import com.ra4king.opengl.util.math.Matrix4;
import com.ra4king.opengl.util.math.Vector4;

import net.indiespot.struct.cp.Struct;
import net.indiespot.struct.cp.TakeStruct;

/**
 * @author Roi Atalla
 */
public class PerformanceGraph {
	private Supplier<? extends Number> stepValueSupplier;
	private float maxValue;
	
	private int x, y;
	private int width, height;
	private int maxSteps;
	private int stepWidth;
	
	private Vector4 color = Struct.malloc(Vector4.class);
	
	private static ShaderProgram uiProgram;
	
	private Matrix4 projectionMatrix;
	private int vbo, vao;
	
	private FloatBuffer graphData;
	private int graphOffset;
	private int stepCount;
	
	/**
	 * Creates a PerformanceGraph at location (x,y) with size (maxSteps*stepWidth, graphHeight).
	 * The origin (0,0) is at the top left corner of the window.
	 *
	 * @param stepValueSupplier The Supplier of the values of each step
	 * @param maxValue The value to represent as 100%
	 * @param x The left side of the graph
	 * @param y The top of the graph
	 * @param maxSteps The number of steps in the graph
	 * @param stepWidth The size (in pixels) of each step
	 * @param graphHeight The height of the graph
	 * @param color The color of the graph
	 */
	public PerformanceGraph(float maxValue, int x, int y, int maxSteps, int stepWidth, int graphHeight, Vector4 color, Supplier<? extends Number> stepValueSupplier) {
		this.stepValueSupplier = stepValueSupplier;
		setMaxValue(maxValue);
		
		setX(x);
		setY(y);
		this.width = maxSteps * stepWidth;
		this.height = graphHeight;
		this.maxSteps = maxSteps;
		this.stepWidth = stepWidth;
		
		init();
		
		this.setColor(color);
	}
	
	@Override
	protected void finalize() throws Throwable {
		try {
			Struct.free(color);
		} finally {
			super.finalize();
		}
	}
	
	private static void initProgram() {
		uiProgram = new ShaderProgram(Utils.readFully(PerformanceGraph.class.getResourceAsStream(RenderUtils.SHADERS_PATH + "perf_graph.vert")),
				Utils.readFully(PerformanceGraph.class.getResourceAsStream(RenderUtils.SHADERS_PATH + "perf_graph.frag")));
	}
	
	private void init() {
		if(uiProgram == null)
			initProgram();
		
		vao = RenderUtils.glGenVertexArrays();
		RenderUtils.glBindVertexArray(vao);
		
		float[] graph = {
				getX(), getY(),
				getX(), getY() + getHeight(),
				getX(), getY(),
				getX() + getWidth(), getY()
		};
		
		graphOffset = graph.length;
		
		graphData = BufferUtils.createFloatBuffer(getMaxSteps() * 2);
		stepCount = 0;
		
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, (graphOffset + graphData.capacity()) * Float.BYTES, GL_STREAM_DRAW);
		
		glBufferSubData(GL_ARRAY_BUFFER, 0, (FloatBuffer)BufferUtils.createFloatBuffer(graph.length).put(graph).flip());
		
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		RenderUtils.glBindVertexArray(0);
		
		projectionMatrix = new Matrix4().clearToOrtho(0, RenderUtils.getWidth(), 0, RenderUtils.getHeight(), 0, 1);
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public float getMaxValue() {
		return maxValue;
	}
	
	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
	}
	
	@TakeStruct
	public Vector4 getColor() {
		return color;
	}
	
	public void setColor(Vector4 color) {
		this.color.set(color);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getMaxSteps() {
		return maxSteps;
	}
	
	public int getStepWidth() {
		return stepWidth;
	}
	
	private long elapsedTime;
	
	public void update(long deltaTime) {
		elapsedTime += deltaTime;
		
		while(elapsedTime >= 1e9) {
			elapsedTime -= 1e9;
			
			graphData.clear();
			
			if(stepCount < getMaxSteps()) {
				stepCount++;
			}
			
			for(int a = stepCount * 2 - 2; a >= 2; a -= 2) {
				graphData.put(a, graphData.get(a - 2) - getStepWidth());
				graphData.put(a + 1, graphData.get(a - 1));
			}
			
			float stepHeight = height * stepValueSupplier.get().floatValue() / getMaxValue();
			
			if(Float.isNaN(stepHeight))
				stepHeight = 0;
			
			graphData.put(0, getX() + getWidth() - getStepWidth());
			graphData.put(1, getY() + stepHeight);
			
			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			glBufferSubData(GL_ARRAY_BUFFER, graphOffset * Float.BYTES, graphData);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		}
	}
	
	public void render() {
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_CULL_FACE);
		glDisable(GL_BLEND);
		
		uiProgram.begin();
		
		glUniform4(uiProgram.getUniformLocation("color"), color.toBuffer());
		glUniformMatrix4(uiProgram.getUniformLocation("projectionMatrix"), false, projectionMatrix.toBuffer());
		
		RenderUtils.glBindVertexArray(vao);
		glDrawArrays(GL_LINES, 0, graphOffset / 2);
		glDrawArrays(GL_LINE_STRIP, graphOffset / 2, stepCount);
		RenderUtils.glBindVertexArray(0);
		
		uiProgram.end();
		
		glEnable(GL_BLEND);
		glEnable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
	}
}
