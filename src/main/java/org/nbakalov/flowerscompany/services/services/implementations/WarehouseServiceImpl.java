package org.nbakalov.flowerscompany.services.services.implementations;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.Warehouse;
import org.nbakalov.flowerscompany.data.repositories.WarehouseRepository;
import org.nbakalov.flowerscompany.errors.unabled.NotPossibleToEmptyWarehouseException;
import org.nbakalov.flowerscompany.errors.dublicates.WarehouseAllreadyExistException;
import org.nbakalov.flowerscompany.errors.illegalservicemodels.IllegalWarehouseServiceModelException;
import org.nbakalov.flowerscompany.errors.notfound.WarehouseNotFoundException;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;
import org.nbakalov.flowerscompany.services.models.WarehouseServiceModel;
import org.nbakalov.flowerscompany.services.services.WarehouseService;
import org.nbakalov.flowerscompany.services.validations.WarehouseServiceModelValidatorService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nbakalov.flowerscompany.constants.WarehouseConstants.*;

@Service
@AllArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

  private final WarehouseRepository warehouseRepository;
  private final WarehouseServiceModelValidatorService validatorService;
  private final ModelMapper modelMapper;

  @Override
  public WarehouseServiceModel createWarehouse(WarehouseServiceModel warehouseServiceModel) {

    if (!validatorService.isValid(warehouseServiceModel)) {
      throw new IllegalWarehouseServiceModelException(WAREHOUSE_BAD_CREDENTIALS);
    }

    if (warehouseRepository.findByName(warehouseServiceModel.getName()).isPresent()) {
      throw new WarehouseAllreadyExistException(WAREHOUSE_ALLREADY_EXIST);
    }

    warehouseServiceModel.setTemperature(getRandomNumber());
    warehouseServiceModel.setBatches(new LinkedHashSet<>());
    warehouseServiceModel.setCurrCapacity(INITIAL_CURRENT_CAPACITY);

    Warehouse warehouse = modelMapper.map(warehouseServiceModel, Warehouse.class);
    warehouseRepository.saveAndFlush(warehouse);

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

    //TODO EXCEPTION
  }

  @Override
  public WarehouseServiceModel editWarehouse(String id, WarehouseServiceModel warehouseServiceModel) {

    Warehouse warehouse = warehouseRepository.findById(id)
            .orElseThrow(() -> new WarehouseNotFoundException(WAREHOUSE_NOT_FOUND));

    Set<FlowersBatchServiceModel> batches = warehouse.getBatches()
            .stream()
            .map(flowersBatch -> modelMapper.map(flowersBatch, FlowersBatchServiceModel.class))
            .collect(Collectors.toSet());

    warehouseServiceModel.setBatches(batches);

    return modelMapper.map(warehouseRepository.saveAndFlush(warehouse), WarehouseServiceModel.class);
  }

  @Override
  public void deleteWarehouse(String id) {
    Warehouse warehouse = warehouseRepository.findById(id)
            .orElseThrow(() -> new WarehouseNotFoundException(WAREHOUSE_NOT_FOUND));

    warehouseRepository.delete(warehouse);
  }

  @Override
  public void emptyWarehouse(String id) {

    if (warehouseRepository.count() == 1) {
      throw new IllegalArgumentException(NOT_POSSIBLE_TO_EMPTY);
      //TODO NotPossibleToEmptyWarehouseException
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
  }

  @Override
  public WarehouseServiceModel updateCurrCapacity(WarehouseServiceModel warehouseServiceModel) {

    int currCapacity = warehouseServiceModel.getBatches()
            .stream()
            .mapToInt(FlowersBatchServiceModel::getTrays)
            .sum();

    warehouseServiceModel.setCurrCapacity(currCapacity);

    Warehouse warehouse = modelMapper.map(warehouseServiceModel, Warehouse.class);

    return modelMapper.map(warehouseRepository.saveAndFlush(warehouse), WarehouseServiceModel.class);
  }

  private boolean isPossibleToMoveStock(int firstWarehouseCurrCapacity,
                                        int secondWarehouseCurrCapacity,
                                        int secondWarehouseMaxCapacity) {
    return firstWarehouseCurrCapacity + secondWarehouseCurrCapacity <= secondWarehouseMaxCapacity;
  }


  private Double getRandomNumber() {

    return (Math.random()
            * ((MAX_WAREHOUSE_TEMPERATURE - MIN_WAREHOUSE_TEMPERATURE) + 1))
            + MIN_WAREHOUSE_TEMPERATURE;
  }
}
