package raf.sk_schedule;

import raf.sk_schedule.config.AppConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("caos...");
            return;
        }
        try {

            Objects.requireNonNull(
                            AppConfig.initScheduleAppClient(args[0]))
                    .start();


        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 ClassNotFoundException e) {
            System.out.println(args[0]);
            e.printStackTrace();

        }


    }

}


