package raf.sk_schedule.engine;

import raf.sk_schedule.api.ScheduleManager;
import raf.sk_schedule.controller.ScheduleAppController;

import java.util.Scanner;


public class ScheduleAppEngine extends Thread {
    private final ScheduleManager scheduleManager;

    public ScheduleAppEngine(ScheduleManager scheduleManager) {
        this.scheduleManager = scheduleManager;

    }


    @Override
    public void run() {

        ScheduleAppController controller = new ScheduleAppController().init();

        if(controller != null) {
            do {
                System.out.println(
                        """  
                                -----------------------------------------------------------------------------------------------
                                Type "exit" to close application, or type: -h or --help to list all the commands you can use.
                                Enter commands:""");
            }
            while (controller.instructionScheduler());
        }

        System.out.println("Closing the program.");
    }


}



