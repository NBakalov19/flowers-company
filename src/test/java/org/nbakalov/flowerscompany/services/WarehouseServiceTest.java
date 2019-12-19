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
import org.nbakalov.flowerscompany.data.repositories.WarehouseRepository;
import org.nbakalov.flowerscompany.errors.dublicates.WarehouseAllreadyExistException;
import org.nbakalov.flowerscompany.errors.illegalservicemodels.IllegalWarehouseServiceModelException;
import org.nbakalov.flowerscompany.errors.notfound.WarehouseNotFoundException;
import org.nbakalov.flowerscompany.errors.unabled.NotPossibleToEmptyWarehouseException;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;
import org.nbakalov.flowerscompany.services.models.WarehouseServiceModel;
import org.nbakalov.flowerscompany.services.services.LogService;
import org.nbakalov.flowerscompany.services.services.implementations.WarehouseServiceImpl;
import org.nbakalov.flowerscompany.services.validators.WarehouseServiceModelValidatorService;
import org.nbakalov.flowerscompany.services.validators.implementation.WarehouseServiceModelValidatorServiceImpl;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.nbakalov.flowerscompany.data.models.enums.Variety.VANILLAPEACH;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class WarehouseServiceTest {

  public static final Warehouse WAREHOUSE = new Warehouse();
  public static final WarehouseServiceModel MODEL = new WarehouseServiceModel();

  public static final String CURRENT_USER = "Niko";
  public static final String ID = "valid_id";
  public static final String VALID_NAME = "South";
  public static final Double VALID_TEMPERATURE = 2.22;
  public static final Integer INIT_CURR_CAPACITY = 0;
  public static final Integer MAX_CAPACITY = 10000;
  private static final Warehouse WAREHOUSE_2 = new Warehouse();

  @InjectMocks
  private WarehouseServiceImpl warehouseService;

  @Mock
  private WarehouseRepository warehouseRepository;

  @Mock
  private WarehouseServiceModelValidatorService validatorService;

  @Mock
  private LogService logService;
  @Mock
  private ModelMapper modelMapper;

  @Before
  public void init() {

    WarehouseServiceModelValidatorServiceImpl modelValidatorService =
            new WarehouseServiceModelValidatorServiceImpl();
    ModelMapper actualMapper = new ModelMapper();

    when(modelMapper.map(any(WarehouseServiceModel.class), eq(Warehouse.class)))
            .thenAnswer(invocationOnMock ->
                    actualMapper.map(invocationOnMock.getArguments()[0], Warehouse.class));

    when(modelMapper.map(any(Warehouse.class), eq(WarehouseServiceModel.class)))
            .thenAnswer(invocationOnMock ->
                    actualMapper.map(invocationOnMock.getArguments()[0], WarehouseServiceModel.class));

    when(validatorService.isValid(any()))
            .thenAnswer(invocationOnMock ->
                    modelValidatorService.isValid((WarehouseServiceModel) invocationOnMock.getArguments()[0]));

    when(logService.saveLog(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);


    WAREHOUSE.setName(VALID_NAME);
    WAREHOUSE.setTemperature(VALID_TEMPERATURE);
    WAREHOUSE.setCurrCapacity(INIT_CURR_CAPACITY);
    WAREHOUSE.setMaxCapacity(MAX_CAPACITY);
    WAREHOUSE.setBatches(new HashSet<>());

    MODEL.setName(VALID_NAME);
    MODEL.setTemperature(VALID_TEMPERATURE);
    MODEL.setCurrCapacity(INIT_CURR_CAPACITY);
    MODEL.setMaxCapacity(MAX_CAPACITY);
    MODEL.setBatches(new HashSet<>());
  }

  @Test(expected = IllegalWarehouseServiceModelException.class)
  public void createWarehouse_WhenNotValid_ShouldThrow() {

    when(validatorService.isValid(MODEL))
            .thenReturn(false);

    warehouseService.createWarehouse(MODEL, CURRENT_USER);
  }

  @Test(expected = WarehouseAllreadyExistException.class)
  public void createWarehouse_WhenWarehouseExist_ShouldThrow() {

    when(warehouseRepository.findByName(VALID_NAME)).thenReturn(Optional.of(WAREHOUSE));

    warehouseService.createWarehouse(MODEL, VALID_NAME);
  }

  @Test
  public void createWarehouse_WhenInputIsValid_ShouldWorkCorrect() {

    when(validatorService.isValid(MODEL)).thenReturn(true);
    when(warehouseRepository.saveAndFlush(any(Warehouse.class))).thenReturn(WAREHOUSE);

    WarehouseServiceModel result = warehouseService.createWarehouse(MODEL, CURRENT_USER);

    assertNotNull(result);
    assertEquals(VALID_NAME, result.getName());
  }

  @Test
  public void findAllWarehouse_WhenHaveWarehouseInRepo_ShouldReturnAll() {

    List<Warehouse> warehouses = new ArrayList<>();
    warehouses.add(WAREHOUSE);

   lenient().when(warehouseRepository.findAll()).thenReturn(warehouses);

    assertEquals(1, warehouses.size());
    assertEquals(VALID_NAME, warehouses.get(0).getName());
  }

  @Test
  public void getWarehouseCount_ShouldReturnCorrectSize() {

    when(warehouseRepository.count()).thenReturn(1L);

    Long result = warehouseService.getWarehousesCount();

    assertEquals(Long.valueOf(1), result);
  }

  @Test(expected = WarehouseNotFoundException.class)
  public void findWarehouseById_WithInvalidId_ShouldThrow() {

    warehouseService.findWarehouseById(ID);
  }

  @Test
  public void deleteWarehouse_ShouldDeleteCorrect() {

    when(warehouseRepository.findById(ID)).thenReturn(Optional.of(WAREHOUSE));

    warehouseService.deleteWarehouse(ID, CURRENT_USER);

    verify(warehouseRepository, times(1)).delete(WAREHOUSE);
  }

  @Test
  public void isPossibleToMoveStock_ReturnTrueWithCorrectInput() {

    int firstWarehouseCurrCapacity = 10;
    int secondWarehouseCurrCapacity = 10;
    int secondWarehouseMaxCapacity = 100;

    assertTrue(warehouseService.isPossibleToMoveStock(firstWarehouseCurrCapacity,
            secondWarehouseCurrCapacity,
            secondWarehouseMaxCapacity));
  }

  @Test
  public void updateCurrCapacityWorkCorrect() {

    FlowersBatchServiceModel flowersBatch =
            new FlowersBatchServiceModel() {{
              setTrays(100);
              setBunchesPerTray(16);
              setVariety(VANILLAPEACH);
              setFieldName("BIG HOLE");
              setTeamSupervisor("Ivan");
              setWarehouse(MODEL);
            }};

    MODEL.getBatches().add(flowersBatch);

    when(warehouseService.updateCurrCapacity(MODEL)).thenReturn(MODEL);

    WarehouseServiceModel result = warehouseService.updateCurrCapacity(MODEL);

    assertEquals(Integer.valueOf(100), result.getCurrCapacity());
  }

  @Test
  public void editWarehouseWithCorrectInput_ShouldWorkCorrect() {

    when(warehouseRepository.findById(ID)).thenReturn(Optional.of(WAREHOUSE));
    when(warehouseService.findWarehouseById(ID)).thenReturn(MODEL);
    when(warehouseRepository.saveAndFlush(any(Warehouse.class))).thenReturn(WAREHOUSE);
    when(warehouseService.editWarehouse(ID, MODEL, VALID_NAME)).thenReturn(MODEL);

    MODEL.setName("North");
    MODEL.setMaxCapacity(15000);

    WarehouseServiceModel result = warehouseService.editWarehouse(ID, MODEL, VALID_NAME);

    assertEquals("North", result.getName());
    assertEquals(Integer.valueOf(15000), result.getMaxCapacity());
  }

  @Test(expected = NotPossibleToEmptyWarehouseException.class)
  public void emptyWarehouse_ShouldThrowWhenIsOnlyOneWarehouse() {

    when(warehouseRepository.count()).thenReturn(1L);

    warehouseService.emptyWarehouse(ID, CURRENT_USER);
  }

  @Test
  public void emptyWarehouse_ShouldWorkCorrect() {

    when(warehouseRepository.count()).thenReturn(2L);
    when(warehouseRepository.findById(ID)).thenReturn(Optional.of(WAREHOUSE));
    when(warehouseRepository.findFirstByOrderByCurrCapacityAsc()).thenReturn(WAREHOUSE_2);

    WAREHOUSE_2.setName("North");
    WAREHOUSE_2.setTemperature(VALID_TEMPERATURE);
    WAREHOUSE_2.setCurrCapacity(0);
    WAREHOUSE_2.setMaxCapacity(50000);
    WAREHOUSE_2.setBatches(new HashSet<>());

    FlowersBatch flowersBatch =
            new FlowersBatch() {{
              setTrays(100);
              setBunchesPerTray(16);
              setVariety(VANILLAPEACH);
              setFieldName("BIG HOLE");
              setTeamSupervisor("Ivan");
              setWarehouse(WAREHOUSE);
            }};

    WAREHOUSE.getBatches().add(flowersBatch);

    warehouseService.emptyWarehouse(ID, CURRENT_USER);

  }

  @Test(expected = NotPossibleToEmptyWarehouseException.class)
  public void emptyWarehouse_ShouldThrowIfNotHaveSpaceInOtherWarehouse() {

    when(warehouseRepository.count()).thenReturn(2L);
    when(warehouseRepository.findById(ID)).thenReturn(Optional.of(WAREHOUSE));
    when(warehouseRepository.findFirstByOrderByCurrCapacityAsc()).thenReturn(WAREHOUSE_2);

    WAREHOUSE.setCurrCapacity(1500);
    WAREHOUSE_2.setName("North");
    WAREHOUSE_2.setTemperature(VALID_TEMPERATURE);
    WAREHOUSE_2.setCurrCapacity(1500);
    WAREHOUSE_2.setMaxCapacity(2500);
    WAREHOUSE_2.setBatches(new HashSet<>());

    FlowersBatch flowersBatch =
            new FlowersBatch() {{
              setTrays(100);
              setBunchesPerTray(16);
              setVariety(VANILLAPEACH);
              setFieldName("BIG HOLE");
              setTeamSupervisor("Ivan");
              setWarehouse(WAREHOUSE);
            }};

    WAREHOUSE.getBatches().add(flowersBatch);

    warehouseService.emptyWarehouse(ID, CURRENT_USER);

  }
}

