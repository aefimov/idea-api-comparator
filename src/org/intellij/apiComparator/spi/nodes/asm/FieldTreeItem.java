package org.intellij.apiComparator.spi.nodes.asm;

import org.intellij.apiComparator.spi.markup.TreeItemType;
import org.intellij.apiComparator.spi.nodes.ObfuscatorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;

/**
 * <p>The tree item for java class fields.
 *
 * @author <a href="mailto:thibaut.fagart@gmail.com">Thibaut Fagart</a>
 */
public class FieldTreeItem extends AbstractAsmTreeItem {
    public FieldTreeItem(String name, String signature, Object value, int accessFlags) {
        super(getDisplayName(name, signature, value), name, isScrambled(name, signature), accessFlags);

        setType(AsmUtil.isStatic(accessFlags) ? TreeItemType.STATIC_FIELD : TreeItemType.FIELD);
    }

    /**
     * @param fieldName the name of this field
     * @param typeDescriptor asm TypeDescriptor for this field {@link Type#getType(String)}
     * @param value constant value for this field if any
     *
     * @return a string of the form name : type (=VALUE)
     */
    private static String getDisplayName(
        @NotNull String fieldName, @NotNull String typeDescriptor, @Nullable Object value
    ) {
        StringBuffer buffer = new StringBuffer();

        buffer.append(fieldName);
        buffer.append(':');
        buffer.append(Type.getType(typeDescriptor).getClassName());

        if (value != null) {
            buffer.append(" = ");
            buffer.append(value);
        }

        return buffer.toString();
    }

    /**
     * @param name the field's name
     * @param fieldTypeDescriptor the field's typeDescriptor  {@link Type#getType(String)}
     *
     * @return whether the field is scrambled
     */
    private static boolean isScrambled(String name, String fieldTypeDescriptor) {
        boolean isScrambled = ObfuscatorUtil.isScrambled(name);
        return !isScrambled && ObfuscatorUtil.isScrambled(Type.getType(fieldTypeDescriptor).getClassName());
    }

}