package com.stu.file.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Qi.qingshan
 * @date 2020/4/12
 */
public class BigFileReader {

    private int threadSize;

    private String charset;

    private ExecutorService executor;

    private int bufferSize;

    private long fileLength;

    private IFileHandle handle;

    private CyclicBarrier barrier;

    private RandomAccessFile accessFile;

    private Set<StartEndPair> startEndPair;

    private AtomicLong counter = new AtomicLong(0);

    public BigFileReader(int threadSize, String charset, ExecutorService executor, int bufferSize,
                         File file, IFileHandle handle) {
        this.threadSize = threadSize;
        this.charset = charset;
        this.executor = executor;
        this.bufferSize = bufferSize;
        this.fileLength = file.length();
        this.handle = handle;
        try {
            this.accessFile = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.startEndPair = new HashSet<StartEndPair>();
    }

    public void start() {
        long handSize = fileLength / threadSize;
        try {
            calculateStartEnd(0, handSize);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final long startTime = System.currentTimeMillis();
        barrier = new CyclicBarrier(startEndPair.size(), new Runnable() {
            public void run() {
                System.out.println("cost times : " + (System.currentTimeMillis() - startTime));
                System.out.println("all lines : " + counter.get());
            }
        });

        for (StartEndPair pair : startEndPair) {
            executor.execute(new ReaderTask(pair,bufferSize,accessFile,counter,charset,handle,barrier));
        }
    }

    private void calculateStartEnd(long start, long size) throws IOException {
        if (start > fileLength - 1) {
            return;
        }

        StartEndPair pair = new StartEndPair();
        pair.setStart(start);

        long endPosition = start + size - 1;
        if (endPosition > fileLength - 1) {
            pair.setEnd(fileLength - 1);
            startEndPair.add(pair);
            return;
        }

        accessFile.seek(endPosition);
        byte temp = (byte) accessFile.read();
        while (temp != '\n' && temp != '\t') {
            endPosition++;
            if (endPosition > fileLength - 1) {
                endPosition = fileLength - 1;
                pair.setEnd(endPosition);
                break;
            }
            accessFile.seek(endPosition);
            temp = (byte) accessFile.read();
        }

        pair.setEnd(endPosition);
        startEndPair.add(pair);
        calculateStartEnd(endPosition + 1, size);
    }


    public class StartEndPair {
        public long start;
        public long end;

        @Override
        public String toString() {
            return "StartEndPair{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StartEndPair that = (StartEndPair) o;
            return start == that.start &&
                    end == that.end;
        }

        public long getStart() {
            return start;
        }

        public void setStart(long start) {
            this.start = start;
        }

        public long getEnd() {
            return end;
        }

        public void setEnd(long end) {
            this.end = end;
        }
    }

    public static class Builder {

        private int threadSize;

        private String charset;

        private ExecutorService executor;

        private int bufferSize;

        private IFileHandle handle;

        private File file;

        public Builder(String file, IFileHandle handle) {
            this.file = new File(file);
            if (!this.file.exists())
                throw new IllegalArgumentException("文件不存在！");
            this.handle = handle;
        }

        public Builder withThreadSize(int threadSize) {
            this.threadSize = threadSize;
            return this;
        }

        public Builder withCharset(String charset) {
            this.charset = charset;
            return this;
        }

        public Builder withExecutor(ExecutorService executor){
            this.executor = executor;
            return this;
        }

        public Builder withBufferSize(int bufferSize){
            this.bufferSize = bufferSize;
            return this;
        }

        public BigFileReader build() {
            return new BigFileReader(this.threadSize, this.charset, this.executor,
                    this.bufferSize, this.file, this.handle);
        }
    }
}
