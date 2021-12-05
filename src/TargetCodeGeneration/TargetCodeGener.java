package TargetCodeGeneration;

import MidCodeGeneration.MidCodeEntry;
import MidCodeGeneration.MidCodeGener;
import MidCodeGeneration.Str;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

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
    private String data = ".data 0x10000000\n";
    private String text = ".text\n";

    public TargetCodeGener(File targetcodeFile,boolean debug) {
        global = MidCodeGener.getGlobal();
        strList = MidCodeGener.getStrList();
        midCodeList = MidCodeGener.getMidCodeList();
        opMideCodeList = MidCodeGener.getOpMidCodeList();
        try {
            this.writer = new FileWriter(targetcodeFile);
            File file;
            if (debug) {
                file = new File("opmips.txt");
            } else {
                file = targetcodeFile;
            }
            this.opWriter = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveTargetCode() {
        try {
            genGlobal(writer);
            genStr(writer);
            genHead(writer);
            Iterator iterator = midCodeList.iterator();
            while (iterator.hasNext()) {
                MidCodeEntry midCodeEntry = (MidCodeEntry) iterator.next();
                writer.write(midCodeEntry.toTargetCode() + "\n");
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveOpTarCode() {
        try {
            genGlobal(opWriter);
            genStr(opWriter);
            genHead(opWriter);
            Iterator iterator = opMideCodeList.iterator();
            while (iterator.hasNext()) {
                MidCodeEntry midCodeEntry = (MidCodeEntry) iterator.next();
                opWriter.write(midCodeEntry.toTargetCode() + "\n");
            }
            opWriter.flush();
            opWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void genGlobal(FileWriter writer) {
        try {
            writer.write(data);
            writer.write(globalStart);
            Iterator iterator = global.iterator();
            while (iterator.hasNext()) {
                MidCodeEntry midCodeEntry = (MidCodeEntry) iterator.next();
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
