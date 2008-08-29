package org.intellij.apiComparator.spi.nodes.asm;

import com.intellij.openapi.util.text.StringUtil;
import org.intellij.apiComparator.spi.markup.TreeItemType;
import org.intellij.apiComparator.spi.nodes.ObfuscatorUtil;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Type;

/**
 * <p>The tree item for java methods.
 *
 * @author <a href="mailto:thibaut.fagart@gmail.com">Thibaut Fagart</a>
 */
public class MethodTreeItem extends AbstractAsmTreeItem {
    public MethodTreeItem(String name, String methodDescriptor, int accessFlags) {
        super(getDisplayName(name, methodDescriptor), name, isScrambled(name, methodDescriptor), accessFlags);

        setType(AsmUtil.isStatic(accessFlags) ? TreeItemType.STATIC_METHOD : TreeItemType.METHOD);
    }

    /**
     * @param methodName       the name of the method
     * @param methodDescriptor the method descriptor {@link Type#getReturnType(String)} {@link
     *                         Type#getArgumentTypes(String)}
     * @return a string of the form: name((argType,)):returnType
     */
    private static String getDisplayName(@NotNull String methodName, @NotNull String methodDescriptor) {
        StringBuffer buffer = new StringBuffer();

        buffer.append(methodName);
        buffer.append('(');
        String[] argTypesStrings = getArgTypes(methodDescriptor);
        buffer.append(StringUtil.join(argTypesStrings, ", "));
        buffer.append("):");
        buffer.append(Type.getReturnType(methodDescriptor).getClassName());
        return buffer.toString();

    }

    private static String[] getArgTypes(String methodDescriptor) {
        Type[] argTypes = Type.getArgumentTypes(methodDescriptor);
        String[] argTypesStrings = new String[argTypes.length];
        for (int i = 0; i < argTypes.length; i++) {
            Type argType = argTypes[i];
            argTypesStrings[i] = argType.getClassName();
        }
        return argTypesStrings;
    }

    /**
     * A method is scrambled if either of it's name or all the used types (arguments or return values) is scrambled
     *
     * @param name             the method name
     * @param methodDescriptor the method descriptor
     * @return whether this method is scrambled
     */
    private static boolean isScrambled(String name, String methodDescriptor) {
        boolean isScrambled = ObfuscatorUtil.isScrambled(name);

        String[] argTypes = getArgTypes(methodDescriptor);
        for (String argType : argTypes) {
            if (ObfuscatorUtil.isScrambled(argType)) {
                return true;
            }
        }
        return !isScrambled && ObfuscatorUtil.isScrambled(Type.getReturnType(methodDescriptor).getClassName());
    }
}