package org.nbakalov.flowerscompany.services.services;

import org.springframework.scheduling.annotation.Async;

import java.io.IOException;

public interface ScheduleService {

  void writeTodayLogs() throws IOException;

  void writeTodaysBatches() throws IOException;
}
