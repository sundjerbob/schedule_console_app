package raf.sk_schedule.engine;

import raf.sk_schedule.api.ScheduleManager;
import raf.sk_schedule.controller.ScheduleController;


public class ScheduleAppEngine extends Thread {
    private final ScheduleManager scheduleManager;

    public ScheduleAppEngine(ScheduleManager scheduleManager) {
        this.scheduleManager = scheduleManager;

    }


    @Override
    public void run() {

        ScheduleController controller = new ScheduleController(scheduleManager);

        System.out.println("------------------------------------------------------------------------\n" +
                "Hello from " + scheduleManager.getClass().getSimpleName() + "!");
        try {
            do {
                System.out.println("\nType 'schedule_close' to exit, -h or --help to list all the commands you can use.\nEnter commands:");

            } while (controller.actionScheduler());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}



