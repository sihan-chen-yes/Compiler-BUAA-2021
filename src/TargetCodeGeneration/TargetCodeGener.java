package TargetCodeGeneration;

import Enum.OpType;
import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;
import MidCodeGeneration.Str;
import Optimizer.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TargetCodeGener {
    private FileWriter writer;
    private FileWriter opWriter;
    private ArrayList<MidCodeEntry> global;
    private ArrayList<Str> strList;
    private ArrayList<MidCodeEntry> midCodeList;
    private ArrayList<MidCodeEntry> opMideCodeList;

    private String globalStart = "########################################GLOBAL START########################################\n";
    private String globalEnd = "########################################GLOBAL END##########################################\n";
    private String asciizStart = "########################################ASCIIZ START########################################\n";
    private String asciizEnd = "########################################ASCIIZ END##########################################\n";
    private String funcStart = "########################################FUNC START##########################################\n";
    private String funcEnd = "########################################FUNC END############################################\n";
    private String data = ".data 0x10000000\n";
    private String text = ".text\n";

    public TargetCodeGener(File targetcodeFile,File opTargetCodeFile) {
        global = MidCodeGener.getGlobal();
        strList = MidCodeGener.getStrList();
        midCodeList = MidCodeGener.getMidCodeList();
        opMideCodeList = MidCodeGener.getOpMidCodeList();
        try {
            this.writer = new FileWriter(targetcodeFile);
            if (Optimizer.isDebug()) {
                this.opWriter = new FileWriter(opTargetCodeFile);
            } else {
                this.opWriter = new FileWriter(targetcodeFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveTargetCode() {
        Optimizer.setOp(false);
        save(writer,midCodeList);
    }

    public void saveOpTarCode() {
        Optimizer.setOp(true);
        save(opWriter,opMideCodeList);
    }

    public void save(FileWriter writer,ArrayList<MidCodeEntry> midCodeList) {
        try {
            genGlobal(writer);
            genStr(writer);
            genHead(writer);
            Iterator iterator = midCodeList.iterator();
            while (iterator.hasNext()) {
                MidCodeEntry midCodeEntry = (MidCodeEntry) iterator.next();
                if (midCodeEntry.getOpType() == OpType.FUNC_DECLARE)  {
                    writer.write(funcStart);
                    if (Optimizer.isOp()) {
                        HashMap<String, String> varToReg = midCodeEntry.getVarToReg();
                        Iterator<Map.Entry<String, String>> iterator1 = varToReg.entrySet().iterator();
                        while (iterator1.hasNext()) {
                            Map.Entry<String,String> entry = iterator1.next();
                            String var = entry.getKey();
                            String reg = entry.getValue();
                            writer.write(String.format("####var:%s reg:%s\n",var,reg));
                        }
                    }
                }
                writer.write(midCodeEntry.toTargetCode() + "\n");
                if (midCodeEntry.getOpType() == OpType.EXIT
                || midCodeEntry.getOpType() == OpType.RET_VALUE
                || midCodeEntry.getOpType() == OpType.RET_VOID) {
                    writer.write(funcEnd);
                }
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void genGlobal(FileWriter writer) {
        try {
            writer.write(data);
            writer.write(globalStart);
            for (MidCodeEntry midCodeEntry:global) {
                writer.write(midCodeEntry.toTargetCode() + "\n");
            }
            writer.write(globalEnd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void genStr(FileWriter writer) {
        try {
            writer.write(asciizStart);
            Iterator iterator = strList.iterator();
            while (iterator.hasNext()) {
                Str asciiz = (Str) iterator.next();
                writer.write(asciiz.toTargetCode() + "\n");
            }
            writer.write(asciizEnd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void genHead(FileWriter writer) {
        try {
            writer.write(text);
            writer.write(String.format("addiu $sp,$sp,-%d",MidCodeGener.getSymbolTable().getLocalSize("main") - 4));
            writer.write("\n");
            writer.write("j main");
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
