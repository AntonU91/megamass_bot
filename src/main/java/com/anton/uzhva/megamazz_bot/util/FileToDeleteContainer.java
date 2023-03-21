package com.anton.uzhva.megamazz_bot.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
@AllArgsConstructor
public class FileToDeleteContainer {
    List<File> filesToDelete;

    public void  addFileToContainer(File file) {
        synchronized (this) {
            filesToDelete.add(file);
            notify();
        }
    }

    public void deleteFile()  {
        synchronized (this) {
            while (filesToDelete.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (File file : filesToDelete) {
                filesToDelete.remove(file);
                if (file.exists() && file.isFile()) {
                    file.delete();
                }
            }
        }
    }
}
