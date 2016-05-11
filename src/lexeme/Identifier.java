package lexeme;

/**
 * Created by Николай on 08.05.2016.
 */
public class Identifier extends Lexeme {
    private int index;

    public Identifier(String alias, int id, int lineNumber, int index) {
        super(alias, id, lineNumber);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
