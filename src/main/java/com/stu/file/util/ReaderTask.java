package com.stu.file.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Qi.qingshan
 * @date 2020/4/12
 */
public class ReaderTask implements Runnable {
    private RandomAccessFile accessFile;
    private long start;
    private long sliceSize;
    private byte[] readBuff;
    private int buffSize;
    private String charset;
    private CyclicBarrier barrier;
    private IFileHandle handle;
    private AtomicLong counter;


    public ReaderTask(BigFileReader.StartEndPair pair, int buffSize, RandomAccessFile accessFile,
                      AtomicLong counter, String charset, IFileHandle handle, CyclicBarrier barrier) {
        start = pair.start;
        sliceSize = pair.end - pair.start + 1;
        readBuff = new byte[buffSize];
        this.accessFile = accessFile;
        this.buffSize = buffSize;

        this.charset = charset;
        this.handle = handle;
        this.barrier = barrier;
        this.counter = counter;
    }

    public void run() {
        System.out.println("thread : " + Thread.currentThread().getName());
        try {
            MappedByteBuffer byteBuffer = accessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, start, sliceSize);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            for (int offset = 0; offset < sliceSize; offset += buffSize) {
                int readLength;
                if (offset + buffSize <= sliceSize) {
                    readLength = buffSize;
                } else {
                    readLength = (int) (sliceSize - offset);
                }
                byteBuffer.get(readBuff, 0, readLength);
                for (int i = 0; i < readLength; i++) {
                    byte temp = readBuff[i];
                    if (temp == '\n' || temp == '\t') {
                        byte[] bytes = outputStream.toByteArray();
                        //handle
                        handle(bytes);
                        outputStream.reset();
                    } else {
                        outputStream.write(temp);
                    }
                }
                if (outputStream.size() > 0) {
                    //handle
                    handle(outputStream.toByteArray());
                }
                barrier.await();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    private void handle(byte[] bytes) throws UnsupportedEncodingException {
        String line;
        if (charset == null) {
            line = new String(bytes);
        } else {
            line = new String(bytes, charset);
        }
        if ((line != null) && (line.length() != 0)) {
            handle.handle(line);
            counter.incrementAndGet();
        }
    }
}
