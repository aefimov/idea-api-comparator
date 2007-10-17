package org.intellij.apiComparator.tree;

import org.intellij.apiComparator.spi.markup.*;
import org.intellij.apiComparator.spi.nodes.TreeItem;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * Renderer for {@link JTree}
 * 
 * @author <a href="mailto:aefimov@spklabs.com">Alexey Efimov</a>
 */
public class TreeItemRenderer extends DefaultTreeCellRenderer {
    /**
     * Markup model for items.
     */
    private MarkupModel markupModel;

    /**
     * Access icon
     */
    private Icon accessIcon;

    /**
     * Default icon gap
     */
    private static final int defaultIconGap = 4;

    public TreeItemRenderer(MarkupModel markupModel) {
        this.markupModel = markupModel;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Paint access modifier icon
        if (accessIcon != null) {
            accessIcon.paintIcon(this, g, (getIcon() != null ? getIcon().getIconWidth() : 0), 0);
        }
    }

    public Component getTreeCellRendererComponent(
        JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus
        ) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        this.accessIcon = null;
        setIconTextGap(defaultIconGap);

        if (value instanceof TreeItem) {
            TreeItem item = (TreeItem)value;

            // Set up icon for node
            TreeItemType type = item.getType();
            if (type != null) {
                setIcon(expanded ? type.getOpen() : item.getType().getClosed());
            }

            TreeItemAccessType accessType = item.getAccessType();
            if (accessType != null) {
                setAccessIcon(accessType.getIcon());
            }

            // Set text
            setText(item.toString());

            // Set markup
            TreeItemMarker marker = item.getMarker();
            MarkupAttributes attributes = markupModel.getAttributes(marker);
            if (attributes != null) {
                setBackground(attributes.getBackground());
                setForeground(attributes.getForeground());
                setFont(attributes.getFont());
            }
        }

        return this;
    }

    private void setAccessIcon(Icon accessIcon) {
        // Inc icon gap
        if (this.accessIcon == null && accessIcon != null) {
            setIconTextGap(getIconTextGap() + accessIcon.getIconWidth());
        }
        this.accessIcon = accessIcon;
    }
}
