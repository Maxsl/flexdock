package org.flexdock.docking.drag;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;

import javax.swing.JComponent;

import org.flexdock.docking.DockingPort;
import org.flexdock.docking.drag.preview.AlphaPreview;
import org.flexdock.docking.drag.preview.DragPreview;


public class DockingPortCover extends JComponent {
	private Rectangle northRegion;
	private Rectangle southRegion;
	private Rectangle eastRegion;
	private Rectangle westRegion;
	private Rectangle centerRegion;
	private Rectangle[] regions;
	private HashMap regionMap;
	private HashMap previewMap;
	
	private DragPreview previewDelegate;
	private DockingPort dockingPort;
	private String currentRegion;
	private Point currentMouse;
	
	public DockingPortCover(DockingPort port) {
		dockingPort = port;
//		previewDelegate = new XORPreview();
		previewDelegate = new AlphaPreview();
		
		northRegion = new Rectangle();
		southRegion = new Rectangle();
		eastRegion = new Rectangle();
		westRegion = new Rectangle();
		centerRegion = new Rectangle();
		
		regions = new Rectangle[] {northRegion, southRegion, eastRegion, westRegion, centerRegion };
		regionMap = new HashMap();
	}

	String getDragTokenRegion(DragToken token) {
		currentMouse = token.getCurrentMouse(this);
		return getRegion(currentMouse);
	}
	
	DockingPort getPort() {
		return dockingPort;
	}

	public void doLayout() {
		super.doLayout();
		int h3 = getHeight()/3;
		int w3 = getWidth()/3;
		
		northRegion.setBounds(0, 0, getWidth(), h3);
		southRegion.setBounds(0, getHeight()-h3, getWidth(), h3);
		eastRegion.setBounds(getWidth()-w3, 0, w3, getHeight());
		westRegion.setBounds(0, 0, w3, getHeight());
		centerRegion.setBounds(getBounds());
		
		updateRegionMap();
	}
	
	private void updateRegionMap() {
		regionMap.put(northRegion, DockingPort.NORTH_REGION);
		regionMap.put(southRegion, DockingPort.SOUTH_REGION);
		regionMap.put(eastRegion, DockingPort.EAST_REGION);
		regionMap.put(westRegion, DockingPort.WEST_REGION);
		regionMap.put(centerRegion, DockingPort.CENTER_REGION);
	}
	
	void setCurrentRegion(String region) {
		currentRegion = region;
	}
	
	String getCurrentRegion() {
		return currentRegion;
	}


	protected void paintComponent(Graphics g) {
		if(currentRegion==null)
			return;

		// Testing code during drag operation
		/*
		SwingUtility.drawRect(g, northRegion);
		SwingUtility.drawRect(g, southRegion);
		SwingUtility.drawRect(g, eastRegion);
		SwingUtility.drawRect(g, westRegion);
		*/
		
		Rectangle r = getPreviewRect(currentMouse);
		previewDelegate.drawPreview((Graphics2D)g, r);
	}
	
	private Rectangle getPreviewRect(Point mouse) {
		int w = getWidth();
		int h = getHeight();
		int h2 = getHeight()/2;
		int w2 = getWidth()/2;
		
		if(northRegion.contains(mouse))
			return new Rectangle(0, 0, w, h2);
		if(southRegion.contains(mouse))
			return new Rectangle(0, h-h2, w, h2);
		if(eastRegion.contains(mouse))
			return new Rectangle(w-w2, 0, w2, h);
		if(westRegion.contains(mouse))
			return new Rectangle(0, 0, w2, h);
		return new Rectangle(0, 0, w, h);
	}
	
	private String getRegion(Point mouse) {
		for(int i=0; i<regions.length; i++) {
			if(regions[i].contains(mouse))
				return (String)regionMap.get(regions[i]);
		}
		return DockingPort.CENTER_REGION;
	}
}