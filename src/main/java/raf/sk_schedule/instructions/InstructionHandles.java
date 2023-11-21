package raf.sk_schedule.instructions;

import raf.sk_schedule.engine.InputScanner;
import raf.sk_schedule.exception.ScheduleException;
import raf.sk_schedule.model.location_node.RoomProperties;
import raf.sk_schedule.model.schedule_node.ScheduleSlot;
import raf.sk_schedule.util.filter.SearchCriteria;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static raf.sk_schedule.config.ScheduleAppConfig.scheduleManager;
import static raf.sk_schedule.util.date_formater.DateTimeFormatter.formatDate;
import static raf.sk_schedule.util.date_formater.DateTimeFormatter.parseDate;
import static raf.sk_schedule.util.filter.CriteriaFilter.*;

public class InstructionHandles {

    private static final String helpFilePath = "src/main/resources/help.txt";

    private String helpPrompt;

    final Map<String, Handle> handlesMap;

    public interface Handle {
        void handle(InputScanner inputScanner);
    }

    final Handle help = inputScanner -> System.out.println(helpPrompt);

    final Handle scheduleTimeSlot = inputScanner -> {
        ScheduleSlot.Builder slotBuilder = new ScheduleSlot.Builder();

        System.out.println("Enter date in format \"yyyy-mm-dd\" (\"2023-10-23\") or day of the week on witch the slot will be scheduled on : ");
        String scheduleOn = inputScanner.nextLine().trim();

        if (!scheduleOn.matches("\\d{4}-\\d{2}-\\d{2}")) {
            //TODO: todo
            /*System.out.println("Enter Enter date in format \"yyyy-mm-dd\" (\"2023-10-23\"), starting from witch slot will be scheduled on every " + scheduleOn.toLowerCase() + " :");
            System.out.println("Enter Enter date in format \"yyyy-mm-dd\" (\"2023-10-23\"), slot will be scheduled on every "
                    + scheduleOn.toLowerCase() + " starting from " + /* +s/ " until date: ");
            System.out.println("Slot will be scheduled every " + scheduleOn.toLowerCase() + " starting from date " + startingDate + " until date " + endingDate + ".");
            System.out.println("Enter starting time in format \"HH:mm\" (\"12:25\") : ");
            System.out.println("Enter a duration in minutes (whole number), or ending time in format \"HH:mm\" (\"12:25\") :");
            */
        } else {

            slotBuilder.setDate(parseDate(scheduleOn));

            System.out.println("Enter starting time in format \"HH:mm\" (\"12:25\") : ");
            slotBuilder.setStartTime(inputScanner.nextLine().trim());

            System.out.println("Enter a duration in minutes (whole number), or ending time in format \"HH:mm\" (\"12:25\") :");
            String endTime = inputScanner.nextLine().trim();
            if (endTime.matches("\\d{2}:\\d{2}")) {
                slotBuilder.setEndTime(endTime);

            } else if (endTime.matches("\\d+"))
                slotBuilder.setDuration(Integer.parseInt(endTime));
        }

        System.out.println("Enter name of the room where you want to schedule the time slot: ");
        RoomProperties room;
        while ((room = scheduleManager.getRoom(inputScanner.nextLine().trim())) == null) {
            System.out.println("There is no room with that name! Please choose existing rooms name.");
        }
        slotBuilder.setLocation(room);

        while (true) {
            System.out.println("Enter additional attributes name or type in \"done\" if there is no additional attributes associated with this slot: ");
            String line = inputScanner.nextLine().trim();

            if (line.isEmpty() || line.equalsIgnoreCase("done"))
                break;

            System.out.println("Enter value for attribute named \"" + line + "\" :");

            slotBuilder.setAttribute(line, inputScanner.nextLine().trim());
        }

        // ************** adding the slot to schedule ************** //
        ScheduleSlot slot = slotBuilder.build();
        scheduleManager.scheduleTimeSlot(slot);
        // ********************************************************* //


        System.out.println("The new slot has been scheduled on date: " + formatDate(slot.getDate())
                + "starts at: " + slot.getStartTime() + ", has duration of: " + slot.getDuration() + " minutes, "
                + " and ends at: " + slot.getEndTime() + ".");

    };

    final Handle moveSlot = inputScanner -> {
        System.out.println("Enter date in format \"yyyy-mm-dd\" (\"2023-10-23\") or day of the week on witch the slot is scheduled currently: ");

    };


    final Handle addRoom = inputScanner -> {
        RoomProperties.Builder roomBuilder = new RoomProperties.Builder();

        System.out.println("Enter the room name: ");

        roomBuilder.setName(inputScanner.nextLine().trim());


        System.out.println("Enter room capacity that is positive whole number: ");
        String capacity = inputScanner.nextLine().trim();
        if (capacity.matches("\\d+"))
            roomBuilder.setCapacity(Integer.parseInt(capacity));
        else roomBuilder.setCapacity(1);

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

            roomBuilder.setAttribute(line, inputScanner.nextLine().trim());
        }

        RoomProperties newRoom = roomBuilder.build();
        try {
            scheduleManager.addRoom(newRoom);
            System.out.println("Room " + newRoom.getName() + " has been created!");

        } catch (ScheduleException e) {
            System.out.println(e.getMessage());
        }

    };

    final Handle removeRoom = inputScanner -> {
        System.out.println("Enter the name of the room you want to be removed: ");
        String roomName = inputScanner.nextLine();
        try {
            scheduleManager.deleteRoom(roomName);
        } catch (ScheduleException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Room " + roomName + " has been removed!");
    };

    final Handle updateRoom = inputScanner -> {

        System.out.println("Enter the name of the room you want to modify: ");
        String roomName = inputScanner.nextLine().trim();
        if (scheduleManager.hasRoom(roomName)) {
            System.out.println("There is no room with the selected name. Please choose another name.");
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

            roomBuilder.setAttribute(line, inputScanner.nextLine().trim());
        }
    };

    final Handle importRooms = inputScanner -> {
        System.out.println("Type in the path to csv file to import roomProperties:");
        String fileName = inputScanner.nextLine().trim();

        int numberOfRows = scheduleManager.loadRoomsSCV(fileName);
        System.out.println(
                "Room import finished successfully, " + numberOfRows + " row" + (numberOfRows > 1 ? "s are" : " is") + " added!"
        );

    };

    final Handle importSchedule = inputScanner -> {

        if (scheduleManager.getAllRooms().isEmpty()) {
            System.out.println("You currently don't have any rooms in witch you could schedule slots, you need to import rooms first. ");
            return;
        }
        System.out.println("Type in the path to csv file to import schedule:");
        String fileName = inputScanner.nextLine().trim();

        int numberOfRows = scheduleManager.loadScheduleSCV(fileName);

        System.out.println(
                "Schedule import finished successfully; "
                        + numberOfRows + " row" + (numberOfRows > 1 ? "s are" : " is") + " added!");

    };


    final Handle getWholeSchedule = inputScanner -> {

        List<ScheduleSlot> schedule = scheduleManager.getWholeSchedule();
        if (schedule.isEmpty())
            System.out.println("Your schedule is empty.");
        for (ScheduleSlot curr : schedule) {
            System.out.println(curr);
        }
    };

    final Handle getAllRooms = inputScanner -> {
        List<RoomProperties> rooms = scheduleManager.getAllRooms();
        if (rooms.isEmpty())
            System.out.println("There is no rooms in schedule.");
        for (RoomProperties curr : rooms) {
            System.out.println(curr);
        }
    };


    final Handle filterSchedule = inputScanner -> {

        SearchCriteria.Builder searchBuilder = new SearchCriteria.Builder();

        System.out.println("Enter lower bound date, that is the earliest for look up. Please enter date in format \"yyyy-mm-dd\" (\"2023-10-23\") :");
        searchBuilder.setCriteria(LOWER_BOUND_DATE_KEY, inputScanner.nextLine().trim());

        System.out.println("Enter upper bound date, that is the latest for look up. Please enter date in format \"yyyy-mm-dd\" (\"2023-10-23\") :");
        searchBuilder.setCriteria(UPPER_BOUND_DATE_KEY, inputScanner.nextLine().trim());

        System.out.println("Enter lower bound time, that is the time of the day earliest for look up. Please enter time in format \"HH:mm\" (\"2023-10-23\") :");
        searchBuilder.setCriteria(LOWER_BOUND_DATE_KEY, inputScanner.nextLine().trim());

        System.out.println("Enter lower bound time, that is the time of the day earliest for look up. Please enter time in format \"HH:mm\" (\"2023-10-23\") :");
        searchBuilder.setCriteria(UPPER_BOUND_TIME_KEY, inputScanner.nextLine().trim());


        Map<String, Object> dynamicAttributes = new HashMap<>();
        while (true) {
            System.out.println("Enter name of the attribute you want to apply filter for, or type \"done\" if you want to finish configuring search parameters:");
            String line = inputScanner.nextLine().trim();

            if (line.isEmpty())
                continue;

            if (line.trim().equalsIgnoreCase("done"))
                break;

            System.out.println("Enter value for attribute named \"" + line + "\" :");

            dynamicAttributes.put(line, inputScanner.nextLine().trim());
        }
        if (!dynamicAttributes.isEmpty())
            searchBuilder.setCriteria(DYNAMIC_ATTRIBUTES_KEY, dynamicAttributes);

        List<ScheduleSlot> result = scheduleManager.searchTimeSlots(searchBuilder.build());

        for (ScheduleSlot curr : result) {
            System.out.println(curr.toString());
        }
    };


    final Handle exportScheduleJSON = inputScanner -> {

        System.out.println("Enter the path you want to export schedule to:");
        String filePath = inputScanner.nextLine().trim();
        System.out.println("Enter the starting date for schedule export. So the slots that start on that date and after will be exported:");
        String startingDate = inputScanner.nextLine().trim();
        System.out.println("Enter the last date for schedule export. So the slots that before that date including the date itself will be exported:");
        String endingDate = inputScanner.nextLine().trim();
        System.out.println(
                scheduleManager.exportScheduleJSON(filePath, !startingDate.isBlank() ? startingDate : null, !endingDate.isBlank() ? endingDate : null)
                        + " object exported file on path: " + filePath + " !");

    };


    final Handle exportFilteredScheduleJSON = inputScanner -> {
        System.out.println("Enter the path you want to export schedule to:");
        String filePath = inputScanner.nextLine().trim();

        SearchCriteria.Builder searchBuilder = new SearchCriteria.Builder();

        System.out.println("Enter lower bound date, that is the earliest for look up. Please enter date in format \"yyyy-mm-dd\" (\"2023-10-23\") :");
        searchBuilder.setCriteria(LOWER_BOUND_DATE_KEY, inputScanner.nextLine().trim());

        System.out.println("Enter upper bound date, that is the latest for look up. Please enter date in format \"yyyy-mm-dd\" (\"2023-10-23\") :");
        searchBuilder.setCriteria(UPPER_BOUND_DATE_KEY, inputScanner.nextLine().trim());

        System.out.println("Enter lower bound time, that is the time of the day earliest for look up. Please enter time in format \"HH:mm\" (\"2023-10-23\") :");
        searchBuilder.setCriteria(LOWER_BOUND_DATE_KEY, inputScanner.nextLine().trim());

        System.out.println("Enter lower bound time, that is the time of the day earliest for look up. Please enter time in format \"HH:mm\" (\"2023-10-23\") :");
        searchBuilder.setCriteria(UPPER_BOUND_TIME_KEY, inputScanner.nextLine().trim());


        Map<String, Object> dynamicAttributes = new HashMap<>();
        while (true) {
            System.out.println("Enter name of the attribute you want to apply filter for, or type \"done\" if you want to finish configuring search parameters:");
            String line = inputScanner.nextLine().trim();

            if (line.isEmpty())
                continue;

            if (line.trim().equalsIgnoreCase("done"))
                break;

            System.out.println("Enter value for attribute named \"" + line + "\" :");

            dynamicAttributes.put(line, inputScanner.nextLine().trim());
        }
        if (!dynamicAttributes.isEmpty())
            searchBuilder.setCriteria(DYNAMIC_ATTRIBUTES_KEY, dynamicAttributes);


        System.out.println(
                scheduleManager.exportFilteredScheduleJSON(filePath, searchBuilder.build())
                        + " objects successfully exported in file on path: " + filePath + " !"
        );

    };


    final Handle exportScheduleCSV = inputScanner -> {
        System.out.println("Enter the path you want to export schedule to:");
        String filePath = inputScanner.nextLine().trim();
        System.out.println("Enter the starting date for schedule export. So the slots that start on that date and after will be exported. If you want to export slots from the beginning press enter:");
        String startingDate = inputScanner.nextLine().trim();
        System.out.println("Enter the last date for schedule export. So the slots that before that date including the date itself will be exported. If you want to export  slots until the last date in schedule press enter:");
        String endingDate = inputScanner.nextLine();
        System.out.println(
                scheduleManager.exportScheduleCSV(filePath, startingDate.isBlank() ? endingDate : null, endingDate.isBlank() ? endingDate : null)
                        + " object exported file on path: " + filePath + " !");
    };


    final Handle exportFilteredScheduleCSV = inputScanner -> {
        System.out.println("Enter the path you want to export schedule to:");
        String filePath = inputScanner.nextLine().trim();

        SearchCriteria.Builder searchBuilder = new SearchCriteria.Builder();

        System.out.println("Enter lower bound date, that is the earliest for look up. Please enter date in format \"yyyy-mm-dd\" (\"2023-10-23\") :");
        searchBuilder.setCriteria(LOWER_BOUND_DATE_KEY, inputScanner.nextLine().trim());

        System.out.println("Enter upper bound date, that is the latest for look up. Please enter date in format \"yyyy-mm-dd\" (\"2023-10-23\") :");
        searchBuilder.setCriteria(UPPER_BOUND_DATE_KEY, inputScanner.nextLine().trim());

        System.out.println("Enter lower bound time, that is the time of the day earliest for look up. Please enter time in format \"HH:mm\" (\"2023-10-23\") :");
        searchBuilder.setCriteria(LOWER_BOUND_DATE_KEY, inputScanner.nextLine().trim());

        System.out.println("Enter lower bound time, that is the time of the day earliest for look up. Please enter time in format \"HH:mm\" (\"2023-10-23\") :");
        searchBuilder.setCriteria(UPPER_BOUND_TIME_KEY, inputScanner.nextLine().trim());


        Map<String, Object> dynamicAttributes = new HashMap<>();
        while (true) {
            System.out.println("Enter name of the attribute you want to apply filter for, or type \"done\" if you want to finish configuring search parameters:");
            String line = inputScanner.nextLine().trim();

            if (line.isEmpty())
                continue;

            if (line.trim().equalsIgnoreCase("done"))
                break;

            System.out.println("Enter value for attribute named \"" + line + "\" :");

            dynamicAttributes.put(line, inputScanner.nextLine().trim());
        }
        if (!dynamicAttributes.isEmpty())
            searchBuilder.setCriteria(DYNAMIC_ATTRIBUTES_KEY, dynamicAttributes);

        System.out.println(
                scheduleManager.exportFilteredScheduleCSV(filePath, searchBuilder.build())
                        + " object exported file on path: " + filePath + " !");
    };


    public InstructionHandles() {


        handlesMap = new HashMap<>();


        try {
            helpPrompt = Files.readString(Path.of(helpFilePath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Help app documentation file not found!");
        }

        handlesMap.put("-h", help);
        handlesMap.put("--help", help);
        handlesMap.put("schedule_slot", scheduleTimeSlot);
        handlesMap.put("move_slot", moveSlot);
        handlesMap.put("add_room", addRoom);
        handlesMap.put("remove_room", removeRoom);
        handlesMap.put("edit_room", updateRoom);
        handlesMap.put("list_rooms", getAllRooms);
        handlesMap.put("show_schedule", getWholeSchedule);
        handlesMap.put("filter_schedule", filterSchedule);
        /*
        handleMap.put("is_free", isSlotFree);
        handleMap.put("show_free_schedule", getFreeSchedule);
        handleMap.put("filter_free_schedule", filterFreeSchedule);
        */
        handlesMap.put("import_rooms", importRooms);
        handlesMap.put("import_schedule", importSchedule);
        handlesMap.put("export_schedule_json", exportScheduleJSON);
        handlesMap.put("export_filtered_schedule_json", exportFilteredScheduleJSON);
        handlesMap.put("export_schedule_csv", exportScheduleCSV);
        handlesMap.put("export_filtered_schedule_scv", exportFilteredScheduleCSV);
    }


    public Handle getHandle(String instruction) {

        return handlesMap.get(instruction);
    }


}
