package com.anton.uzhva.megamazz_bot.util;

import org.springframework.stereotype.Component;

@Component
public class FileRemover implements Runnable {
    FileToDeleteContainer fileToDeleteContainer;

    public FileRemover(FileToDeleteContainer fileToDeleteContainer) {
        this.fileToDeleteContainer = fileToDeleteContainer;
        new Thread(this).start();
    }

    @Override
    public void run() {
        fileToDeleteContainer.deleteFile();
    }
}
