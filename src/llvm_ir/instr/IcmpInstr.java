package llvm_ir.instr;

import back_end.mips.MipsBuilder;
import back_end.mips.Register;
import back_end.mips.assembly.CmpAsm;
import back_end.mips.assembly.LiAsm;
import back_end.mips.assembly.MemAsm;
import llvm_ir.Constant;
import llvm_ir.Instr;
import llvm_ir.UndefinedValue;
import llvm_ir.Value;
import llvm_ir.type.BaseType;

public class IcmpInstr extends Instr {
    public enum Op {
        EQ,
        NE,
        SGT,
        SGE,
        SLT,
        SLE
    }
    private Op op;
    // private Value operand1;
    // private Value operand2;


    public IcmpInstr(String name, Op op, Value operand1, Value operand2) {
        super(BaseType.INT1, name, InstrType.ICMP);
        this.op = op;
        addOperands(operand1);
        addOperands(operand2);
    }

    public Value getOperand1() {
        return operands.get(0);
    }

    public Value getOperand2() {
        return operands.get(1);
    }

    @Override
    public boolean canBeUsed() {
        return true;
    }

    @Override
    public String toString() {
        Value operand1 = getOperand1();
        Value operand2 = getOperand2();
        return name + " = icmp " + op.toString().toLowerCase() + " i32 " + operand1.getName() + ", " + operand2.getName();
    }

    @Override
    public void toAssembly() {
        super.toAssembly();
        Value operand1 = getOperand1();
        Value operand2 = getOperand2();
        // 对于>和<，尽量使用slt和sgt，因为这两个是基础指令
        // 对于>=和<=, 暂时采用sle和sge, 这两个指令都会翻译成三个基础指令
        // 对于==和!=，暂时采用seq和sne，seq会翻译成三条基础指令，slt会翻译成两条基础指令
        // 即使是和数字比较，我们也将数字存到寄存器中再比较，类似于ALUAsm，TODO：后期可以考虑常数优化

        // 获得operand1的值，存到t0中
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
        // 获得operand2的值，存到t1中
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
        // 根据op进行比较，结果存储在t2中
        switch (op) {
            case EQ: new CmpAsm(CmpAsm.Op.SEQ, Register.T2, Register.T0, Register.T1); break;
            case NE: new CmpAsm(CmpAsm.Op.SNE, Register.T2, Register.T0, Register.T1); break;
            case SGT: new CmpAsm(CmpAsm.Op.SGT, Register.T2, Register.T0, Register.T1); break;
            case SGE: new CmpAsm(CmpAsm.Op.SGE, Register.T2, Register.T0, Register.T1); break;
            case SLT: new CmpAsm(CmpAsm.Op.SLT, Register.T2, Register.T0, Register.T1); break;
            case SLE: new CmpAsm(CmpAsm.Op.SLE, Register.T2, Register.T0, Register.T1); break;
        }
        // 为Value开一个栈空间，将t2的值store到堆栈上
        MipsBuilder.getInstance().subCurOffset(4);
        int curOffset = MipsBuilder.getInstance().getCurOffset();
        MipsBuilder.getInstance().addValueOffsetMap(this, curOffset);
        new MemAsm(MemAsm.Op.SW, Register.T2, Register.SP, curOffset);
    }
}
