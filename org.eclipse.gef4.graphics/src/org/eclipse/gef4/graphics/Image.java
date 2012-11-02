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
package org.eclipse.gef4.graphics;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.eclipse.gef4.graphics.images.IImageOperation;
import org.eclipse.gef4.graphics.render.IBlitProperties.InterpolationHint;
import org.eclipse.gef4.graphics.render.IGraphics;

// TODO: Provide more methods that are available on a BufferedImage.

/**
 * An {@link Image} stores color and alpha data for a rectangular pixel raster.
 * 
 * @author mwienand
 * 
 */
public class Image {

	/**
	 * The number of data channels in an {@link Image}. GEF4 Graphics
	 * {@link Image}s do always consist of three color channels (red, green, and
	 * blue) and one alpha channel. Currently, the number of channels is kept
	 * constant to ease computations, but this may change in a future version.
	 */
	public static final int NUM_CHANNELS = 4;

	private static BufferedImage getCopy(BufferedImage bufferedImage) {
		BufferedImage copy = new BufferedImage(bufferedImage.getWidth(),
				bufferedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bufferedImage.createGraphics();
		g2d.drawImage(bufferedImage, null, 0, 0);
		g2d.dispose();
		return copy;
	}

	/**
	 * Stores the image data.
	 */
	private BufferedImage bufferedImage = null;

	// TODO: add default constructor. (What should its semantics be?)
	// TODO: add (width, height, ?background-color?) constructor.

	/**
	 * Constructs a new {@link Image} from the given {@link BufferedImage}.
	 * 
	 * @param bufferedImage
	 */
	public Image(BufferedImage bufferedImage) {
		this.bufferedImage = getCopy(bufferedImage);
	}

	/**
	 * <p>
	 * Returns the result of the given {@link IImageOperation} on
	 * <code>this</code> {@link Image} as a new {@link Image}. <code>this</code>
	 * {@link Image} is not modified.
	 * </p>
	 * 
	 * <p>
	 * This is a convenience method to apply several {@link IImageOperation}s in
	 * a row to one {@link Image}: <blockquote>
	 * 
	 * <pre>
	 * image.apply(op0).apply(op1)...;
	 * </pre>
	 * 
	 * </blockquote>
	 * </p>
	 * 
	 * @param op
	 *            the {@link IImageOperation} to apply
	 * @return the result {@link Image} of the given {@link IImageOperation} on
	 *         <code>this</code> {@link Image}
	 */
	public Image apply(IImageOperation op) {
		return op.apply(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Image) {
			if (obj == this) {
				return true;
			}

			Image o = (Image) obj;
			if (getWidth() != o.getWidth() || getHeight() != o.getHeight()) {
				return false;
			}

			for (int x = 0; x < getWidth(); x++) {
				for (int y = 0; y < getHeight(); y++) {
					if (getPixel(x, y) != o.getPixel(x, y)) {
						return false;
					}
				}
			}

			return true;
		}
		return false;
	}

	/**
	 * <p>
	 * Returns a histogram of the alpha-channel values used in this
	 * {@link Image}.
	 * </p>
	 * 
	 * <p>
	 * The histogram is represented by an <code>int</code>-array with
	 * <code>256</code> elements. The number of elements equals the number of
	 * possible alpha-channel values. At every index, the number of pixels with
	 * an alpha-channel value equal to that index is stored in the array.
	 * </p>
	 * 
	 * @return a histogram of the alpha-channel values used in this
	 *         {@link Image}
	 */
	public int[] getAlphaHistogram() {
		int[] hist = new int[256];

		for (int i = 0; i < hist.length; i++) {
			hist[i] = 0;
		}

		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				hist[Color.getPixelAlpha(getPixel(x, y))]++;
			}
		}

		return hist;
	}

	/**
	 * <p>
	 * Returns a histogram of the blue-channel values used in this {@link Image}
	 * .
	 * </p>
	 * 
	 * <p>
	 * The histogram is represented by an <code>int</code>-array with
	 * <code>256</code> elements. The number of elements equals the number of
	 * possible blue-channel values. At every index, the number of pixels with a
	 * blue-channel value equal to that index is stored in the array.
	 * </p>
	 * 
	 * @return a histogram of the blue-channel values used in this {@link Image}
	 */
	public int[] getBlueHistogram() {
		int[] hist = new int[256];

		for (int i = 0; i < hist.length; i++) {
			hist[i] = 0;
		}

		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				hist[Color.getPixelBlue(getPixel(x, y))]++;
			}
		}

		return hist;
	}

	/**
	 * Returns a copy of <code>this</code> {@link Image}.
	 * 
	 * @return a copy of <code>this</code> {@link Image}
	 */
	public Image getCopy() {
		return new Image(this.bufferedImage);
	}

	/**
	 * <p>
	 * Returns a histogram of the green-channel values used in this
	 * {@link Image}.
	 * </p>
	 * 
	 * <p>
	 * The histogram is represented by an <code>int</code>-array with
	 * <code>256</code> elements. The number of elements equals the number of
	 * possible green-channel values. At every index, the number of pixels with
	 * a green-channel value equal to that index is stored in the array.
	 * </p>
	 * 
	 * @return a histogram of the green-channel values used in this
	 *         {@link Image}
	 */
	public int[] getGreenHistogram() {
		int[] hist = new int[256];

		for (int i = 0; i < hist.length; i++) {
			hist[i] = 0;
		}

		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				hist[Color.getPixelGreen(getPixel(x, y))]++;
			}
		}

		return hist;
	}

	/**
	 * Returns the height of <code>this</code> {@link Image}.
	 * 
	 * @return the height of <code>this</code> {@link Image}
	 */
	public int getHeight() {
		return bufferedImage.getHeight();
	}

	/**
	 * <p>
	 * Returns a histogram of the pixel-intensity values used in this
	 * {@link Image}.
	 * </p>
	 * 
	 * <p>
	 * The histogram is represented by an <code>int</code>-array with
	 * <code>256</code> elements. The number of elements equals the number of
	 * possible pixel-intensity values. At every index, the number of pixels
	 * with an intensity value equal to that index is stored in the array.
	 * </p>
	 * 
	 * @return a histogram of the pixel-intensity values used in this
	 *         {@link Image}
	 */
	public int[] getIntensityHistogram() {
		int[] hist = new int[256];

		for (int i = 0; i < hist.length; i++) {
			hist[i] = 0;
		}

		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				hist[Color.computePixelIntensity(getPixel(x, y))]++;
			}
		}

		return hist;
	}

	/**
	 * Returns the ARGB pixel value at the given position.
	 * 
	 * @param x
	 *            x-coordinate of the pixel to return
	 * @param y
	 *            y-coordinate of the pixel to return
	 * @return the ARGB pixel value at the given position
	 */
	public int getPixel(int x, int y) {
		if (x < 0 || x >= getWidth()) {
			throw new IllegalArgumentException("x-coordinate (" + x
					+ ") out of image bounds (0-" + getWidth() + ").");
		}
		if (y < 0 || y >= getHeight()) {
			throw new IllegalArgumentException("y-coordinate (" + y
					+ ") out of image bounds (0-" + getHeight() + ").");
		}
		return bufferedImage.getRGB(x, y);
	}

	/**
	 * <p>
	 * Returns a histogram of the red-channel values used in this {@link Image}.
	 * </p>
	 * 
	 * <p>
	 * The histogram is represented by an <code>int</code>-array with
	 * <code>256</code> elements. The number of elements equals the number of
	 * possible red-channel values. At every index, the number of pixels with a
	 * red-channel value equal to that index is stored in the array.
	 * </p>
	 * 
	 * @return a histogram of the red-channel values used in this {@link Image}
	 */
	public int[] getRedHistogram() {
		int[] hist = new int[256];

		for (int i = 0; i < hist.length; i++) {
			hist[i] = 0;
		}

		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				hist[Color.getPixelRed(getPixel(x, y))]++;
			}
		}

		return hist;
	}

	/**
	 * <p>
	 * Returns a scaled version of this {@link Image}. The dimensions of the
	 * resulting {@link Image} are computed by multiplying this {@link Image}'s
	 * dimensions with the given <i>scaleFactor</i>.
	 * </p>
	 * 
	 * <p>
	 * The interpolation is done exactly as with drawing an {@link Image}
	 * transformed with an {@link IGraphics} using the
	 * {@link InterpolationHint#QUALITY QUALITY} {@link InterpolationHint}.
	 * </p>
	 * 
	 * @param scaleFactor
	 * @return a scaled version of this {@link Image}
	 */
	public Image getScaled(double scaleFactor) {
		int newWidth = (int) (getWidth() * scaleFactor);
		int newHeight = (int) (getHeight() * scaleFactor);
		return getScaledTo(newWidth, newHeight);
	}

	/**
	 * Returns a version of this {@link Image} that is scaled to the passed-in
	 * <i>width</i> and <i>height</i>.
	 * 
	 * @param width
	 *            the width to scale to
	 * @param height
	 *            the height to scale to
	 * @return a scaled version of this {@link Image}
	 */
	public Image getScaledTo(int width, int height) {
		BufferedImage scaled = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = scaled.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.drawImage(bufferedImage, 0, 0, width, height, null);
		g2d.dispose();

		// TODO: Implement the possibility to construct an Image from a
		// BufferedImage, without copying the image data

		// TODO: Integrate the IGraphics / Remove duplicate code.

		return new Image(scaled);
	}

	/**
	 * Cuts out a portion of this {@link Image} starting at the given <i>x</i>
	 * and <i>y</i> coordinates and extending by the given <i>width</i> and
	 * <i>height</i>.
	 * 
	 * @param x
	 *            starting x coordinate for the sub-{@link Image} to cut out
	 * @param y
	 *            starting y coordinate for the sub-{@link Image} to cut out
	 * @param width
	 *            width of the sub-{@link Image}
	 * @param height
	 *            height of the sub-{@link Image}
	 * @return a sub-{@link Image} of this {@link Image}
	 */
	public Image getSubImage(int x, int y, int width, int height) {
		return new Image(bufferedImage.getSubimage(x, y, width, height));
	}

	/**
	 * <p>
	 * Creates a thumbnail {@link Image} from this {@link Image}. The dimensions
	 * of the thumbnail are gueranteed to not exceed the passed-in maximum
	 * <i>width</i> and <i>height</i>.
	 * </p>
	 * 
	 * @param maxWidth
	 *            maximum width of the thumbnail
	 * @param maxHeight
	 *            maximum height of the thumbnail
	 * @return a thumbnail of this {@link Image}
	 */
	public Image getThumbnail(int maxWidth, int maxHeight) {
		int width = getWidth(), height = getHeight();
		double wScale = ((double) maxWidth) / width;
		double hScale = ((double) maxHeight) / height;
		double scale = wScale < hScale ? wScale : hScale;
		return getScaled(scale);
	}

	/**
	 * Returns the width of <code>this</code> {@link Image}.
	 * 
	 * @return the width of <code>this</code> {@link Image}
	 */
	public int getWidth() {
		return bufferedImage.getWidth();
	}

	/**
	 * Assigns the given ARGB value to the pixel at the passed-in position.
	 * 
	 * @param x
	 *            x-coordinate of the pixel to assign a new value
	 * @param y
	 *            y-coordinate of the pixel to assign a new value
	 * @param argb
	 *            the new ARGB value for the pixel
	 */
	public void setPixel(int x, int y, int argb) {
		if (x < 0 || x >= getWidth()) {
			throw new IllegalArgumentException("x-coordinate (" + x
					+ ") out of image bounds (0-" + getWidth() + ").");
		}
		if (y < 0 || y >= getHeight()) {
			throw new IllegalArgumentException("y-coordinate (" + y
					+ ") out of image bounds (0-" + getHeight() + ").");
		}
		bufferedImage.setRGB(x, y, argb);
	}

	// /**
	// * Returns the {@link BufferedImage} that is used to store the image data
	// of
	// * <code>this</code> {@link Image}.
	// *
	// * @return the {@link BufferedImage} that is used to store the image data
	// of
	// * <code>this</code> {@link Image}
	// */
	// public BufferedImage toBufferedImage() {
	// return getCopy(bufferedImage);
	// }

	@Override
	public String toString() {
		// TODO: we need a smarter output, possibly containing the image's width
		// and height, number of channels, bit depth, etc.
		return "Image(bufferedImage = " + bufferedImage + ")";
	}

}