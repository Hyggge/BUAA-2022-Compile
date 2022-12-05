package utils;

import back_end.mips.AssemblyTable;
import front_end.lexer.Token;
import llvm_ir.Module;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class Printer {
    public static boolean OUT_PERM = false;
    public static boolean ERR_PERM = true;
    public static boolean LLVM_PERM = true;
    public static boolean MIPS_PERM = true;
    public static boolean onOff = true;

    private static FileOutputStream outFile = null;
    private static FileOutputStream errFile = null;
    private static FileOutputStream oriLLVMFile = null;
    private static FileOutputStream phiLLVMFile = null;
    private static FileOutputStream moveLLVMFile = null;
    private static FileOutputStream mipsFile = null;
    private static HashMap<Integer, ErrorType> errorMap;

    public static void init() throws Exception{
        Printer.outFile = new FileOutputStream("output.txt");
        Printer.errFile = new FileOutputStream("error.txt");
        Printer.oriLLVMFile = new FileOutputStream("llvm_ir.txt");
        Printer.phiLLVMFile = new FileOutputStream("llvm_ir_phi.txt");
        Printer.moveLLVMFile = new FileOutputStream("llvm_ir_move.txt");
        Printer.mipsFile = new FileOutputStream("mips.txt");
        Printer.errorMap = new HashMap<>();
    }

    public static void printToken(Token token){
        String content = token.toString();
        if (onOff & OUT_PERM) {
            try {outFile.write(content.getBytes());} catch (IOException e) {throw new RuntimeException(e);}
        }
    }

    public static void printSyntaxVarType(SyntaxVarType type) {
        String content = "<" + type.toString() + ">\n";
        if (onOff & OUT_PERM) {
            try {outFile.write((content).getBytes());} catch (IOException e) {throw new RuntimeException(e);}
        }
    }

    public static void addErrorMsg(Integer lineNumber, ErrorType errorType) {
        errorMap.put(lineNumber, errorType);
    }

    public static void printAllErrorMsg() {
        if (onOff & ERR_PERM) {
            Object[] lineNumbers = errorMap.keySet().toArray();
            Arrays.sort(lineNumbers);
            for (Object lineNumber : lineNumbers) {
                String content = lineNumber + " " + errorMap.get((Integer)lineNumber) + "\n";
                try {errFile.write((content).getBytes());} catch (IOException e) {throw new RuntimeException(e);}
            }
        }
    }

    public static void printOriLLVM(Module module) {
        if (onOff & LLVM_PERM) {
            try {oriLLVMFile.write(module.toString().getBytes());} catch (IOException e) {throw new RuntimeException(e);}
        }
    }

    public static void printPhiLLVM(Module module) {
        if (onOff & LLVM_PERM) {
            try {phiLLVMFile.write(module.toString().getBytes());} catch (IOException e) {throw new RuntimeException(e);}
        }
    }

    public static void printMoveLLVM(Module module) {
        if (onOff & LLVM_PERM) {
            try {moveLLVMFile.write(module.toString().getBytes());} catch (IOException e) {throw new RuntimeException(e);}
        }
    }

    public static void printMIPS(AssemblyTable assemblyTable) {
        if (onOff & MIPS_PERM) {
            try {mipsFile.write(assemblyTable.toString().getBytes());} catch (IOException e) {throw new RuntimeException(e);}
        }
    }

}
