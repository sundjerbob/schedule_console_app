package raf.sk_schedule.controller;

import raf.sk_schedule.engine.InputScanner;
import raf.sk_schedule.instructions.InstructionHandles;


import static raf.sk_schedule.config.ScheduleAppConfig.scheduleManager;


public class ScheduleAppController {

    private final InstructionHandles instructionHandles;

    InputScanner inputScanner;


    final String helpFilePath = "src/main/java/raf/sk_schedule/toolkit/help.txt";

    public ScheduleAppController() {
        instructionHandles = new InstructionHandles();
        inputScanner = new InputScanner();
    }


    public ScheduleAppController init() {
        System.out.println
                (
                        "-----------------------------------------------------------------------------------------------\n"
                                + "Hello from " + scheduleManager.getClass().getSimpleName() + "!\n"
                                + "-----------------------------------------------------------------------------------------------\n"
                                + "First you need to configure schedule component..."
                );

        while (true) {

            try {
                System.out.println("Enter the date in format yyyy-mm-dd \"2023-10-14\", from witch the schedule will start:");
                String firstDate = inputScanner.nextLine().trim();

                System.out.println("Enter the date in format yyyy-mm-dd \"2023-10-14\", when the schedule will end:");
                String lastDate = inputScanner.nextLine().trim();

                scheduleManager.initialize(firstDate, lastDate);

                System.out.println("Your schedule is initialized, starting from " + firstDate + " and ending at " + lastDate + "!");

                return this;

            } catch (Exception e) {
                if (inputScanner.isOpen)
                    System.err.println("Schedule unit initialization failed: " + e.getMessage());
                else
                    return null;
            }
        }
    }


    public boolean instructionScheduler() {
        try {
            String command = inputScanner.nextLine().trim().toLowerCase();

            InstructionHandles.Handle handle = instructionHandles.getHandle(command);

            if (handle == null)
                System.out.println("\"" + command + "\" is unsupported command. Type --help or -h to see all of the supported command you can use.");

            else
                handle.handle(inputScanner);

            return true;
        } catch (Exception e) {
            if (inputScanner.isOpen) {
                e.printStackTrace(System.err);
                System.err.println(e.getMessage());
                return true;
            }
        }
        return false;
    }

}
