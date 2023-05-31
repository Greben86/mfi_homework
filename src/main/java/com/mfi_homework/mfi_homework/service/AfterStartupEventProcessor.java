package com.mfi_homework.mfi_homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Component
public class AfterStartupEventProcessor {

    @Value("${startup.thread.count}")
    private int threadCount;
    @Value("${articles.limit_for_all}")
    private int limit_all;
    @Value("${articles.limit_for_thread}")
    private int limit_thread;
    @Value("${articles.black_list}")
    private String black_list;
    @Value("${source.news.url}")
    private String url;

    public final NewsBufferService newsBufferService;

    @EventListener(ApplicationReadyEvent.class)
    public void process() {
        ExecutorService service = Executors.newFixedThreadPool(threadCount);

        var black_array = black_list.split(",");
        for (int i = 0; i < threadCount; i++) {
            service.execute(new ReadNewsTask(url, limit_thread, limit_all, black_array, newsBufferService));
        }

        service.shutdown();
    }
}
