package com.ittouch.ttt.service.ttt;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class TttGameTimerService {

    private final TaskScheduler taskScheduler;

    // Map to keep track of game timeouts
    private final Map<String, ScheduledFuture<?>> gameTimeoutTasks = new ConcurrentHashMap<>();

    public TttGameTimerService() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        this.taskScheduler = scheduler;
    }

    public void startGameTimer(String gameId, long timeLimit, Runnable onTimeout) {
        cancelTaskForGame(gameId);

        var timeoutTask = taskScheduler.schedule(
                onTimeout,
                new Date(System.currentTimeMillis() + timeLimit).toInstant()
        );

        gameTimeoutTasks.put(gameId, timeoutTask);
    }

    public void cancelTaskForGame(String gameId) {
        var timeoutTask = gameTimeoutTasks.remove(gameId);
        if (timeoutTask != null) {
            timeoutTask.cancel(false);
        }
    }
}
