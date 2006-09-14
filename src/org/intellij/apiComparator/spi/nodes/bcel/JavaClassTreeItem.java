package org.intellij.apiComparator.spi.nodes.bcel;

import org.apache.bcel.classfile.JavaClass;
import org.intellij.apiComparator.spi.markup.TreeItemAccessType;
import org.intellij.apiComparator.spi.markup.TreeItemType;

/**
 * JavaClass {@link org.intellij.apiComparator.spi.nodes.TreeItem}.
 * 
 * @author <a href="mailto:aefimov@spklabs.com">Alexey Efimov</a>
 */
public class JavaClassTreeItem extends ScrambleableTreeItem {
    /**
     * Package names
     */
    private String[] packages;

    public JavaClassTreeItem(JavaClass javaClass) {
        super(javaClass.getClassName(), getName(javaClass), ObfuscatorUtil.isScrambled(getName(javaClass)));

        packages = javaClass.getPackageName().split(PackageTreeItem.REGEX_POINT);
        if (javaClass.isClass()) {
            setType(javaClass.isStatic() ? TreeItemType.STATIC_CLASS : TreeItemType.CLASS);
        } else if (javaClass.isInterface()) {
            setType(javaClass.isStatic() ? TreeItemType.STATIC_INTERFACE : TreeItemType.INTERFACE);
        }
        if (javaClass.isPublic()) {
            setAccessType(TreeItemAccessType.PUBLIC);
        } else if (javaClass.isProtected()) {
            setAccessType(TreeItemAccessType.PROTECTED);
        } else if (javaClass.isPrivate()) {
            setAccessType(TreeItemAccessType.PRIVATE);
        } else {
            setAccessType(TreeItemAccessType.PLOCAL);
        }
    }

    private static String getName(JavaClass javaClass) {
        String className = javaClass.getClassName();
        String[] names = className.split(PackageTreeItem.REGEX_POINT);
        String shortName = names[names.length - 1];
        return shortName;
    }

    public String[] getPackageNames() {
        return packages;
    }
}
