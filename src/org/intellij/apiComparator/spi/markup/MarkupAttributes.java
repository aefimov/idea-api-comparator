/* $Id$ */
package org.intellij.apiComparator.spi.markup;

import java.awt.*;

/**
 * Attributes for painting tree.
 *
 * @author <a href="mailto:aefimov@tengry.com">Alexey Efimov</a>
 * @version $Revision$
 */
public class MarkupAttributes {
    private Color foreground;
    private Color background;
    private Font font;

    public MarkupAttributes(Color foreground) {
        this.foreground = foreground;
    }

    public MarkupAttributes(Color foreground, Color background) {
        this.foreground = foreground;
        this.background = background;
    }

    public MarkupAttributes(Color foreground, Color background, Font font) {
        this.foreground = foreground;
        this.background = background;
        this.font = font;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Color getForeground() {
        return foreground;
    }

    public void setForeground(Color foreground) {
        this.foreground = foreground;
    }

    public Color getBackground() {
        return background;
    }

    public void setBackground(Color background) {
        this.background = background;
    }
}
