/* $Id$ */
package org.intellij.apiComparator.spi.nodes;

import org.jetbrains.annotations.NonNls;

/**
 * Common attributes.
 *
 * @author Alexey Efimov
 */
public interface TreeItemAttributes {
    @NonNls
    String ATTR_VALUE = "value";
    /**
     * Name attribute
     */
    @NonNls
    String ATTR_NAME = "name";
    /**
     * Text attribute
     */
    @NonNls
    String ATTR_TEXT = "text";
}
