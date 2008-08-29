package org.intellij.apiComparator.spi.nodes.asm;

import org.intellij.apiComparator.spi.markup.TreeItemType;
import org.intellij.apiComparator.spi.nodes.ObfuscatorUtil;
import org.intellij.apiComparator.spi.nodes.PackageTreeItem;

/**
 * JavaClass {@link org.intellij.apiComparator.spi.nodes.TreeItem}.
 *
 * @author <a href="mailto:thibaut.fagart@gmail.com">Thibaut Fagart</a>
 */
public class JavaClassTreeItem extends AbstractAsmTreeItem {
    /**
     * Package names
     */
    private String[] packages;

    /**
     * @param fqcn  the fully qualified class name, java source style (ex : "org.intellij.apiComparator.spi.nodes.asm.JavaClassTreeItem")
     * @param flags the access flags
     */
    public JavaClassTreeItem(String fqcn, int flags) {
        super(fqcn, getName(fqcn), ObfuscatorUtil.isScrambled(getName(fqcn)), flags);

        packages = getPackageName(fqcn).split(PackageTreeItem.REGEX_POINT);
        if (AsmUtil.isClass(flags)) {
            setType(AsmUtil.isStatic(flags) ? TreeItemType.STATIC_CLASS : TreeItemType.CLASS);
        } else if (AsmUtil.isInterface(flags)) {
            setType(AsmUtil.isStatic(flags) ? TreeItemType.STATIC_INTERFACE : TreeItemType.INTERFACE);
        }

    }

    /**
     * returns the package of this class
     *
     * @param fqcn the fully qualified class name
     * @return the full package name
     */
    private String getPackageName(String fqcn) {
        return fqcn.substring(0, fqcn.lastIndexOf("."));
    }

    /**
     * @param fqcn the fully qualified class name
     * @return the short name of this class (without the package
     */
    private static String getName(String fqcn) {
        return fqcn.substring(fqcn.lastIndexOf('.'));
    }

    /**
     * @return an array with all parent packages for this class (ex :["org","intellij","apiComparator","spi","nodes","asm"])
     */
    public String[] getPackageNames() {
        return packages;
    }
}