/* $Id$ */
package org.intellij.apiComparator.spi.markup;

import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;
import java.text.MessageFormat;

/**
 * Access icons.
 *
 * @author Alexey Efimov
 */
public enum TreeItemAccessType {
    PUBLIC("/nodes/c_public.png"),
    PROTECTED("/nodes/c_protected.png"),
    PRIVATE("/nodes/c_private.png"),
    PLOCAL("/nodes/c_plocal.png");

    private final Icon icon;

    public static TreeItemAccessType valueOf(int value) {
        for (TreeItemAccessType constant : TreeItemAccessType.values()) {
            if (constant.ordinal() == value) {
                return constant;
            }
        }
        throw new IllegalArgumentException(MessageFormat.format("can''t get enum for value {0}", value));
    }

    private TreeItemAccessType(@NonNls String icon) {
        this(IconLoader.getIcon(icon));
    }

    private TreeItemAccessType(Icon icon) {
        this.icon = icon;
    }

    public Icon getIcon() {
        return icon;
    }
}
