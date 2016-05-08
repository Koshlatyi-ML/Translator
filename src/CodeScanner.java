import lexeme.Lexeme;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by Николай on 08.05.2016.
 */
public class CodeScanner {

    private List<Lexeme> lexemeTable;
    private List<String> delimiters;
    private Map<Integer, String> enterLexemeTable;
    private File delimitersFile;
    private File exisitingLexemesFile;

    public CodeScanner() {
        this.delimitersFile = new File("delimiters.txt");
        this.exisitingLexemesFile = new File("existing lexemes.txt");

        this.lexemeTable = new ArrayList<>();
        this.delimiters = getDelimiters(delimitersFile);
        this.enterLexemeTable =  getEnterLexemeTable(exisitingLexemesFile);
    }

    private List<String> getDelimiters(File delimitersFile) {
        List<String> delimiters = new ArrayList<>();
        try(Scanner scanner = new Scanner(delimitersFile)) {
            while (scanner.hasNext()) {
                delimiters.add(scanner.next());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return delimiters;
    }

    private Map<Integer, String> getEnterLexemeTable(File exisitingLexemesFile) {
        Map<Integer, String> enterLexemes = new HashMap<>();
        try(Scanner scanner = new Scanner(exisitingLexemesFile)) {
            while (scanner.hasNext()) {
                enterLexemes.put(scanner.nextInt(), scanner.next());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return enterLexemes;
    }

    public void getLexems() {
        try(FileInputStream fIn = new FileInputStream("code.mylang");
            FileChannel fChan = fIn.getChannel()) {

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


