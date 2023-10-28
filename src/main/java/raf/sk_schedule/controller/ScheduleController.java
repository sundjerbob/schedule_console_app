package raf.sk_schedule.controller;

import raf.sk_schedule.api.ScheduleManager;
import raf.sk_schedule.model.RoomProperties;
import raf.sk_schedule.model.ScheduleSlot;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;


public class ScheduleController {

    private final InstructionHandles instructionHandles;

    Scanner inputScanner;

    final String help;

    final String helpFilePath = "src/main/java/raf/sk_schedule/toolkit/help.txt";

    public ScheduleController(ScheduleManager scheduleManager) {
        instructionHandles = new InstructionHandles(scheduleManager);
        this.inputScanner = new Scanner(System.in);


        try {
            help = Files.readString(Path.of(helpFilePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Help document not found!");
        }

    }

    public boolean actionScheduler() {

        String command = inputScanner.nextLine().trim().toLowerCase();


        if ("schedule_close".equalsIgnoreCase(command)) {
            System.out.println("Closing the program.");
            return false;
        }
        if ("--help".equalsIgnoreCase(command) || "-h".equalsIgnoreCase(command))
            System.out.println(help);

        InstructionHandles.Handle handle = instructionHandles.getHandle(command);

        if (handle == null)
            System.out.println(command + " is unsupported command. Type --help or -h to see all of the supported command you can use.");

        else
            handle.handle(inputScanner);

        return true;

    }


}
