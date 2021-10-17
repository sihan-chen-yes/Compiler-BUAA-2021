public class Error implements Comparable {
    private String msg;
    private int line;
    private ErrorType error;
    private char errorCode;

    public Error(int line,ErrorType error) {
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
            return 0;
        } else {
            return 1;
        }
    }
}
