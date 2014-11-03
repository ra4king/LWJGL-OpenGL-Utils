package com.ra4king.opengl.util.buffers;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL44.*;

import java.nio.ByteBuffer;

/**
 * @author Roi Atalla
 */
public class BufferStorage extends GLBuffer {
	private ByteBuffer mappedBuffer;
	private final int numBuffers;
	private int bufferIndex;
	
	public BufferStorage(int type, int size, boolean isStreaming, int numBuffers) {
		super(type, size, isStreaming);
		
		this.numBuffers = numBuffers;
		bufferIndex = 0;
		
		init();
	}
	
	@Override
	protected void init() {
		if(numBuffers == 0)
			return;
		
		glBindBuffer(type, name);
		
		int flags = GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT;
		
		glBufferStorage(type, numBuffers * size, flags);
		mappedBuffer = glMapBufferRange(type, 0, numBuffers * size, flags, null);
	}
	
	public void nextBuffer() {
		bufferIndex = (bufferIndex + 1) % numBuffers;
	}
	
	public int getBufferIndex() {
		return bufferIndex;
	}
	
	public int getNumBuffers() {
		return numBuffers;
	}
	
	@Override
	public ByteBuffer bind(int offset, int size) {
		int bufferOffset = bufferIndex * this.size;
		mappedBuffer.limit(bufferOffset + offset + size).position(bufferOffset + offset);
		return mappedBuffer;
	}
	
	@Override
	public void unbind() {
		// nothing
	}
}
