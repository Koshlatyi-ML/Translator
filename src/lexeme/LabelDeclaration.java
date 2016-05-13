package lexeme;

/**
 * Created by Николай on 12.05.2016.
 */
public class LabelDeclaration extends Label {
    private int index;

    public LabelDeclaration(String alias, int id, int lineNumber, int index) {
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
