package raf.sk_schedule.controller;

import raf.sk_schedule.api.ScheduleManager;

import java.util.Scanner;


public class ScheduleController {

    private final InstructionHandles instructionHandles;

    Scanner inputScanner;


    final String helpFilePath = "src/main/java/raf/sk_schedule/toolkit/help.txt";

    public ScheduleController(ScheduleManager scheduleManager) {
        instructionHandles = new InstructionHandles(scheduleManager);
        this.inputScanner = new Scanner(System.in);




    }


    public boolean instructionScheduler() {

        String command = inputScanner.nextLine().trim().toLowerCase();


        if ("exit".equalsIgnoreCase(command)) {
            System.out.println("Closing the program.");
            inputScanner.close();
            return false;
        }

        InstructionHandles.Handle handle = instructionHandles.getHandle(command);

        if (handle == null)
            System.out.println(command + " is unsupported command. Type --help or -h to see all of the supported command you can use.");

        else
            handle.handle(inputScanner);

        return true;

    }


}
