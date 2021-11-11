package TargetCodeGeneration;

import MidCodeGeneration.MidCodeEntry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class TargetCodeGener {
    private FileWriter writer;
    private ArrayList<MidCodeEntry> midCodeList;

    public TargetCodeGener(ArrayList<MidCodeEntry> midCodeList, File targetcodeFile) {
        this.midCodeList = midCodeList;
        try {
            this.writer = new FileWriter(targetcodeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveTargetCode() {
        try {
            Iterator iterator = midCodeList.iterator();
            while (iterator.hasNext()) {
                MidCodeEntry midCodeEntry = (MidCodeEntry) iterator.next();
                writer.write(midCodeEntry.toTargetCode());
            }
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
