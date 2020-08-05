package org.nbakalov.flowerscompany.services.services.implementations;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.FlowersBatch;
import org.nbakalov.flowerscompany.data.models.enums.Variety;
import org.nbakalov.flowerscompany.data.models.models.flowers.MoveBatchModel;
import org.nbakalov.flowerscompany.data.repositories.FlowersBatchRepository;
import org.nbakalov.flowerscompany.errors.illegalservicemodels.IllegalFlowersBatchServiceModelException;
import org.nbakalov.flowerscompany.errors.notfound.FlowersBatchNotFoundException;
import org.nbakalov.flowerscompany.errors.unabled.NotPossibleToRegisterBatchException;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;
import org.nbakalov.flowerscompany.services.models.LogServiceModel;
import org.nbakalov.flowerscompany.services.models.WarehouseServiceModel;
import org.nbakalov.flowerscompany.services.services.FlowersBatchService;
import org.nbakalov.flowerscompany.services.services.LogService;
import org.nbakalov.flowerscompany.services.services.WarehouseService;
import org.nbakalov.flowerscompany.services.validators.FlowersBatchServiceModelValidatorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nbakalov.flowerscompany.constants.FlowersBatchConstants.*;
import static org.nbakalov.flowerscompany.constants.GlobalConstants.*;
import static org.nbakalov.flowerscompany.constants.LogConstants.*;

@Service
@AllArgsConstructor
public class FlowersBatchServiceImpl implements FlowersBatchService {

  private final FlowersBatchRepository flowersBatchRepository;
  private final FlowersBatchServiceModelValidatorService validatorService;
  private final WarehouseService warehouseService;
  private final LogService logService;
  private final ModelMapper modelMapper;

  @Override
  public FlowersBatchServiceModel registerBatch(FlowersBatchServiceModel serviceModel, String currentUser) {

    if (!validatorService.isValid(serviceModel)) {
      throw new IllegalFlowersBatchServiceModelException(FLOWERS_BATCH_BAD_CREDENTIALS);
    }

    serviceModel.setDatePicked(TODAY);

    WarehouseServiceModel warehouseServiceModel = serviceModel.getWarehouse();

    hasRoomInWarehouse(warehouseServiceModel, serviceModel);

    FlowersBatch flowersBatch =
            modelMapper.map(serviceModel, FlowersBatch.class);

    flowersBatchRepository.saveAndFlush(flowersBatch);

    LogServiceModel log = createLog(currentUser, CREATED_FLOWERS_BATCH);
    logService.saveLog(log);

    return modelMapper.map(flowersBatch, FlowersBatchServiceModel.class);
  }

  @Override
  public FlowersBatchServiceModel editFlowerBatch(String id,
                                                  FlowersBatchServiceModel updateModel,
                                                  String currentUser) {

    if (!validatorService.isValid(updateModel)) {
      throw new IllegalFlowersBatchServiceModelException(FLOWERS_BATCH_BAD_CREDENTIALS);
    }

    FlowersBatchServiceModel serviceModel = findBatchById(id);

    hasRoomInWarehouse(serviceModel.getWarehouse(), updateModel);

    serviceModel.setTeamSupervisor(updateModel.getTeamSupervisor());
    serviceModel.setFieldName(updateModel.getFieldName());
    serviceModel.setVariety(updateModel.getVariety());
    serviceModel.setTrays(updateModel.getTrays());
    serviceModel.setBunchesPerTray(updateModel.getBunchesPerTray());

    FlowersBatch flowersBatch = modelMapper.map(serviceModel, FlowersBatch.class);

    flowersBatchRepository.saveAndFlush(flowersBatch);

    LogServiceModel log = createLog(currentUser, EDITED_FLOWERS_BATCH);
    logService.saveLog(log);

    return modelMapper.map(flowersBatch, FlowersBatchServiceModel.class);
  }

  @Override
  public FlowersBatchServiceModel findBatchById(String id) {

    return flowersBatchRepository.findById(id)
            .map(flowersBatch -> modelMapper.map(flowersBatch, FlowersBatchServiceModel.class))
            .orElseThrow(() -> new FlowersBatchNotFoundException(FLOWERS_BATCH_NOT_FOUND));
  }


  @Override
  public List<FlowersBatchServiceModel> findAllBatchesRegisteredToday() {

    return flowersBatchRepository.findAllByDatePickedBetweenOrderByDatePickedAsc(BEGIN_OF_DAY, END_OF_DAY)
            .stream()
            .map(batch -> modelMapper.map(batch, FlowersBatchServiceModel.class))
            .collect(Collectors.toList());
  }

  @Override
  public List<FlowersBatchServiceModel> findAllBatchesByVarietyAndBunchesPerTray(Variety variety, Integer bunchesPerTray) {
    return flowersBatchRepository
            .findAllByVarietyAndBunchesPerTrayOrderByTraysDesc(variety, bunchesPerTray)
            .stream()
            .map(batch -> modelMapper.map(batch, FlowersBatchServiceModel.class))
            .collect(Collectors.toList());
  }

  @Override
  public List<FlowersBatchServiceModel> findAllBatchesByBunchesPerTray(Integer bunchesPerTray) {
    return flowersBatchRepository.findAllByBunchesPerTrayOrderByTrays(bunchesPerTray)
            .stream()
            .map(batch -> modelMapper.map(batch, FlowersBatchServiceModel.class))
            .collect(Collectors.toList());
  }

  @Override
  public void moveBatch(String id, MoveBatchModel model, String currentUser) {

    FlowersBatchServiceModel flowersBatchServiceModel = findBatchById(id);

    WarehouseServiceModel oldWarehouse =
            warehouseService.findWarehouseById(flowersBatchServiceModel.getWarehouse().getId());

    WarehouseServiceModel newWarehouse =
            warehouseService.findWarehouseById(model.warehouse);

    hasRoomInWarehouse(newWarehouse, flowersBatchServiceModel);

    flowersBatchServiceModel.setWarehouse(newWarehouse);

    Set<FlowersBatchServiceModel> oldWarehouseNewBatches = oldWarehouse.getBatches()
            .stream()
            .filter(fb -> !fb.getId().equals(flowersBatchServiceModel.getId()))
            .collect(Collectors.toSet());

    oldWarehouse.setBatches(oldWarehouseNewBatches);
    newWarehouse.getBatches().add(flowersBatchServiceModel);

    warehouseService.updateCurrCapacity(oldWarehouse);
    warehouseService.updateCurrCapacity(newWarehouse);

    LogServiceModel log = createLog(currentUser,
            String.format(MOVED_FLOWERS_BATCH, oldWarehouse.getName(), newWarehouse.getName()));

    logService.saveLog(log);
  }

  @Override
  public void deleteBatch(String id, String currentUser) {

    flowersBatchRepository.deleteById(id);

    LogServiceModel log = createLog(currentUser, DELETED_FLOWERS_BATCH);
    logService.saveLog(log);
  }

  public void hasRoomInWarehouse(WarehouseServiceModel warehouseServiceModel,
                                 FlowersBatchServiceModel flowersBatchServiceModel) {

    int currCapacity = warehouseServiceModel.getCurrCapacity();

    if (currCapacity + flowersBatchServiceModel.getTrays() > warehouseServiceModel.getMaxCapacity()) {
      throw new NotPossibleToRegisterBatchException(
              String.format(NOT_POSSIBLE_TO_REGISTER, warehouseServiceModel.getName()));
    }
  }

  private LogServiceModel createLog(String username, String description) {

    LogServiceModel log = new LogServiceModel();
    log.setCreatedOn(NOW);
    log.setUsername(username);
    log.setDescription(description);

    return log;
  }
}
