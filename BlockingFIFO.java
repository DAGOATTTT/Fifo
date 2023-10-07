package org.example;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingFIFO {
    private Task[] buffer;
    private int count;
    private int in;
    private int out;
    private Lock lock;
    private Condition notFull;
    private Condition notEmpty;

    public BlockingFIFO(int bufferSize) {
        buffer = new Task[bufferSize];
        count = 0;
        in = 0;
        out = 0;
        lock = new ReentrantLock();
        notFull = lock.newCondition();
        notEmpty = lock.newCondition();
    }

    public void put(Task task) throws InterruptedException {
        lock.lock();
        try {
            while (count == buffer.length) {
                notFull.await();
            }

            System.out.println("we are in put()");

            buffer[in] = task;
            in = (in + 1) % buffer.length;
            count++;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public Task take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }

            System.out.println("we are in take()");

            Task item = buffer[out];
            out = (out + 1) % buffer.length;
            count--;
            notFull.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }
}
