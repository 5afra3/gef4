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
package org.eclipse.gef4.swt.canvas.ex;

import org.eclipse.gef4.swt.canvas.gc.ArcType;
import org.eclipse.gef4.swt.canvas.gc.GraphicsContext;
import org.eclipse.gef4.swt.canvas.gc.RgbaColor;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Canvas;

public class PrimitivesExample implements IExample {

	public static void main(String[] args) {
		new Example(new PrimitivesExample());
	}

	private Canvas c;

	public PrimitivesExample() {
	}

	@Override
	public void addUi(Canvas c) {
		this.c = c;
	}

	@Override
	public int getHeight() {
		return 480;
	}

	@Override
	public String getTitle() {
		return "Primitives";
	}

	@Override
	public int getWidth() {
		return 640;
	}

	@Override
	public void render(GraphicsContext g) {
		long time = System.currentTimeMillis();

		g.clearRect(0, 0, c.getSize().x, c.getSize().y);

		g.setFill(new RgbaColor(255, 0, 0));
		g.fillArc(20, 20, 100, 100, 30, 130, ArcType.ROUND);

		g.setFill(new RgbaColor(0, 255, 0));
		g.fillOval(140, 20, 100, 100);

		g.setFill(new RgbaColor(0, 0, 255));
		g.fillPolygon(new double[] { 70, 120, 20 }, new double[] { 140, 220,
				220 }, 3);

		g.setFill(new RgbaColor(255, 255, 0));
		g.fillRect(140, 140, 100, 100);

		g.setFill(new RgbaColor(255, 0, 255));
		g.fillRoundRect(260, 20, 100, 100, 20, 20);

		g.setFill(new RgbaColor(0, 255, 255));

		for (int size = 16, y = 140; size > 6; size -= 2, y += 20) {
			FontData fontData = g.getFont().getFontData()[0];
			fontData.setHeight(size);
			g.setFont(fontData);
			g.fillText("Too long for 100px?", 260, y, 100);
		}

		System.out.println("render time = "
				+ (System.currentTimeMillis() - time) + "ms");
	}

}
