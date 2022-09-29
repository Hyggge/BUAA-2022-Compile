package front_end.AST;

import front_end.parser.SyntaxVarType;

import java.util.ArrayList;

public class MainFuncDef extends Node {
    public MainFuncDef(int startLine, int endLine, SyntaxVarType type, ArrayList<Node> children) {
        super(startLine, endLine, type, children);
    }
}