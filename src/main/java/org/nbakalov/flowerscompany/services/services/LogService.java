package org.nbakalov.flowerscompany.services.services;

import org.nbakalov.flowerscompany.services.models.LogServiceModel;

import java.util.List;

public interface LogService {

  LogServiceModel saveLog(LogServiceModel model);

  List<LogServiceModel> findAllLogsFromToday();
}
