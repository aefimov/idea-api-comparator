package org.intellij.apiComparator.spi.parsers.asm;

import org.intellij.apiComparator.spi.markup.TreeItemType;
import org.intellij.apiComparator.spi.nodes.TreeItem;
import org.intellij.apiComparator.spi.nodes.TreeItemAttributes;
import org.intellij.apiComparator.spi.nodes.asm.JavaClassTreeItem;
import org.intellij.apiComparator.spi.parsers.AbstractTreeParser;
import org.jetbrains.annotations.NonNls;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * The JAR tree loader implementation.
 *
 * @author <a href="mailto:thibaut.fagart@gmail.com">Thibaut Fagart</a>
 * @see com.intellij.openapi.command.CommandAdapter
 */
class JarFileParser extends AbstractTreeParser {
    /**
     * Class file extension
     */
    @NonNls
    private static final String CLASS_EXT = ".class";

    /**
     * Map with all classes links in jar to track inners
     */
    private SortedMap<String, TreeItem> classes = new TreeMap<String, TreeItem>();
    private static final char INNER_SEPARATOR = '$';

    public JarFileParser(JarFile jarFile) {
        super(jarFile);
        setSourceSize(((JarFile) source).size());
    }

    protected void performParse() {

        TreeParserManager parserManager = TreeParserManager.getInstance();
        TreeItem root = new TreeItem(((JarFile) source).getName());
        root.setType(TreeItemType.ARCHIVE);
        setCurrentItem(root);

        try {
            Enumeration<JarEntry> enumeration = ((JarFile) source).entries();

            while (enumeration.hasMoreElements()) {

                JarEntry jarEntry = enumeration.nextElement();
                String entryName = jarEntry.getName();

                if (entryName.endsWith(CLASS_EXT)) {

                    InputStream inputStream = ((JarFile) source).getInputStream(jarEntry);
                    try {
                        ClassReader classReader = new ClassReader(inputStream);

                        TreeItem rootPackage = parserManager.getParser(classReader).parse();
                        if (rootPackage != null) {
                            TreeItem classItem = slideDown(rootPackage);
                            String name = classItem.toString();
                            classes.put(name, classItem);
                            if (name.indexOf(INNER_SEPARATOR) == -1) {
                                // Add only top level classes
                                root.addChild(rootPackage);
                                setCurrentItem(classItem);
                            }
                        }

                    } finally {
                        inputStream.close();
                    }
                }

                incCurrentIndex();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                ((JarFile) source).close();
            } catch (IOException e) {
            }
        }

        // Append Inners
        for (String className : classes.keySet()) {
            int innerIndex = className.lastIndexOf(INNER_SEPARATOR);
            if (innerIndex != -1) {
                String parentName = className.substring(0, innerIndex);
                TreeItem parent = classes.get(parentName);
                if (parent != null) {
                    TreeItem item = classes.get(className);
                    // Change className
                    @NonNls String innerName = className.substring(innerIndex + 1);
                    // Skip annonimous classes
                    if (!innerName.matches("^\\d+$")) {
                        item.setAttribute(TreeItemAttributes.ATTR_NAME, innerName);
                        // Add child
                        parent.addChild(item);
                    }
                }
            }

        }
        classes.clear();
        // Set result
        setResult(root);
    }

    private TreeItem slideDown(TreeItem currentItem) {
        if (currentItem != null) {
            while (currentItem.getChildren().size() == 1 && !(currentItem instanceof JavaClassTreeItem)) {
                currentItem = (TreeItem) currentItem.getChildren().get(0);
            }
        }
        return currentItem;
    }
}