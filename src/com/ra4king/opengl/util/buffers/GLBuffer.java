package com.ra4king.opengl.util.buffers;

import static org.lwjgl.opengl.GL15.*;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GLContext;

/**
 * @author Roi Atalla
 */
public abstract class GLBuffer {
	protected final int name;
	protected final int type;
	protected final int size;
	protected final boolean isStreaming;
	
	public GLBuffer(int type, int size, boolean isStreaming) {
		this.name = glGenBuffers();
		this.type = type;
		this.size = size;
		this.isStreaming = isStreaming;
		
		init();
	}
	
	public static GLBuffer createBuffer(int type, int size, boolean isStreaming) {
		if(isStreaming) {
			if(GLContext.getCapabilities().GL_ARB_buffer_storage)
				return new BufferStorage(type, size, isStreaming, 1);
			
			if(GLContext.getCapabilities().GL_ARB_map_buffer_range)
				return new MappedBuffer(type, size, isStreaming);
		}
		
		return new BufferSubData(type, size, isStreaming, true);
	}
	
	public int getName() {
		return name;
	}
	
	public int getType() {
		return type;
	}
	
	public int getSize() {
		return size;
	}
	
	protected void init() {
		glBindBuffer(type, name);
		glBufferData(type, size, isStreaming ? GL_STREAM_DRAW : GL_STATIC_DRAW);
	}
	
	public abstract ByteBuffer bind(int offset, int size);
	
	public abstract void unbind();
}
