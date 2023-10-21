package raf.sk_schedule;

import raf.sk_schedule.cofig.ScheduleAppEngine;

public class Main {
    public static void main(String[] args) {
        String pathToIMPL = "raf.sk_schedule.ScheduleSlotsManager";

        new ScheduleAppEngine(pathToIMPL).start();

    }

}


