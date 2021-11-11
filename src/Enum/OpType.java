package Enum;

public enum OpType {
    GLOBAL_DECLARE,
    FUNC_DECLARE,
    SET_ARG,

    LOAD_ARRAY,
    STORE_ARRAY,

    ASSIGN,
    PRINT,
    RET_VALUE,
    RET_VOID,
    GETINT,

    PREPARE_CALL,
    CALL,
    FIN_CALL,

    ADD,
    SUB,
    MULT,
    DIV,
    MOD,
    SLT,
    SLE,
    SGT,
    SGE,
    SEQ,
    SNE,

    LABEL_GEN,
    BNE,
    GOTO
}
