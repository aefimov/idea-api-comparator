package org.intellij.apiComparator.spi.nodes.asm;

import org.objectweb.asm.Opcodes;

/**
 * Helper class for Asm flags decoding {@link Opcodes}.
 *
 * @author <a href="mailto:thibaut.fagart@gmail.com">Thibaut Fagart</a>
 */
public class AsmUtil {

    public static boolean isPublic(int access) {
        return (Opcodes.ACC_PUBLIC & access) == Opcodes.ACC_PUBLIC;
    }

    public static boolean isProtected(int access) {
        return (Opcodes.ACC_PROTECTED & access) == Opcodes.ACC_PROTECTED;
    }

    public static boolean isStatic(int accessFlags) {
        return (Opcodes.ACC_STATIC & accessFlags) == Opcodes.ACC_STATIC;
    }

    public static boolean isPrivate(int accessFlags) {
        return (Opcodes.ACC_PRIVATE & accessFlags) == Opcodes.ACC_PRIVATE;
    }

    public static boolean isClass(int flags) {
        return (Opcodes.ACC_INTERFACE & flags) != Opcodes.ACC_INTERFACE;
    }

    public static boolean isInterface(int flags) {
        return (Opcodes.ACC_INTERFACE & flags) == Opcodes.ACC_INTERFACE;
    }

}
