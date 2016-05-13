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

    /** Overrode for comparing in identifiers tables */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        if (!alias.equals(((Identifier) obj).getAlias()))
            return false;
        return true;
    }
}
