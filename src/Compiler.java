import compiler.lexer.Source;
import compiler.error.ErrorRecorder;
import compiler.mips.Asm;
import compiler.mips.Translator;
import compiler.representation.Generator;
import compiler.representation.module.Module;
import compiler.syntax.Nonterminal;
import compiler.parser.Parser;
import compiler.parser.Tokens;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class Compiler {
    public static void main(String[] args) throws IOException {
        String src = "testfile.txt";
        String dst = "output.txt";

        Source source = new Source(src);

        Tokens tokens = new Tokens(source);

        Parser parser = new Parser(tokens);

        Nonterminal root = parser.parse();

        Generator.reset();
        root.translate(new HashMap<>(), new HashMap<>());

        String errors = ErrorRecorder.info();

        Module module = Generator.getModule();
        System.out.println(module);

        Translator.reset(module);
        Translator.translate();
        Asm asm = Translator.getAsm();

        writeToFile(asm.toString(), "mips.txt");

        //writeToFile(root.toString(), "output.txt");
        //writeToFile(errors, "error.txt");
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
