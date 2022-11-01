package compiler.lexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Source {
    private String content;

    private int begin = 0;
    private int end = 0;
    private int begin_row = 1;
    private int end_row =1;

    public Source(String filename) {
        try {
            content = new String(Files.readAllBytes(Paths.get(filename)));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean isEnd() {
        return end == content.length();
    }

    public boolean isDigit() {
        if (isEnd())
            return false;
        char ch = check(0);
        return '0' <= ch && ch <= '9';
    }

    public boolean isLetter() {
        if (isEnd()) return false;
        char ch = check(0);
        return 'a' <= ch && ch <= 'z' || 'A' <= ch && ch <='Z' || ch == '_';
    }

    public boolean isEmpty() {
        if (isEnd()) return false;
        char ch = check(0);
        return ch == ' ' || ch =='\t' || ch == '\n' || ch == '\r';
    }

    public int getRow() {
        return begin_row;
    }

    public String readBuffer() {
        return content.substring(begin, end);
    }

    public char check(int offset) {
        int index = end + offset;
        if (index >= content.length())
            return '\0';
        return content.charAt(index);
    }

    public void flush() {
        begin = end;
        begin_row = end_row;
    }

    public boolean tryGetChar(char ch) {
        if (isEnd())
            return false;
        if (content.charAt(end) == ch) {
            getChar();
            return true;
        }
        return false;
    }

    public char getChar() {
        if (isEnd())
            return '\0';
        char ch = content.charAt(end);
        end++;
        if (ch == '\n') {
            end_row++;
        }
        return ch;
    }
}
