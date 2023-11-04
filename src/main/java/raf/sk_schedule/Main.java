package raf.sk_schedule;

import raf.sk_schedule.config.AppConfig;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) {

        if(args.length != 1)
            return;
        try {

            AppConfig.initScheduleAppClient(args[0]).start();


        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}


