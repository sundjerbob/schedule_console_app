package raf.sk_schedule.controller;

import raf.sk_schedule.api.ScheduleManager;
import raf.sk_schedule.exception.ScheduleException;
import raf.sk_schedule.model.RoomProperties;
import raf.sk_schedule.model.ScheduleSlot;

import java.text.ParseException;
import java.util.Scanner;

public class ScheduleController {
    ScheduleManager scheduleManager;

    Scanner inputScanner;
    public ScheduleController(Scanner input, ScheduleManager scheduleManager) {
        this.inputScanner = input;

        this.scheduleManager = scheduleManager;
    }
    public boolean actionScheduler() {
        String line = inputScanner.nextLine();

        if ("schedule_close".equalsIgnoreCase(line.trim())) {
            System.out.println("Closing the program.");
            return false;
        }
        if ("list_schedule".equalsIgnoreCase(line.trim())) {
            listSchedule();
            return true;
        }
        if ("schedule_slot".equalsIgnoreCase(line.trim())) {
            addScheduleSlot();
            return true;
        }
        return false;
    }

    private void addScheduleSlot() {
        System.out.println("Enter date for a slot want to schedule: ");
        String date = inputScanner.nextLine();
        System.out.println("Enter starting time: ");
        String startingTime = inputScanner.nextLine();
        System.out.println("Enter a duration in minutes or ending time");
        String endTime = inputScanner.nextLine();
        System.out.println("Enter name of the room where you want to schedule the time slot: ");
        String roomName = inputScanner.nextLine();
        System.out.println("Enter next attribute name or enter done to schedule a slot without additional attributes: ");
        String input = inputScanner.nextLine();
        if(!input.equalsIgnoreCase("done"))
            return;

        long duration = 0;
        if (endTime.contains(":"))
            endTime = date + " " + endTime;
        else
            duration = Long.parseLong(endTime);

        try {

            scheduleManager.addTimeSlot(
                    new ScheduleSlot.Builder()
                            .setStart(date + " " + startingTime)
                            .setDuration((duration > 0) ? duration : 1)
                            .setLocation(
                                    new RoomProperties.Builder().setName(roomName).build()
                            )
                            .build()
            );

            System.out.println("The new slot is created!");

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }catch (ScheduleException e) {
            System.out.println(e.getMessage());
        }
    }

    private void listSchedule() {
        for (ScheduleSlot curr : scheduleManager.getWholeSchedule()) {
            System.out.println(curr);
        }
    }
}
