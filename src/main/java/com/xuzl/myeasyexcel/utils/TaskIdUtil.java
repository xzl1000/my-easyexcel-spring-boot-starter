package com.xuzl.myeasyexcel.utils;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author xuzl
 * @version 1.0.0
 * @ClassName TaskIdUtil.java
 * @Description TODO
 * @createTime 2022-03-08 16:12
 */
public class TaskIdUtil {

    private final static ReadWriteLock lock = new ReentrantReadWriteLock();

    private static final TaskIdCreator taskIdCreator = TaskIdCreator.createSnowflake(0,0);

    public static long nextId(){
        // 保证可以获取到id
        for (;;){
            try {
                // 读锁上锁，排斥写锁，保证读取期间workerId、dataCenterId不会被修改
                lock.readLock().lock();
                // 确保snowflake对象不为空
                return taskIdCreator.nextId();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.readLock().unlock();
            }
            // 每次循环间隙停顿200ms
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
