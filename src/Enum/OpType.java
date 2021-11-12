package Enum;

public enum OpType {
    GLOBAL_DECLARE,

    FUNC_DECLARE,
    PUSH_PARAM,
    STORE_RET,

    LOAD_ARRAY_1D,
    STORE_ARRAY_1D,
    LOAD_ARRAY_2D,
    STORE_ARRAY_2D,
    LOAD_ARRDESS,

    ASSIGN,
    PRINT_STRING,
    PRINT_INT,
    RET_VALUE,
    RET_VOID,
    GETINT,

    PREPARE_CALL,
    CALL,
    FIN_CALL,
    EXIT,

    ADD,
    SUB,
    MULT,
    DIV,
    MOD,
    NEG,

    SLT,
    SLE,
    SGT,
    SGE,
    SEQ,
    SNE,
    NOT,

    LABEL_GEN,
    BNE,
    GOTO
}
