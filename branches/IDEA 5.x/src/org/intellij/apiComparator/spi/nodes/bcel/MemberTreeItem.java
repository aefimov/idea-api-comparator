package org.intellij.apiComparator.spi.nodes.bcel;

import org.apache.bcel.classfile.*;
import org.intellij.apiComparator.spi.markup.TreeItemAccessType;
import org.intellij.apiComparator.spi.markup.TreeItemType;
import org.intellij.apiComparator.spi.nodes.TreeItemAttributes;
import org.phantom.lang.Strings;

/**
 * <p>The tree item for java class members (such as fields and methods).
 * 
 * @author <a href="mailto:aefimov@spklabs.com">Alexey Efimov</a>
 */
public class MemberTreeItem extends ScrambleableTreeItem {
    public MemberTreeItem(FieldOrMethod member) {
        super(member.toString(), getName(member), isScrambled(member));

        String displayName = getDisplayName(member);
        setAttribute(TreeItemAttributes.ATTR_TEXT, displayName);

        if (member instanceof Field) {
            setType(member.isStatic() ? TreeItemType.STATIC_FIELD : TreeItemType.FIELD);
        } else if (member instanceof Method) {
            setType(member.isStatic() ? TreeItemType.STATIC_METHOD : TreeItemType.METHOD);
        }
        if (member.isPublic()) {
            setAccessType(TreeItemAccessType.PUBLIC);
        } else if (member.isProtected()) {
            setAccessType(TreeItemAccessType.PROTECTED);
        } else if (member.isPrivate()) {
            setAccessType(TreeItemAccessType.PRIVATE);
        } else {
            setAccessType(TreeItemAccessType.PLOCAL);
        }
    }

    private static String getName(FieldOrMethod member) {
        if (member == null) {
            throw new IllegalArgumentException("member is null");
        }
        StringBuffer buffer = new StringBuffer();
        if (member instanceof Field) {
            Field field = (Field)member;
            buffer.append(field.getName());
            buffer.append(':');
            buffer.append(Utility.signatureToString(field.getSignature()));
        } else if (member instanceof Method) {
            Method method = (Method)member;
            buffer.append(method.getName());
            buffer.append('(');
            String[] argTypes = Utility.methodSignatureArgumentTypes(method.getSignature());
            buffer.append(Strings.join(argTypes, ", "));
            buffer.append(")");
        }
        return buffer.toString();
    }

    private static String getDisplayName(FieldOrMethod member) {
        if (member == null) {
            throw new IllegalArgumentException("member is null");
        }
        StringBuffer buffer = new StringBuffer();
        if (member instanceof Field) {
            Field field = (Field)member;
            buffer.append(field.getName());
            buffer.append(':');
            buffer.append(Utility.signatureToString(field.getSignature()));

            ConstantValue cv = field.getConstantValue();
            if (cv != null) {
                buffer.append(" = ");
                buffer.append(cv);
            }
        } else if (member instanceof Method) {
            Method method = (Method)member;
            buffer.append(method.getName());
            buffer.append('(');
            String[] argTypes = Utility.methodSignatureArgumentTypes(method.getSignature());
            buffer.append(Strings.join(argTypes, ", "));
            buffer.append("):");
            buffer.append(Utility.methodSignatureReturnType(method.getSignature()));
        }
        return buffer.toString();
    }

    private static boolean isScrambled(FieldOrMethod member) {
        boolean isScrambled = ObfuscatorUtil.isScrambled(getDisplayName(member));
        if (!isScrambled) {
            if (member instanceof Field) {
                Field field = (Field)member;
                return ObfuscatorUtil.isScrambled(Utility.signatureToString(field.getSignature()));
            } else if (member instanceof Method) {
                Method method = (Method)member;
                String[] argTypes = Utility.methodSignatureArgumentTypes(method.getSignature());
                for (int i = 0; i < argTypes.length; i++) {
                    if (ObfuscatorUtil.isScrambled(argTypes[i])) {
                        return true;
                    }
                }
                return ObfuscatorUtil.isScrambled(Utility.methodSignatureReturnType(method.getSignature()));
            }
        }
        return isScrambled;
    }

    public String toString() {
        return (String)getAttributeValue(TreeItemAttributes.ATTR_TEXT);
    }
}
