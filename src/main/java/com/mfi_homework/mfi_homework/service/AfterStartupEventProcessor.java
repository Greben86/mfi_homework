package com.mfi_homework.mfi_homework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Component
public class AfterStartupEventProcessor {

    @Value("${startup.thread.count}")
    private int threadCount;
    @Value("${thread.duration.sleep.seconds}")
    private int threadDurationSleep;
    @Value("${articles.limit_for_all}")
    private int limitAll;
    @Value("${articles.limit_for_thread}")
    private int limitThread;
    @Value("${articles.black_list}")
    private String blackList;
    @Value("${source.news.url}")
    private String url;

    public final WebClient webClient;
    public final NewsBufferService newsBufferService;

    @EventListener(ApplicationReadyEvent.class)
    public void process() {
        ExecutorService service = Executors.newFixedThreadPool(threadCount);

        var blackArray = blackList.split(",");
        for (int i = 0; i < threadCount; i++) {
            service.execute(
                    new ReadNewsTask(url, limitThread, limitAll, threadDurationSleep, blackArray, webClient.mutate().build(), newsBufferService));
        }

        service.shutdown();
    }
}
