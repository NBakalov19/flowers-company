package org.nbakalov.flowerscompany.services.services.implementations;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.Warehouse;
import org.nbakalov.flowerscompany.data.repositories.WarehouseRepository;
import org.nbakalov.flowerscompany.errors.dublicates.WarehouseAllreadyExistException;
import org.nbakalov.flowerscompany.errors.illegalservicemodels.IllegalWarehouseServiceModelException;
import org.nbakalov.flowerscompany.errors.notfound.WarehouseNotFoundException;
import org.nbakalov.flowerscompany.errors.unabled.NotPossibleToEmptyWarehouseException;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;
import org.nbakalov.flowerscompany.services.models.LogServiceModel;
import org.nbakalov.flowerscompany.services.models.WarehouseServiceModel;
import org.nbakalov.flowerscompany.services.services.LogService;
import org.nbakalov.flowerscompany.services.services.WarehouseService;
import org.nbakalov.flowerscompany.services.validators.WarehouseServiceModelValidatorService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.nbakalov.flowerscompany.constants.LogConstants.*;
import static org.nbakalov.flowerscompany.constants.WarehouseConstants.*;

@Service
@AllArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

  private final WarehouseRepository warehouseRepository;
  private final WarehouseServiceModelValidatorService validatorService;
  private final LogService logService;
  private final ModelMapper modelMapper;

  @Override
  public WarehouseServiceModel createWarehouse(WarehouseServiceModel serviceModel, String currentUser) {

    if (!validatorService.isValid(serviceModel)) {
      throw new IllegalWarehouseServiceModelException(WAREHOUSE_BAD_CREDENTIALS);
    }

    if (warehouseRepository.findByName(serviceModel.getName()).isPresent()) {
      throw new WarehouseAllreadyExistException(WAREHOUSE_ALLREADY_EXIST);
    }

    serviceModel.setTemperature(getRandomNumber());
    serviceModel.setBatches(new LinkedHashSet<>());
    serviceModel.setCurrCapacity(INITIAL_CURRENT_CAPACITY);

    Warehouse warehouse = modelMapper.map(serviceModel, Warehouse.class);
    warehouseRepository.saveAndFlush(warehouse);

    LogServiceModel log =
            createLog(currentUser, String.format(CREATED_WAREHOUSE, warehouse.getName()));

    logService.saveLog(log);

    return modelMapper.map(warehouse, WarehouseServiceModel.class);
  }

  @Override
  public List<WarehouseServiceModel> findAllWarehouses() {

    return warehouseRepository.findAll()
            .stream()
            .map(warehouse -> modelMapper.map(warehouse, WarehouseServiceModel.class))
            .map(this::updateCurrCapacity)
            .collect(Collectors.toList());
  }

  @Override
  public Long getWarehousesCount() {
    return warehouseRepository.count();
  }

  @Override
  public WarehouseServiceModel findWarehouseById(String id) {
    return warehouseRepository.findById(id)
            .map(warehouse -> modelMapper.map(warehouse, WarehouseServiceModel.class))
            .orElseThrow(() -> new WarehouseNotFoundException(WAREHOUSE_NOT_FOUND));
  }

  @Override
  public WarehouseServiceModel editWarehouse(String warehouseId,
                                             WarehouseServiceModel updateModel,
                                             String currentUser) {

    WarehouseServiceModel serviceModel = findWarehouseById(warehouseId);

    serviceModel.setName(updateModel.getName());
    serviceModel.setMaxCapacity(updateModel.getMaxCapacity());

    Warehouse warehouse = modelMapper.map(serviceModel, Warehouse.class);

    warehouseRepository.saveAndFlush(warehouse);

    LogServiceModel log = createLog(currentUser, String.format(EDITED_WAREHOUSE, warehouse.getName()));
    logService.saveLog(log);

    return modelMapper.map(warehouse, WarehouseServiceModel.class);
  }

  @Override
  public void deleteWarehouse(String id, String currentUser) {
    Warehouse warehouse = warehouseRepository.findById(id)
            .orElseThrow(() -> new WarehouseNotFoundException(WAREHOUSE_NOT_FOUND));

    warehouseRepository.delete(warehouse);

    LogServiceModel log = createLog(currentUser, String.format(DELETED_WAREHOUSE, warehouse.getName()));

    logService.saveLog(log);
  }

  @Override
  public void emptyWarehouse(String id, String currentUser) {

    if (warehouseRepository.count() == 1) {
      throw new NotPossibleToEmptyWarehouseException(NOT_POSSIBLE_TO_EMPTY);
    }

    Warehouse warehouse = warehouseRepository.findById(id)
            .orElseThrow(() -> new WarehouseNotFoundException(WAREHOUSE_NOT_FOUND));

    Warehouse emptiestWarehouse = warehouseRepository.findFirstByOrderByCurrCapacityAsc();

    boolean isPossibleToMoveStock = isPossibleToMoveStock(
            warehouse.getCurrCapacity(),
            emptiestWarehouse.getCurrCapacity(),
            emptiestWarehouse.getMaxCapacity());

    if (isPossibleToMoveStock) {

      warehouse.getBatches()
              .forEach(flowersBatch -> flowersBatch.setWarehouse(emptiestWarehouse));
      emptiestWarehouse.getBatches().addAll(warehouse.getBatches());
      warehouse.getBatches().clear();

      updateCurrCapacity(modelMapper.map(warehouse, WarehouseServiceModel.class));
      updateCurrCapacity(modelMapper.map(emptiestWarehouse, WarehouseServiceModel.class));

    } else {
      throw new NotPossibleToEmptyWarehouseException(NOT_POSSIBLE_TO_EMPTY);
    }

    LogServiceModel log =
            createLog(currentUser,
                    String.format(MOVE_ALL_BATCHES, warehouse.getName(), emptiestWarehouse.getName()));

    logService.saveLog(log);
  }

  @Override
  public WarehouseServiceModel updateCurrCapacity(WarehouseServiceModel serviceModel) {

    int currCapacity = serviceModel.getBatches()
            .stream()
            .mapToInt(FlowersBatchServiceModel::getTrays)
            .sum();

    serviceModel.setCurrCapacity(currCapacity);

    Warehouse warehouse = modelMapper.map(serviceModel, Warehouse.class);

    warehouseRepository.saveAndFlush(warehouse);

    return modelMapper.map(warehouse, WarehouseServiceModel.class);
  }

  public boolean isPossibleToMoveStock(int firstWarehouseCurrCapacity,
                                       int secondWarehouseCurrCapacity,
                                       int secondWarehouseMaxCapacity) {
    return firstWarehouseCurrCapacity + secondWarehouseCurrCapacity <= secondWarehouseMaxCapacity;
  }


  public Double getRandomNumber() {

    return (Math.random()
            * ((MAX_WAREHOUSE_TEMPERATURE - MIN_WAREHOUSE_TEMPERATURE) + 1))
            + MIN_WAREHOUSE_TEMPERATURE;
  }

  private LogServiceModel createLog(String username, String description) {

    LogServiceModel log = new LogServiceModel();
    log.setCreatedOn(NOW);
    log.setUsername(username);
    log.setDescription(description);

    return log;
  }
}
