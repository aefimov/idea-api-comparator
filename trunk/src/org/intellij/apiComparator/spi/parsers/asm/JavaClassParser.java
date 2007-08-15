package org.intellij.apiComparator.spi.parsers.asm;

import org.intellij.apiComparator.spi.nodes.ObfuscatorUtil;
import org.intellij.apiComparator.spi.nodes.PackageTreeItem;
import org.intellij.apiComparator.spi.nodes.TreeItem;
import org.intellij.apiComparator.spi.nodes.asm.AsmUtil;
import org.intellij.apiComparator.spi.nodes.asm.FieldTreeItem;
import org.intellij.apiComparator.spi.nodes.asm.JavaClassTreeItem;
import org.intellij.apiComparator.spi.nodes.asm.MethodTreeItem;
import org.intellij.apiComparator.spi.parsers.AbstractTreeParser;
import org.jetbrains.annotations.NonNls;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.EmptyVisitor;

import java.text.MessageFormat;

/**
 * The parser for java class. Uses the {@link ClassVisitor ASM class visitor} to visit each part of the class. Note :
 * contrary to original bcel implementation we can't set the source size at construct time
 *
 * @author <a href="mailto:thibaut.fagart@gmail.com">Thibaut Fagart</a>
 */
public class JavaClassParser extends AbstractTreeParser implements ClassVisitor {
    @NonNls
    private static final String L_0 = "L{0};";
    private static final EmptyVisitor EMPTY_VISITOR = new EmptyVisitor();

    TreeItem cursor = null;
    boolean isScrambled = false;

    public JavaClassParser(ClassReader source) {
        super(source);
        // ASM core api does not allow access expected size of the class
        // was (number of packages in FQCN)+1+fields.count+methods.count
        setSourceSize(0);
    }

    protected void performParse() {
        ((ClassReader) source).accept(this, false);
    }

    public void visit(
            int version, int access, String classNameJVMStyle, String signature, String superName, String[] interfaces
    ) {

        String fqcn = Type.getType(MessageFormat.format(L_0, classNameJVMStyle)).getClassName();
        isScrambled = ObfuscatorUtil.isScrambled(fqcn);
        if (!isScrambled) {
            JavaClassTreeItem classItem = new JavaClassTreeItem(fqcn, access);
            // Split packages class names
            String[] names = classItem.getPackageNames();
            String fullName = "";

            cursor = null;
            // Package tree
            for (String name : names) {
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

                // Notify listeners if available
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
        }
    }

    /**
     * Add every non scrambled and public/protected field if the current class is not scrambled
     *
     * @see ClassVisitor#visitField(int,String,String,String,Object) for parameter descriptions
     */
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (!isScrambled) {
            FieldTreeItem fieldItem = new FieldTreeItem(name, desc, value, access);
            if (!fieldItem.isScrambled() && (AsmUtil.isProtected(access) || AsmUtil.isPublic(access))) {
                cursor.addChild(fieldItem);
                // Notify listeners if avaible
                setCurrentItem(fieldItem);
            }
        }
        return EMPTY_VISITOR;
    }

    /**
     * Add every non scrambled and public/protected method if the current class is not scrambled
     *
     * @see ClassVisitor#visitMethod(int,String,String,String,String[])  for parameter descriptions
     */
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (!isScrambled) {
            MethodTreeItem methodTreeItem = new MethodTreeItem(name, desc, access);
            if (!methodTreeItem.isScrambled() && (AsmUtil.isProtected(access) || AsmUtil.isPublic(access))) {
                cursor.addChild(methodTreeItem);
                // Notify listeners if avaible
                setCurrentItem(methodTreeItem);
            }
        }
        return EMPTY_VISITOR;
    }

    public void visitSource(String source, String debug) {
    }

    /* Inner and outer classes are handled by JarClassParser */
    public void visitOuterClass(String owner, String name, String desc) {
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return EMPTY_VISITOR;
    }

    public void visitAttribute(Attribute attr) {
    }

    /* Inner and outer classes are handled by JarClassParser */
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
    }

    public void visitEnd() {
    }
}
