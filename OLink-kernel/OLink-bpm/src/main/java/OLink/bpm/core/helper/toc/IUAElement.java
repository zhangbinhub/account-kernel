/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package OLink.bpm.core.helper.toc;

/**
 * An element in a UA document, which may have conditional enablement (may be
 * filtered based on certain conditions) and may have sub-elements, or children.
 * 
 * @since 3.3
 */
public interface IUAElement {
	/**
	 * Returns all sub-elements (children) of this element.
	 * 
	 * @return the sub-elements of this element
	 */
	IUAElement[] getChildren();
}
