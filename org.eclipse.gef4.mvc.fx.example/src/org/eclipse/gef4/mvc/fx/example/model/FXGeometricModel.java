package org.eclipse.gef4.mvc.fx.example.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Light.Distant;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.CurvedPolygon;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.PolyBezier;

public class FXGeometricModel {

	private static final double GEF_STROKE_WIDTH = 3.5;
	// private static final Color GEF_COLOR_BLUE = Color.rgb(97, 102, 170);
	// private static final Color GEF_COLOR_GREEN = Color.rgb(99, 123, 71);
	private static final Color GEF_COLOR_BLUE = Color.rgb(135, 150, 220);
	private static final Color GEF_COLOR_GREEN = Color.rgb(99, 123, 71);
	private static final Effect GEF_SHADOW_EFFECT = createShadowEffect();
	private static final double[] GEF_DASH_PATTERN = new double[] { 13, 8 };

	private FXGeometricShape topLeftSelectionHandle = new FXGeometricShape(
			createHandleShapeGeometry(), new AffineTransform(1, 0, 0, 1, 12,
					135), Color.WHITE, GEF_SHADOW_EFFECT);
	private FXGeometricShape topRightSelectionHandle = new FXGeometricShape(
			createHandleShapeGeometry(), new AffineTransform(1, 0, 0, 1, 243,
					135), Color.WHITE, GEF_SHADOW_EFFECT);
	private FXGeometricShape bottomLeftSelectionHandle = new FXGeometricShape(
			createHandleShapeGeometry(), new AffineTransform(1, 0, 0, 1, 12,
					229), Color.WHITE, GEF_SHADOW_EFFECT);
	private FXGeometricShape bottomRightSelectionHandle = new FXGeometricShape(
			createHandleShapeGeometry(), new AffineTransform(1, 0, 0, 1, 243,
					229), Color.WHITE, GEF_SHADOW_EFFECT);

	// TODO: add transform to the curves
	private FXGeometricCurve selectionBoundsTopLine = new FXGeometricCurve(
			new Point[] { new Point(140, 144) },
			GEF_COLOR_GREEN, GEF_STROKE_WIDTH, GEF_DASH_PATTERN, null);
	
	private FXGeometricCurve selectionBoundsLeftLine = new FXGeometricCurve(
			new Point[] { new Point(19, 190) },
			GEF_COLOR_GREEN, GEF_STROKE_WIDTH, GEF_DASH_PATTERN, null);

	private FXGeometricCurve selectionBoundsBottomLine = new FXGeometricCurve(
			new Point[] { new Point(140, 238)},
			GEF_COLOR_GREEN, 3.5, new double[] { 15, 10 }, null);
	
	private FXGeometricCurve selectionBoundsRightLine = new FXGeometricCurve(
			new Point[] { new Point(250, 190)},
			GEF_COLOR_GREEN, 3.5, new double[] { 15, 10 }, null);

	public FXGeometricModel() {
		// anchor curves to shapes
		topLeftSelectionHandle.addAnchored(selectionBoundsTopLine);
		topRightSelectionHandle.addAnchored(selectionBoundsTopLine);
		topLeftSelectionHandle.addAnchored(selectionBoundsLeftLine);
		bottomLeftSelectionHandle.addAnchored(selectionBoundsLeftLine);
		bottomLeftSelectionHandle.addAnchored(selectionBoundsBottomLine);
		bottomRightSelectionHandle.addAnchored(selectionBoundsBottomLine);
		topRightSelectionHandle.addAnchored(selectionBoundsRightLine);
		bottomRightSelectionHandle.addAnchored(selectionBoundsRightLine);
		
		// TODO: anchor points to letter shapes
	}

	public List<AbstractFXGeometricElement<? extends IGeometry>> getShapeVisuals() {
		List<AbstractFXGeometricElement<? extends IGeometry>> visualShapes = new ArrayList<AbstractFXGeometricElement<? extends IGeometry>>();

		// add shapes in z-order
		visualShapes.add(selectionBoundsTopLine);
		visualShapes.add(selectionBoundsLeftLine);
		visualShapes.add(selectionBoundsBottomLine);
		visualShapes.add(selectionBoundsRightLine);

		visualShapes.add(topLeftSelectionHandle);
		visualShapes.add(topRightSelectionHandle);
		visualShapes.add(bottomLeftSelectionHandle);
		visualShapes.add(bottomRightSelectionHandle);
		
		// TODO: create multi shape visual for G shape
		// g shape
		visualShapes.add(new FXGeometricShape(createGBaseShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 27, 146), GEF_COLOR_BLUE,
				GEF_SHADOW_EFFECT));
		visualShapes.add(new FXGeometricShape(createGTopShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 27, 146), GEF_COLOR_BLUE,
				GEF_SHADOW_EFFECT));
		visualShapes.add(new FXGeometricShape(createGMiddleShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 27, 146), GEF_COLOR_BLUE,
				GEF_SHADOW_EFFECT));

		// e shape
		visualShapes.add(new FXGeometricShape(createEShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 101, 146), GEF_COLOR_BLUE,
				GEF_SHADOW_EFFECT));

		// f shape
		visualShapes.add(new FXGeometricShape(createFShapeGeometry(), null,
				GEF_COLOR_BLUE, GEF_SHADOW_EFFECT));

		// gDotShape
		visualShapes.add(new FXGeometricShape(createDotShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 87, 224), GEF_COLOR_BLUE,
				GEF_SHADOW_EFFECT));

		// eDotShape
		visualShapes.add(new FXGeometricShape(createDotShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 170, 224), GEF_COLOR_BLUE,
				GEF_SHADOW_EFFECT));

		// fDotShape
		visualShapes.add(new FXGeometricShape(createDotShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 225, 224), GEF_COLOR_BLUE,
				GEF_SHADOW_EFFECT));

		return visualShapes;
	}

	private IShape createHandleShapeGeometry() {
		List<BezierCurve> segments = new ArrayList<BezierCurve>();
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(1, 1, 9, 0,
				17, 1).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(17, 1, 16, 8,
				17, 16).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(17, 16, 7,
				15, 1, 16).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(1, 16, 0, 8,
				1, 1).toBezier()));
		return new CurvedPolygon(segments);
	}

	private static Effect createShadowEffect() {
		DropShadow outerShadow = new DropShadow();
		outerShadow.setRadius(3);
		outerShadow.setSpread(0.2);
		outerShadow.setOffsetX(3);
		outerShadow.setOffsetY(3);
		outerShadow.setColor(new Color(0.3, 0.3, 0.3, 1));

		Distant light = new Distant();
		light.setAzimuth(-135.0f);

		Lighting l = new Lighting();
		l.setLight(light);
		l.setSurfaceScale(3.0f);

		Blend effects = new Blend(BlendMode.MULTIPLY);
		effects.setTopInput(l);
		effects.setBottomInput(outerShadow);

		return effects;
	}

	private IShape createGTopShapeGeometry() {
		List<BezierCurve> segments = new ArrayList<BezierCurve>();
		segments.add(new Line(0, 47, 8, 45));
		segments.add(new Line(8, 45, 9, 34));
		segments.add(new Line(9, 34, 20, 34));
		segments.add(new Line(20, 34, 24, 22));
		segments.add(new Line(24, 22, 35, 23));
		segments.add(new Line(35, 23, 39, 11));
		segments.add(new Line(39, 11, 51, 13));
		segments.add(new Line(51, 13, 54, 4));
		segments.add(new Line(54, 4, 52, 0));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(52, 0, 40, 5,
				14, 24, 1, 39, 0, 47).toBezier()));
		return new CurvedPolygon(segments);
	}

	private IShape createGBaseShapeGeometry() {
		List<BezierCurve> segments = new ArrayList<BezierCurve>();
		segments.add(new Line(0, 51, 10, 50));
		segments.add(new Line(10, 50, 13, 38));
		segments.add(new Line(13, 38, 23, 38));
		segments.add(new Line(23, 38, 25, 28));
		segments.add(new Line(25, 28, 37, 29));
		segments.add(new Line(37, 29, 42, 19));
		segments.add(new Line(42, 19, 54, 20));
		segments.add(new Line(54, 20, 57, 8));
		segments.add(new Line(57, 8, 65, 24));
		segments.add(new Line(65, 24, 60, 27));
		segments.add(new Line(60, 27, 57, 26));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(57, 26, 45,
				31, 22, 49, 14, 69, 40, 65).toBezier()));
		segments.add(new Line(40, 65, 39, 57));
		segments.add(new Line(39, 57, 46, 54));
		segments.add(new Line(46, 54, 54, 59));
		segments.add(new Line(54, 59, 62, 53));
		segments.add(new Line(62, 53, 67, 54));
		segments.add(new Line(67, 54, 67, 61));
		segments.add(new Line(67, 61, 55, 62));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(55, 62, 53,
				73, 51, 82).toBezier()));
		segments.add(new Line(51, 82, 47, 82));
		segments.add(new Line(47, 82, 46, 67));
		segments.add(new Line(46, 67, 25, 80));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(25, 80, 17,
				82, 14, 80).toBezier()));
		segments.add(new Line(14, 80, 0, 51));
		return new CurvedPolygon(segments);
	}

	private IShape createGMiddleShapeGeometry() {
		List<BezierCurve> segments = new ArrayList<BezierCurve>();
		segments.add(new Line(37, 44, 38, 52));
		segments.add(new Line(38, 52, 45, 48));
		segments.add(new Line(45, 48, 54, 53));
		segments.add(new Line(54, 53, 62, 47));
		segments.add(new Line(62, 47, 67, 50));
		segments.add(new Line(67, 50, 67, 41));
		segments.add(new Line(67, 41, 62, 40));
		segments.add(new Line(62, 40, 62, 44));
		segments.add(new Line(62, 44, 37, 44));
		return new CurvedPolygon(segments);
	}

	private IShape createDotShapeGeometry() {
		List<BezierCurve> segments = new ArrayList<BezierCurve>();
		segments.add(new Line(3, 0, 0, 4));
		segments.add(new Line(0, 4, 4, 9));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(4, 9, 8, 5,
				3, 0).toBezier()));
		return new CurvedPolygon(segments);
	}

	private IShape createEShapeGeometry() {
		List<BezierCurve> segments = new ArrayList<BezierCurve>();
		segments.add(new Line(0, 6, 5, 6));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(5, 6, 4, 21,
				6, 48, 5, 66, 5, 77).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(5, 77, 4, 77,
				2, 80).toBezier()));
		segments.add(new Line(2, 80, 2, 83));
		segments.add(new Line(2, 83, 63, 82));
		segments.add(new Line(63, 82, 64, 75));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(64, 75, 58,
				77, 50, 78).toBezier()));
		segments.add(new Line(50, 78, 11, 78));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(11, 78, 10,
				52, 10, 26).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(10, 26, 26,
				26, 44, 27).toBezier()));
		segments.add(new Line(44, 27, 47, 21));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(47, 21, 34,
				23, 18, 23, 9, 22).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(9, 22, 9, 16,
				10, 6).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(10, 6, 23, 7,
				30, 7, 50, 8).toBezier()));
		segments.add(new Line(50, 8, 54, 2));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(54, 2, 44, 3,
				32, 4, 14, 3, 6, 2).toBezier()));
		segments.add(new Line(6, 2, 0, 6));
		return new CurvedPolygon(segments);
	}

	private IShape createFShapeGeometry() {
		List<BezierCurve> segments = new ArrayList<BezierCurve>();
		segments.add(new Line(178, 155, 178, 165));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(178, 165,
				185, 167, 192, 168).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(192, 168,
				191, 176, 189, 186, 185, 188).toBezier()));
		segments.add(new Line(185, 188, 185, 195));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(185, 195,
				183, 212, 182, 225).toBezier()));
		segments.add(new Line(182, 225, 188, 231));
		segments.add(new Line(188, 231, 192, 227));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(192, 227,
				193, 211, 195, 196).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(195, 196,
				207, 194, 220, 192).toBezier()));
		segments.add(new Line(220, 192, 220, 187));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(220, 187,
				208, 188, 197, 188).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(197, 188,
				198, 176, 200, 168).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(200, 168,
				217, 168, 225, 165, 237, 160, 242, 158).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(242, 158,
				242, 154, 239, 152).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(239, 152,
				229, 156, 217, 158, 203, 158).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(203, 158,
				204, 153, 204, 150, 206, 142).toBezier()));
		segments.add(new Line(206, 142, 199, 145));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(199, 145,
				199, 150, 198, 154).toBezier()));
		segments.add(new Line(198, 154, 195, 153));
		segments.add(new Line(195, 153, 195, 157));
		segments.addAll(Arrays.asList(PolyBezier.interpolateCubic(195, 157,
				188, 157, 178, 155).toBezier()));
		return new CurvedPolygon(segments);
	}

}
