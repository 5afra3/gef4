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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swt.fx.CanvasFigure;
import org.eclipse.gef4.swt.fx.ControlNode;
import org.eclipse.gef4.swt.fx.Group;
import org.eclipse.gef4.swt.fx.IParent;
import org.eclipse.gef4.swt.fx.event.IEventHandler;
import org.eclipse.gef4.swt.fx.event.MouseEvent;
import org.eclipse.gef4.swt.fx.gc.ArcType;
import org.eclipse.gef4.swt.fx.gc.Gradient;
import org.eclipse.gef4.swt.fx.gc.GraphicsContext;
import org.eclipse.gef4.swt.fx.gc.LinearGradient;
import org.eclipse.gef4.swt.fx.gc.RadialGradient;
import org.eclipse.gef4.swt.fx.gc.RgbaColor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class NestedControls implements IExample {

	public static void main(String[] args) {
		new Example(new NestedControls());
	}

	private boolean gammaCorrection = false;

	private void addGradientStops(Gradient<?> gradient) {
		gradient.addStop(0.0, new RgbaColor(0, 0, 0))
				.addStop(0.125, new RgbaColor(255, 0, 0))
				.addStop(0.25, new RgbaColor(255, 0, 255))
				.addStop(0.375, new RgbaColor(0, 0, 255))
				.addStop(0.5, new RgbaColor(0, 255, 255))
				.addStop(0.625, new RgbaColor(0, 255, 0))
				.addStop(0.75, new RgbaColor(255, 255, 0))
				.addStop(0.875, new RgbaColor(255, 200, 180))
				.addStop(1, new RgbaColor(255, 255, 255));
	}

	@Override
	public void addUi(final Group root) {
		{
			Group naviGroup = new Group(root);
			naviGroup.resize(100, 300);
			naviGroup
					.setBackground(new Color(root.getDisplay(), 255, 128, 128));

			button(naviGroup, "New", onClick("onNew"), 5, 5, 90);
			button(naviGroup, "Open", onClick("onOpen"), 5, 40, 90);
			button(naviGroup, "Save", onClick("onSave"), 5, 75, 90);
			button(naviGroup, "Quit", onClick("onQuit"), 5, 300 - 35, 90);
		}

		Group contentGroup = new Group(root);
		contentGroup.resizeRelocate(100, 0, 300, 300);
		contentGroup.setBackground(new Color(root.getDisplay(), 128, 255, 128));

		final CanvasFigure canvas = new CanvasFigure(300, 200);
		contentGroup.addFigures(canvas);
		canvas.relocate(0, 100); // leave space for some options
		drawGradients(canvas);

		{
			Group optionsGroup = new Group(contentGroup);
			optionsGroup.resize(300, 100);
			optionsGroup.setBackground(new Color(root.getDisplay(), 128, 128,
					255));

			int h = checkbox(optionsGroup, 5, 5, 290,
					"Gamma correction (x^1/2.2)",
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							gammaCorrection = !gammaCorrection;
							drawGradients(canvas);
							canvas.update();
						}
					});

			checkbox(optionsGroup, 5, 10 + h, 290, "Invert colors",
					new IEventHandler<MouseEvent>() {
						boolean checked = false;

						@Override
						public void handle(MouseEvent event) {
							checked = !checked;
							// canvas.setEffect(checked ? new InvertEffect()
							// : null);
							if (checked) {
								Image image = canvas.getImage();
								ImageData data = image.getImageData();
								for (int x = 0; x < data.width; x++) {
									for (int y = 0; y < data.height; y++) {
										int pixel = data.getPixel(x, y);
										RGB rgb = data.palette.getRGB(pixel);
										rgb.red = 255 - rgb.red;
										rgb.green = 255 - rgb.green;
										rgb.blue = 255 - rgb.blue;
										data.setPixel(x, y,
												data.palette.getPixel(rgb));
									}
								}
								GraphicsContext gc = canvas
										.getGraphicsContext();
								Image tmpImage = new Image(
										Display.getCurrent(), data);
								gc.drawImage(tmpImage, 0, 0);
								tmpImage.dispose();
							} else {
								drawGradients(canvas);
							}
							canvas.update();
						}
					});
		}
	}

	public int button(IParent container, String text,
			IEventHandler<MouseEvent> clickedHandler, int x, int y, int width) {
		Button button = new Button(container.getSwtComposite(), SWT.PUSH);
		button.setText(text);
		ControlNode<Button> node = new ControlNode<Button>(button);
		int height = (int) node.getLayoutBounds().getHeight();
		node.resizeRelocate(x, y, width, height);
		node.addEventHandler(MouseEvent.MOUSE_RELEASED, clickedHandler);
		return height;
	}

	private int checkbox(IParent container, int x, int y, int width,
			String label, IEventHandler<MouseEvent> clickHandler) {
		Composite compo = container.getSwtComposite();
		Button control = new Button(compo, SWT.CHECK);
		control.setText(label);
		control.setBackground(compo.getBackground());

		ControlNode<Button> checkbox = new ControlNode<Button>(control);
		Rectangle bb = checkbox.getLayoutBounds();
		int height = (int) bb.getHeight();
		control.setBounds(x, y, width, height);

		checkbox.addEventHandler(MouseEvent.MOUSE_RELEASED, clickHandler);

		return height;
	}

	/**
	 * @param canvas
	 */
	private void drawGradients(final CanvasFigure canvas) {
		GraphicsContext gc = canvas.getGraphicsContext();
		gc.setFill(new RgbaColor(255, 255, 255));
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

		RadialGradient radialGradient = new RadialGradient(new Ellipse(0, 0,
				canvas.getWidth(), canvas.getHeight()));
		LinearGradient linearGradient = new LinearGradient(new Point(0, 0),
				new Point(canvas.getWidth(), 0));

		addGradientStops(radialGradient);
		addGradientStops(linearGradient);

		if (gammaCorrection) {
			linearGradient.setGammaCorrection(2.2);
			radialGradient.setGammaCorrection(2.2);
		}

		gc.setFill(radialGradient);
		gc.fillArc(0, 0, canvas.getWidth(), canvas.getHeight(), 30, 120,
				ArcType.ROUND);

		gc.setFill(linearGradient);
		gc.fillRect(0, 150, canvas.getWidth(), 50);
	}

	@Override
	public int getHeight() {
		return 300;
	}

	@Override
	public String getTitle() {
		return "Nested Controls";
	}

	@Override
	public int getWidth() {
		return 400;
	}

	private IEventHandler<MouseEvent> onClick(final String methodName) {
		return new IEventHandler<MouseEvent>() {
			private Method method;

			@Override
			public void handle(MouseEvent event) {
				try {
					// System.out.println("get method <" + methodName + ">");
					method = NestedControls.this.getClass().getMethod(
							methodName, null);
					// System.out.println("found method " + method);
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}

				try {
					// System.out.println("invoking...");
					method.invoke(NestedControls.this, null);
					// System.out.println("done.");
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		};
	}

	public void onNew() {
		System.out.println("new");
	}

	public void onOpen() {
		System.out.println("open");
	}

	public void onQuit() {
		System.exit(0);
	}

	public void onSave() {
		System.out.println("save");
	}

}
