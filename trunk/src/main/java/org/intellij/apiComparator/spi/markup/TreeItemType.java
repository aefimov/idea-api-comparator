/* $Id$ */
package org.intellij.apiComparator.spi.markup;

import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.LayeredIcon;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.text.MessageFormat;

/**
 * {@link org.intellij.apiComparator.spi.nodes.TreeItem} type.
 *
 * @author Alexey Efimov
 */
public enum TreeItemType {
    UNKNOWN(-1, StdFileTypes.UNKNOWN.getIcon()),
    ARCHIVE(0, StdFileTypes.ARCHIVE.getIcon()),
    FOLDER(0, "/nodes/folder.png", "/nodes/folderOpen.png"),
    PACKAGE(1, "/nodes/packageClosed.png", "/nodes/packageOpen.png"),
    STATIC_CLASS(2, makeStaticIcon("/nodes/class.png")),
    CLASS(2, "/nodes/class.png"),
    STATIC_INTERFACE(2, makeStaticIcon("/nodes/interface.png")),
    INTERFACE(2, "/nodes/interface.png"),
    STATIC_FIELD(3, makeStaticIcon("/nodes/field.png")),
    FIELD(3, "/nodes/field.png"),
    STATIC_METHOD(4, makeStaticIcon("/nodes/method.png")),
    METHOD(4, "/nodes/method.png");

    private static Icon makeStaticIcon(@NotNull String icon) {
        return LayeredIcon.create(IconLoader.getIcon(icon), IconLoader.getIcon("/nodes/staticMark.png"));
    }

    private Icon closed;
    private Icon open;
    private int level;

    private TreeItemType(int level, @NonNls String icon) {
        this(level, icon, icon);
    }

    private TreeItemType(int level, @NonNls String iconClosed, @NonNls String iconOpen) {
        this(level, IconLoader.getIcon(iconClosed), IconLoader.getIcon(iconOpen));
    }

    private TreeItemType(int level, Icon icon) {
        this(level, icon, icon);
    }

    private TreeItemType(int level, Icon iconClosed, Icon iconOpen) {
        this.level = level;
        this.closed = iconClosed;
        this.open = iconOpen;
    }

    public static TreeItemType valueOf(int value) {
        TreeItemType[] treeItemTypes = TreeItemType.values();
        for (TreeItemType itemType : treeItemTypes) {
            if (itemType.ordinal() == value) {
                return itemType;
            }
        }
        throw new IllegalArgumentException(MessageFormat.format("can''t get enum for value {0}", value));
    }

    public Icon getClosed() {
        return closed;
    }

    public Icon getOpen() {
        return open;
    }

    public boolean isMember() {
        return FIELD.equals(this) || STATIC_FIELD.equals(this) || METHOD.equals(this) || STATIC_METHOD.equals(this);
    }

    public boolean isField() {
        return FIELD.equals(this) || STATIC_FIELD.equals(this);
    }

    public boolean isMethod() {
        return METHOD.equals(this) || STATIC_METHOD.equals(this);
    }

    public boolean isPackage() {
        return PACKAGE.equals(this);
    }

    public boolean isClass() {
        return CLASS.equals(this) || STATIC_CLASS.equals(this) || INTERFACE.equals(this) || STATIC_INTERFACE.equals(
                this
        );
    }

    public int getLevel() {
        return level;
    }
}

