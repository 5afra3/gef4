/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.graphics.render;

import org.eclipse.gef4.geometry.planar.Path;

/**
 * <p>
 * An {@link IFillProperties} manages the properties used when displaying a
 * geometric object using one of the
 * {@link IGraphics#fill(org.eclipse.gef4.geometry.planar.IMultiShape)
 * fill(IMultiShape)},
 * {@link IGraphics#fill(org.eclipse.gef4.geometry.planar.IShape) fill(IShape)},
 * or {@link IGraphics#fill(org.eclipse.gef4.geometry.planar.Path) fill(Path)}
 * methods.
 * </p>
 * 
 * <p>
 * The modifiable properties are:
 * <ul>
 * <li>the anti-aliasing setting ({@link #isAntialiasing()},
 * {@link #setAntialiasing(boolean)})</li>
 * <li>the {@link IFillMode} in use ({@link #getMode()},
 * {@link #setMode(IFillMode)})</li>
 * </ul>
 * </p>
 * 
 * @author mwienand
 * 
 */
public interface IFillProperties extends IGraphicsProperties {

	/**
	 * Anti-aliasing is enabled per default.
	 */
	static final boolean DEFAULT_ANTIALIASING = true;

	/**
	 * The default {@link IFillMode} is a {@link ColorFill}.
	 */
	static final ColorFill DEFAULT_MODE = new ColorFill();

	/**
	 * Applies the {@link IFillProperties} stored in this object to the
	 * underlying graphics system of the passed-in {@link IGraphics}. This
	 * operation renders the given {@link Path}. It is called when the
	 * {@link IGraphics#fill(Path)} method is called.
	 * 
	 * @param g
	 *            the {@link IGraphics} to apply the {@link IFillProperties} on
	 * @param p
	 *            the {@link Path} to render
	 */
	void applyOn(IGraphics g, Path p);

	// /**
	// * Returns the {@link Color fill color} associated with this
	// * {@link IFillProperties}.
	// *
	// * @return the current {@link Color fill color}
	// */
	// Color getColor();

	@Override
	IFillProperties getCopy();

	// GradientFill getGradient();
	//
	// Image getImage();
	//
	// FillMode getMode();

	/**
	 * Returns a copy of the currently in-use {@link IFillMode}.
	 * 
	 * @return a copy of the currently in-use {@link IFillMode}
	 */
	IFillMode getMode();

	/**
	 * Returns <code>true</code> if anti-aliasing is enabled. Otherwise,
	 * <code>false</code> is returned.
	 * 
	 * @return <code>true</code> if anti-aliasing is enabled, otherwise
	 *         <code>false</code>
	 */
	boolean isAntialiasing();

	/**
	 * Enables or disables anti-aliasing for this {@link IFillProperties}. When
	 * given a <code>true</code> value, anti-aliasing is enabled. Otherwise,
	 * anti-aliasing is disabled.
	 * 
	 * @param antialiasing
	 *            the new anti-aliasing setting for this {@link IFillProperties}
	 * @return <code>this</code> for convenience
	 */
	IFillProperties setAntialiasing(boolean antialiasing);

	/**
	 * Sets the {@link IFillMode} to use for filling geometry objects.
	 * 
	 * @param mode
	 * @return <code>this</code> for convenience
	 */
	IFillProperties setMode(IFillMode mode);

	// /**
	// * Sets the {@link Color fill color} associated with this
	// * {@link IFillProperties} to the given value.
	// *
	// * @param fillColor
	// * the new {@link Color fill color}
	// * @return <code>this</code> for convenience
	// */
	// IFillProperties setColor(Color fillColor);
	//
	// IFillProperties setGradient(GradientFill gradient);
	//
	// IFillProperties setImage(Image image);
	//
	// IFillProperties setMode(FillMode mode);

}
