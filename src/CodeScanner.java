import lexeme.Lexeme;

import java.io.File;
import java.io.IOException;
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
        try(Scanner scanner = new Scanner(new File("lexemes table"))) {
            while(scanner.hasNext()) {
                enterLexemeTable.put(scanner.nextInt(), scanner.next());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


