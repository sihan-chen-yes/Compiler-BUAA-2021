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
    private ArrayList<MidCodeEntry> global;
    private ArrayList<Str> strList;
    private ArrayList<MidCodeEntry> midCodeList;

    private String globalStart = "########################################GLOBAL START########################################\n";
    private String globalEnd = "########################################GLOBAL END##########################################\n";
    private String asciizStart = "########################################ASCIIZ START########################################\n";
    private String asciizEnd = "########################################ASCIIZ END##########################################\n";
    private String data = ".data 0x10000000\n";
    private String text = ".text\n";

    public TargetCodeGener(File targetcodeFile) {
        global = MidCodeGener.getGlobal();
        strList = MidCodeGener.getStrList();
        midCodeList = MidCodeGener.getMidCodeList();
        try {
            this.writer = new FileWriter(targetcodeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveTargetCode() {
        try {
            genGlobal();
            genStr();
            writer.write(text);
            writer.write(String.format("subiu $sp,$sp,%d",MidCodeGener.getSymbolTable().getLocalSize("main") - 4));
            writer.write("\n");
            writer.write("j main");
            writer.write("\n");
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

    public void genGlobal() {
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

    public void genStr() {
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
}
