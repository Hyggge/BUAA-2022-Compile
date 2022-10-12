package front_end.AST.Stmt;

import front_end.AST.Exp.LValExp;
import front_end.AST.Node;
import utils.ErrorType;
import utils.Printer;
import utils.SyntaxVarType;

import java.util.ArrayList;

// AssignStmt ==> LVal '=' Exp ';'
public class AssignStmt extends Stmt {
    public AssignStmt(int startLine, int endLine, SyntaxVarType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }

    @Override
    public void checkError() {
        // check Error h
        LValExp lValExp = (LValExp) children.get(0);
        if (lValExp.isConst()) {
            Printer.printErrorMsg(lValExp.getEndLine(), ErrorType.h);
        }
        super.checkError();
    }
}
