package com.ra4king.opengl.util.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.ra4king.opengl.util.Mesh;
import com.ra4king.opengl.util.Mesh.Attribute;
import com.ra4king.opengl.util.Mesh.RenderCommand;
import com.ra4king.opengl.util.StringUtil;

/**
 * @author Roi Atalla
 */
public class XMLMeshLoader {
	private ArrayList<RenderCommand> renderCommands = new ArrayList<>();
	private ArrayList<Attribute> attributes = new ArrayList<>();
	private ByteBuffer attributeData, indexData;
	
	private HashMap<String,ArrayList<Attribute>> renderVariations = new HashMap<>();
	
	public XMLMeshLoader(URL url) throws IOException, XMLMeshParseException {
		attributeData = ByteBuffer.allocate(0).order(ByteOrder.nativeOrder());
		
		class VAO {
			String name;
			ArrayList<Integer> sources;
			
			VAO(String name) {
				this.name = name;
				
				sources = new ArrayList<>();
			}
		}
		
		ArrayList<VAO> vaos = null;
		
		try(InputStream is = url.openStream()) {
			XmlPullParser xml = XmlPullParserFactory.newInstance().newPullParser();
			xml.setInput(is, "UTF-8");
			
			xml.next();
			
			xml.require(XmlPullParser.START_TAG, null, "mesh");
			
			do {
				switch(xml.nextTag()) {
					case XmlPullParser.START_TAG:
						switch(xml.getName()) {
							case "attribute": {
								String index = xml.getAttributeValue(null, "index");
								String type = xml.getAttributeValue(null, "type");
								String size = xml.getAttributeValue(null, "size");
								
								if(index == null)
									throw new XMLMeshParseException("<attribute> missing 'index'");
								if(type == null)
									throw new XMLMeshParseException("<attribute> missing 'type'");
								if(size == null)
									throw new XMLMeshParseException("<attribute> missing 'size'");
								
								Attribute attrib = new Attribute(Integer.parseInt(index), type, Integer.parseInt(size));
								attributes.add(attrib);
								
								xml.next();
								xml.require(XmlPullParser.TEXT, null, null);
								
								attributeData = attrib.storeData(attributeData, StringUtil.clean(StringUtil.split(xml.getText().trim().replace("\r\n", " ").replace('\n', ' '), ' ')));
								
								xml.next();
								xml.require(XmlPullParser.END_TAG, null, "attribute");
								
								break;
							}
							case "indices": {
								if(indexData == null)
									indexData = ByteBuffer.allocate(0).order(ByteOrder.nativeOrder());
								
								String primitive = xml.getAttributeValue(null, "cmd");
								String type = xml.getAttributeValue(null, "type");
								
								if(primitive == null)
									throw new XMLMeshParseException("<indices> missing 'cmd'");
								if(type == null)
									throw new XMLMeshParseException("<indices> missing 'type'");
								
								RenderCommand cmd = new RenderCommand(primitive, type);
								renderCommands.add(cmd);
								
								xml.next();
								xml.require(XmlPullParser.TEXT, null, null);
								
								indexData = cmd.storeIndices(indexData, StringUtil.clean(StringUtil.split(xml.getText().trim().replace("\r\n", " ").replace('\n', ' '), ' ')));
								
								xml.next();
								xml.require(XmlPullParser.END_TAG, null, "indices");
								
								break;
							}
							case "vao": {
								if(vaos == null)
									vaos = new ArrayList<>();
								
								String name = xml.getAttributeValue(null, "name");
								
								if(name == null)
									throw new XMLMeshParseException("<vao> missing 'name'");
								
								VAO vao = new VAO(name);
								vaos.add(vao);
								
								while(xml.nextTag() == XmlPullParser.START_TAG) {
									xml.require(XmlPullParser.START_TAG, null, "source");
									
									String attrib = xml.getAttributeValue(null, "attrib");
									
									if(attrib == null)
										throw new XMLMeshParseException("<source> missing 'attrib'");
									
									vao.sources.add(Integer.parseInt(attrib));
									xml.nextTag();
									xml.require(XmlPullParser.END_TAG, null, "source");
								}
								
								xml.require(XmlPullParser.END_TAG, null, "vao");
								
								break;
							}
							case "arrays": {
								String primitive = xml.getAttributeValue(null, "cmd");
								String start = xml.getAttributeValue(null, "start");
								String count = xml.getAttributeValue(null, "count");
								
								if(primitive == null)
									throw new XMLMeshParseException("<arrays> missing 'cmd'");
								if(start == null)
									throw new XMLMeshParseException("<arrays> missing 'start'");
								if(count == null)
									throw new XMLMeshParseException("<arrays> missing 'count'");
								
								RenderCommand cmd = new RenderCommand(primitive, Integer.parseInt(start), Integer.parseInt(count));
								renderCommands.add(cmd);
								
								xml.next();
								xml.require(XmlPullParser.END_TAG, null, "arrays");
								
								break;
							}
							default:
								throw new XMLMeshParseException("Invalid TAG name: " + xml.getName());
						}
						
						break;
				}
			} while(xml.next() != XmlPullParser.END_DOCUMENT);
		} catch(XmlPullParserException exc) {
			throw new XMLMeshParseException(exc.getMessage());
		}
		
		if(attributes.size() == 0)
			throw new XMLMeshParseException("There must be at least 1 set of attributes.");
		if(renderCommands.size() == 0)
			throw new XMLMeshParseException("There must be at least 1 render command.");
		
		attributeData.flip();
		attributeData = (ByteBuffer)BufferUtils.createByteBuffer(attributeData.remaining()).put(attributeData).flip();
		
		if(indexData != null) {
			indexData.flip();
			indexData = (ByteBuffer)BufferUtils.createByteBuffer(indexData.remaining()).put(indexData).flip();
		}
		
		if(vaos != null) {
			for(VAO v : vaos) {
				ArrayList<Attribute> variation = new ArrayList<>();
				
				for(Attribute attrib : attributes) {
					if(v.sources.contains(attrib.index)) {
						variation.add(attrib);
					}
				}
				
				renderVariations.put(v.name, variation);
			}
		}
	}
	
	public Mesh createDefaultMesh() {
		return new Mesh(attributeData, attributes, renderCommands, indexData);
	}
	
	public Mesh createMesh(String name) {
		return new Mesh(attributeData, renderVariations.get(name), renderCommands, indexData);
	}
	
	public static Mesh createMesh(URL url) throws IOException, XMLMeshParseException {
		return new XMLMeshLoader(url).createDefaultMesh();
	}
	
	public static class XMLMeshParseException extends RuntimeException {
		public XMLMeshParseException(String message) {
			super(message);
		}
		
		public XMLMeshParseException(String message, Throwable t) {
			super(message, t);
		}
	}
}
