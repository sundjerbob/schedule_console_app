package raf.sk_schedule.controller;

import raf.sk_schedule.api.ScheduleManager;
import raf.sk_schedule.exception.ScheduleException;
import raf.sk_schedule.exception.ScheduleIOException;
import raf.sk_schedule.model.RoomProperties;
import raf.sk_schedule.model.ScheduleSlot;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class InstructionHandles {


    private  ScheduleManager scheduleManager;


    public InstructionHandles(ScheduleManager scheduleManager) {
        this.scheduleManager = scheduleManager;
    }

    public interface Handle {
        void handle(Scanner inputScanner);
    }
    Handle scheduleTimeSlot = inputScanner -> {
        ScheduleSlot.Builder slotBuilder = new ScheduleSlot.Builder();

        System.out.println("Enter date in format \"yyyy-mm-dd\" (\"2023-10-23\") or day of the week on witch the slot will be scheduled on : ");
        String scheduleOn = inputScanner.nextLine().trim();

        if (!scheduleOn.matches("\\d{4}-\\d{2}-\\d{2}")) {

            slotBuilder.setAttribute("weekly", scheduleOn);

            System.out.println("Enter Enter date in format \"yyyy-mm-dd\" (\"2023-10-23\"), starting from witch slot will be scheduled on every " + scheduleOn.toLowerCase() + " :");
            String startingDate = inputScanner.nextLine().trim();
            slotBuilder.setAttribute("startingDate", startingDate);

            System.out.println("Enter Enter date in format \"yyyy-mm-dd\" (\"2023-10-23\"), slot will be scheduled on every "
                    + scheduleOn.toLowerCase() + " starting from " + startingDate + " until date: ");
            String endingDate = inputScanner.nextLine().trim();
            slotBuilder.setAttribute("endingDate", endingDate);

            System.out.println("Slot will be scheduled every " + scheduleOn.toLowerCase() + " starting from date " + startingDate + " until date " + endingDate + ".");

            System.out.println("Enter starting time in format \"hours:minutes\" (\"12:25\") : ");
            String startingTime = inputScanner.nextLine().trim();
            slotBuilder.setAttribute("startTime", startingTime);

            System.out.println("Enter a duration in minutes (whole number), or ending time in format \"hours:minutes\" (\"12:25\") :");
            String endTime = inputScanner.nextLine().trim();

            if (endTime.matches("\\d{2}:\\d{2}"))
                slotBuilder.setAttribute("endTime", endTime);
            else
                slotBuilder.setDuration(Long.parseLong(endTime));

        } else {

            System.out.println("Enter starting time in format \"hours:minutes\" (\"12:25\") : ");
            String startingTime = inputScanner.nextLine().trim();

            try {
                slotBuilder.setStart(scheduleOn + " " + startingTime);
            } catch (ParseException e) {
                System.out.println(e.getMessage());
            }


            System.out.println("Enter a duration in minutes (whole number), or ending time in format \"hours:minutes\" (\"12:25\") :");
            String endTime = inputScanner.nextLine().trim();
            if (endTime.matches("\\d{2}:\\d{2}")) {
                try {
                    slotBuilder.setEnd(scheduleOn + " " + endTime);
                } catch (ParseException e) {
                    System.out.println(e.getMessage());
                }
            } else
                slotBuilder.setDuration(Long.parseLong(endTime));
        }

        System.out.println("Enter name of the room where you want to schedule the time slot: ");
        RoomProperties room = null;
        while (true) {
            room = scheduleManager.getRoom(inputScanner.nextLine().trim());
            if (room != null)
                break;

            System.out.println("There is no room with that name");
        }
        slotBuilder.setLocation(room);

        while (true) {
            System.out.println("Enter additional attributes name or type in \"done\" if there is no additional attributes associated with this slot: ");
            String line = inputScanner.nextLine().trim();

            if (line.isEmpty())
                continue;

            if (line.equalsIgnoreCase("done"))
                break;

            System.out.println("Enter value for attribute named \"" + line + "\" :");

            slotBuilder.setAttribute(line, inputScanner.nextLine().trim());
        }

        // ************** adding the slot to schedule ************** //
        ScheduleSlot slot = slotBuilder.build();
        try {
            scheduleManager.scheduleTimeSlot(slot);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        // ********************************************************* //

        if (slot.hasAttribute("weekly"))
            System.out.println("The new slot is has been scheduled on every " + slot.getAttribute("weekly")
                    + " starts at: " + slot.getAttribute("startTime")
                    + (slot.hasAttribute("endTime") ?
                    " and ends at: " + slot.getAttribute("endTime") : " with a duration of " + slot.getDuration() + " minutes ")
                    + " starting from date: " + slot.getAttribute("start")
                    + " until date: " + slot.getAttribute("until"));
        else {
            try {
                System.out.println("The new slot has been scheduled on date: " + slot.getStartAsString().split(" ")[0]
                        + "starts at: " + slot.getStartAsString().split(" ")[1] + ", has duration of " + slot.getDuration() + " minutes, "
                        + " and ends at: " + slot.getEndAsString().split(" ")[1] + ".");
            } catch (ParseException e) {
                System.out.println(e.getMessage());
            }
        }


    };


    private Handle addRoom = inputScanner -> {
        RoomProperties.Builder roomBuilder = new RoomProperties.Builder();

        System.out.println("Enter the room name: ");

        roomBuilder.setName(inputScanner.nextLine().trim());

        System.out.println("Enter room capacity that is positive whole number: ");

        roomBuilder.setCapacity(Integer.parseInt(inputScanner.nextLine()));

        System.out.println("Does this room have computers? Type \"y\" if yes or \"n\" if no.");

        roomBuilder.setHasComputers(inputScanner.nextLine().trim().equalsIgnoreCase("y"));

        System.out.println("Does this room have a projector? Type \"y\" if yes or \"n\" if no.");

        roomBuilder.setHasProjector(inputScanner.nextLine().trim().equalsIgnoreCase("y"));

        while (true) {
            System.out.println("Enter additional attributes name or type in \"done\" if there is no additional attributes associated with this room: ");
            String line = inputScanner.nextLine().trim();

            if (line.isEmpty())
                continue;

            if (line.equalsIgnoreCase("done"))
                break;

            System.out.println("Enter value for attribute named \"" + line + "\" :");

            roomBuilder.addExtra(line, inputScanner.nextLine().trim());
        }

        RoomProperties newRoom = roomBuilder.build();
        scheduleManager.addRoom(newRoom);

        System.out.println("Room " + newRoom.getName() + " has been created!");

    };

    private Handle removeRoom = inputScanner -> {
        System.out.println("Enter the name of the room you want to be removed: ");
        String roomName = inputScanner.nextLine();
        try {
            scheduleManager.deleteRoom(roomName);
        } catch (ScheduleException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Room " + roomName + " has been removed!");
    };

    private Handle updateRoom = inputScanner -> {

        System.out.println("Enter the name of the room you want to modify: ");
        String roomName = inputScanner.nextLine().trim();
        if (scheduleManager.hasRoom(roomName)) {
            System.out.println("There is no room with the selected name.");
            return;
        }
        RoomProperties.Builder roomBuilder = new RoomProperties.Builder();

        System.out.println("Enter new the room name or just press enter if you want to keep current room name:");
        String newRoomName = inputScanner.nextLine();
        roomBuilder.setName(newRoomName.isBlank() ? roomName : newRoomName);

        System.out.println("Enter room capacity that is positive whole number or just press enter if you want to keep current capacity:");
        String newCapacity = inputScanner.nextLine().trim();
        if (!newCapacity.isBlank())
            roomBuilder.setCapacity(Integer.parseInt(inputScanner.nextLine()));

        System.out.println("Does this room have computers? Type \"y\" if yes or \"n\" if no or just press enter if you want to keep current state:");
        String hasComputers = inputScanner.nextLine().trim();
        if (!hasComputers.isBlank())
            roomBuilder.setHasComputers(hasComputers.equalsIgnoreCase("y"));


        System.out.println("Does this room have a projector? Type \"y\" if yes or \"n\" if no or just press enter if you want to keep current state:");
        String hasProjector = inputScanner.nextLine().trim();
        if (!hasProjector.isBlank())
            roomBuilder.setHasProjector(hasProjector.equalsIgnoreCase("y"));


        while (true) {
            System.out.println("Enter additional attributes name you want to add or change, or type in \"done\" if you want to finish editing rooms information: ");
            String line = inputScanner.nextLine().trim();

            if (line.isEmpty())
                continue;

            if (line.equalsIgnoreCase("done"))
                break;

            System.out.println("Enter value for attribute named \"" + line + "\" :");

            roomBuilder.addExtra(line, inputScanner.nextLine().trim());
        }


    };

    private Handle importRooms = inputScanner -> {
        System.out.println("Type in the path to csv file to import roomProperties.");
        String fileName = inputScanner.nextLine().trim();

        try {

            int numberOfRows = scheduleManager.loadRoomsSCV(fileName);
            System.out.println(
                    "Room import finished successfully; "
                            + numberOfRows + " row" + (numberOfRows > 1 ? "s are" : " is") + " added!"
            );
        } catch (ScheduleIOException | IOException e) {
            System.out.println(e.getMessage());
        }

    };

    private Handle importSchedule = inputScanner -> {
        System.out.println("Type in the path to csv file to import roomProperties.");
        String fileName = inputScanner.nextLine().trim();

        try {
            int numberOfRows = scheduleManager.loadRoomsSCV(fileName);

            System.out.println(
                    "Schedule import finished successfully; "
                            + numberOfRows + " row" + (numberOfRows > 1 ? "s are" : " is") + " added!");
        } catch (ScheduleIOException | IOException e) {
            System.out.println(e.getMessage());
        }

    };


    private Handle getWholeSchedule = inputScanner -> {

        List<ScheduleSlot> schedule = scheduleManager.getWholeSchedule();
        if (schedule.isEmpty())
            System.out.println("Your schedule is empty.");
        for (ScheduleSlot curr : schedule) {
            System.out.println(curr);
        }
    };

    private Handle getAllRooms = inputScanner -> {
        List<RoomProperties> rooms = scheduleManager.getAllRooms();
        if (rooms.isEmpty())
            System.out.println("There is no rooms in schedule.");
        for (RoomProperties curr : rooms) {
            System.out.println(curr);
        }
    };


    private Map<String, Handle> handlesMap =
            Map.of(
                    "schedule_slot", scheduleTimeSlot,
                    "add_room", addRoom,
                    "remove_room", removeRoom,
                    "edit_room", updateRoom,
                    "list_rooms", getAllRooms,
                    "show_schedule", getWholeSchedule,
                    "import_rooms", importRooms,
                    "import_schedule", importSchedule
            );






    public Handle getHandle(String instruction) {

        return handlesMap.get(instruction);
    }


}
