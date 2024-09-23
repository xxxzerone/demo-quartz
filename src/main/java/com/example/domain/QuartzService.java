package com.example.domain;

import com.example.listener.QuartzJobListener;
import com.example.listener.QuartzTriggerListener;
import com.example.quartz.QuartzJob;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuartzService {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Scheduler scheduler;

    public void init() {
        try {
            scheduler.getListenerManager().addJobListener(new QuartzJobListener());
            scheduler.getListenerManager().addTriggerListener(new QuartzTriggerListener());

            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("status", false);
            paramsMap.put("startedAt", LocalDateTime.now().format(dateTimeFormatter));

            addJob(QuartzJob.class, "AuctionQuartzJob", "경매 종료 QuartzJob 입니다.", paramsMap);

            if (!scheduler.isStarted()) {
                scheduler.start();
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private void addJob(Class<? extends Job> jobClass, String jobName, String jobDescription, Map<String, Object> jobDataMap) throws SchedulerException {
        JobDetail jobDetail = buildJobDetail(jobClass, jobName, jobDescription, jobDataMap);
        Trigger trigger = buildTrigger(jobName, LocalDateTime.parse((String) jobDataMap.get("startedAt"), dateTimeFormatter));

        if (scheduler.checkExists(jobDetail.getKey())) {
            log.info("업데이트 할 Job : {}", jobDetail.getKey());
            scheduler.deleteJob(jobDetail.getKey());
        } else {
            log.info("새로운 Job 추가 : {}", jobDetail.getKey());
        }
        scheduler.scheduleJob(jobDetail, trigger);
    }

    private Trigger buildTrigger(String jobName, LocalDateTime startedAt) {
        log.info("buildTrigger jobName: {}", jobName);
        LocalDateTime dateTime = startedAt.plusMinutes(2);
        log.info("buildTrigger startedAt: {}, dateTime: {}", startedAt, dateTime);

        return TriggerBuilder.newTrigger()
                .withIdentity("auctionEndTrigger_" + jobName)
                .startAt(Date.from(dateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant()))
                .build();
    }

    private JobDetail buildJobDetail(Class<? extends Job> job, String name, String description, Map<String, Object> paramsMap) {
        log.info("buildJobDetail paramsMap: {}", paramsMap);
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.putAll(paramsMap);

        return JobBuilder.newJob(job)
                .withIdentity(name)
                .withDescription(description)
                .usingJobData(jobDataMap)
                .build();
    }
}
