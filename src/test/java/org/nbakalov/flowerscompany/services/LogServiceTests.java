package org.nbakalov.flowerscompany.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.Log;
import org.nbakalov.flowerscompany.data.repositories.LogRepository;
import org.nbakalov.flowerscompany.errors.illegalservicemodels.IllegalLogServiceModelException;
import org.nbakalov.flowerscompany.services.models.LogServiceModel;
import org.nbakalov.flowerscompany.services.services.implementations.LogServiceImpl;
import org.nbakalov.flowerscompany.services.validators.LogServiceModelValidatorService;
import org.nbakalov.flowerscompany.services.validators.implementation.LogServiceModelValidatorImpl;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class LogServiceTests {

  public static final Log LOG = new Log();
  public static final LogServiceModel MODEL = new LogServiceModel();
  public static final LocalDateTime VALID_DATE_TIME = LocalDateTime.now();
  public static final String VALID_USER = "Niko";
  public static final String VALID_DESCRIPTION = "did this";

  @InjectMocks
  LogServiceImpl logService;

  @Mock
  private LogRepository logRepository;

  @Mock
  LogServiceModelValidatorService validatorService;

  @Mock
  private ModelMapper modelMapper;

  @Before
  public void init() {
    LogServiceModelValidatorImpl modelValidator =
            new LogServiceModelValidatorImpl();

    ModelMapper actualMapper = new ModelMapper();

    when(modelMapper.map(any(LogServiceModel.class), eq(Log.class)))
            .thenAnswer(invocationOnMock ->
                    actualMapper.map(invocationOnMock.getArguments()[0], Log.class));

    when(modelMapper.map(any(Log.class), eq(LogServiceModel.class)))
            .thenAnswer(invocationOnMock ->
                    actualMapper.map(invocationOnMock.getArguments()[0], LogServiceModel.class));

    when(validatorService.isValid(any()))
            .thenAnswer(invocationOnMock ->
                    modelValidator.isValid((LogServiceModel) invocationOnMock.getArguments()[0]));

    LOG.setCreatedOn(VALID_DATE_TIME);
    LOG.setUsername(VALID_USER);
    LOG.setDescription(VALID_DESCRIPTION);

    MODEL.setCreatedOn(VALID_DATE_TIME);
    MODEL.setUsername(VALID_USER);
    MODEL.setDescription(VALID_DESCRIPTION);
  }

  @Test(expected = IllegalLogServiceModelException.class)
  public void saveLog_ShouldThrowWhenNotValid() {

    when(validatorService.isValid(MODEL))
            .thenReturn(false);

    logService.saveLog(MODEL);
  }

  @Test
  public void saveLog_WhenInputIsValid_ShouldWorkCorrect() {

    when(validatorService.isValid(MODEL)).thenReturn(true);
    when(logRepository.saveAndFlush(any(Log.class))).thenReturn(LOG);

    LogServiceModel result = logService.saveLog(MODEL);

    assertNotNull(result);
    assertEquals(VALID_USER, result.getUsername());
    assertEquals(VALID_DESCRIPTION, result.getDescription());
  }

  @Test
  public void findAllLogs_ShouldReturnAllSavedLogs() {

    List<LogServiceModel> logs = new ArrayList<>();
    logs.add(MODEL);

    lenient().when(logService.findAllLogsFromToday())
            .thenReturn(logs);

    assertEquals(1, logs.size());
    assertEquals(VALID_USER, logs.get(0).getUsername());
  }
}
