package GrammarAnalysis;
import Enum.*;
public class Error implements Comparable {
    private String msg;
    private int line;
    private ErrorType error;
    private char errorCode;

    public Error(int line, ErrorType error) {
        this.line = line;
        this.error = error;
        if (error == ErrorType.nonSEMICN) {
            msg = "no ;";
            errorCode = 'i';
        } else if (error == ErrorType.nonRPARENT) {
            msg = "no )";
            errorCode = 'j';
        } else if (error == ErrorType.nonRBRACK) {
            msg = "no ]";
            errorCode = 'k';
        } else if (error == ErrorType.illegalString) {
            msg = "illegalChar";
            errorCode = 'a';
        } else if (error == ErrorType.reDef) {
            msg = "redefine Ident";
            errorCode = 'b';
        } else if (error == ErrorType.unDef) {
            msg = "undefine Ident";
            errorCode = 'c';
        } else if (error == ErrorType.paramsNumError) {
            msg = "params number is not right";
            errorCode = 'd';
        } else if (error == ErrorType.paramsTypeError) {
            msg = "params type is not right";
            errorCode = 'e';
        } else if (error == ErrorType.redundantReturn) {
            msg = "redundant return stmt";
            errorCode = 'f';
        } else if (error == ErrorType.unReturn) {
            msg = "no return stmt";
            errorCode = 'g';
        } else if (error == ErrorType.constAssign) {
            msg = "const can't be changed";
            errorCode = 'h';
        } else if (error == ErrorType.printNumError) {
            msg = "print number is not right";
            errorCode = 'l';
        } else if (error == ErrorType.loopError) {
            msg = "break or continue not in loop";
            errorCode = 'm';
        }
    }

    public void print() {
        System.out.println("Line" + line + " : " + msg + " " + errorCode);
    }

    public int getLine() {
        return line;
    }

    public char getErrorCode() {
        return errorCode;
    }

    @Override
    public int compareTo(Object o) {
        Error error = (Error) o;
        if (this.getLine() < error.getLine()) {
            return -1;
        } else if (this.getLine() == error.getLine()) {
            if (this.errorCode < ((Error) o).errorCode) {
                return -1;
            } else if (this.errorCode == ((Error) o).errorCode) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }

    public String getMsg() {
        return msg;
    }
}
