package compiler.error;

import java.util.PriorityQueue;

public class ErrorRecorder {
    private static final PriorityQueue<Error> errors = new PriorityQueue<>();

    public static void insert(Error error) {
        errors.add(error);
    }

    public static String info() {
        StringBuilder sb = new StringBuilder();
        while (errors.size() > 0) {
            Error error = errors.poll();
            sb.append(error);
            sb.append('\n');
        }
        return sb.toString();
    }
}
