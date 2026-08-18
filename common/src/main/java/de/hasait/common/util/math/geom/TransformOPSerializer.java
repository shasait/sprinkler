/*
 * Copyright (C) 2026 by Sebastian Hasait (sebastian at hasait dot de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.hasait.common.util.math.geom;

import de.hasait.common.util.AssertUtil;

public final class TransformOPSerializer {

	private static final int TYPE_ID_OP2_AL = 1;
	private static final int TYPE_ID_OP2_AR = 2;
	private static final int TYPE_ID_OP2_XY = 3;
	private static final int TYPE_ID_OP2_DD = 4;

	public static TransformOP2 createTransformOP2(int pTypeID, int pOperand1, int pOperand2) {
		if (pTypeID == TYPE_ID_OP2_AL) {
			return new ALTransformOP2(Angle.fromRawValue(pOperand1), pOperand2, false);
		}
		if (pTypeID == TYPE_ID_OP2_AR) {
			return new ARTransformOP2(Angle.fromRawValue(pOperand1), pOperand2, false);
		}
		if (pTypeID == TYPE_ID_OP2_DD) {
			return new DDTransformOP2(Angle.fromRawValue(pOperand1), pOperand2);
		}
		if (pTypeID == TYPE_ID_OP2_XY) {
			return new XYTransformOP2(pOperand1, pOperand2);
		}
		throw AssertUtil.createNotReachable("Unknown typeID: " + pTypeID); //$NON-NLS-1$
	}

	public static int getOperand1(TransformOP2 pTransformOP2) {
		if (pTransformOP2 instanceof ALTransformOP2) {
			return ((ALTransformOP2) pTransformOP2).getAngle().getRawValue();
		}
		if (pTransformOP2 instanceof ARTransformOP2) {
			return ((ARTransformOP2) pTransformOP2).getAngle().getRawValue();
		}
		if (pTransformOP2 instanceof DDTransformOP2) {
			return ((DDTransformOP2) pTransformOP2).getDirection().getRawValue();
		}
		if (pTransformOP2 instanceof XYTransformOP2) {
			return ((XYTransformOP2) pTransformOP2).getX();
		}
		throw AssertUtil.createNotReachable("Unknown instance: " + pTransformOP2); //$NON-NLS-1$
	}

	public static int getOperand2(TransformOP2 pTransformOP2) {
		if (pTransformOP2 instanceof ALTransformOP2) {
			return ((ALTransformOP2) pTransformOP2).getLength();
		}
		if (pTransformOP2 instanceof ARTransformOP2) {
			return ((ARTransformOP2) pTransformOP2).getRadius();
		}
		if (pTransformOP2 instanceof DDTransformOP2) {
			return ((DDTransformOP2) pTransformOP2).getDistance();
		}
		if (pTransformOP2 instanceof XYTransformOP2) {
			return ((XYTransformOP2) pTransformOP2).getY();
		}
		throw AssertUtil.createNotReachable("Unknown instance: " + pTransformOP2); //$NON-NLS-1$
	}

	public static int getTypeID(TransformOP2 pTransformOP2) {
		if (pTransformOP2 instanceof ALTransformOP2) {
			return TYPE_ID_OP2_AL;
		}
		if (pTransformOP2 instanceof ARTransformOP2) {
			return TYPE_ID_OP2_AR;
		}
		if (pTransformOP2 instanceof DDTransformOP2) {
			return TYPE_ID_OP2_DD;
		}
		if (pTransformOP2 instanceof XYTransformOP2) {
			return TYPE_ID_OP2_XY;
		}
		throw AssertUtil.createNotReachable("Unknown instance: " + pTransformOP2); //$NON-NLS-1$
	}

	private TransformOPSerializer() {
		super();
	}

}
