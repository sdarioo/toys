/**
 * (C) Copyright ParaSoft Corporation 2010. All rights reserved.
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF ParaSoft
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.github.sdarioo.testgen.recorder;

import java.util.Collection;

public interface IAggregateParameter
    extends IParameter
{
    /**
     * @return direct components of this aggregate parameters. If parameter component is also IAggregateParameter
     * than its components are not returned.
     */
    Collection<IParameter> getComponents();
}
