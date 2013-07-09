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
package org.eclipse.gef4.swt.fx.examples;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swt.fx.AbstractFigure;
import org.eclipse.gef4.swt.fx.CanvasFigure;
import org.eclipse.gef4.swt.fx.Group;
import org.eclipse.gef4.swt.fx.ShapeFigure;
import org.eclipse.gef4.swt.fx.gc.GraphicsContext;
import org.eclipse.gef4.swt.fx.gc.RgbaColor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TestFigures {

	// private static CurvedPolygon generateCurvedPolygon(Point... points) {
	// Polygon polygon = new Polygon(points);
	// Line[] lines = polygon.getOutlineSegments();
	// BezierCurve[] curves = new BezierCurve[lines.length];
	// for (int i = 0; i < lines.length; i++) {
	// Line line = lines[i];
	// curves[i] = new QuadraticCurve(line.getP1(), line.get(0.5)
	// .translate(0, 50), line.getP2());
	// }
	// return new CurvedPolygon(curves);
	// }

	public static void main(String[] args) {
		new TestFigures();
	}

	private Display display;

	private Shell shell;

	public TestFigures() {
		display = new Display();
		shell = new Shell(display);
		shell.setText("org.eclipse.gef4.swt.fx");
		shell.setLayout(new GridLayout());

		Group root = new Group(shell);
		root.setLayoutData(new GridData(GridData.FILL_BOTH));

		// ShapeFigure curved = new ShapeFigure(generateCurvedPolygon(new Point(
		// 250, 50), new Point(450, 200), new Point(400, 450), new Point(
		// 250, 350), new Point(100, 450), new Point(50, 200)));
		// colorize(curved, new RgbaColor(255, 255, 0));
		// trafo(curved).translate(50, 200);

		AbstractFigure rect = new ShapeFigure(new Rectangle(0, 0, 100, 100));
		colorize(rect, new RgbaColor(0, 128, 128, 128));
		trafo(rect).translate(50, 50);

		AbstractFigure ellipse = new ShapeFigure(new Ellipse(0, 0, 100, 100));
		colorize(ellipse, new RgbaColor(128, 0, 128, 128));
		trafo(ellipse).translate(100, 100);

		CanvasFigure canvas = new CanvasFigure(100, 100);
		GraphicsContext g = canvas.getGraphicsContext();
		g.setStroke(new RgbaColor(255, 0, 0, 255));
		g.strokeText("canvas output", 0, 0);

		// root.addFigures(curved, rect, ellipse, canvas);
		root.addFigures(rect, ellipse, canvas);

		Button button = new Button(root, SWT.PUSH);
		button.setText("push me");
		button.setLocation(300, 100);
		button.setSize(100, 50);

		shell.pack();
		shell.open();
		shell.redraw();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void colorize(AbstractFigure sf, RgbaColor color) {
		sf.getPaintStateByReference().getFillByReference()
				.setColorByReference(color);
	}

	private AffineTransform trafo(AbstractFigure sf) {
		return sf.getPaintStateByReference().getTransformByReference();
	}

}
