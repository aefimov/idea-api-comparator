package org.intellij.apiComparator.spi.nodes;

import org.intellij.apiComparator.spi.markup.TreeItemType;

/**
 * <p>Package tree item
 *
 * @author Alexey Efimov
 */
public class PackageTreeItem extends TreeItem {
    public static final String REGEX_POINT = "\\.";

    public PackageTreeItem(String name) {
        super(name, getName(name));
        setType(TreeItemType.PACKAGE);
    }

    private static String getName(String fullName) {
        String[] names = fullName.split(REGEX_POINT);
        return names[names.length - 1];
    }
}
