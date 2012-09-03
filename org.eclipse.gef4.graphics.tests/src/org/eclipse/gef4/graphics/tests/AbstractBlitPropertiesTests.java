package org.eclipse.gef4.graphics.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef4.graphics.IBlitProperties;
import org.eclipse.gef4.graphics.IBlitProperties.InterpolationHint;
import org.junit.Test;

public abstract class AbstractBlitPropertiesTests extends AbstractGraphicsTests {

	@Test
	public void getCopy() {
		IBlitProperties gbp = graphics.getBlitProperties();
		IBlitProperties bp = gbp.getCopy();

		assertTrue(bp != gbp);
		assertEquals(gbp.getInterpolationHint(), bp.getInterpolationHint());

		bp.setInterpolationHint(InterpolationHint.SPEED);
		IBlitProperties speedy = bp.getCopy();
		bp.setInterpolationHint(InterpolationHint.QUALITY);

		assertTrue(bp != speedy);
		assertEquals(InterpolationHint.QUALITY, bp.getInterpolationHint());
		assertEquals(InterpolationHint.SPEED, speedy.getInterpolationHint());
	}

	@Test
	public void getInterpolationHint_default() {
		IBlitProperties bp = graphics.getBlitProperties().getCopy();

		assertEquals(IBlitProperties.DEFAULT_INTERPOLATION_HINT,
				bp.getInterpolationHint());
	}

	@Test
	public void setInterpolationHint() {
		IBlitProperties bp = graphics.getBlitProperties().getCopy();

		InterpolationHint hint = bp.getInterpolationHint();
		assertEquals(hint, bp.getInterpolationHint());

		bp.setInterpolationHint(InterpolationHint.SPEED);
		assertEquals(InterpolationHint.SPEED, bp.getInterpolationHint());

		bp.setInterpolationHint(InterpolationHint.QUALITY);
		assertEquals(InterpolationHint.QUALITY, bp.getInterpolationHint());
	}

}
