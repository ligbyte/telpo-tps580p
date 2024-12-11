package com.stkj.cashier.util;

import java.util.ArrayDeque;
import java.util.Queue;

public class QueueManager {
    private Queue<Runnable> taskQueue;

    public QueueManager() {
        taskQueue = new ArrayDeque<>();
    }

    public void addTask(Runnable task) {
        taskQueue.offer(task);

    }

    public Runnable getNextTask() {
        return taskQueue.poll();
    }

    public boolean hasMoreTasks() {
        return !taskQueue.isEmpty();
    }

    public void clearTasks() {
        taskQueue.clear();
    }

}
