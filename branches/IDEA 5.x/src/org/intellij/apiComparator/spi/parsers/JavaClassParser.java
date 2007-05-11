package org.intellij.apiComparator.spi.parsers;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.intellij.apiComparator.spi.nodes.TreeItem;
import org.intellij.apiComparator.spi.nodes.bcel.JavaClassTreeItem;
import org.intellij.apiComparator.spi.nodes.bcel.MemberTreeItem;
import org.intellij.apiComparator.spi.nodes.bcel.PackageTreeItem;

/**
 * The parser for java class.
 * 
 * @author <a href="mailto:aefimov@spklabs.com">Alexey Efimov</a>
 */
class JavaClassParser extends AbstractTreeParser {
    /**
     * Java Class item
     */
    private JavaClassTreeItem classItem;

    public JavaClassParser(JavaClass javaClass) {
        super(javaClass);

        classItem = new JavaClassTreeItem(((JavaClass)source));

        // Set size of child items
        // Size of all posible will build items
        // packages tree, class itself, methods and fields
        setSourceSize(
            classItem.getPackageNames().length +
            ((JavaClass)source).getFields().length +
            ((JavaClass)source).getMethods().length +
            1
        );
    }

    protected void performParse() {

        if (!classItem.isScrambled()) {

            // Split packages class names
            String[] names = classItem.getPackageNames();
            String fullName = "";

            TreeItem cursor = null;
            // Package tree
            for (int i = 0; i < names.length; i++) {

                String name = names[i];
                fullName += name;

                TreeItem item = new PackageTreeItem(fullName);
                if (cursor != null) {
                    cursor.addChild(item);
                } else {
                    // First item - it a root
                    setResult(item);
                }
                // Move to next
                cursor = item;

                // Notify listeners if avaible
                setCurrentItem(item);

                fullName += ".";
            }

            // Add class item
            if (cursor != null) {
                cursor.addChild(classItem.getParent() != null ? classItem.getParent() : classItem);
            }
            cursor = classItem;
            // Notify listeners if avaible
            setCurrentItem(classItem);

            Field[] fields = ((JavaClass)source).getFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                MemberTreeItem memberItem = new MemberTreeItem(field);
                if (!memberItem.isScrambled() && (field.isProtected() || field.isPublic())) {
                    cursor.addChild(memberItem);
                    // Notify listeners if avaible
                    setCurrentItem(memberItem);
                }
            }

            Method[] methods = ((JavaClass)source).getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                MemberTreeItem memberItem = new MemberTreeItem(method);
                if (!memberItem.isScrambled() && (method.isProtected() || method.isPublic())) {
                    cursor.addChild(memberItem);
                    // Notify listeners if avaible
                    setCurrentItem(memberItem);
                }
            }
        }
    }
}
