package org.nbakalov.flowerscompany.services.services.implementations;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.Log;
import org.nbakalov.flowerscompany.data.repositories.LogRepository;
import org.nbakalov.flowerscompany.errors.illegalservicemodels.IllegalLogServiceModelException;
import org.nbakalov.flowerscompany.services.models.LogServiceModel;
import org.nbakalov.flowerscompany.services.services.LogService;
import org.nbakalov.flowerscompany.services.validators.LogServiceModelValidatorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static org.nbakalov.flowerscompany.constants.GlobalConstants.BEGIN_OF_DAY;
import static org.nbakalov.flowerscompany.constants.GlobalConstants.END_OF_DAY;
import static org.nbakalov.flowerscompany.constants.LogConstants.LOG_BAD_CREDENTIAL;

@Service
@AllArgsConstructor
public class LogServiceImpl implements LogService {

  private final LogRepository logRepository;
  private final LogServiceModelValidatorService validatorService;
  private final ModelMapper modelMapper;


  @Override
  public LogServiceModel saveLog(LogServiceModel model) {

    Log log = modelMapper.map(model, Log.class);
    logRepository.saveAndFlush(log);

    return modelMapper.map(log, LogServiceModel.class);
  }

  @Override
  public List<LogServiceModel> findAllLogsFromToday() {
    return logRepository.findAllByCreatedOnBetweenOrderByCreatedOnAsc(BEGIN_OF_DAY, END_OF_DAY)
            .stream()
            .map(log -> modelMapper.map(log, LogServiceModel.class))
            .collect(Collectors.toList());
  }
}
