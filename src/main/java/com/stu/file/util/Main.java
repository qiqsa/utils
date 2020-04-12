package com.stu.file.util;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Qi.qingshan
 * @date 2020/4/12
 */
public class Main {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        BigFileReader bigFileReader= new BigFileReader.Builder("e://11.txt", new IFileHandle() {
            public void handle(String line) {
                System.out.println("line : " + line);
            }
        }).withBufferSize(1024)
                .withCharset("utf-8")
                .withThreadSize(2)
                .withExecutor(executorService)
                .build();
        bigFileReader.start();


    }
}
