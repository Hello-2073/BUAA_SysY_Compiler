package compiler.error;

public class Error extends Exception implements Comparable <Error> {
    private int row;
    private String code;

    public Error(String code) {
        this.code = code;
    }

    public Error(int row, String code) {
        this.row = row;
        this.code = code;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getRow() {
        return row;
    }

    public String getCode() {
        return code;
    }

    @Override
    public int compareTo(Error o) {
        int oRow = o.getRow();
        if (row < oRow) {
            return -1;
        } else if (row > oRow) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return row + " " + code;
    }
}
