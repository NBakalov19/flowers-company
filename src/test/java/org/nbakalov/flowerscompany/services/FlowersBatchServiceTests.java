package org.nbakalov.flowerscompany.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.FlowersBatch;
import org.nbakalov.flowerscompany.data.models.enums.Variety;
import org.nbakalov.flowerscompany.data.repositories.FlowersBatchRepository;
import org.nbakalov.flowerscompany.errors.illegalservicemodels.IllegalFlowersBatchServiceModelException;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;
import org.nbakalov.flowerscompany.services.models.WarehouseServiceModel;
import org.nbakalov.flowerscompany.services.services.LogService;
import org.nbakalov.flowerscompany.services.services.WarehouseService;
import org.nbakalov.flowerscompany.services.services.implementations.FlowersBatchServiceImpl;
import org.nbakalov.flowerscompany.services.validators.FlowersBatchServiceModelValidatorService;
import org.nbakalov.flowerscompany.services.validators.implementation.FlowersBatchServiceModelValidatorServiceImpl;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.nbakalov.flowerscompany.services.WarehouseServiceTest.CURRENT_USER;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class FlowersBatchServiceTests {

  public static final FlowersBatch FLOWERS_BATCH = new FlowersBatch();
  public static final FlowersBatchServiceModel MODEL = new FlowersBatchServiceModel();

  public static final Integer VALID_TRAYS = 100;
  public static final Integer VALID_BUNCHES_PER_TRAY = 16;
  public static final Variety VALID_VARIETY = Variety.APOTHEOSE;
  public static final String VALID_FIELD_NAME = "BIG HOLE";
  public static final String VALID_TEAM_SUPERVISIOR = "IVAN";

  public static final WarehouseServiceModel WAREHOUSE_SERVICE_MODEL = new WarehouseServiceModel();
  public static final String ID = "valid_id";
  public static final String WAREHOUSE_VALID_NAME = "South";
  public static final Double WAREHOUSE_VALID_TEMPERATURE = 2.22;
  public static final Integer WAREHOUSE_INIT_CURR_CAPACITY = 0;
  public static final Integer WAREHOUSE_MAX_CAPACITY = 10000;

  @InjectMocks
  private FlowersBatchServiceImpl flowersBatchService;

  @Mock
  private FlowersBatchRepository flowersBatchRepository;

  @Mock
  private FlowersBatchServiceModelValidatorService validatorService;

  @Mock
  private WarehouseService warehouseService;

  @Mock
  private LogService logService;

  @Mock
  private ModelMapper modelMapper;

  @Before
  public void init() {
    FlowersBatchServiceModelValidatorServiceImpl modelValidatorService =
            new FlowersBatchServiceModelValidatorServiceImpl();

    ModelMapper actualMapper = new ModelMapper();

    when(modelMapper.map(any(FlowersBatchServiceModel.class), eq(FlowersBatch.class)))
            .thenAnswer(invocationOnMock ->
                    actualMapper.map(invocationOnMock.getArguments()[0], FlowersBatch.class));

    when(modelMapper.map(any(FlowersBatch.class), eq(FlowersBatchServiceModel.class)))
            .thenAnswer(invocationOnMock ->
                    actualMapper.map(invocationOnMock.getArguments()[0], FlowersBatchServiceModel.class));

    when(validatorService.isValid(any()))
            .thenAnswer(invocationOnMock ->
                    modelValidatorService.isValid((FlowersBatchServiceModel) invocationOnMock.getArguments()[0]));

    when(logService.saveLog(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

    when(logService.saveLog(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

    FLOWERS_BATCH.setFieldName(VALID_FIELD_NAME);
    FLOWERS_BATCH.setTeamSupervisor(VALID_TEAM_SUPERVISIOR);
    FLOWERS_BATCH.setVariety(VALID_VARIETY);
    FLOWERS_BATCH.setTrays(VALID_TRAYS);
    FLOWERS_BATCH.setBunchesPerTray(VALID_BUNCHES_PER_TRAY);

    MODEL.setFieldName(VALID_FIELD_NAME);
    MODEL.setTeamSupervisor(VALID_TEAM_SUPERVISIOR);
    MODEL.setTrays(VALID_TRAYS);
    MODEL.setBunchesPerTray(VALID_BUNCHES_PER_TRAY);

    WAREHOUSE_SERVICE_MODEL.setName(WAREHOUSE_VALID_NAME);
    WAREHOUSE_SERVICE_MODEL.setCurrCapacity(WAREHOUSE_INIT_CURR_CAPACITY);
    WAREHOUSE_SERVICE_MODEL.setMaxCapacity(WAREHOUSE_MAX_CAPACITY);
    WAREHOUSE_SERVICE_MODEL.setTemperature(WAREHOUSE_VALID_TEMPERATURE);
  }

  @Test(expected = IllegalFlowersBatchServiceModelException.class)
  public void registerBatch_WhenNotValid_ShouldThrow() {

    when(validatorService.isValid(MODEL))
            .thenReturn(false);

    flowersBatchService.registerBatch(MODEL, CURRENT_USER);
  }

  @Test
  public void registerBatch_WhenInputIsValid_ShouldWorkCorrect() {

    when(validatorService.isValid(MODEL)).thenReturn(true);
    when(flowersBatchRepository.saveAndFlush(any(FlowersBatch.class))).thenReturn(FLOWERS_BATCH);

    FlowersBatchServiceModel result = flowersBatchService.registerBatch(MODEL, CURRENT_USER);

    assertNotNull(result);
    assertEquals(VALID_TEAM_SUPERVISIOR, result.getTeamSupervisor());
    assertEquals(VALID_BUNCHES_PER_TRAY, result.getBunchesPerTray());
    assertEquals(VALID_TRAYS, result.getTrays());
    assertEquals(VALID_VARIETY, result.getVariety());
  }
}
