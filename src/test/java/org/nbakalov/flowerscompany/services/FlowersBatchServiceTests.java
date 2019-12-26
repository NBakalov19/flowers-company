package org.nbakalov.flowerscompany.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.FlowersBatch;
import org.nbakalov.flowerscompany.data.models.entities.Warehouse;
import org.nbakalov.flowerscompany.data.models.enums.Variety;
import org.nbakalov.flowerscompany.data.models.models.flowers.MoveBatchModel;
import org.nbakalov.flowerscompany.data.repositories.FlowersBatchRepository;
import org.nbakalov.flowerscompany.errors.illegalservicemodels.IllegalFlowersBatchServiceModelException;
import org.nbakalov.flowerscompany.errors.notfound.FlowersBatchNotFoundException;
import org.nbakalov.flowerscompany.errors.unabled.NotPossibleToRegisterBatchException;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;
import org.nbakalov.flowerscompany.services.models.WarehouseServiceModel;
import org.nbakalov.flowerscompany.services.services.LogService;
import org.nbakalov.flowerscompany.services.services.WarehouseService;
import org.nbakalov.flowerscompany.services.services.implementations.FlowersBatchServiceImpl;
import org.nbakalov.flowerscompany.services.validators.FlowersBatchServiceModelValidatorService;
import org.nbakalov.flowerscompany.services.validators.implementation.FlowersBatchServiceModelValidatorServiceImpl;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.nbakalov.flowerscompany.constants.GlobalConstants.BEGIN_OF_DAY;
import static org.nbakalov.flowerscompany.constants.GlobalConstants.END_OF_DAY;
import static org.nbakalov.flowerscompany.services.WarehouseServiceTest.CURRENT_USER;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class FlowersBatchServiceTests {

  private static final Integer VALID_TRAYS = 100;
  private static final Integer VALID_BUNCHES_PER_TRAY = 16;
  private static final Variety VALID_VARIETY = Variety.APOTHEOSE;
  private static final String VALID_FIELD_NAME = "BIG HOLE";
  private static final String VALID_TEAM_SUPERVISIOR = "IVAN";
  private static final LocalDateTime VALID_DATE_TIME_TODAY = LocalDateTime.now();
  public static final Variety EDITED_VARIETY = Variety.VANILLAPEACH;

  private static final String VALID_ID = "valid_id";
  private static final String WAREHOUSE_VALID_NAME = "South";
  private static final Double WAREHOUSE_VALID_TEMPERATURE = 2.22;
  private static final Integer WAREHOUSE_INIT_CURR_CAPACITY = 0;
  private static final Integer WAREHOUSE_MAX_CAPACITY = 10000;

  public static FlowersBatch flowersBatch;
  public static FlowersBatchServiceModel flowersBatchServiceModel;

  private static Warehouse warehouse;
  private static WarehouseServiceModel warehouseServiceModel;

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


    flowersBatch = initBatch();
    flowersBatchServiceModel = initFlowerBatchModel();

    warehouseServiceModel = initWarehouseModel();
    warehouse = initWarehouse();

    flowersBatchServiceModel.setWarehouse(warehouseServiceModel);
    flowersBatch.setWarehouse(warehouse);
  }

  private FlowersBatch initBatch() {

    FlowersBatch batch = new FlowersBatch();

    batch.setId(VALID_ID);
    batch.setFieldName(VALID_FIELD_NAME);
    batch.setTeamSupervisor(VALID_TEAM_SUPERVISIOR);
    batch.setVariety(VALID_VARIETY);
    batch.setTrays(VALID_TRAYS);
    batch.setBunchesPerTray(VALID_BUNCHES_PER_TRAY);
    batch.setDatePicked(VALID_DATE_TIME_TODAY);

    return batch;
  }

  private FlowersBatchServiceModel initFlowerBatchModel() {

    FlowersBatchServiceModel model = new FlowersBatchServiceModel();

    model.setId(VALID_ID);
    model.setFieldName(VALID_FIELD_NAME);
    model.setTeamSupervisor(VALID_TEAM_SUPERVISIOR);
    model.setVariety(VALID_VARIETY);
    model.setTrays(VALID_TRAYS);
    model.setBunchesPerTray(VALID_BUNCHES_PER_TRAY);

    return model;
  }

  private WarehouseServiceModel initWarehouseModel() {
    WarehouseServiceModel model = new WarehouseServiceModel();

    model.setId(VALID_ID);
    model.setName(WAREHOUSE_VALID_NAME);
    model.setCurrCapacity(WAREHOUSE_INIT_CURR_CAPACITY);
    model.setMaxCapacity(WAREHOUSE_MAX_CAPACITY);
    model.setTemperature(WAREHOUSE_VALID_TEMPERATURE);
    model.setBatches(Set.of(flowersBatchServiceModel));

    return model;
  }

  private Warehouse initWarehouse() {

    Warehouse warehouse = new Warehouse();

    warehouse.setId(VALID_ID);
    warehouse.setName(WAREHOUSE_VALID_NAME);
    warehouse.setCurrCapacity(WAREHOUSE_INIT_CURR_CAPACITY);
    warehouse.setMaxCapacity(WAREHOUSE_MAX_CAPACITY);
    warehouse.setTemperature(WAREHOUSE_VALID_TEMPERATURE);
    warehouse.setBatches(Set.of(flowersBatch));

    return warehouse;
  }

  @Test(expected = IllegalFlowersBatchServiceModelException.class)
  public void registerBatch_WhenNotValid_ShouldThrow() {

    when(validatorService.isValid(flowersBatchServiceModel))
            .thenReturn(false);

    flowersBatchService.registerBatch(flowersBatchServiceModel, CURRENT_USER);
  }

  @Test
  public void registerBatch_WhenInputIsValid_ShouldWorkCorrect() {

    when(validatorService.isValid(flowersBatchServiceModel)).thenReturn(true);
    when(flowersBatchRepository.saveAndFlush(any(FlowersBatch.class))).thenReturn(flowersBatch);

    FlowersBatchServiceModel result = flowersBatchService.registerBatch(flowersBatchServiceModel, CURRENT_USER);

    assertNotNull(result);
    assertEquals(VALID_TEAM_SUPERVISIOR, result.getTeamSupervisor());
    assertEquals(VALID_BUNCHES_PER_TRAY, result.getBunchesPerTray());
    assertEquals(VALID_TRAYS, result.getTrays());
    assertEquals(VALID_VARIETY, result.getVariety());
  }

  @Test
  public void editFlowersBatch_ShouldWorkCorrectWithValidInput() {

    when(validatorService.isValid(flowersBatchServiceModel)).thenReturn(true);
    when(flowersBatchRepository.findById(VALID_ID)).thenReturn(Optional.of(flowersBatch));

    flowersBatchServiceModel.setVariety(EDITED_VARIETY);

    FlowersBatchServiceModel result = flowersBatchService.editFlowerBatch(VALID_ID, flowersBatchServiceModel, CURRENT_USER);

    assertEquals(EDITED_VARIETY, result.getVariety());
  }

  @Test
  public void moveBatch_ShouldWorkCorrectWithValidInput() {

    WarehouseServiceModel serviceModel = new WarehouseServiceModel() {{
      setId("someId");
      setName("North");
      setBatches(new HashSet<>());
      setTemperature(2.22);
      setCurrCapacity(0);
      setMaxCapacity(5000);
    }};

    MoveBatchModel moveBatchModel = new MoveBatchModel() {{
      setWarehouse("someId");
    }};

    when(flowersBatchRepository.findById(VALID_ID))
            .thenReturn(Optional.of(flowersBatch));

    when(warehouseService.findWarehouseById(VALID_ID))
            .thenReturn(warehouseServiceModel);

    when(warehouseService.findWarehouseById("someId"))
            .thenReturn(serviceModel);

    flowersBatchService.moveBatch(VALID_ID, moveBatchModel, CURRENT_USER);

    assertEquals(0, warehouseServiceModel.getBatches().size());
    assertEquals(1, serviceModel.getBatches().size());
  }

  @Test(expected = NotPossibleToRegisterBatchException.class)
  public void hasRoomInWarehouseShouldThrow_WhenNotHaveRoom() {

    warehouseServiceModel.setMaxCapacity(1000);
    flowersBatchServiceModel.setTrays(1001);

    flowersBatchService.hasRoomInWarehouse(warehouseServiceModel, flowersBatchServiceModel);
  }

  @Test(expected = FlowersBatchNotFoundException.class)
  public void findBatchByIdShouldThrow_WhenNoBatchWithId() {

    when(flowersBatchRepository.findById(VALID_ID)).thenReturn(Optional.empty());

    flowersBatchService.findBatchById(VALID_ID);
  }

  @Test
  public void findBatchByIdShouldFindBatch_WhenBatchIsInDb() {

    when(flowersBatchRepository.findById(VALID_ID)).thenReturn(Optional.of(flowersBatch));

    FlowersBatchServiceModel result = flowersBatchService.findBatchById(VALID_ID);

    assertEquals(VALID_VARIETY, result.getVariety());
    assertEquals(VALID_TRAYS, result.getTrays());
    assertEquals(VALID_BUNCHES_PER_TRAY, result.getBunchesPerTray());
  }

  @Test
  public void deleteFlowersBatchShouldDeleteBatch_WhenExist() {

    when(flowersBatchRepository.findById(VALID_ID)).thenReturn(Optional.of(flowersBatch));

    flowersBatchService.deleteBatch(VALID_ID, CURRENT_USER);

    verify(flowersBatchRepository, times(1)).deleteById(VALID_ID);
  }

  @Test
  public void findAllBatchesRegisteredToday_ShouldReturnBatchesFromToday() {

    flowersBatch.setWarehouse(warehouse);

    List<FlowersBatch> batches = List.of(flowersBatch);

    when(flowersBatchRepository
            .findAllByDatePickedBetweenOrderByDatePickedAsc(BEGIN_OF_DAY, END_OF_DAY))
            .thenReturn(batches);

    List<FlowersBatchServiceModel> result = flowersBatchService.findAllBatchesRegisteredToday();

    assertEquals(1, result.size());
    assertEquals(LocalDate.now(), result.get(0).getDatePicked().toLocalDate());
  }

  @Test
  public void findAllBatchesRegisteredToday_ShouldReturnNothingWhenNoBatchesAreRegistered() {

    flowersBatch.setWarehouse(warehouse);

    when(flowersBatchRepository
            .findAllByDatePickedBetweenOrderByDatePickedAsc(BEGIN_OF_DAY, END_OF_DAY))
            .thenReturn(new ArrayList<>());

    List<FlowersBatchServiceModel> result = flowersBatchService.findAllBatchesRegisteredToday();

    assertEquals(0, result.size());
  }

  @Test
  public void findAllBatchesByVarietyAndBunchesPerTray_ShouldWorkCorrect() {

    flowersBatch.setWarehouse(warehouse);

    List<FlowersBatch> batches = List.of(flowersBatch);

    when(flowersBatchRepository
            .findAllByVarietyAndBunchesPerTrayOrderByTraysDesc(any(), any()))
            .thenReturn(batches);

    List<FlowersBatchServiceModel> result = flowersBatchService
            .findAllBatchesByVarietyAndBunchesPerTray(VALID_VARIETY, VALID_BUNCHES_PER_TRAY);

    assertEquals(1, result.size());
    assertEquals(VALID_VARIETY, result.get(0).getVariety());
    assertEquals(VALID_BUNCHES_PER_TRAY, result.get(0).getBunchesPerTray());
  }

  @Test
  public void findAllBatchesByVarietyAndBunchesPerTray_ShouldReturnNothingWhenNoBatchesAreRegistered() {

    flowersBatch.setWarehouse(warehouse);

    when(flowersBatchRepository
            .findAllByVarietyAndBunchesPerTrayOrderByTraysDesc(any(), any()))
            .thenReturn(new ArrayList<>());

    List<FlowersBatchServiceModel> result = flowersBatchService
            .findAllBatchesByVarietyAndBunchesPerTray(VALID_VARIETY, VALID_BUNCHES_PER_TRAY);

    assertEquals(0, result.size());
  }

  @Test
  public void findAllBatchesByBunchesPerTray_ShouldWorkCorrect() {

    flowersBatch.setWarehouse(warehouse);

    List<FlowersBatch> batches = List.of(flowersBatch);

    when(flowersBatchRepository
            .findAllByBunchesPerTrayOrderByTrays(VALID_BUNCHES_PER_TRAY))
            .thenReturn(batches);

    List<FlowersBatchServiceModel> result = flowersBatchService
            .findAllBatchesByBunchesPerTray(VALID_BUNCHES_PER_TRAY);

    assertEquals(1, result.size());
    assertEquals(VALID_BUNCHES_PER_TRAY, result.get(0).getBunchesPerTray());
  }

  @Test
  public void findAllBatchesByBunchesPerTray_ShouldReturnNothingWhenNoBatchesAreRegistered() {

    flowersBatch.setWarehouse(warehouse);

    when(flowersBatchRepository
            .findAllByBunchesPerTrayOrderByTrays(VALID_BUNCHES_PER_TRAY))
            .thenReturn(new ArrayList<>());

    List<FlowersBatchServiceModel> result = flowersBatchService
            .findAllBatchesByBunchesPerTray(VALID_BUNCHES_PER_TRAY);

    assertEquals(0, result.size());
  }
}
