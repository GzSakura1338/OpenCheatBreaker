package optifine.json;

public class ParseException extends Exception {
    public static final long serialVersionUID = -7880698968187728548L;
    public static final int ERROR_UNEXPECTED_CHAR = 0;
    public static final int ERROR_UNEXPECTED_TOKEN = 1;
    public static final int ERROR_UNEXPECTED_EXCEPTION = 2;
    public int position;
    public int errorType;
    public Object unexpectedObject;

    public ParseException(int n) {
        this(-1, n, null);
    }

    public ParseException(int n, Object obj) {
        this(-1, n, obj);
    }

    public ParseException(int n, int n2, Object obj) {
        this.position = n;
        this.errorType = n2;
        this.unexpectedObject = obj;
    }

    public Object getUnexpectedObject() {
        return this.unexpectedObject;
    }

    public void setUnexpectedObject(Object obj) {
        this.unexpectedObject = obj;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        switch (this.errorType) {
            case 0:
                sb.append("Unexpected character (").append(this.unexpectedObject).append(") at position ").append(this.position).append(".");
                break;
            case 1:
                sb.append("Unexpected token ").append(this.unexpectedObject).append(" at position ").append(this.position).append(".");
                break;
            case 2:
                sb.append("Unexpected exception at position ").append(this.position).append(": ").append(this.unexpectedObject);
                break;
            default:
                sb.append("Unkown error at position ").append(this.position).append(".");
        }

        return sb.toString();
    }

    public int getErrorType() {
        return this.errorType;
    }

    public void setPosition(int n) {
        this.position = n;
    }

    public int getPosition() {
        return this.position;
    }

    public void setErrorType(int n) {
        this.errorType = n;
    }
}
