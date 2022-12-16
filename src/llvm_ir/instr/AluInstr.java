package llvm_ir.instr;

import back_end.mips.MipsBuilder;
import back_end.mips.Register;
import back_end.mips.assembly.AluAsm;
import back_end.mips.assembly.HiLoAsm;
import back_end.mips.assembly.LiAsm;
import back_end.mips.assembly.MDAsm;
import back_end.mips.assembly.MemAsm;
import llvm_ir.Constant;
import llvm_ir.Instr;
import llvm_ir.UndefinedValue;
import llvm_ir.Value;
import llvm_ir.type.BaseType;

public class AluInstr extends Instr {
    public enum Op {
        ADD,
        SUB,
        SREM,
        MUL,
        SDIV,
        AND,
        OR
    }

    private Op op;
    // private Value operand1;
    // private Value operand2;

    public AluInstr(String name, Op op, Value operand1, Value operand2) {
        super(BaseType.INT32, name, InstrType.ALU);
        this.op = op;
        addOperands(operand1);
        addOperands(operand2);
    }

    public Op getOp() {
        return op;
    }

    public Value getOperand1() {
        return operands.get(0);
    }

    public Value getOperand2() {
        return operands.get(1);
    }

    public String getGVNHash() {
        String operand1 = getOperand1().getName();
        String operand2 = getOperand2().getName();
        if (op == Op.ADD || op == Op.MUL) {
            if (operand1.compareTo(operand2) < 0) {
                return operand1 + " " + op + " " + operand2;
            } else {
                return operand2 + " " + op+ " " + operand1;
            }
        } else {
            return operand1 + " " + op + " " + operand2;
        }
    }

    @Override
    public boolean canBeUsed() {
        return true;
    }

    @Override
    public String toString() {
        Value operand1 = getOperand1();
        Value operand2 = getOperand2();
        return name + " = " + op.toString().toLowerCase() + " i32 " + operand1.getName() + ", " + operand2.getName();
    }

    @Override
    public void toAssembly() {
        super.toAssembly();
        Value operand1 = getOperand1();
        Value operand2 = getOperand2();
        // TODO: 优化思路——可以利用addi等进行优化
        // 将第一个操作数的值保存到t0
        if (operand1 instanceof Constant || operand1 instanceof UndefinedValue) {
            new LiAsm(Register.T0, Integer.parseInt(operand1.getName()));
        } else {
            Integer operand1Offset = MipsBuilder.getInstance().getOffsetOf(operand1);
            if (operand1Offset == null) {
                MipsBuilder.getInstance().subCurOffset(4);
                operand1Offset = MipsBuilder.getInstance().getCurOffset();
                MipsBuilder.getInstance().addValueOffsetMap(operand1, operand1Offset);
            }
            new MemAsm(MemAsm.Op.LW, Register.T0, Register.SP, operand1Offset);
        }
        // 将第二个操作数的值保存到t0
        if (operand2 instanceof Constant || operand2 instanceof UndefinedValue) {
            new LiAsm(Register.T1, Integer.parseInt(operand2.getName()));
        } else {
            Integer operand2Offset = MipsBuilder.getInstance().getOffsetOf(operand2);
            if (operand2Offset == null) {
                MipsBuilder.getInstance().subCurOffset(4);
                operand2Offset = MipsBuilder.getInstance().getCurOffset();
                MipsBuilder.getInstance().addValueOffsetMap(operand2, operand2Offset);
            }
            new MemAsm(MemAsm.Op.LW, Register.T1, Register.SP, operand2Offset);
        }
        // 计算，将结果保存到t2寄存器中
        switch (op) {
            case ADD:
                new AluAsm(AluAsm.Op.ADDU, Register.T2, Register.T0, Register.T1);
                break;
            case SUB:
                new AluAsm(AluAsm.Op.SUBU, Register.T2, Register.T0, Register.T1);
                break;
            case AND:
                new AluAsm(AluAsm.Op.AND, Register.T2, Register.T0, Register.T1);
                break;
            case OR:
                new AluAsm(AluAsm.Op.OR, Register.T2, Register.T0, Register.T1);
                break;
            case MUL:
                new MDAsm(MDAsm.Op.MULT, Register.T0, Register.T1);
                new HiLoAsm(HiLoAsm.Op.MFLO, Register.T2);
                break;
            case SDIV:
                new MDAsm(MDAsm.Op.DIV, Register.T0, Register.T1);
                new HiLoAsm(HiLoAsm.Op.MFLO, Register.T2);
                break;
            case SREM:
                new MDAsm(MDAsm.Op.DIV, Register.T0, Register.T1);
                new HiLoAsm(HiLoAsm.Op.MFHI, Register.T2);
                break;
        }
        // 为Value开一个栈空间，将t2的值store到堆栈上
        MipsBuilder.getInstance().subCurOffset(4);
        int curOffset = MipsBuilder.getInstance().getCurOffset();
        MipsBuilder.getInstance().addValueOffsetMap(this, curOffset);
        new MemAsm(MemAsm.Op.SW, Register.T2, Register.SP, curOffset);
    }
}
