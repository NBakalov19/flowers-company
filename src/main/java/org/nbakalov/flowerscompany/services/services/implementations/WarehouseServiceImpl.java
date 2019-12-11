package org.nbakalov.flowerscompany.services.services.implementations;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.Warehouse;
import org.nbakalov.flowerscompany.data.repositories.WarehouseRepository;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;
import org.nbakalov.flowerscompany.services.models.WarehouseServiceModel;
import org.nbakalov.flowerscompany.services.services.WarehouseService;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nbakalov.flowerscompany.constants.WarehouseConstants.*;

@Service
@AllArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

  private final WarehouseRepository warehouseRepository;
  private final ModelMapper modelMapper;

  @Override
  public WarehouseServiceModel createWarehouse(WarehouseServiceModel warehouseServiceModel) {

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
  public WarehouseServiceModel findWarehouseById(String id) {
    return warehouseRepository.findById(id)
            .map(warehouse -> modelMapper.map(warehouse, WarehouseServiceModel.class))
            .orElseThrow(() -> new NoResultException("Warehouse not found."));

    //TODO EXCEPTION
  }

  @Override
  public WarehouseServiceModel editWarehouse(String id, WarehouseServiceModel warehouseServiceModel) {

    //TODO TEST

    Warehouse warehouse = warehouseRepository.findById(id)
            .orElseThrow(() -> new NoResultException("Warehouse not found."));

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
            .orElseThrow(() -> new NoResultException("Warehouse not found."));

    warehouseRepository.delete(warehouse);
  }

  @Override
  public void emptyWarehouse(String id) {

    //TODO TEST AFTER CREATE BATCHES

    if (warehouseRepository.count() == 1) {
      throw new IllegalArgumentException("Not Possible To Empty Warehouse");
      //TODO NotPossibleToEmptyWarehouseException
    }

    Warehouse warehouse = warehouseRepository.findById(id)
            .orElseThrow(() -> new NoResultException("Warehouse not found."));

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
      throw new IllegalArgumentException("Not Possible To Empty Warehouse.");
    }
  }

  @Override
  public WarehouseServiceModel findWarehouseByName(String name) {
    return warehouseRepository.findByName(name)
            .map(warehouse -> modelMapper.map(warehouse, WarehouseServiceModel.class))
            .orElseThrow(() -> new NoResultException("Warehouse not found."));

    //TODO EXCEPTION
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
