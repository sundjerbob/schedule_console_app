package raf.sk_schedule.engine;

import raf.sk_schedule.api.ScheduleManager;
import raf.sk_schedule.controller.ScheduleController;

import java.util.Scanner;


public class ScheduleAppEngine extends Thread {
    private final ScheduleManager scheduleManager;

    public ScheduleAppEngine(ScheduleManager scheduleManager) {
        this.scheduleManager = scheduleManager;

    }


    @Override
    public void run() {

        ScheduleController controller = new ScheduleController(scheduleManager);

        System.out.println
                (
                        "-----------------------------------------------------------------------------------------------\n"
                                + "Hello from " + scheduleManager.getClass().getSimpleName() + "!\n"
                                + "-----------------------------------------------------------------------------------------------\n"
                                + "First you need to configure schedule component...\n"
                                + "Enter the date in format yyyy-mm-dd \"2023-10-14\", from witch the schedule will start:"
                );

        Scanner scanner = new Scanner(System.in);

        String firstDate = scanner.nextLine().trim();

        System.out.println("Enter the date in format yyyy-mm-dd \"2023-10-14\", when the schedule will end:");

        String lastDate = scanner.nextLine().trim();

        scheduleManager.initialize(firstDate, lastDate);

        System.out.println("Your schedule is initialized, starting from " + firstDate + " and ending at " + lastDate + "!");

        try {
            do {
                System.out.println(
                        """  
                                -----------------------------------------------------------------------------------------------
                                Type "exit" to close application, or type: -h or --help to list all the commands you can use.
                                Enter commands:""");
            }
            while (controller.instructionScheduler());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}



