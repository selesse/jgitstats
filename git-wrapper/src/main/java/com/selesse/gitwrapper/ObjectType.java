package com.selesse.gitwrapper;

public enum ObjectType {
    BAD_OBJECT(-1),
    OBJ_EXT(0),
    COMMIT(1),
    TREE(2),
    BLOB(3),
    TAG(4),
    ;

    private final int integerValue;

    ObjectType(int integerValue) {
        this.integerValue = integerValue;
    }

    public static ObjectType fromInteger(int integer) {
        for (ObjectType objectType : values()) {
            if (objectType.integerValue == integer) {
                return objectType;
            }
        }

        return BAD_OBJECT;
    }
}
