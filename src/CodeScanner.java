import lexeme.Lexeme;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    private List<String> delimiters;
    private Map<String, Integer> enterLexemeTable;
    private File delimitersFile;
    private File exisitingLexemesFile;

    public CodeScanner() {
        this.delimitersFile = new File("delimiters.txt");
        this.exisitingLexemesFile = new File("existing lexemes.txt");

        this.lexemeTable = new ArrayList<>();
        this.delimiters = getDelimiters(delimitersFile);
        this.enterLexemeTable = getEnterLexemeTable(exisitingLexemesFile);
    }

    private List<String> getDelimiters(File delimitersFile) {
        List<String> delimiters = new ArrayList<>();
        try (Scanner scanner = new Scanner(delimitersFile)) {
            while (scanner.hasNext()) {
                delimiters.add(scanner.next());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return delimiters;
    }

    private Map<String, Integer> getEnterLexemeTable(File exisitingLexemesFile) {
        Map<String, Integer> enterLexemes = new HashMap<>();
        try (Scanner scanner = new Scanner(exisitingLexemesFile)) {
            Integer id = scanner.nextInt();
            String alias = scanner.next();
            while (scanner.hasNext()) {
                enterLexemes.put(alias, id);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return enterLexemes;
    }

    public void getLexems() {
        try (FileInputStream fIn = new FileInputStream("code.mylang");
             FileChannel fChan = fIn.getChannel()) {

            MappedByteBuffer mBuf = fChan.map(FileChannel.MapMode.READ_ONLY, 0, fChan.size());
            Charset cs = Charset.forName("UTF-8");
            CharBuffer cBuf = cs.decode(mBuf);
            int count = cBuf.length();
            for (int i = 0; i < count; i++) {
                System.out.print(cBuf.get());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


