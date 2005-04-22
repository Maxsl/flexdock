/*
 * Created on Apr 18, 2005
 */
package org.flexdock.dockbar;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import org.flexdock.dockbar.event.ResizeListener;
import org.flexdock.docking.Dockable;
import org.flexdock.plaf.common.border.SlideoutBorder;

/**
 * @author Christopher Butler
 */
public class ViewPane extends JPanel implements SwingConstants, MouseListener, MouseMotionListener {
	private static final Dimension RESIZE_DIMS = new Dimension(3, 3);
	public static final int UNSPECIFIED_PREFERRED_SIZE = -1;
	private DockbarManager manager;
	private JPanel dragEdge;
	private int prefSize;

	
	public ViewPane(DockbarManager mgr) {
		super(new BorderLayout(0, 0));
		setBorder(new SlideoutBorder());
		
		manager = mgr;
		prefSize = UNSPECIFIED_PREFERRED_SIZE;
		
		dragEdge = new JPanel();
		dragEdge.setPreferredSize(RESIZE_DIMS);
		
		ResizeListener listener = new ResizeListener(mgr);
		dragEdge.addMouseListener(listener);
		dragEdge.addMouseMotionListener(listener);
		
		updateOrientation();
		
		// intercept rouge mouse events so they don't fall 
		// through to the content pane
		addMouseListener(this);
		addMouseMotionListener(this);
	}


	
	public void updateContents() {
		// remove the currently docked component
		Component[] children = getComponents();
		for(int i=0; i<children.length; i++) {
			if(children[i]!=dragEdge)
				remove(children[i]);
		}

		// add the new component
		Dockable d = manager.getActiveDockable();
		Component c = d==null? null: d.getDockable();
		if(c!=null)
			add(c, BorderLayout.CENTER);
	}

	
	public void updateOrientation() {
		Border border = getBorder();
		if(border instanceof SlideoutBorder)
			((SlideoutBorder)border).setOrientation(manager.getActiveEdge());
		
		// update the drag edge
		remove(dragEdge);
		add(dragEdge, getEdgeRegion());
		dragEdge.setCursor(getResizeCursor());

		// revalidate
		revalidate();
	}
	
	private String getEdgeRegion() {
		int orientation = manager.getActiveEdge();
		switch(orientation) {
			case TOP:
				return BorderLayout.SOUTH;
			case BOTTOM:
				return BorderLayout.NORTH;
			case RIGHT:
				return BorderLayout.WEST;
			default:
				return BorderLayout.EAST;
		}
	}
	
	public Cursor getResizeCursor() {
		int orientation = manager.getActiveEdge();
		return orientation==LEFT || orientation==RIGHT? Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR): Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
	}

	public void mouseClicked(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}
	public void mouseDragged(MouseEvent e) {
	}
	public void mouseMoved(MouseEvent e) {
	}
	
	
	public int getPrefSize() {
		return prefSize;
	}
	public void setPrefSize(int prefSize) {
		this.prefSize = prefSize;
	}
}