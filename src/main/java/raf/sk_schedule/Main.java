package raf.sk_schedule;

import raf.sk_schedule.config.ScheduleAppConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {

        if (args.length != 1) {
            return;
        }
        try {

            Objects.requireNonNull(
                            ScheduleAppConfig.initScheduleAppClient(args[0]))
                    .start();


        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }


    }

}


