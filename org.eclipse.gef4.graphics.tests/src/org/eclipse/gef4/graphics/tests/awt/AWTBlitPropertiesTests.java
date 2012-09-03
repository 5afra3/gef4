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
package org.eclipse.gef4.graphics.tests.awt;

import org.eclipse.gef4.graphics.tests.AbstractBlitPropertiesTests;

public class AWTBlitPropertiesTests extends AbstractBlitPropertiesTests {

	@Override
	public void initializeGraphics() {
		graphics = Utils.createGraphics();
	}

}
