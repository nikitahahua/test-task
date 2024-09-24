package com.privatbank.test_task;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableRetry
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "1m")
public class TestTaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestTaskApplication.class, args);
	}

}
