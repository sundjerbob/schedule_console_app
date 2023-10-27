package raf.sk_schedule.cofig;

import raf.sk_schedule.api.ScheduleManager;
import raf.sk_schedule.engine.ScheduleAppEngine;
import raf.sk_schedule.util.ScheduleMapper;

import java.lang.reflect.InvocationTargetException;

public class AppConfig {

    private static ScheduleAppEngine appEngine;

    public static ScheduleAppEngine initScheduleAppClient(String classPath) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {

        if(appEngine != null && appEngine.isAlive())
            return null;

        Class<?> scheduleManagerImplementationClass = Class.forName(classPath);

        // useless check in case we got some random clas as scheduleManagerImplementationClass
        if (!scheduleManagerImplementationClass.getSuperclass().isAssignableFrom(ScheduleManager.class))
            throw new RuntimeException("Select the class that implements ScheduleManager interface from ");

        Object scheduleManager = scheduleManagerImplementationClass.getDeclaredConstructor().newInstance();

        // another useless check
        if (!(scheduleManager instanceof ScheduleManager))
            throw new RuntimeException("Smt went wrong in dependency class configuration... (x),(x)");

        return appEngine = new ScheduleAppEngine((ScheduleManager) scheduleManager);
    }
}
