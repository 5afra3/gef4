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
package org.eclipse.gef4.graphics.examples.doc;

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.Gradient;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.examples.IExample;
import org.eclipse.gef4.graphics.examples.SwtExample;

public class Example002 implements IExample {

	public static void main(String[] args) {
		new SwtExample(new Example002());
	}

	@Override
	public int getHeight() {
		return 200;
	}

	@Override
	public String getTitle() {
		return "Example 002 - Gradient.Linear";
	}

	@Override
	public int getWidth() {
		return 200;
	}

	@Override
	public void renderScene(IGraphics g) {
		g.translate(50, 50);
		Rectangle rect = new Rectangle(0, 0, 100, 100);
		Gradient.Linear linearGradient = new Gradient.Linear(rect.getTopLeft(),
				rect.getTopRight()).addStop(0.0, new Color(255, 255, 255))
				.addStop(0.5, new Color(255, 0, 0))
				.addStop(1.0, new Color(0, 0, 0));
		g.setFill(linearGradient);
		g.fill(rect);
	}

}
