package com.mfi_homework.mfi_homework;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AfterStartupEventProcessor {

    @EventListener(ApplicationReadyEvent.class)
    public void process() {
        System.out.println("I am ready!");
    }
}
