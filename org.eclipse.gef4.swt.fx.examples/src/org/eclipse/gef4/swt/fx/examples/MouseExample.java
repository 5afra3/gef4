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

import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swt.fx.Group;
import org.eclipse.gef4.swt.fx.IFigure;
import org.eclipse.gef4.swt.fx.ShapeFigure;
import org.eclipse.gef4.swt.fx.event.Event;
import org.eclipse.gef4.swt.fx.event.IEventHandler;
import org.eclipse.gef4.swt.fx.event.MouseEvent;
import org.eclipse.gef4.swt.fx.event.TraverseEvent;
import org.eclipse.gef4.swt.fx.gc.GraphicsContext;
import org.eclipse.gef4.swt.fx.gc.RgbaColor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

public class MouseExample implements IExample {

	static class FigureDragger {
		private IFigure figure;
		private boolean dragging;
		private Point start;

		public FigureDragger(final IFigure f) {
			figure = f;
			f.addEventFilter(Event.ANY, new IEventHandler<Event>() {
				@Override
				public void handle(Event event) {
					if (event.getEventType().getName().equals("TraverseEvent")) {
						System.out.println("fig: " + event);
					}
				}
			});
			f.addEventFilter(TraverseEvent.ANY,
					new IEventHandler<TraverseEvent>() {
						@Override
						public void handle(TraverseEvent event) {
							f.update();
						}
					});
			f.addEventHandler(MouseEvent.MOUSE_ENTERED,
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							System.out.println("entered " + f);
						}
					});
			f.addEventHandler(MouseEvent.MOUSE_EXITED,
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							System.out.println("exited " + f);
						}
					});
			f.addEventHandler(MouseEvent.MOUSE_PRESSED,
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent e) {
							f.requestFocus();
							dragging = true;
							start = new Point(e.getX(), e.getY());
						}
					});
			f.addEventHandler(MouseEvent.MOUSE_RELEASED,
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent e) {
							dragging = false;
						}
					});
			f.addEventHandler(MouseEvent.MOUSE_MOVED,
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent e) {
							if (dragging) {
								figure.getPaintStateByReference()
										.getTransformByReference()
										.translate(e.getX() - start.x,
												e.getY() - start.y);
								start.x = e.getX();
								start.y = e.getY();
								figure.update();
							}
						}
					});
		}
	}

	public static void main(String[] args) {
		new Example(new MouseExample());
	}

	private static ShapeFigure shape(IShape shape, RgbaColor color) {
		ShapeFigure figure = new ShapeFigure(shape) {
			@Override
			public void paint(GraphicsContext g) {
				g.pushState(getPaintStateByReference());
				IShape shape = getBounds().getShape();
				g.fillPath(shape.toPath());
				if (hasFocus()) {
					g.getGcByReference().setLineDash(new int[] { 20, 20 });
					g.setLineWidth(5);
					g.setStroke(new RgbaColor());
					g.strokePath(getShape().toPath());
				}
				g.restore();
			}
		};
		figure.getPaintStateByReference().getFillByReference().setColor(color);
		return figure;
	}

	private ShapeFigure rectFigure = shape(new Rectangle(0, 0, 200, 100),
			new RgbaColor(0, 64, 255, 255));
	private ShapeFigure ovalFigure = shape(new Ellipse(0, 0, 100, 200),
			new RgbaColor(255, 64, 0, 255));
	private Group root;

	@Override
	public void addUi(Group root) {
		new FigureDragger(rectFigure);
		new FigureDragger(ovalFigure);

		this.root = root;
		root.addFigures(rectFigure, ovalFigure);

		root.addEventFilter(Event.ANY, new IEventHandler<Event>() {
			@Override
			public void handle(Event event) {
				if (event.getEventType().getName().equals("TraverseEvent")) {
					System.out.println("root: " + event);
				}
			}
		});

		// create SWT control
		Button resetButton = new Button(root, SWT.PUSH);
		resetButton.setText("Reset");
		org.eclipse.swt.graphics.Point size = resetButton.computeSize(
				SWT.DEFAULT, SWT.DEFAULT);
		resetButton.setBounds(20, root.getSize().y - size.y - 20, size.x,
				size.y);
		resetButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resetFigures();
			}
		});

		Button snd = new Button(root, SWT.PUSH);
		snd.setText("snd");
		snd.setBounds(40 + size.x, root.getSize().y - size.y - 20, size.x,
				size.y);

		resetFigures();
	}

	@Override
	public int getHeight() {
		return 480;
	}

	@Override
	public String getTitle() {
		return "Mouse Example";
	}

	@Override
	public int getWidth() {
		return 640;
	}

	private void resetFigures() {
		rectFigure.getPaintStateByReference().getTransformByReference()
				.setToIdentity();
		ovalFigure.getPaintStateByReference().getTransformByReference()
				.setToIdentity();
		root.redraw();
	}

}
