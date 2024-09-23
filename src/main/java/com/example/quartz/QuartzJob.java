package com.example.quartz;

import com.example.domain.AuctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuartzJob implements Job {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");

    private final AuctionRepository auctionRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        auctionEnds(context);
    }

    private void auctionEnds(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        log.info("auctionEnds jobDataMap: {}", jobDataMap);
        boolean status = jobDataMap.getBoolean("status");
        LocalDateTime createdAt = LocalDateTime.parse(jobDataMap.getString("createdAt"), dateTimeFormatter);
        log.info("auctionEnds status: {}, createdAt: {}", status, createdAt);
    }
}
