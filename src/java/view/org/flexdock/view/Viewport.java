/*
 * Created on Mar 4, 2005
 */
package org.flexdock.view;

import java.awt.Component;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JTabbedPane;

import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingConstants;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.defaults.DefaultDockingPort;
import org.flexdock.docking.defaults.StandardBorderManager;
import org.flexdock.docking.event.DockingEvent;
import org.flexdock.view.tracking.ViewListener;
import org.flexdock.view.tracking.ViewTracker;

/**
 * @author Christopher Butler
 */
public class Viewport extends DefaultDockingPort implements DockingConstants {
	protected HashSet blockedRegions;
	
	static {
		DockingManager.setDockingStrategy(Viewport.class, View.VIEW_DOCKING_STRATEGY);
	}

	public Viewport() {
		this(null);
	}
	
	public Viewport(String portId) {
		super(portId);
		blockedRegions = new HashSet(5);
		setBorderManager(new StandardBorderManager());
	}

	
	public void setRegionBlocked(String region, boolean b) {
		if(isValidDockingRegion(region)) {
			if(b)
				blockedRegions.add(region);
			else
				blockedRegions.remove(region);
		}
	}
	
	public boolean isDockingAllowed(Component comp, String region) {
		// if we're already blocked, then no need to interrogate
		// the components in this dockingport
		boolean blocked = !super.isDockingAllowed(comp, region);
		if(blocked)
			return false;
		
		// check to see if the region itself has been blocked for some reason
		if(blockedRegions.contains(region))
			return false;
		
		// by default, allow docking in non-CENTER regions
		if(!CENTER_REGION.equals(region))
			return true;
		
		// allow docking in the CENTER if there's nothing already there,
		// of if there's no Dockable associated with the component there
		Dockable dockable = getCenterDockable();
		if(dockable==null)
			return true;
		
		// otherwise, only allow docking in the CENTER if the dockable
		// doesn't mind
		return !dockable.getDockingProperties().isTerritoryBlocked(region).booleanValue();
	}
	
	public boolean dock(Dockable dockable) {
		return dock(dockable, CENTER_REGION);
	}
	
	protected JTabbedPane createTabbedPane() {
		JTabbedPane pane = super.createTabbedPane();
		pane.addChangeListener(ViewListener.getInstance());
		return pane;
	}
	

    public Set getViewset() {
    	// return ALL views, recursing to maximum depth
    	return getDockableSet(-1, 0, View.class);
    }
    
    public Set getViewset(int depth) {
    	// return all views, including subviews up to the specified depth
    	return getDockableSet(depth, 0, View.class);
    }
    

    




	
	public void dockingComplete(DockingEvent evt) {
		Object src = evt.getSource();
		if(!(src instanceof View) || !isShowing() || evt.getNewDockingPort()!=this)
			return;

		ViewTracker.requestViewActivation((View)src);
	}
	
	public String toString() {
		String id = getPersistentId();
		return "ViewPort[id=" + id + "]";
	}
}
