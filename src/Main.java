import lexeme.*;

/**
 * Created by Николай on 08.05.2016.
 */
public class Main {
    public static void main(String[] args) {
        CodeScanner codeScanner = new CodeScanner();
        codeScanner.getLexems("code.mylang");

        Identifier id = new Identifier("idn", 1, 1, 1);
        System.out.println(id.getClass());
        System.out.println(id.getClass() == (Identifier.class));

    }
}
