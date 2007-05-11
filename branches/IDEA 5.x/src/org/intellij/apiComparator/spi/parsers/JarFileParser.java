package org.intellij.apiComparator.spi.parsers;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.intellij.apiComparator.spi.markup.TreeItemType;
import org.intellij.apiComparator.spi.nodes.TreeItem;
import org.intellij.apiComparator.spi.nodes.TreeItemAttributes;
import org.intellij.apiComparator.spi.nodes.bcel.JavaClassTreeItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * The JAR tree loader implementation.
 * 
 * @author <a href="mailto:aefimov@spklabs.com">Alexey Efimov</a>
 * @see com.intellij.openapi.command.CommandAdapter
 */
class JarFileParser extends AbstractTreeParser {
    /** Class file extension */
    private static final String CLASS_EXT = ".class";

    /** Map with all classes links in jar to track inners */
    private SortedMap classes = new TreeMap();
    private static final char INNER_SEPARATOR = '$';

    public JarFileParser(JarFile jarFile) {
        super(jarFile);
        setSourceSize(((JarFile)source).size());
    }

    protected void performParse() {

        TreeParserManager parserManager = TreeParserManager.getInstance();
        TreeItem root = new TreeItem(((JarFile)source).getName());
        root.setType(TreeItemType.ARCHIVE);
        setCurrentItem(root);

        try {
            Enumeration enumeration = ((JarFile)source).entries();

            while (enumeration.hasMoreElements()) {

                JarEntry jarEntry = (JarEntry)enumeration.nextElement();
                String entryName = jarEntry.getName();

                if (entryName.endsWith(CLASS_EXT)) {

                    InputStream inputStream = ((JarFile)source).getInputStream(jarEntry);
                    try {
                        JavaClass javaClass = new ClassParser(inputStream, entryName).parse();
                        TreeItem rootPackage = parserManager.getParser(javaClass).parse();
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
                ((JarFile)source).close();
            } catch (IOException e) {
            }
        }

        // Append Inners
        Iterator names = classes.keySet().iterator();
        while (names.hasNext()) {
            String name = (String)names.next();
            int innerIndex = name.lastIndexOf(INNER_SEPARATOR);
            if (innerIndex != -1) {
                String parrentName = name.substring(0, innerIndex);
                TreeItem parent = (TreeItem)classes.get(parrentName);
                if (parent != null) {
                    TreeItem item = (TreeItem)classes.get(name);
                    // Change name
                    String innerName = name.substring(innerIndex + 1);
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
                currentItem = (TreeItem)currentItem.getChildren().get(0);
            }
        }
        return currentItem;
    }
}
