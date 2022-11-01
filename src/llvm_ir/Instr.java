package llvm_ir;

import llvm_ir.type.Type;

public class Instr extends User{
    public static enum InstrType {
        ALU,
        ALLOCA,
        BRANCH,
        CALL,
        GEP,
        ICMP,
        JUMP,
        LOAD,
        PHI,
        RETURN,
        STORE,
        ZEXT,
        IO
    }

    private InstrType instrType;
    private BasicBlock parentBB;

    public Instr(Type type, String name, InstrType instrType) {
        super(type, name);
        this.instrType = instrType;
        this.parentBB = null;
        IRBuilder.getInstance().addInstr(this);
    }

    public void setParentBB(BasicBlock parentBB) {
        this.parentBB = parentBB;
    }
}