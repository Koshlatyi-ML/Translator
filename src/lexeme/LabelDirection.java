package lexeme;

/**
 * Created by Николай on 12.05.2016.
 */
public class LabelDirection extends Label {
    private LabelDeclaration declaration;

    public LabelDirection(String alias, int id, int lineNumber, int index, LabelDeclaration declaration) {
        super(alias, id, lineNumber, index);
        this.declaration = declaration;
    }

    public LabelDeclaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(LabelDeclaration declaration) {
        this.declaration = declaration;
    }
}
