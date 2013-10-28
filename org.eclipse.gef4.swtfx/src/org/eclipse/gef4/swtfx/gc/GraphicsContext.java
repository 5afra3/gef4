/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
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
package org.eclipse.gef4.swtfx.gc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.eclipse.gef4.geometry.convert.swt.SWT2Geometry;
import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Arc;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.CubicCurve;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.geometry.planar.Path.Segment;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.PolyBezier;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.geometry.planar.RoundedRectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;

/**
 * <p>
 * The {@link GraphicsContext} is an alternative graphics API wrapping around
 * SWT's {@link GC} that is inspired by the JavaFX GraphicsContext2D (which
 * itself is inspired by the HTML 5 Canvas API).
 * </p>
 * 
 * <p>
 * Note, that not all of the JavaFx GraphicsContext2D functionality is provided
 * by this {@link GraphicsContext}. This is the list of missing features:
 * <ul>
 * <li>void appendSVGPath(String svgPath);</li>
 * <li>void applyEffect(Effect effect);</li>
 * <li>void setEffect(Effect effect);</li>
 * <li>PixelWriter getPixelWriter();</li>
 * <li>void setBlendMode(BlendMode);</li>
 * </ul>
 * </p>
 * 
 * <p>
 * Note, that you have to call the {@link #cleanUp()} method when you are done
 * with the {@link GraphicsContext} in order to reset the underlying {@link GC}
 * to its prior state.
 * </p>
 * 
 * @author mwienand
 * 
 */
public class GraphicsContext {

	/*
	 * The various apply*() methods are used to modify the underlying {@link
	 * GC}. When the user changes an attribute, this change is passed along to
	 * the current {@link GraphicsContextState} and the appropriate apply*()
	 * method is called to update the {@link GC}.
	 */

	// TODO: remove PaintType
	private static enum PaintType {
		FILL, STROKE,
	}

	/*
	 * TODO: evaluate if WITH_ALPHA() is a good idea
	 */
	private static void WITH_ALPHA(GraphicsContext g, int alpha, Runnable code) {
		int globalAlpha = g.getGlobalAlpha();
		if (alpha != 255) {
			g.gc.setAlpha((int) (alpha * globalAlpha / 255d));
		}
		code.run();
		if (alpha != 255) {
			g.gc.setAlpha(globalAlpha);
		}
	}

	/**
	 * The current {@link Path} of this {@link GraphicsContext}.
	 */
	private Path path;

	/**
	 * The SWT {@link GC} associated with this {@link GraphicsContext}.
	 */
	private GC gc;

	/**
	 * The initial {@link GC}'s state is saved so that we can restore it later.
	 */
	private final GcState initialGcState;

	/**
	 * The {@link #states} {@link Stack} is used to manage graphics properties.
	 */
	private Stack<GraphicsContextState> states = new Stack<GraphicsContextState>();

	private org.eclipse.swt.graphics.Path swtPathClip;
	private org.eclipse.swt.graphics.Path swtPathFill;
	private org.eclipse.swt.graphics.Color swtColorPaint;
	private org.eclipse.swt.graphics.Pattern swtPatternPaint;
	private Image swtImageGradient;
	private Transform swtTransform;

	/**
	 * Constructs a new {@link GraphicsContext} associated with the given
	 * {@link GC}.
	 * 
	 * @param gc
	 *            the SWT {@link GC} to use when drawing
	 */
	public GraphicsContext(GC gc) {
		this.gc = gc;
		initialGcState = new GcState(gc);
		states.push(new GraphicsContextState());
		applyStateToGc(states.peek());
	}

	private void afterShowText() {
		switch (states.peek().getTextBaseline()) {
		case TOP:
			break;
		case BASELINE:
			translate(0, gc.getFontMetrics().getAscent());
			break;
		case BOTTOM:
			translate(0, gc.getFontMetrics().getHeight());
			break;
		case CENTER:
			translate(0, gc.getFontMetrics().getHeight() / 2);
			break;
		default:
			throw new IllegalStateException("Unknown TextVPos: "
					+ states.peek().getTextBaseline());
		}
	}

	/**
	 * Appends the given {@link Path} <i>p</p> to the current {@link Path} of
	 * this {@link GraphicsContext}. The {@link Segment}s of both {@link Path}s
	 * are concatenated. The resulting {@link Path} is used as the current
	 * {@link Path} of this {@link GraphicsContext}.
	 * 
	 * @param p
	 *            the {@link Path} to append to the current {@link Path}
	 */
	private void appendPath(Path p) {
		if (path == null) {
			// TODO: Is a missing beginPath() an error?
			beginPath();
		}

		List<Segment> segs = new LinkedList<Segment>();

		if (path != null && p != null) {
			segs.addAll(Arrays.asList(path.getSegments()));
			segs.addAll(Arrays.asList(p.getSegments()));
		}

		path = new Path(segs.toArray(new Segment[] {}));
	}

	/**
	 * @param clip
	 */
	private void applyClip(Path clip) {
		if (clip == null) {
			gc.setClipping((org.eclipse.swt.graphics.Rectangle) null);
		} else {
			if (swtPathClip != null && !swtPathClip.isDisposed()) {
				swtPathClip.dispose();
				swtPathClip = null;
			}
			swtPathClip = SwtUtils.createSwtPath(clip, gc.getDevice());
			gc.setClipping(swtPathClip);
		}
	}

	private void applyDashes(double[] dashesByReference) {
		float[] dashes = new float[dashesByReference.length];
		for (int i = 0; i < dashes.length; i++) {
			dashes[i] = (float) dashesByReference[i];
		}
		LineAttributes lineAttrs = gc.getLineAttributes();
		lineAttrs.dash = dashes;
		lineAttrs.style = SWT.LINE_CUSTOM;
		gc.setLineAttributes(lineAttrs);
	}

	private void applyDashOffset(double dashOffset) {
		LineAttributes lineAttrs = gc.getLineAttributes();
		lineAttrs.dashOffset = (float) dashOffset;
		gc.setLineAttributes(lineAttrs);
	}

	/**
	 * @param fillRule
	 */
	private void applyFillRule(FillRule fillRule) {
		switch (fillRule) {
		case EVEN_ODD:
			gc.setFillRule(SWT.FILL_EVEN_ODD);
			break;
		case WIND_NON_ZERO:
			gc.setFillRule(SWT.FILL_WINDING);
			break;
		default:
			throw new IllegalStateException("Unknown FillRule: " + fillRule);
		}
	}

	/**
	 * @param font
	 */
	private void applyFont(Font font) {
		gc.setFont(font);
	}

	private void applyGlobalAlpha(double globalAlpha) {
		gc.setAlpha((int) globalAlpha);
	}

	private void applyLineCap(LineCap lineCap) {
		LineAttributes lineAttrs = gc.getLineAttributes();

		switch (lineCap) {
		case FLAT:
			lineAttrs.cap = SWT.CAP_FLAT;
			break;
		case ROUND:
			lineAttrs.cap = SWT.CAP_ROUND;
			break;
		case SQUARE:
			lineAttrs.cap = SWT.CAP_SQUARE;
			break;
		default:
			throw new IllegalStateException("Unknown LineCap: " + lineCap);
		}

		gc.setLineAttributes(lineAttrs);
	}

	/**
	 * @param lineJoin
	 * @return
	 */
	private void applyLineJoin(LineJoin lineJoin) {
		LineAttributes lineAttrs = gc.getLineAttributes();

		switch (lineJoin) {
		case BEVEL:
			lineAttrs.join = SWT.JOIN_BEVEL;
			break;
		case ROUND:
			lineAttrs.join = SWT.JOIN_ROUND;
			break;
		case MITER:
			lineAttrs.join = SWT.JOIN_MITER;
			break;
		default:
			throw new IllegalStateException("Unknwon LineJoin: " + lineJoin);
		}

		gc.setLineAttributes(lineAttrs);
	}

	private void applyLineWidth(double lineWidth) {
		LineAttributes lineAttrs = gc.getLineAttributes();
		lineAttrs.width = (float) lineWidth;
		gc.setLineAttributes(lineAttrs);
	}

	private void applyMiterLimit(double miterLimit) {
		LineAttributes lineAttrs = gc.getLineAttributes();
		lineAttrs.miterLimit = (float) miterLimit;
		gc.setLineAttributes(lineAttrs);
	}

	// TODO: split into applyFill(...) and applyStroke(...)
	private void applyPaint(Paint paint, PaintType type, Rectangle drawingBounds) {
		// no need to apply anything when nothing is to be drawn
		if (drawingBounds.getWidth() <= 0 || drawingBounds.getHeight() <= 0) {
			return;
		}

		boolean isColor = false;

		switch (paint.getMode()) {
		case COLOR:
			isColor = true;
			RgbaColor c = paint.getRgbaColorByReference();
			disposeSwtColorPaint();
			swtColorPaint = SwtUtils.createSwtColor(gc.getDevice(), c);
			// FIXME: opacity is ignored
			break;
		case GRADIENT:
			Gradient<?> gradient = paint.getGradient();
			ImageData gradientImageData = SwtUtils.createGradientImageData(
					gc.getDevice(), drawingBounds, gradient);

			disposeSwtImageGradient();
			swtImageGradient = new Image(gc.getDevice(), gradientImageData);

			disposeSwtPatternPaint();
			swtPatternPaint = new org.eclipse.swt.graphics.Pattern(
					gc.getDevice(), swtImageGradient);
			break;
		case IMAGE:
			disposeSwtPatternPaint();
			swtPatternPaint = new org.eclipse.swt.graphics.Pattern(
					gc.getDevice(), paint.getImage());
			break;
		default:
			throw new IllegalStateException("Unknown Paint.Mode: "
					+ paint.getMode());
		}

		switch (type) {
		case FILL:
			if (isColor) {
				gc.setBackground(swtColorPaint);
			} else {
				// workaround for bug #399109
				gc.setBackgroundPattern(null);
				gc.setBackgroundPattern(swtPatternPaint);
			}
			break;
		case STROKE:
			if (isColor) {
				gc.setForeground(swtColorPaint);
			} else {
				gc.setForegroundPattern(null);
				gc.setForegroundPattern(swtPatternPaint);
			}
			break;
		default:
			throw new IllegalStateException("Unknown PaintType: " + type);
		}
	}

	private void applyPattern(Paint pattern, PaintType type) {
		applyPaint(pattern, type, new Rectangle(0, 0, 100, 100));
	}

	private void applyStateToGc(GraphicsContextState state) {
		applyGlobalAlpha(state.getGlobalAlpha());
		applyClip(state.getClipPathByReference());
		applyPattern(state.getFillByReference(), PaintType.FILL);
		applyFillRule(state.getFillRule());
		applyFont(new Font(gc.getDevice(), state.getFontDataByReference()));
		applyLineCap(state.getLineCap());
		applyLineJoin(state.getLineJoin());
		applyLineWidth(state.getLineWidth());
		applyMiterLimit(state.getMiterLimit());
		applyPattern(state.getStrokeByReference(), PaintType.STROKE);
		applyTransform(state.getTransformByReference());
		applyDashes(state.getDashesByReference());
		applyDashOffset(state.getDashOffset());
	}

	/**
	 * @param at
	 */
	private void applyTransform(AffineTransform at) {
		disposeSwtTransform();
		swtTransform = SwtUtils.createSwtTransform(at, gc.getDevice());
		gc.setTransform(swtTransform);
		gc.getGCData().state |= 1 << 9;
	}

	/**
	 * Adds an arc to the current {@link Path}.
	 * 
	 * @param centerX
	 *            the x coordinate of arc's center
	 * @param centerY
	 *            the y coordinate of arc's center
	 * @param radiusX
	 *            the horizontal radius
	 * @param radiusY
	 *            the vertical radius
	 * @param startAngleDeg
	 *            the start angle in degrees
	 * @param angularExtentDeg
	 *            the angular extent in degrees
	 */
	public void arc(double centerX, double centerY, double radiusX,
			double radiusY, double startAngleDeg, double angularExtentDeg) {
		appendPath(new Arc(centerX - radiusX, centerY - radiusY, 2 * radiusX,
				2 * radiusY, Angle.fromDeg(startAngleDeg),
				Angle.fromDeg(angularExtentDeg)).toPath());
	}

	/**
	 * Adds an arc to the current {@link Path}.
	 * 
	 * @param x0
	 *            the x coordinate of the first point
	 * @param y0
	 *            the y coordinate of the first point
	 * @param x1
	 *            the x coordinate of the second point
	 * @param y1
	 *            the y coordinate of the second point
	 * @param radius
	 *            the radius of the arc
	 */
	public void arcTo(double x0, double y0, double x1, double y1, double radius) {
		throw new UnsupportedOperationException("Not Yet Implemented (NYI)");
	}

	private void beforeShowText() {
		switch (states.peek().getTextBaseline()) {
		case TOP:
			break;
		case BASELINE:
			translate(0, -gc.getFontMetrics().getAscent());
			break;
		case BOTTOM:
			translate(0, -gc.getFontMetrics().getHeight());
			break;
		case CENTER:
			translate(0, -gc.getFontMetrics().getHeight() / 2);
			break;
		default:
			throw new IllegalStateException("Unknown TextVPos: "
					+ states.peek().getTextBaseline());
		}
	}

	/**
	 * Resets the current {@link Path}.
	 */
	public void beginPath() {
		path = new Path();
	}

	/**
	 * Adds a cubic Bezier curve to the current {@link Path}.
	 * 
	 * @param hx0
	 *            the x coordinate of the first handle point
	 * @param hy0
	 *            the y coordinate of the first handle point
	 * @param hx1
	 *            the x coordinate of the second handle point
	 * @param hy1
	 *            the y coordinate of the second handle point
	 * @param x
	 *            the x coordinate of the end point
	 * @param y
	 *            the y coordinate of the end point
	 */
	public void bezierCurveTo(double hx0, double hy0, double hx1, double hy1,
			double x, double y) {
		if (path == null) {
			beginPath();
		}
		path.cubicTo(hx0, hy0, hx1, hy1, x, y);
	}

	public void cleanUp() {
		initialGcState.apply(gc);
		disposeSwtColorPaint();
		disposeSwtImageGradient();
		disposeSwtPathClip();
		disposeSwtPathFill();
		disposeSwtPatternPaint();
		disposeSwtTransform();
	}

	/**
	 * Clears the rectangular area given by <i>x</i>, <i>y</i>, <i>w</i>, and
	 * <i>h</i> by filling it with an opaque white.
	 * 
	 * @param x
	 *            the x coordinate of the rectangular area to clear
	 * @param y
	 *            the y coordinate of the rectangular area to clear
	 * @param w
	 *            the width of the rectangular area to clear
	 * @param h
	 *            the height of the rectangular area to clear
	 */
	public void clearRect(double x, double y, double w, double h) {
		Object fill = getFill();
		setFill(new RgbaColor(255, 255, 255, 255));
		fillRect(x, y, w, h);
		setFill(fill);
	}

	/**
	 * Uses the current {@link Path} to clip subsequent drawings.
	 */
	public void clip() {
		disposeSwtPathClip();
		swtPathClip = SwtUtils.createSwtPath(path, gc.getDevice());
		gc.setClipping(swtPathClip);
	}

	/**
	 * Adds a {@link Path.Segment#CLOSE} {@link Segment} to the current
	 * {@link Path}.
	 */
	public void closePath() {
		path.close();
	}

	private void disposeSwtColorPaint() {
		if (swtColorPaint != null && !swtColorPaint.isDisposed()) {
			swtColorPaint.dispose();
		}
	}

	/**
	 * 
	 */
	private void disposeSwtImageGradient() {
		if (swtImageGradient != null && !swtImageGradient.isDisposed()) {
			swtImageGradient.dispose();
		}
	}

	private void disposeSwtPathClip() {
		if (swtPathClip != null && !swtPathClip.isDisposed()) {
			swtPathClip.dispose();
		}
	}

	private void disposeSwtPathFill() {
		if (swtPathFill != null && !swtPathFill.isDisposed()) {
			swtPathFill.dispose();
		}
	}

	/**
	 * 
	 */
	private void disposeSwtPatternPaint() {
		if (swtPatternPaint != null && !swtPatternPaint.isDisposed()) {
			swtPatternPaint.dispose();
		}
	}

	/**
	 * 
	 */
	private void disposeSwtTransform() {
		if (swtTransform != null && !swtTransform.isDisposed()) {
			swtTransform.dispose();
		}
	}

	/**
	 * Draws the given {@link Image} at the specified location.
	 * 
	 * @param swtImageGradient
	 * @param x
	 *            destination x coordinate
	 * @param y
	 *            destination y coordinate
	 */
	public void drawImage(Image image, double x, double y) {
		fix253670();
		gc.drawImage(image, (int) Math.round(x), (int) Math.round(y));
	}

	/**
	 * Draws the given {@link Image} at the specified location.
	 * 
	 * @param swtImageGradient
	 * @param x
	 *            destination x coordinate
	 * @param y
	 *            destination y coordinate
	 * @param w
	 *            destination width
	 * @param h
	 *            destination height
	 */
	public void drawImage(Image image, double x, double y, double w, double h) {
		org.eclipse.swt.graphics.Rectangle bounds = image.getBounds();
		fix253670();
		gc.drawImage(image, 0, 0, bounds.width, bounds.height,
				(int) Math.round(x), (int) Math.round(y), (int) Math.round(w),
				(int) Math.round(h));
	}

	/**
	 * Draws the specified portion of the given {@link Image} at the specified
	 * location.
	 * 
	 * @param swtImageGradient
	 */
	public void drawImage(Image image, double srcX, double srcY,
			double srcWidth, double srcHeight, double x, double y, double w,
			double h) {
		fix253670();
		gc.drawImage(image, (int) Math.round(srcX), (int) Math.round(srcY),
				(int) Math.round(srcWidth), (int) Math.round(srcHeight),
				(int) Math.round(x), (int) Math.round(y), (int) Math.round(w),
				(int) Math.round(h));
	}

	public void fill() {
		fillPath(path);
	}

	public void fillArc(final double x, final double y, final double w,
			final double h, final double startAngleDeg,
			final double angularExtentDeg, final ArcType arcType) {
		final Rectangle bounds = new Rectangle(x, y, w, h);
		WITH_ALPHA(this, getBackgroundAlpha(), new Runnable() {
			@Override
			public void run() {
				switch (arcType) {
				case ROUND: {
					Arc arc = new Arc(x, y, w, h, Angle.fromDeg(startAngleDeg),
							Angle.fromDeg(angularExtentDeg));
					List<BezierCurve> arcSegs = new ArrayList<BezierCurve>();
					CubicCurve[] beziers = arc.toBezier();
					arcSegs.addAll(Arrays.asList(beziers));
					arcSegs.add(new Line(beziers[beziers.length - 1].getP2(),
							arc.getCenter()));
					arcSegs.add(new Line(arc.getCenter(), beziers[0].getP1()));
					fillPath(
							new PolyBezier(arcSegs
									.toArray(new BezierCurve[] {})).toPath(),
							bounds);
					break;
				}
				case CHORD:
				case OPEN: {
					Arc arc = new Arc(x, y, w, h, Angle.fromDeg(startAngleDeg),
							Angle.fromDeg(angularExtentDeg));
					List<BezierCurve> arcSegs = new ArrayList<BezierCurve>();
					CubicCurve[] beziers = arc.toBezier();
					arcSegs.addAll(Arrays.asList(beziers));
					arcSegs.add(new Line(beziers[beziers.length - 1].getP2(),
							beziers[0].getP1()));
					fillPath(new PolyBezier(arcSegs
							.toArray(new BezierCurve[] {})).toPath());
					break;
				}
				default:
					throw new IllegalStateException("Unknown ArcType: "
							+ arcType);
				}

			}
		});
	}

	public void fillOval(final double x, final double y, final double w,
			final double h) {
		if (states.peek().getFillByReference().getMode() != PaintMode.COLOR) {
			fillPath(new Ellipse(x, y, w, h).toPath());
			return;
		}

		WITH_ALPHA(this, getBackgroundAlpha(), new Runnable() {
			@Override
			public void run() {
				fix253670();
				gc.fillOval((int) Math.round(x), (int) Math.round(y),
						(int) Math.round(w), (int) Math.round(h));
			}
		});
	}

	public void fillPath(Path path) {
		fillPath(path, path.getBounds().getUnioned(new Point()));
	}

	public void fillPath(final Path path, final Rectangle bounds) {
		WITH_ALPHA(this, getBackgroundAlpha(), new Runnable() {
			@Override
			public void run() {
				setFillRule(path.getWindingRule() == Path.WIND_EVEN_ODD ? FillRule.EVEN_ODD
						: FillRule.WIND_NON_ZERO);
				applyPaint(getFillPattern(), PaintType.FILL, bounds);

				org.eclipse.swt.graphics.Path swtPathFill = SwtUtils
						.createSwtPath(path, gc.getDevice());
				fix253670();
				gc.fillPath(swtPathFill);
				swtPathFill.dispose();
			}
		});
	}

	public void fillPolygon(double[] xs, double[] ys, int n) {
		final int[] swtPointsArray = SwtUtils.createSwtPointsArray(xs, ys, n);

		if (states.peek().getFillByReference().getMode() != PaintMode.COLOR) {
			fillPath(SWT2Geometry.toPolygon(swtPointsArray).toPath());
			return;
		}

		WITH_ALPHA(this, getBackgroundAlpha(), new Runnable() {
			@Override
			public void run() {
				fix253670();
				gc.fillPolygon(swtPointsArray);
			}
		});
	}

	public void fillRect(final double x, final double y, final double w,
			final double h) {
		if (states.peek().getFillByReference().getMode() != PaintMode.COLOR) {
			fillPath(new Rectangle(x, y, w, h).toPath());
			return;
		}

		WITH_ALPHA(this, getBackgroundAlpha(), new Runnable() {
			@Override
			public void run() {
				fix253670();
				gc.fillRectangle((int) Math.round(x), (int) Math.round(y),
						(int) Math.round(w), (int) Math.round(h));
			}
		});
	}

	public void fillRoundRect(final double x, final double y, final double w,
			final double h, final double arcWidth, final double arcHeight) {
		if (states.peek().getFillByReference().getMode() != PaintMode.COLOR) {
			fillPath(
					new RoundedRectangle(x, y, w, h, arcWidth, arcHeight)
							.toPath(),
					new Rectangle(x, y, w, h));
			return;
		}

		WITH_ALPHA(this, getBackgroundAlpha(), new Runnable() {
			@Override
			public void run() {
				fix253670();
				gc.fillRoundRectangle((int) Math.round(x), (int) Math.round(y),
						(int) Math.round(w), (int) Math.round(h),
						(int) Math.round(arcWidth), (int) Math.round(arcHeight));
			}
		});
	}

	public void fillText(final String text, final double x, final double y) {
		WITH_ALPHA(this, getBackgroundAlpha(), new Runnable() {
			@Override
			public void run() {
				Object stroke = getStroke();
				setStroke(getFill());
				showText(text, x, y, true);
				setStroke(stroke);
			}
		});
	}

	public void fillText(final String text, final double x, final double y,
			final double maxWidth) {
		WITH_ALPHA(this, getBackgroundAlpha(), new Runnable() {
			@Override
			public void run() {
				Object stroke = getStroke();
				setStroke(getFill());
				showText(trim(text, maxWidth), x, y, true);
				setStroke(stroke);
			}
		});
	}

	/**
	 * Prefix GC drawing operation by a call to this method to fix SWT Bugs
	 * #253670/#335769.
	 * <p>
	 * It is not ensured, that this fix works on all platforms. Moreover, it is
	 * not clear which GC drawing operations really need this fix. Both points
	 * For the various {@link GraphicsContext#fix253670()} calls, separate those
	 * that are really needed from those that are superfluous. At the moment,
	 * every GC drawing operation is prefixed by one call.
	 */
	private void fix253670() {
		applyLineWidth(getLineWidth());
	}

	private int getBackgroundAlpha() {
		Paint fill = states.peek().getFillByReference();
		if (fill.getMode() == PaintMode.COLOR) {
			return fill.getRgbaColorByReference().getAlpha();
		}
		return 255;
	}

	public Path getClipping() {
		if (swtPathClip == null) {
			return null;
		}

		return SWT2Geometry.toPath(
				getFillRule() == FillRule.EVEN_ODD ? Path.WIND_EVEN_ODD
						: Path.WIND_NON_ZERO, swtPathClip.getPathData());
	}

	public double[] getDashes() {
		double[] dashes = states.peek().getDashesByReference();
		return Arrays.copyOf(dashes, dashes.length);
	}

	public double getDashOffset() {
		return states.peek().getDashOffset();
	}

	public Object getFill() {
		return states.peek().getFillByReference().getActive();
	}

	public Paint getFillPattern() {
		return states.peek().getFillByReference();
	}

	public FillRule getFillRule() {
		return states.peek().getFillRule();
	}

	public Font getFont() {
		return gc.getFont();
	}

	private int getForegroundAlpha() {
		Paint stroke = states.peek().getStrokeByReference();
		if (stroke.getMode() == PaintMode.COLOR) {
			return stroke.getRgbaColorByReference().getAlpha();
		}
		return 255;
	}

	/**
	 * Returns the SWT {@link GC} which is used to perform drawing operations.
	 * Modifications of the state of the returned GC will affect every
	 * {@link GraphicsContext} which uses that GC. Moreover, such modifications
	 * are <b>not</b> propagated to the current {@link GraphicsContextState}.
	 * Therefore, directly using the GC is not recommended.
	 * 
	 * @return the SWT {@link GC} used to perform drawing operations
	 */
	public GC getGcByReference() {
		return gc;
	}

	public int getGlobalAlpha() {
		/*
		 * TODO: decide if [0;255] or [0;1] is our channel range
		 * 
		 * [0;1] => double values => more precision, simple usage (1,0,0,1) ==
		 * RED
		 * 
		 * [0;255] => integer values => less precision, superfluous digits
		 * (255,0,0,255) == RED
		 */
		return (int) states.peek().getGlobalAlpha();
	}

	public LineCap getLineCap() {
		return states.peek().getLineCap();
	}

	public LineJoin getLineJoin() {
		return states.peek().getLineJoin();
	}

	public double getLineWidth() {
		return states.peek().getLineWidth();
	}

	public double getMiterLimit() {
		return states.peek().getMiterLimit();
	}

	public GraphicsContextState getState() {
		return states.peek().getCopy();
	}

	public Object getStroke() {
		return states.peek().getStrokeByReference().getActive();
	}

	public Paint getStrokePattern() {
		return states.peek().getStrokeByReference();
	}

	public TextAlignment getTextAlign() {
		return states.peek().getTextAlign();
	}

	public TextVPos getTextBaseline() {
		return states.peek().getTextBaseline();
	}

	public AffineTransform getTransform() {
		return states.peek().getTransformByReference();
	}

	public void getTransform(AffineTransform transform) {
		Transform t = new Transform(gc.getDevice());
		gc.getTransform(t);
		float[] m = new float[6];
		t.getElements(m);
		t.dispose();
		transform.setTransform(m[0], m[1], m[2], m[3], m[4], m[5]);
	}

	public boolean isPointInPath(double x, double y) {
		return path.contains(new Point(x, y));
	}

	public void lineTo(double x, double y) {
		path.lineTo(x, y);
	}

	public void moveTo(double x, double y) {
		path.moveTo(x, y);
	}

	public void pushState(GraphicsContextState state) {
		states.push(state);
		applyStateToGc(state);
	}

	public void quadraticCurveTo(double hx, double hy, double x, double y) {
		path.quadTo(hx, hy, x, y);
	}

	public void rect(double x, double y, double w, double h) {
		appendPath(new Rectangle(x, y, w, h).toPath());
	}

	public void restore() {
		if (states.peek().isGuarded()) {
			throw new IllegalStateException(
					"restore() not allowed on guarded state.");
		}
		states.pop();
		applyStateToGc(states.peek());
	}

	public void rotate(double angleDeg) {
		states.peek().getTransformByReference()
				.rotate(Angle.fromDeg(angleDeg).rad());
		applyTransform(states.peek().getTransformByReference());
	}

	public void save() {
		states.push(states.peek().getCopy());

		// take down guard on copy
		if (states.peek().isGuarded()) {
			states.peek().setGuarded(false);
		}
	}

	public void scale(double sx, double sy) {
		states.peek().getTransformByReference().scale(sx, sy);
		applyTransform(states.peek().getTransformByReference());
	}

	// public void setClipping(Path clippingPath) {
	// if (clippingPath == null) {
	// gc.setClipping((org.eclipse.swt.graphics.Rectangle) null);
	// return;
	// }
	//
	// disposeSwtPathClip();
	// swtPathClip = SwtUtils.createSwtPath(clippingPath, gc.getDevice());
	// gc.setClipping(swtPathClip);
	// }

	public void setDashes(double... dashes) {
		double[] dashesCopy = Arrays.copyOf(dashes, dashes.length);
		states.peek().setDashesByReference(dashesCopy);
		applyDashes(dashesCopy);
	}

	public void setDashOffset(double dashOffset) {
		states.peek().setDashOffset(dashOffset);
		applyDashOffset(dashOffset);
	}

	public void setFill(Color fillColor) {
		Paint fillPattern = states.peek().getFillByReference();
		fillPattern.setColor(SwtUtils.createRgbaColor(fillColor, 255));
		fillPattern.setMode(PaintMode.COLOR);
		applyPattern(fillPattern, PaintType.FILL);
	}

	public void setFill(Gradient<?> fillGradient) {
		Paint fillPattern = states.peek().getFillByReference();
		fillPattern.setGradient(fillGradient);
		fillPattern.setMode(PaintMode.GRADIENT);
		applyPattern(fillPattern, PaintType.FILL);
	}

	public void setFill(Image fillImage) {
		Paint fillPattern = states.peek().getFillByReference();
		fillPattern.setImage(fillImage);
		fillPattern.setMode(PaintMode.IMAGE);
		applyPattern(fillPattern, PaintType.FILL);
	}

	public void setFill(Object fill) {
		if (fill instanceof Color) {
			setFill(fill);
		} else if (fill instanceof RgbaColor) {
			setFill((RgbaColor) fill);
		} else if (fill instanceof Gradient<?>) {
			setFill((Gradient<?>) fill);
		} else if (fill instanceof Image) {
			setFill((Image) fill);
		} else {
			throw new IllegalArgumentException("Not a valid Paint Object: "
					+ fill);
		}
	}

	public void setFill(RgbaColor fillColor) {
		Paint fillPattern = states.peek().getFillByReference();
		fillPattern.setColor(fillColor);
		fillPattern.setMode(PaintMode.COLOR);
		applyPattern(fillPattern, PaintType.FILL);
	}

	public void setFillRule(FillRule rule) {
		states.peek().setFillRule(rule);
		applyFillRule(rule);
	}

	public void setFont(Font font) {
		// TODO: We do not care about multiple FontData objects?
		states.peek().setFontDataByReference(font.getFontData()[0]);
		applyFont(font);
	}

	public void setFont(FontData fontData) {
		states.peek().setFontDataByReference(fontData);
		applyFont(new Font(gc.getDevice(), fontData));
	}

	// TODO: double vs. int
	public void setGlobalAlpha(double alpha) {
		states.peek().setGlobalAlpha(alpha);
		applyGlobalAlpha(alpha);
	}

	public void setLineCap(LineCap cap) {
		states.peek().setLineCap(cap);
		applyLineCap(cap);
	}

	public void setLineJoin(LineJoin join) {
		states.peek().setLineJoin(join);
		applyLineJoin(join);
	}

	public void setLineWidth(double lineWidth) {
		states.peek().setLineWidth(lineWidth);
		applyLineWidth(lineWidth);
	}

	public void setMiterLimit(double miterLimit) {
		states.peek().setMiterLimit(miterLimit);
		applyMiterLimit(miterLimit);
	}

	public void setStroke(Color strokeColor) {
		Paint strokePattern = states.peek().getStrokeByReference();
		strokePattern.setColor(SwtUtils.createRgbaColor(strokeColor, 255));
		strokePattern.setMode(PaintMode.COLOR);
		applyPattern(strokePattern, PaintType.STROKE);
	}

	public void setStroke(Gradient<?> strokeGradient) {
		Paint strokePattern = states.peek().getStrokeByReference();
		strokePattern.setGradient(strokeGradient);
		strokePattern.setMode(PaintMode.GRADIENT);
		applyPattern(strokePattern, PaintType.STROKE);
	}

	public void setStroke(Image strokeImage) {
		Paint strokePattern = states.peek().getStrokeByReference();
		strokePattern.setImage(strokeImage);
		strokePattern.setMode(PaintMode.IMAGE);
		applyPattern(strokePattern, PaintType.STROKE);
	}

	public void setStroke(Object stroke) {
		if (stroke instanceof RgbaColor) {
			setStroke((RgbaColor) stroke);
		} else if (stroke instanceof Gradient<?>) {
			setStroke((Gradient<?>) stroke);
		} else if (stroke instanceof Image) {
			setStroke((Image) stroke);
		} else {
			throw new IllegalArgumentException("Not a valid Paint Object: "
					+ stroke);
		}
	}

	public void setStroke(RgbaColor strokeColor) {
		Paint strokePattern = states.peek().getStrokeByReference();
		strokePattern.setColor(strokeColor);
		strokePattern.setMode(PaintMode.COLOR);
		applyPattern(strokePattern, PaintType.STROKE);
	}

	public void setTextAlign(TextAlignment textAlign) {
		states.peek().setTextAlign(textAlign);
	}

	public void setTextBaseline(TextVPos textBaseline) {
		states.peek().setTextBaseline(textBaseline);
	}

	public void setTransform(AffineTransform transform) {
		states.peek().setTransformByReference(transform);
		applyTransform(transform);
	}

	public void setTransform(double mxx, double myx, double mxy, double myy,
			double tx, double ty) {
		setTransform(new AffineTransform(mxx, myx, mxy, myy, tx, ty));
	}

	/**
	 * Sets up a guard for the current state. {@link #restore()} operations are
	 * checked to not pop a guarded state.
	 */
	public void setUpGuard() {
		if (states.peek().isGuarded()) {
			throw new IllegalStateException(
					"The current GraphicsContextState is already guarded.");
		}
		states.peek().setGuarded(true);
	}

	private void showText(String text, double x, double y, boolean isFill) {
		beforeShowText();

		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}

		org.eclipse.swt.graphics.Path path = new org.eclipse.swt.graphics.Path(
				display);
		path.addString(text, (float) x, (float) y, getFont());

		fix253670();
		if (isFill) {
			gc.fillPath(path);
		} else {
			gc.drawPath(path);
		}

		afterShowText();
	}

	public void stroke() {
		strokePath(path);
	}

	// TODO: split strokeArc() into different methods (strokeOpenArc,
	// strokeChordArc, strokeRoundArc)
	public void strokeArc(final double x, final double y, final double w,
			final double h, final double startAngleDeg,
			final double angularExtentDeg, final ArcType arcType) {
		WITH_ALPHA(this, getForegroundAlpha(), new Runnable() {
			@Override
			public void run() {
				fix253670();
				switch (arcType) {
				case OPEN:
					gc.drawArc((int) Math.round(x), (int) Math.round(y),
							(int) Math.round(w), (int) Math.round(h),
							(int) Math.round(startAngleDeg),
							(int) Math.round(angularExtentDeg));
					break;
				case CHORD:
					Arc arc = new Arc(x, y, w, h, Angle.fromDeg(startAngleDeg),
							Angle.fromDeg(angularExtentDeg));
					gc.drawArc((int) Math.round(x), (int) Math.round(y),
							(int) Math.round(w), (int) Math.round(h),
							(int) Math.round(startAngleDeg),
							(int) Math.round(angularExtentDeg));
					Point start = arc.getP1();
					Point end = arc.getP2();
					fix253670();
					gc.drawLine((int) Math.round(end.x),
							(int) Math.round(end.y), (int) Math.round(start.x),
							(int) Math.round(start.y));
					break;
				case ROUND:
					arc = new Arc(x, y, w, h, Angle.fromDeg(startAngleDeg),
							Angle.fromDeg(angularExtentDeg));
					gc.drawArc((int) Math.round(x), (int) Math.round(y),
							(int) Math.round(w), (int) Math.round(h),
							(int) Math.round(startAngleDeg),
							(int) Math.round(angularExtentDeg));
					start = arc.getP1();
					end = arc.getP2();
					Point center = arc.getCenter();
					fix253670();
					gc.drawLine((int) Math.round(end.x),
							(int) Math.round(end.y),
							(int) Math.round(center.x),
							(int) Math.round(center.y));
					fix253670();
					gc.drawLine((int) Math.round(center.x),
							(int) Math.round(center.y),
							(int) Math.round(start.x),
							(int) Math.round(start.y));
					break;
				default:
					throw new IllegalStateException("Unknown ArcType: "
							+ arcType);
				}
			}
		});
	}

	public void strokeLine(final double x0, final double y0, final double x1,
			final double y1) {
		WITH_ALPHA(this, getForegroundAlpha(), new Runnable() {
			@Override
			public void run() {
				fix253670();
				gc.drawLine((int) Math.round(x0), (int) Math.round(y0),
						(int) Math.round(x1), (int) Math.round(y1));
			}
		});
	}

	public void strokeOval(final double x, final double y, final double w,
			final double h) {
		WITH_ALPHA(this, getForegroundAlpha(), new Runnable() {
			@Override
			public void run() {
				fix253670();
				gc.drawOval((int) Math.round(x), (int) Math.round(y),
						(int) Math.round(w), (int) Math.round(h));
			}
		});
	}

	public void strokePath(final Path path) {
		WITH_ALPHA(this, getForegroundAlpha(), new Runnable() {
			@Override
			public void run() {
				org.eclipse.swt.graphics.Path swtPathStroke = SwtUtils
						.createSwtPath(path, gc.getDevice());
				fix253670();
				gc.drawPath(swtPathStroke);
				swtPathStroke.dispose();
			}
		});
	}

	public void strokePolygon(final double[] xs, final double[] ys, final int n) {
		WITH_ALPHA(this, getForegroundAlpha(), new Runnable() {
			@Override
			public void run() {
				fix253670();
				gc.drawPolygon(SwtUtils.createSwtPointsArray(xs, ys, n));
			}
		});
	}

	public void strokePolyline(final double[] xs, final double[] ys, final int n) {
		WITH_ALPHA(this, getForegroundAlpha(), new Runnable() {
			@Override
			public void run() {
				fix253670();
				gc.drawPolyline(SwtUtils.createSwtPointsArray(xs, ys, n));
			}
		});
	}

	public void strokeRect(final double x, final double y, final double w,
			final double h) {
		WITH_ALPHA(this, getForegroundAlpha(), new Runnable() {
			@Override
			public void run() {
				fix253670();
				gc.drawRectangle((int) Math.round(x), (int) Math.round(y),
						(int) Math.round(w), (int) Math.round(h));
			}
		});
	}

	public void strokeRoundRect(final double x, final double y, final double w,
			final double h, final double arcWidth, final double arcHeight) {
		WITH_ALPHA(this, getForegroundAlpha(), new Runnable() {
			@Override
			public void run() {
				fix253670();
				gc.drawRoundRectangle((int) Math.round(x), (int) Math.round(y),
						(int) Math.round(w), (int) Math.round(h),
						(int) Math.round(arcWidth), (int) Math.round(arcHeight));
			}
		});
	}

	public void strokeText(final String text, final double x, final double y) {
		WITH_ALPHA(this, getForegroundAlpha(), new Runnable() {
			@Override
			public void run() {
				showText(text, x, y, false);
			}
		});
	}

	public void strokeText(final String text, final double x, final double y,
			final double maxWidth) {
		WITH_ALPHA(this, getForegroundAlpha(), new Runnable() {
			@Override
			public void run() {
				showText(trim(text, maxWidth), x, y, false);
			}
		});
	}

	/**
	 * This method removes the previously {@link #setUpGuard() set-up} guard
	 * from the current state.
	 * 
	 * @throws IllegalStateException
	 *             when the current state is not guarded.
	 */
	public void takeDownGuard() throws IllegalStateException {
		if (!states.peek().isGuarded()) {
			throw new IllegalStateException("Current state is not guarded.");
		}
		states.peek().setGuarded(false);
	}

	public Dimension textExtent(String text) {
		org.eclipse.swt.graphics.Point textExtent = gc.textExtent(text);
		return new Dimension(textExtent.x, textExtent.y);
	}

	public void transform(AffineTransform transform) {
		states.peek().getTransformByReference().concatenate(transform);
		applyTransform(states.peek().getTransformByReference());
	}

	void transform(double mxx, double myx, double mxy, double myy, double tx,
			double ty) {
		transform(new AffineTransform(mxx, myx, mxy, myy, tx, ty));
	}

	public void translate(double dx, double dy) {
		states.peek().getTransformByReference().translate(dx, dy);
		applyTransform(states.peek().getTransformByReference());
	}

	/**
	 * @param text
	 * @param maxWidth
	 * @return
	 */
	private String trim(String text, double maxWidth) {
		// TODO: check if GC.textExtent() works correctly.
		// TODO: find a better way to determine the visible text
		// 1) clip to a rect with corresponding width
		// 2) binary search text length
		// 3) ...is there another possibility?
		if (maxWidth <= 0) {
			return "";
		}

		org.eclipse.swt.graphics.Point extent = gc.textExtent(text);
		while (text.length() > 0 && extent.x > maxWidth) {
			text = text.substring(0, text.length() - 1);
			extent = gc.textExtent(text);
		}
		return text;
	}

}
