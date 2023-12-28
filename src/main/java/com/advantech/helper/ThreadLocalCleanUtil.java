/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.advantech.helper;

import java.lang.ref.Reference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Justin.Yeh
 */
public class ThreadLocalCleanUtil {

    /**
     * 得到当前线程组中的所有线程 description:
     *
     * @return
     */
    private static final Logger log = LoggerFactory.getLogger(ThreadLocalCleanUtil.class);

    public ThreadLocalCleanUtil() {
//        BasicConfigurator.configure();
    }

    private static Thread[] getThreads() {
        ThreadGroup tg = Thread.currentThread().getThreadGroup();

        while (tg.getParent() != null) {
            tg = tg.getParent();
        }

        int threadCountGuess = tg.activeCount() + 50;
        Thread[] threads = new Thread[threadCountGuess];
        int threadCountActual = tg.enumerate(threads);

        while (threadCountActual == threadCountGuess) {
            threadCountGuess *= 2;
            threads = new Thread[threadCountGuess];

            threadCountActual = tg.enumerate(threads);
        }

        return threads;
    }

    public static void clearThreadLocals() {

        ClassLoader classloader = Thread
                .currentThread()
                .getContextClassLoader();

        Thread[] threads = getThreads();
        try {
            Field threadLocalsField = Thread.class
                    .getDeclaredField("threadLocals");

            threadLocalsField.setAccessible(true);
            Field inheritableThreadLocalsField = Thread.class
                    .getDeclaredField("inheritableThreadLocals");

            inheritableThreadLocalsField.setAccessible(true);

            Class tlmClass = Class
                    .forName("java.lang.ThreadLocal$ThreadLocalMap");

            Field tableField = tlmClass.getDeclaredField("table");
            tableField.setAccessible(true);

            for (int i = 0; i < threads.length; ++i) {
                if (threads[i] == null) {
                    continue;
                }
                Object threadLocalMap = threadLocalsField.get(threads[i]);
                clearThreadLocalMap(threadLocalMap, tableField, classloader);

                threadLocalMap = inheritableThreadLocalsField.get(threads[i]);

                clearThreadLocalMap(threadLocalMap, tableField, classloader);
            }
        } catch (NoSuchFieldException | SecurityException | ClassNotFoundException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException e) {
        } catch (InvocationTargetException ex) {
            log.error(ex.toString());
        }
    }

    private static void clearThreadLocalMap(Object map,
            Field internalTableField, ClassLoader classloader)
            throws NoSuchMethodException, IllegalAccessException,
            NoSuchFieldException, InvocationTargetException {
        if (map != null) {
            Method mapRemove = map.getClass().getDeclaredMethod("remove",
                    new Class[]{ThreadLocal.class});

            mapRemove.setAccessible(true);
            Object[] table = (Object[]) internalTableField.get(map);
            int staleEntriesCount = 0;
            if (table != null) {
                for (int j = 0; j < table.length; ++j) {
                    if (table[j] != null) {
                        boolean remove = false;

                        Object key = ((Reference) table[j]).get();
                        if ((key != null)
                                && (key.getClass().getClassLoader() == classloader)) {
                            remove = true;

                            log.info("clean threadLocal key,class="
                                    + key.getClass().getCanonicalName()
                                    + ",value=" + key.toString());
                        }

                        Field valueField = table[j]
                                .getClass()
                                .getDeclaredField("value");

                        valueField.setAccessible(true);
                        Object value = valueField.get(table[j]);

                        if ((value != null)
                                && (value.getClass().getClassLoader() == classloader)) {
                            remove = true;
                            log.info("clean threadLocal value,class="
                                    + value.getClass().getCanonicalName()
                                    + ",value=" + value.toString());

                        }

                        if (remove) {

                            if (key == null) {
                                ++staleEntriesCount;
                            } else {
                                mapRemove.invoke(map, new Object[]{key});
                            }
                        }
                    }
                }
            }
            if (staleEntriesCount > 0) {
                Method mapRemoveStale = map
                        .getClass()
                        .getDeclaredMethod("expungeStaleEntries", new Class[0]);

                mapRemoveStale.setAccessible(true);
                mapRemoveStale.invoke(map, new Object[0]);
            }
        }
    }
}
