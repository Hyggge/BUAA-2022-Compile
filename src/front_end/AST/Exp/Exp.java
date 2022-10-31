package front_end.AST.Exp;

import front_end.AST.Node;
import llvm_ir.Value;
import utils.SyntaxVarType;

import java.util.ArrayList;

// Exp ==> AddExp
public class Exp extends Node {
    public Exp(int startLine, int endLine, SyntaxVarType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }

    @Override
    public Integer getDim() {
        for (Node child : children) {
            if (child.getDim() != null) return child.getDim();
        }
        return null;
    }

    @Override
    public int execute() {
        return children.get(0).execute();
    }

    @Override
    public Value genIR() {
        return children.get(0).genIR();
    }
}
