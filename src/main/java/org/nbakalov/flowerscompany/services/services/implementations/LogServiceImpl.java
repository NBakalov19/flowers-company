package org.nbakalov.flowerscompany.services.services.implementations;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.Log;
import org.nbakalov.flowerscompany.data.repositories.LogRepository;
import org.nbakalov.flowerscompany.services.models.LogServiceModel;
import org.nbakalov.flowerscompany.services.services.LogService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LogServiceImpl implements LogService {

  private final LogRepository logRepository;
  private final ModelMapper modelMapper;


  @Override
  public LogServiceModel saveLog(LogServiceModel model) {

    Log log = modelMapper.map(model, Log.class);
    logRepository.saveAndFlush(log);

    return modelMapper.map(log, LogServiceModel.class);
  }

  @Override
  public List<LogServiceModel> findAllLogs() {
    return logRepository.findAllByOrderByCreatedOnDesc()
            .stream()
            .map(log -> modelMapper.map(log, LogServiceModel.class))
            .collect(Collectors.toList());
  }
}
