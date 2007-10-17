/* $Id$ */
package org.intellij.apiComparator.spi.markup;

/**
 * Model for markup tree.
 *
 * @author <a href="mailto:aefimov@tengry.com">Alexey Efimov</a>
 * @version $Revision$
 */
public interface MarkupModel {
    /**
     * Return markup attributes for tree item.
     *
     * @param marker Tree item marker.
     */
    public MarkupAttributes getAttributes(TreeItemMarker marker);
}
