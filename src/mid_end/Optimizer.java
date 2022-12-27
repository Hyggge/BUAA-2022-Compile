package mid_end;

import llvm_ir.Module;
import utils.Printer;

public class Optimizer {
    private static final Optimizer optimizer = new Optimizer();

    public static Optimizer getInstance() {
        return optimizer;
    }

    public void run(Module module) {
        new SimplifyBB(module).run();
        Printer.printOriLLVM(module);

        new CFGBuilder(module).run();
        new Mem2Reg(module).run();
        new GVN(module).run();
        new ActivenessAnalysis(module).run();
        new RegAllocator(module).run();
        new DeadCodeRemove(module).run();


        Printer.printPhiLLVM(module);
        new RemovePhi(module).run();
        Printer.printMoveLLVM(module);

    }



}