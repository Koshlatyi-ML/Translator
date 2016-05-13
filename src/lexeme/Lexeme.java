package lexeme;

/**
 * Created by Николай on 08.05.2016.
 */
public abstract class Lexeme {
    protected String alias;
    protected int id;
    protected int lineNumber;

    public Lexeme(String alias, int id, int lineNumber) {
        this.alias = alias;
        this.id = id;
        this.lineNumber = lineNumber;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
