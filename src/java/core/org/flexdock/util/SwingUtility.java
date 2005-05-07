/*
 * Created on Aug 23, 2004
 */
package org.flexdock.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.flexdock.docking.DockingPort;
import org.flexdock.docking.defaults.DefaultDockingPort;

/**
 * @author Christopher Butler
 */
public class SwingUtility {

	public static Component toComponent(Object obj) {
		return obj instanceof Component? (Component)obj: null;
	}
	
	public static void revalidateComponent(Component comp) {
		if (comp instanceof JComponent)
			 ((JComponent) comp).revalidate();
	}
	
	public static void drawRect(Graphics g, Rectangle r) {
		if(g==null || r==null)
			return;
			
		g.drawRect(r.x, r.y, r.width, r.height);
	}
	
	public static DockingPort[] getChildPorts(DockingPort port) {
		if(!(port instanceof DefaultDockingPort))
			return new DockingPort[0];
	
		DefaultDockingPort parent = (DefaultDockingPort)port;
		Component docked = parent.getDockedComponent();
		if(!(docked instanceof JSplitPane))
			return new DockingPort[0];
			
		JSplitPane split = (JSplitPane)docked;
		DockingPort left = null;
		DockingPort right = null;
		if(split.getLeftComponent() instanceof DockingPort)
			left = (DockingPort)split.getLeftComponent();
		if(split.getRightComponent() instanceof DockingPort)
			right = (DockingPort)split.getRightComponent();
		
		if(left==null && right==null)
			return new DockingPort[0];
		if(left==null)
			return new DockingPort[] {right};
		if(right==null)
			return new DockingPort[] {left};
		return new DockingPort[] {left, right};
			
	}
	
	public static Point[] getPoints(Rectangle rect) {
		return getPoints(rect, null);
	}
	
	public static Point[] getPoints(Rectangle rect, Component convertFromScreen) {
		if(rect==null)
			return null;
		
		Rectangle r = (Rectangle)rect.clone();
		Point p = r.getLocation();
		if(convertFromScreen!=null)
			SwingUtilities.convertPointFromScreen(p, convertFromScreen);
		
		r.setLocation(p);
		
		return new Point[] {
			p, 
			new Point(p.x + r.width, p.y),
			new Point(p.x + r.width, p.y+r.height),
			new Point(p.x, p.y+r.height)
		};
    }

    public static final void centerOnScreen(Window window) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = window.getSize();

        if (windowSize.height > screenSize.height)
            windowSize.height = screenSize.height;

        if (windowSize.width > screenSize.width)
            windowSize.width = screenSize.width;

        window.setLocation((screenSize.width - windowSize.width) / 2,
                (screenSize.height - windowSize.height) / 2);
    }

    public static void center(Window window, Component parent) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        Rectangle bounds = new Rectangle(parent.getLocationOnScreen(), parent.getSize());

        int w = window.getWidth();
        int h = window.getHeight();

        // center according to parent

        int x = ((int) bounds.getCenterX()) - w / 2;
        int y = ((int) bounds.getCenterY()) - h / 2;

        // does it fit on screen?

        if (x < 0)
            x = 0;
        else if (x + w > screenSize.getWidth())
            x = ((int) screenSize.getWidth()) - w;

        if (y < 0)
            y = 0;
        else if (y + h > screenSize.getHeight())
            y = ((int) screenSize.getHeight()) - h;

        // done

        window.setBounds(x, y, w, h);
    }
    
    public static Container getContentPane(Component c) {
		RootWindow rootWin = RootWindow.getRootContainer((Component)c);
		return rootWin==null? null: rootWin.getContentPane();
    }
    
    public static void setPlaf(String lookAndFeelClassName) {
    	try {
    		UIManager.setLookAndFeel(lookAndFeelClassName);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public static void add(Point p1, Point p2) {
    	if(p1!=null && p2!=null) {
    		p1.x += p2.x;
    		p1.y += p2.y;
    	}
    }
    
    public static void subtract(Point p1, Point p2) {
    	if(p1!=null && p2!=null) {
    		p1.x -= p2.x;
    		p1.y -= p2.y;
    	}
    }
    
    public static void translate(Component src, Polygon poly, Component dest) {
    	if(src==null || poly==null || dest==null)
    		return;
    	
    	Rectangle srcRect = src.getBounds();
    	srcRect.setLocation(0, 0);
    	
    	Rectangle destRect = SwingUtilities.convertRectangle(src, srcRect, dest);
    	
    	int deltaX = destRect.x - srcRect.x;
    	int deltaY = destRect.y - srcRect.y;
    	int len = poly.npoints;
    	
    	for(int i=0; i<len; i++) {
    		poly.xpoints[i] += deltaX;
    		poly.ypoints[i] += deltaY;
    	}
    }
    
    public static void focus(Component c) {
		RootWindow window = RootWindow.getRootContainer(c);
		if(window==null)
			return;

		Component root = window.getRootContainer();
		for(Component parent=c.getParent(); parent!=root; parent=c.getParent()) {
			if(parent instanceof JTabbedPane) {
				((JTabbedPane)parent).setSelectedComponent(c);
			}
			c = parent;
		}
	    c.requestFocus();
    }
    
    
    public static Component getNearestFocusableComponent(Component c) {
    	return getNearestFocusableComponent(c, null);
    }
    
	public static Component getNearestFocusableComponent(Component c, Container desiredRoot) {
		if(c==null) 
			c = desiredRoot;
		if(c==null)
			c = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
		
		boolean cachedFocusCycleRoot = false;
		// make the desiredRoot into a focusCycleRoot
		if(desiredRoot!=null) {
			cachedFocusCycleRoot = desiredRoot.isFocusCycleRoot();
			if(!cachedFocusCycleRoot)
				desiredRoot.setFocusCycleRoot(true);
		}
		
		Container focusRoot = null;
		if(c instanceof Container) {
			Container cnt = (Container)c;
			focusRoot = cnt.isFocusCycleRoot(cnt)? cnt: cnt.getFocusCycleRootAncestor();
		}
		else 
			focusRoot = c.getFocusCycleRootAncestor();
		
		Component focuser = null;
		if(focusRoot!=null)
			focuser = focusRoot.getFocusTraversalPolicy().getLastComponent(focusRoot);
		
		// restore the desiredRoot to its previous state
		if(desiredRoot!=null && !cachedFocusCycleRoot) {
			desiredRoot.setFocusCycleRoot(cachedFocusCycleRoot);			
		}
		return focuser;
	}
	
	public static void activateWindow(Component c) {
		RootWindow window = RootWindow.getRootContainer(c);
		if(window!=null && !window.isActive())
			window.toFront();
	}
	
    /* Color utility */
    
    /**
    * @param color
    * @param factor
    * @return a new color, brighter than the one passed as argument by a percentage factor
    * 
    * <br>author Cyril Gambis  - [Mar 17, 2005]
    */
   public static Color brighter(Color color, double factor) {
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();

		int i = (int) (1.0 / (1.0 - factor));
		if (red == 0 && green == 0 && blue == 0) {
			return new Color(i, i, i);
		}
		if (red > 0 && red < i) {
			red = i;
		}
		if (green > 0 && green < i) {
			green = i;
		}
		if (blue > 0 && blue < i) {
			blue = i;
		}

		return new Color(Math.min((int) (red / factor), 255), Math.min((int) (green / factor), 255), Math.min((int) (blue / factor), 255));
	}

	/**
	 * @param color
	 * @param factor
	 * @return a new color, darker than the one passed as argument by a percentage factor
	 * 
	 * <br>author Cyril Gambis  - [Mar 17, 2005]
	 */
	public static Color darker(Color color, double factor) {
		return new Color(Math.max((int) (color.getRed() * factor), 0), Math.max((int) (color.getGreen() * factor), 0), Math.max((int) (color.getBlue() * factor), 0));
	}

	/**
	 * @param color
	 * @return the grey color corresponding to the color passed as parameter
	 * 
	 * <br>author Cyril Gambis  - [Mar 17, 2005]
	 */
	public static Color grayScale(Color color) {
		int grayTone = ((color.getRed() + color.getGreen() + color.getBlue())/3);
		return new Color(grayTone, grayTone, grayTone);
	}
	
	public static void putClientProperty(Component c, Object key, Object value) {
		if(c instanceof JComponent) {
			((JComponent)c).putClientProperty(key, value);
		}
	}
	
	public static Object getClientProperty(Component c, Object key) {
		if(c instanceof JComponent) {
			return ((JComponent)c).getClientProperty(key);
		}
		return null;
	}
	
	public static void removeClientProperty(Component c, Object key) {
		if(c instanceof JComponent) {
			((JComponent)c).putClientProperty(key, null);
		}
	}
	
	public static Window getActiveWindow() {
		KeyboardFocusManager mgr = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		return mgr.getActiveWindow();
	}

	public static BufferedImage createImage(Component comp) {
		if(comp==null)
			return null;
		
		BufferedImage image = (BufferedImage)comp.createImage(comp.getWidth(), comp.getHeight());
		Graphics g = image.createGraphics();
		comp.paintAll(g);
		return image;
	}
}
