import lexeme.Lexeme;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by Николай on 08.05.2016.
 */
public class CodeScanner {

    private List<Lexeme> lexemeTable;
    private Map<Integer, String> enterLexemeTable;

    public CodeScanner() {
        this.lexemeTable = new ArrayList<>();
        this.enterLexemeTable = new HashMap<>();
    }

    public void getLexems() {
        try(Scanner scanner = new Scanner(new File("lexemes table"));
            FileInputStream fIn = new FileInputStream("code");
            FileChannel fChan = fIn.getChannel()) {

            while(scanner.hasNext()) {
                enterLexemeTable.put(scanner.nextInt(), scanner.next());
            }

            ByteBuffer buf = ByteBuffer.allocate(10);
            Charset cs = Charset.forName("UTF-8");
            int count;

            while(fChan.read(buf) != -1) {
                buf.rewind();
                CharBuffer chbuff = cs.decode(buf);
                count = chbuff.length();
                for (int i = 0; i < count; i++) {
                    System.out.print(chbuff.get());
                }
                buf.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


