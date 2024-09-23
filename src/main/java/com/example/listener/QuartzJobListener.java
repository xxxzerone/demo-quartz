package com.example.listener;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.util.ObjectUtils;

@Slf4j
public class QuartzJobListener implements JobListener {

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        log.info("JOB 수행 되기 전: {}", context.getJobDetail().getKey());
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        log.info("JOB 중단: {}", context.getJobDetail().getKey());
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        if (ObjectUtils.isEmpty(jobException)) {
            log.info("JOB 익셉션 발생: {}", context.getJobDetail().getKey(), jobException);
        } else {
            log.info("JOB 수행 완료 후: {}", context.getJobDetail().getKey());
        }
    }
}
