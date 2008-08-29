/* $Id$ */
package org.intellij.apiComparator.spi.markup;

/**
 * Model for markup tree.
 *
 * @author Alexey Efimov
 */
public interface MarkupModel {
    /**
     * Return markup attributes for tree item.
     *
     * @param marker Tree item marker.
     * @return Markup attributes
     */
    public MarkupAttributes getAttributes(TreeItemMarker marker);
}
