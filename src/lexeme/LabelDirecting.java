package lexeme;

/**
 * Created by Николай on 12.05.2016.
 */
public class LabelDirecting extends Label {
    private LabelDeclaration declaration;

    public LabelDirecting(String alias, int id, int lineNumber, LabelDeclaration declaration) {
        super(alias, id, lineNumber);
        this.declaration = declaration;
    }

    public LabelDeclaration getDeclaration() {
        return declaration;
    }

    public void setDeclaration(LabelDeclaration declaration) {
        this.declaration = declaration;
    }
}
