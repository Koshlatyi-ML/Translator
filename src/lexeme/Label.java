package lexeme;

/**
 * Created by Николай on 08.05.2016.
 */
public abstract class Label extends Lexeme {
    public Label(String alias, int id, int lineNumber) {
        super(alias, id, lineNumber);
    }

    /** Overrode for comparing in labels tables */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        if (!this.alias.equals(((Label)obj).getAlias()))
            return false;
        return true;
    }
}
