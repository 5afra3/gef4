package org.eclipse.gef4.geometry.planar;

import org.eclipse.gef4.geometry.Point;

/**
 * a combination of rectangles...
 * 
 * @author nyssen
 * 
 */
public class Region extends AbstractGeometry implements IPolyShape {

	public Rectangle[] getShapes() {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	public boolean contains(Point p) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	public boolean contains(Rectangle r) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	public Rectangle getBounds() {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	public boolean intersects(Rectangle r) {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	public Path toPath() {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

	public IGeometry getCopy() {
		throw new UnsupportedOperationException("Not yet implemented.");
	}

}
