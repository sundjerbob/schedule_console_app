package raf.sk_schedule.config;

import raf.sk_schedule.api.ScheduleManager;
import raf.sk_schedule.engine.ScheduleAppEngine;

import java.lang.reflect.InvocationTargetException;

public class ScheduleAppConfig {

    private static ScheduleAppEngine appEngine;

    public static ScheduleManager scheduleManager;

    public static ScheduleAppEngine initScheduleAppClient(String classPath) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {

        if (appEngine != null && appEngine.isAlive())
            return null;

        Class<?> scheduleManagerImplementationClass = Class.forName(classPath);

        // useless check in case we got some random clas as scheduleManagerImplementationClass
        if (!ScheduleManager.class.isAssignableFrom(scheduleManagerImplementationClass))
            throw new RuntimeException("Select the class that implements ScheduleManager interface from ");

        Object scheduleManagerImplementation = scheduleManagerImplementationClass.getDeclaredConstructor().newInstance();

        // another useless check
        if (!(scheduleManagerImplementation instanceof ScheduleManager))
            throw new RuntimeException("Smt went wrong in schedule component dependency class configuration... (X),(x)' ");

        scheduleManager = (ScheduleManager) scheduleManagerImplementation;
        return appEngine = new ScheduleAppEngine(scheduleManager);
    }
}
