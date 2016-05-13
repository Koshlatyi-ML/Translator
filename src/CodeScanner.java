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
        this.identifiersTable  = new ArrayList<>();
        this.constantsTable  = new ArrayList<>();
        this.labelsTable  = new ArrayList<>();
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

    public List<Lexeme> getLexems(String sourceCodeFile) throws IllegalArgumentException {
        List<Lexeme> lexemesTable = new ArrayList<>();

        try(FileInputStream fIn = new FileInputStream(sourceCodeFile);
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
                    currentLexeme.delete(0, currentLexeme.length());
                }
                else if(delimiters.contains(Character.toString(ch))) {
                    extractStraightLexeme(currentLexeme, lineNumber, lexemesTable);
                }
                else if((ch == '<') || (ch == '>') || (ch == ':')) {
                    firstAngleBracketOrColon(currentLexeme, lineNumber, chBuf, lexemesTable);
                }
                else if (Character.isDigit(ch)) {
                    firstDigitState(currentLexeme, lineNumber, chBuf, lexemesTable);
                }
                else if(Character.isLetter(ch) || ch == '_') {
                    firstLetterState(currentLexeme, lineNumber, chBuf, lexemesTable);
                }
                else {
                    String errMessage = "At line: " + lineNumber + ". Undefined chacter.";
                    throw new IllegalArgumentException(errMessage);
                }
//                class type;
//                lexemesTable.add(type.class.cast(obj));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(Lexeme l: lexemesTable)
            System.out.println(l.getAlias() + "\t" + l.getClass());
        return lexemesTable;
    }

    private void firstAngleBracketOrColon(StringBuffer lex, int lineNumber, CharBuffer chBuf, List<Lexeme> lexemesTable) {
        if (!chBuf.hasRemaining()) {
            extractStraightLexeme(lex, lineNumber, lexemesTable);
        } else {
            char ch = chBuf.get();
            if (ch == '=') {
                lex.append(ch);
                extractStraightLexeme(lex, lineNumber, lexemesTable);
            } else {
                extractStraightLexeme(lex, lineNumber, lexemesTable);
                lex.append(ch);
            }
        }
    }

    private void firstDigitState(StringBuffer lex, int lineNumber, CharBuffer chBuf, List<Lexeme> lexemesTable) {
        Constant con;

        if (!chBuf.hasRemaining()) {
            con = parseConstant(lex, lineNumber);
            lexemesTable.add(con);
            if(!constantsTable.contains(con))
                constantsTable.add(con);
            lex.delete(0, lex.length());
        } else {
            char ch = chBuf.get();
            while (Character.isDigit(ch) && chBuf.hasRemaining()) {
                lex.append(ch);
                ch = chBuf.get();
            }

            con = parseConstant(lex, lineNumber);
            lexemesTable.add(con);
            if(!constantsTable.contains(con))
                constantsTable.add(con);
            lex.delete(0, lex.length());
            lex.append(ch);
        }
    }

    private void firstLetterState(StringBuffer lex, int lineNumber, CharBuffer chBuf, List<Lexeme> lexemesTable) {
        Lexeme lexeme; //???

        if(!chBuf.hasRemaining()) {
            if(lexemesTable.get(lexemesTable.size() - 1).getAlias().equals("goto")) {
                lexeme = parseLabelDirecting(lex, lineNumber);
                lexemesTable.add(lexeme);
                lex.delete(0, lex.length());
            } else {
                lexeme = parseIdentifier(lex, lineNumber);
                lexemesTable.add(lexeme);
                if(!identifiersTable.contains(lexeme))
                    identifiersTable.add((Identifier) lexeme);
                lex.delete(0, lex.length());
            }
        } else {
            char ch = chBuf.get();
            while (Character.isLetter(ch) || Character.isLetter(ch) || ch == '-') {
                lex.append(ch);
                ch = chBuf.get();
            }

            if(enterLexemeTable.containsKey(lex.toString())) {
                lexeme = parseStraightLexeme(lex, lineNumber);
                lexemesTable.add(lexeme);
                lex.delete(0, lex.length());
                lex.append(ch);
            } else if(lexemesTable.get(lexemesTable.size() - 1).getAlias().equals("goto")) {
                lexeme = parseLabelDirecting(lex, lineNumber);
                lexemesTable.add(lexeme);
                lex.delete(0, lex.length());
                lex.append(ch);
            } else {
                chBuf.mark();
                char nextAfterLast = chBuf.get();
                if(ch == ':' && nextAfterLast != '=') {
                    lex.append(ch);
                    lexeme = parseLabelDeclaration(lex, lineNumber);
                    lexemesTable.add(lexeme);
                    if (!lexemesTable.contains(lexeme))
                        labelsTable.add((Label) lexeme);
                    lex.delete(0, lex.length());
                } else {
                    lexeme = parseIdentifier(lex, lineNumber);
                    lexemesTable.add(lexeme);
                    if (!identifiersTable.contains(lexeme))
                        identifiersTable.add((Identifier) lexeme);
                    lex.delete(0, lex.length());
                    lex.append(ch);
                }
                chBuf.reset();
            }
        }
    }

    private void extractStraightLexeme(StringBuffer lex, int lineNumber, List<Lexeme> lexemesTable) {
        lexemesTable.add(new StraightLexeme(lex.toString(), enterLexemeTable.get(lex.toString()), lineNumber));
        lex.delete(0, lex.length());
    }

    private StraightLexeme parseStraightLexeme(StringBuffer lex, int lineNumber) {
        StraightLexeme lexeme = new StraightLexeme(lex.toString(), enterLexemeTable.get(lex.toString()), lineNumber);
        return lexeme;
    }

    private Constant parseConstant(StringBuffer lex, int lineNumber) {
        Constant con = new Constant(lex.toString(), enterLexemeTable.get("con"), lineNumber, constantsTable.size());
        for (Constant detectedConst : constantsTable) {
            if (detectedConst.getAlias().equals(lex.toString())) {
                con.setIndex(detectedConst.getIndex());
                break;
            }
        }
        return con;
    }

    private Identifier parseIdentifier(StringBuffer lex, int lineNumber) {
        Identifier identifier = new Identifier(lex.toString(), enterLexemeTable.get("idn"), lineNumber, identifiersTable.size());
        for(Identifier idn : identifiersTable) {
            if(idn.getAlias().equals(lex.toString())) {
                identifier.setIndex(idn.getIndex());
                break;
            }
        }
        return identifier;
    }

    private LabelDirecting parseLabelDirecting(StringBuffer lex, int lineNumber) {
        LabelDirecting labelDirecting = new LabelDirecting(lex.toString(), enterLexemeTable.get("label"), lineNumber, null);
        for(Label label : labelsTable) {
            if(label.getAlias().equals(lex.append(':').toString())) {
                labelDirecting.setDeclaration((LabelDeclaration) label);
                break;
            }
        }
        return labelDirecting;
    }

    private LabelDeclaration parseLabelDeclaration(StringBuffer lex, int lineNumber) throws IllegalArgumentException {
        for(Label label : labelsTable) {
            if(label.getAlias().equals(lex.toString())) {
                String errMessage = "At line: " + lineNumber + ". More than one " + lex.toString() + " declaration.";
                throw new IllegalArgumentException(errMessage);
            }
        }
        return new LabelDeclaration(lex.toString(), enterLexemeTable.get("label"), lineNumber, labelsTable.size());
    }


}
