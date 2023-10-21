package raf.sk_schedule.cofig;

import raf.sk_schedule.api.ScheduleManager;
import raf.sk_schedule.controller.ScheduleController;
import raf.sk_schedule.exception.ScheduleException;
import raf.sk_schedule.model.RoomProperties;
import raf.sk_schedule.model.ScheduleSlot;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Scanner;

public class ScheduleAppEngine extends Thread {
    private final Class<?> scheduleManagerImplementation;

    public ScheduleAppEngine(String implementationPath) {

        try {
            scheduleManagerImplementation = Class.forName(implementationPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void run() {

        if (!ScheduleManager.class.isAssignableFrom(scheduleManagerImplementation.getSuperclass()))
            System.out.println("ScheduleManager Implementation not found");

        System.out.println("Hello from " + scheduleManagerImplementation.getSimpleName());

        ScheduleManager scheduleManager;
        try {
            scheduleManager = (ScheduleManager) scheduleManagerImplementation.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        Scanner scanner = new Scanner(System.in);

        ScheduleController controller = new ScheduleController(scanner, scheduleManager);

        while (true) {
            System.out.println("Enter commands. Type 'schedule_close' to exit, -h or --help to list all the functionalities u can use.");

            if (!controller.actionScheduler())
                break;

        }

        scanner.close(); // Close the scanner when done
    }


}



