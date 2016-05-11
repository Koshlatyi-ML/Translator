import lexeme.*;

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

    private List<String> delimiters;
    private List<Identifier> identifiersTable;
    private List<Constant> constantsTable;
    private List<Label> labelsTable;
    private Map<String, Integer> enterLexemeTable;
    private File delimitersFile;
    private File exisitingLexemesFile;

    public CodeScanner() {
        this.delimitersFile = new File("delimiters.txt");
        this.exisitingLexemesFile = new File("existing lexemes.txt");

        this.delimiters = getDelimiters(delimitersFile);
        this.enterLexemeTable =  getEnterLexemeTable(exisitingLexemesFile);
        List<Lexeme> identifiersTable  = new ArrayList<>();
        List<Lexeme> constantsTable  = new ArrayList<>();
        List<Lexeme> labelsTable  = new ArrayList<>();
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

    public ArrayList<Lexeme> getLexems(String sourceCodeFile) throws IllegalArgumentException {
        try(FileInputStream fIn = new FileInputStream(sourceCodeFile);
            FileChannel fChan = fIn.getChannel()) {

            List<Lexeme> lexemesTable = new ArrayList<>();

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
                    extractDelimiter(currentLexeme, lineNumber, lexemesTable);
                }
                else if (ch == ':') {
                    firstColonSignState(currentLexeme, lineNumber, chBuf, lexemesTable);
                }
                else if(ch == '<') {
                    firstLessThanSignState(currentLexeme, lineNumber, chBuf, lexemesTable);
                }
                else if(ch == '>') {
                    firstMoreThanSignState(currentLexeme, lineNumber, chBuf, lexemesTable);
                }
                else if (Character.isDigit(ch)) {
                    firstDigitState(currentLexeme, lineNumber, chBuf, lexemesTable);
                }
                else if(Character.isLetter(ch)) {
                    firstLetterState(currentLexeme, lineNumber, chBuf, lexemesTable);
                }
                else {
                    String errMessage = "At line: " + lineNumber + " Undefined chacter.";
                    throw new IllegalArgumentException(errMessage);
                }
//                class type;
//                lexemesTable.add(type.class.cast(obj));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void extractDelimiter(StringBuffer lex, int lineNumber, List<Lexeme> lexemesTable) {
        lexemesTable.add(new StraightLexeme(lex.toString(), enterLexemeTable.get(lex.toString()), lineNumber));
        lex.delete(0, lex.length());
    }

    private void firstColonSignState(StringBuffer lex, int lineNumber, CharBuffer chBuf, List<Lexeme> lexemesTable) {
        if (!chBuf.hasRemaining()) {
            lexemesTable.add(new StraightLexeme(lex.toString(), enterLexemeTable.get(lex.toString()), lineNumber));
            lex.delete(0, lex.length());
        } else {
            char ch = chBuf.get();
            if (ch == '=') {
                lex.append(ch);
                lexemesTable.add(new StraightLexeme(lex.toString(), enterLexemeTable.get(lex.toString()), lineNumber));
                lex.delete(0, lex.length());
            } else {
                lexemesTable.add(new StraightLexeme(lex.toString(), enterLexemeTable.get(lex.toString()), lineNumber));
                lex.delete(0, lex.length());
                lex.append(ch);
            }
        }
    }

    private void firstLessThanSignState(StringBuffer lex, int lineNumber, CharBuffer chBuf, List<Lexeme> lexemesTable) {
        if (!chBuf.hasRemaining()) {
            lexemesTable.add(new StraightLexeme(lex.toString(), enterLexemeTable.get(lex.toString()), lineNumber));
            lex.delete(0, lex.length());
        } else {
            char ch = chBuf.get();
            if (ch == '=') {
                lex.append(ch);
                lexemesTable.add(new StraightLexeme(lex.toString(), enterLexemeTable.get(lex.toString()), lineNumber));
                lex.delete(0, lex.length());
            } else {
                lexemesTable.add(new StraightLexeme(lex.toString(), enterLexemeTable.get(lex.toString()), lineNumber));\
                lex.delete(0, lex.length());
                lex.append(ch);
            }
        }
    }

    private void firstMoreThanSignState(StringBuffer lex, int lineNumber, CharBuffer chBuf, List<Lexeme> lexemesTable) {
        if (!chBuf.hasRemaining()) {
            lexemesTable.add(new StraightLexeme(lex.toString(), enterLexemeTable.get(lex.toString()), lineNumber));
            lex.delete(0, lex.length());
        } else {
            char ch = chBuf.get();
            if (ch == '=') {
                lex.append(ch);
                lexemesTable.add(new StraightLexeme(lex.toString(), enterLexemeTable.get(lex.toString()), lineNumber));
                lex.delete(0, lex.length());
            } else {
                lexemesTable.add(new StraightLexeme(lex.toString(), enterLexemeTable.get(lex.toString()), lineNumber));\
                lex.delete(0, lex.length());
                lex.append(ch);
            }
        }
    }

    private void firstDigitState(StringBuffer lex, int lineNumber, CharBuffer chBuf, List<Lexeme> lexemesTable) {
        Constant con;
        boolean isConstExisting = false;

        if (!chBuf.hasRemaining()) {
            con = retrieveConstant(lex, lineNumber);
            lexemesTable.add(con);
            constantsTable.add(con);
            lex.delete(0, lex.length());
        } else {
            char ch = chBuf.get();
            while (Character.isDigit(ch)) {
                lex.append(ch);
                ch = chBuf.get();
            }
            con = retrieveConstant(lex, lineNumber);
            lexemesTable.add(con);
            constantsTable.add(con);
            lex.delete(0, lex.length());
            lex.append(ch);
        }
    }

    private Constant retrieveConstant(StringBuffer lex, int lineNumber) {
        Constant con = new Constant(lex.toString(), enterLexemeTable.get("con"), lineNumber, constantsTable.size());;
        for (Constant detectedConst : constantsTable) {
            if (detectedConst.getAlias().equals(lex)) {
                con.setIndex(detectedConst.getIndex());
                break;
            }
        }
        return con;
    }


}
