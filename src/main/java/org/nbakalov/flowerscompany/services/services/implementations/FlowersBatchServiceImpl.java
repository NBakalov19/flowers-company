package org.nbakalov.flowerscompany.services.services.implementations;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.FlowersBatch;
import org.nbakalov.flowerscompany.data.models.entities.Variety;
import org.nbakalov.flowerscompany.data.models.models.flowers.MoveBatchModel;
import org.nbakalov.flowerscompany.data.repositories.FlowersBatchRepository;
import org.nbakalov.flowerscompany.errors.illegalservicemodels.IllegalFlowersBatchServiceModelException;
import org.nbakalov.flowerscompany.errors.notfound.FlowersBatchNotFoundException;
import org.nbakalov.flowerscompany.errors.unabled.NotPossibleToRegisterBatchException;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;
import org.nbakalov.flowerscompany.services.models.WarehouseServiceModel;
import org.nbakalov.flowerscompany.services.services.FlowersBatchService;
import org.nbakalov.flowerscompany.services.services.WarehouseService;
import org.nbakalov.flowerscompany.services.validations.FlowersBatchServiceModelValidatorService;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nbakalov.flowerscompany.constants.FlowersBatchConstants.*;

@Service
@AllArgsConstructor
public class FlowersBatchServiceImpl implements FlowersBatchService {

  private final FlowersBatchRepository flowersBatchRepository;
  private final FlowersBatchServiceModelValidatorService validatorService;
  private final WarehouseService warehouseService;
  private final ModelMapper modelMapper;

  @Override
  public FlowersBatchServiceModel registerBatch(FlowersBatchServiceModel flowersBatchServiceModel) {

    if (!validatorService.isValid(flowersBatchServiceModel)) {
      throw new IllegalFlowersBatchServiceModelException(FLOWERS_BATCH_BAD_CREDENTIALS);
    }

    flowersBatchServiceModel.setDatePicked(TODAY);

    WarehouseServiceModel warehouseServiceModel = flowersBatchServiceModel.getWarehouse();

    hasRoomInWarehouse(warehouseServiceModel, flowersBatchServiceModel);

    FlowersBatch flowersBatch =
            modelMapper.map(flowersBatchServiceModel, FlowersBatch.class);

    flowersBatchRepository.saveAndFlush(flowersBatch);

    return modelMapper.map(flowersBatch, FlowersBatchServiceModel.class);
  }

  @Override
  public FlowersBatchServiceModel editFlowerBatch(String id, FlowersBatchServiceModel updateModel) {

    FlowersBatchServiceModel serviceModel =
            flowersBatchRepository.findById(id)
                    .map(flowersBatch -> modelMapper.map(flowersBatch, FlowersBatchServiceModel.class))
                    .orElseThrow(() -> new FlowersBatchNotFoundException(FLOWERS_BATCH_NOT_FOUND));

    hasRoomInWarehouse(serviceModel.getWarehouse(), updateModel);

    serviceModel.setTeamSupervisor(updateModel.getTeamSupervisor());
    serviceModel.setFieldName(updateModel.getFieldName());
    serviceModel.setVariety(updateModel.getVariety());
    serviceModel.setTrays(updateModel.getTrays());
    serviceModel.setBunchesPerTray(updateModel.getBunchesPerTray());

    FlowersBatch flowersBatch = flowersBatchRepository.saveAndFlush(
            modelMapper.map(serviceModel, FlowersBatch.class));

    return modelMapper.map(flowersBatch, FlowersBatchServiceModel.class);
  }

  @Override
  public FlowersBatchServiceModel findBatchById(String id) {

    return flowersBatchRepository.findById(id)
            .map(flowersBatch -> modelMapper.map(flowersBatch, FlowersBatchServiceModel.class))
            .orElseThrow(() -> new NoResultException(FLOWERS_BATCH_NOT_FOUND));
  }


  @Override
  public List<FlowersBatchServiceModel> findAllBatchesRegisteredToday() {

    LocalDate today = LocalDate.now();
    LocalDateTime beginOfDay = today.atStartOfDay();
    LocalDateTime endOfDay = today.atTime(23, 59, 59);

    return flowersBatchRepository.findAllByDatePickedBetweenOrderByDatePicked(beginOfDay, endOfDay)
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
  public void moveBatch(String id, MoveBatchModel model) {

    FlowersBatchServiceModel flowersBatchServiceModel = flowersBatchRepository.findById(id)
            .map(flowersBatch -> modelMapper.map(flowersBatch, FlowersBatchServiceModel.class))
            .orElseThrow(() -> new FlowersBatchNotFoundException(FLOWERS_BATCH_NOT_FOUND));

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

  }

  @Override
  public void deleteBatch(String id) {

    flowersBatchRepository.findById(id)
            .orElseThrow(() -> new FlowersBatchNotFoundException(FLOWERS_BATCH_NOT_FOUND));

    flowersBatchRepository.deleteById(id);
  }

  private void hasRoomInWarehouse(WarehouseServiceModel warehouseServiceModel,
                                  FlowersBatchServiceModel flowersBatchServiceModel) {

    int currCapacity = warehouseServiceModel.getCurrCapacity();

    if (currCapacity + flowersBatchServiceModel.getTrays() > warehouseServiceModel.getMaxCapacity()) {
      throw new NotPossibleToRegisterBatchException(
              String.format(NOT_POSSIBLE_TO_REGISTER, warehouseServiceModel.getName()));
    }
  }
}
