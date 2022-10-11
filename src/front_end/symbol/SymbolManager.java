package front_end.symbol;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

public class SymbolManager {
    private static final SymbolManager MANAGER = new SymbolManager();
    private Stack<SymbolTable> symbolTables;
    private HashMap<String, Stack<SymbolTable>> symbolNameMap;
    private Symbol latestFunc; // for check return sentence
    private int loopDepth; // for check continue and break

    private SymbolManager() {
        this.symbolTables = new Stack<>();
        this.symbolNameMap = new HashMap<>();
        this.latestFunc = null;
        this.loopDepth = 0;
    }

    public static SymbolManager getInstance() {
        return MANAGER;
    }

    public boolean addSymbol(Symbol symbol) {
        SymbolTable topTable = this.symbolTables.peek();
        // insert failed
        if (topTable.has(symbol.getSymbolName())) return false;
        // insert success
        topTable.addSymbol(symbol);
        // maintain symbolNameMap
        symbolNameMap.compute(symbol.getSymbolName(), (k, v) -> {
            if (v == null) v = new Stack<>();
            v.add(topTable);
            return v;
        });
        return true;
    }

    public boolean checkDef (String name) {
        if (symbolNameMap.get(name) == null) return false;
        return !symbolNameMap.get(name).isEmpty();
    }


    public void enterBlock() {
        SymbolTable symbolTable = new SymbolTable();
        this.symbolTables.push(symbolTable);
    }

    public void leaveBlock() {
        SymbolTable topTable = this.symbolTables.pop();
        Iterator<String> iterator = topTable.getNames().iterator();
        while (iterator.hasNext()) {
            String name = iterator.next();
            symbolNameMap.get(name).pop();
        }
    }

    public void enterFunc(Symbol symbol) {
        this.latestFunc = symbol;
    }

    public void leaveFunc() {
        this.latestFunc = null;
    }

    public void enterLoop() {
        this.loopDepth++;
    }

    public void leaveLoop() {
        this.loopDepth--;
    }

    public int getLoopDepth() {
        return loopDepth;
    }

}
