package com.linilq.mybatis.utils;

import cn.hutool.core.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 统计每日第三方回执回掉情况
 */
public class CallBackCalculateUtils {
    public static Logger logger = LoggerFactory.getLogger(CallBackCalculateUtils.class);

    /**
     * 单应用收到回执次数
     */
    private static ConcurrentHashMap<String, AtomicLong> appId2CallNum = new ConcurrentHashMap<String, AtomicLong>(1000);

    private static ConcurrentHashMap<String, AtomicLong> appId2ReceiveCallNum = new ConcurrentHashMap<String, AtomicLong>(1000);

    private static ConcurrentHashMap<String, AtomicLong> appId2ClickCallNum = new ConcurrentHashMap<String, AtomicLong>(1000);

    /**
     * 单应用回回调设备数
     */
    private static ConcurrentHashMap<String, AtomicLong> appId2DeviceNum = new ConcurrentHashMap<String, AtomicLong>();

    private static ConcurrentHashMap<String, AtomicLong> receiveCallDeviceNum = new ConcurrentHashMap<String, AtomicLong>(1000);

    private static ConcurrentHashMap<String, AtomicLong> clickCallDeviceNum = new ConcurrentHashMap<String, AtomicLong>(1000);

    static {
        long sleepSec = getSeconds2NextHour();
        logger.info("定时输出回执统计任务的启动线程，开始休眠{}ms", sleepSec);
        final ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(1);

        pool.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                logCalculateInfo();
            }
        }, sleepSec, 3600, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                pool.shutdown();
            }
        }));
       /* new Thread(new Runnable() {
            @Override
            public void run() {
                long sleepSec = getSeconds2NextHour();
                logger.info("定时输出回执统计任务的启动线程，开始休眠{}ms", sleepSec);
                try {
                    Thread.sleep(sleepSec);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info("启动线程苏醒，启动任务");
                TimerHelper.getTimer().newTimeout(new TimerTask() {
                    @Override
                    public void run(Timeout timeout) throws Exception {
                        logCalculateInfo();
                    }
                }, 1, TimeUnit.HOURS);
                logger.info("定时输出回执任务启动完毕");
            }
        }).start();*/

    }


    private static long getSeconds2NextHour() {
        String time = DateUtil.format(new Date(), "HH:mm:ss");
        int mm = Integer.valueOf(time.split(":")[1]);
        int ss = Integer.valueOf(time.split(":")[2]);

        int seconds = (59 - mm) * 60 + 60 - ss;
        logger.info("距离下一个整点时间{}分{}秒，即{}秒", (59 - mm), (60 - ss), seconds);
        return seconds;
    }

    private static void logCalculateInfo() {

        logger.info("回执统计情况如下");
        logger.info("当前统计应用数:[{}]", appId2CallNum.size());

        Enumeration<String> appIds = appId2CallNum.keys();
        while (appIds.hasMoreElements()) {
            String appId = appIds.nextElement();

            long appId2CallNumL = appId2CallNum.get(appId) == null ? 0L : appId2CallNum.get(appId).longValue();
            long appId2ReceiveCallNumL = appId2ReceiveCallNum.get(appId) == null ? 0L : appId2ReceiveCallNum.get(appId).longValue();
            long appId2ClickCallNumL = appId2ClickCallNum.get(appId) == null ? 0L : appId2ClickCallNum.get(appId).longValue();
            long appId2DeviceNumL = appId2DeviceNum.get(appId) == null ? 0L : appId2DeviceNum.get(appId).longValue();
            long receiveCallDeviceNumL = receiveCallDeviceNum.get(appId) == null ? 0L : receiveCallDeviceNum.get(appId).longValue();
            long clickCallDeviceNumL = clickCallDeviceNum.get(appId) == null ? 0L : clickCallDeviceNum.get(appId).longValue();

            logger.info("回执应用ID:[{}],回执通知次数:[{}],[{}],[{}]，设备总数:[{}],[{}],[{}]", appId, appId2CallNumL
                    , appId2ReceiveCallNumL, appId2ClickCallNumL, appId2DeviceNumL, receiveCallDeviceNumL, clickCallDeviceNumL);

        }

        String timeStr = DateUtil.format(new Date(), "HH:mm:ss");

        if (timeStr.contains("00:00:")) {
            logger.info("{}清理数据", timeStr);
            clearLastDateData();
        }


    }

    public static void calculate(Long pushAppId, int type, long deviceNum) {
        String key = pushAppId + "";

        appId2CallNum(key);
        appId2DeviceNum(key, deviceNum);
//        recordMsgId(key, msgDetail.getMsgId());

        if (type == 1) {
            recordReceiveCallNum(key);
            receiveCallDeviceNum(key, deviceNum);
        }
        if (type == 2) {
            recordClickCallNum(key);
            clickCallDeviceNum(key, deviceNum);
        }

    }

    private static void clickCallDeviceNum(String key, long deviceIdSize) {
        AtomicLong atomicLong = new AtomicLong(deviceIdSize);
        AtomicLong temp = clickCallDeviceNum.putIfAbsent(key, atomicLong);
        if (temp == null || atomicLong == temp) {
            return;
        }
        temp.addAndGet(deviceIdSize);
    }

    private static void receiveCallDeviceNum(String key, long deviceIdSize) {
        AtomicLong atomicLong = new AtomicLong(deviceIdSize);
        AtomicLong temp = receiveCallDeviceNum.putIfAbsent(key, atomicLong);
        if (temp == null || atomicLong == temp) {
            return;
        }
        temp.addAndGet(deviceIdSize);
    }

    private static void appId2DeviceNum(String key, long deviceIdSize) {
        AtomicLong atomicLong = new AtomicLong(deviceIdSize);
        AtomicLong temp = appId2DeviceNum.putIfAbsent(key, atomicLong);
        if (temp == null || atomicLong == temp) {
            return;
        }
        temp.addAndGet(deviceIdSize);
    }

    /*private static void recordMsgId(String key, String msgId) {
        Set<String> msgIds = new HashSet<String>();
        msgIds.add(msgId);
        Set<String> temp = appId2MsgId.putIfAbsent(key, msgIds);
        if (temp.contains(msgId)) {
            return;
        }
        try {
            msgIdWriteLock.lock();
            temp.add(msgId);
        } catch (Exception e) {
            logger.error("recordMsgId error", e);
        } finally {
            msgIdWriteLock.unlock();
        }
    }
*/

    private static void recordClickCallNum(String key) {
        AtomicLong atomicLong = new AtomicLong(1L);
        AtomicLong temp = appId2ClickCallNum.putIfAbsent(key, atomicLong);
        if (temp == null || atomicLong == temp) {
            return;
        }
        temp.incrementAndGet();
    }

    private static void recordReceiveCallNum(String key) {
        AtomicLong atomicLong = new AtomicLong(1L);
        AtomicLong temp = appId2ReceiveCallNum.putIfAbsent(key, atomicLong);
        if (temp == null || atomicLong == temp) {
            return;
        }
        temp.incrementAndGet();
    }


    private static void appId2CallNum(String key) {
        AtomicLong atomicLong = new AtomicLong(1L);
        AtomicLong temp = appId2CallNum.putIfAbsent(key, atomicLong);
        if (temp == null || atomicLong == temp) {
            return;
        }
        temp.incrementAndGet();

    }

    private static void clearLastDateData() {
        appId2CallNum.clear();
        appId2ReceiveCallNum.clear();
        appId2ClickCallNum.clear();
        appId2DeviceNum.clear();
        receiveCallDeviceNum.clear();
        clickCallDeviceNum.clear();
    }


}
