import lexeme.Lexeme;
import lexeme.StraightLexeme;

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

    private Map<String, Integer> getEnterLexemeTable(File exisitingLexemesFile) {
        Map<String, Integer> enterLexemes = new HashMap<>();
        try (Scanner scanner = new Scanner(exisitingLexemesFile)) {
            while (scanner.hasNext()) {
                Integer id = scanner.nextInt();
                String alias = scanner.next();
                enterLexemes.put(alias, id);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return enterLexemes;
    }

    public void getLexems() throws IllegalArgumentException {
        try(FileInputStream fIn = new FileInputStream("code.mylang");
            FileChannel fChan = fIn.getChannel()) {

            MappedByteBuffer mBuf = fChan.map(FileChannel.MapMode.READ_ONLY, 0, fChan.size());
            Charset cs = Charset.forName("UTF-8");
            CharBuffer chBuf = cs.decode(mBuf);
            int lineNumber = 0;
            StringBuffer currentLexeme = new StringBuffer();
            while (chBuf.hasRemaining()) {
                if(currentLexeme.length() == 0)
                    currentLexeme.append(chBuf.get());

                char ch = currentLexeme.charAt(0);

                if(Character.isWhitespace(ch)) {
                    if (ch == '\n')
                        lineNumber++;
                }
                else if(delimiters.contains(Character.toString(ch))) {
                    extractDelimiter(currentLexeme, lineNumber);
                }
                else if (ch == ':') {
                    firstColonSignState(currentLexeme, lineNumber, chBuf);
                }
                else if(ch == '<') {
                    firstLessThanSignState(currentLexeme, lineNumber, chBuf);
                }
                else if(ch == '>') {
                    firstMoreThanSignState(currentLexeme, lineNumber, chBuf);
                }
                else if (Character.isDigit(ch)) {
                    firstDigitState(currentLexeme, lineNumber, chBuf);
                }
                else if(Character.isLetter(ch)) {
                    firstDelimiterState(currentLexeme, lineNumber, chBuf);
                }
                else throw new IllegalArgumentException("Undefined chacter.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void extractDelimiter(StringBuffer lex, int lineNumber) {
        lexemeTable.add(new StraightLexeme(lex.toString(), enterLexemeTable.get(lex.toString()), lineNumber));
        lex.delete(0, lex.length());
    }

    private void firstColonSignState(StringBuffer lex, int lineNumber, CharBuffer chBuf) {
        if (!chBuf.hasRemaining())
            throw new IllegalArgumentException("Unexpceted token \":\"");

        char ch = chBuf.get();
        if (ch == '=') {
            lex.append(ch);
            lexemeTable.add(new StraightLexeme(lex.toString(), enterLexemeTable.get(lex.toString()), lineNumber));
            lex.delete(0, lex.length());
        } else {
            lexemeTable.add(new StraightLexeme(lex.toString(), enterLexemeTable.get(lex.toString()), lineNumber));
            lex.delete(0, lex.length());
            lex.append(ch);
        }
    }
}
