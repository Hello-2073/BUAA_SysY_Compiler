import compiler.Source;
import compiler.error.ErrorRecorder;
import compiler.syntax.Nonterminal;
import compiler.Parser;
import compiler.Tokens;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Compiler {
    public static void main(String[] args) throws IOException {
        String src = "testfile.txt";
        String dst = "output.txt";

        Source source = new Source(src);

        System.out.println("【词法分析开始】");
        Tokens tokens = new Tokens(source);
        System.out.println(tokens);
        System.out.println("【词法分析结束】");

        Parser parser = new Parser(tokens);

        System.out.println("【语法分析开始】");
        Nonterminal root = parser.parse();
        System.out.println(root);
        System.out.println("【语法分析结束】");

        System.out.println("【语义分析开始】");
        root.translate();
        System.out.println("【语义分析结束】");

        System.out.println("【错误报告】");
        String errors = ErrorRecorder.info();
        System.out.println(errors);

        writeToFile(errors, "error.txt");
    }

    private static void writeToFile(String str, String dst)
            throws IOException {
        File out = new File(dst);
        if (!out.exists())
            out.createNewFile();
        FileWriter writer = new FileWriter(out);
        writer.write(str);
        writer.flush();
        writer.close();
    }
}
