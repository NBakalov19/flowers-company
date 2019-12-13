package org.nbakalov.flowerscompany.services.services.implementations;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.FlowersBatch;
import org.nbakalov.flowerscompany.data.models.entities.Variety;
import org.nbakalov.flowerscompany.data.models.models.flowers.MoveBatchModel;
import org.nbakalov.flowerscompany.data.repositories.FlowersBatchRepository;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;
import org.nbakalov.flowerscompany.services.models.WarehouseServiceModel;
import org.nbakalov.flowerscompany.services.services.FlowersBatchService;
import org.nbakalov.flowerscompany.services.services.WarehouseService;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nbakalov.flowerscompany.constants.FlowersBatchConstants.TODAY;

@Service
@AllArgsConstructor
public class FlowersBatchServiceImpl implements FlowersBatchService {

  private final FlowersBatchRepository flowersBatchRepository;
  private final WarehouseService warehouseService;
  private final ModelMapper modelMapper;

  @Override
  public FlowersBatchServiceModel registerBatch(FlowersBatchServiceModel flowersBatchServiceModel) {

    flowersBatchServiceModel.setDatePicked(TODAY);

    WarehouseServiceModel warehouseServiceModel = flowersBatchServiceModel.getWarehouse();

    hasRoomInWarehouse(warehouseServiceModel, flowersBatchServiceModel);

    FlowersBatch flowersBatch =
            modelMapper.map(flowersBatchServiceModel, FlowersBatch.class);

    flowersBatchRepository.saveAndFlush(flowersBatch);

    return modelMapper.map(flowersBatch, FlowersBatchServiceModel.class);
  }

  @Override
  public List<FlowersBatchServiceModel> findAllBatchesRegisteredToday() {
    return flowersBatchRepository.findAllByDatePicked(TODAY)
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
  public FlowersBatchServiceModel findBatchById(String id) {

    return flowersBatchRepository.findById(id)
            .map(flowersBatch -> modelMapper.map(flowersBatch, FlowersBatchServiceModel.class))
            .orElseThrow(() -> new NoResultException("Flower batch not found."));
  }

  @Override
  public FlowersBatchServiceModel editFlowerBatch(String id, FlowersBatchServiceModel updateModel) {

    FlowersBatchServiceModel serviceModel =
            flowersBatchRepository.findById(id)
                    .map(flowersBatch -> modelMapper.map(flowersBatch, FlowersBatchServiceModel.class))
                    .orElseThrow(() -> new NoResultException("Flower batch not found."));

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
  public void moveBatch(String id, MoveBatchModel model) {

    FlowersBatchServiceModel flowersBatchServiceModel = flowersBatchRepository.findById(id)
            .map(flowersBatch -> modelMapper.map(flowersBatch, FlowersBatchServiceModel.class))
            .orElseThrow(() -> new NoResultException("Flower batch not found."));

    WarehouseServiceModel oldWarehouse =
            warehouseService.findWarehouseById(flowersBatchServiceModel.getWarehouse().getId());

    WarehouseServiceModel newWarehouse =
            warehouseService.findWarehouseById(model.warehouse);

    if (flowersBatchServiceModel.getTrays() + newWarehouse.getCurrCapacity() >= newWarehouse.getMaxCapacity()) {
      throw new IllegalArgumentException(
              String.format("No possible to register batch in %s warehouse", newWarehouse.getName()));
    }

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

    FlowersBatch flowersBatch = flowersBatchRepository.findById(id)
            .orElseThrow(() -> new NoResultException("Flower batch not found."));

    flowersBatchRepository.deleteById(id);
  }

  private void hasRoomInWarehouse(WarehouseServiceModel warehouseServiceModel,
                                  FlowersBatchServiceModel flowersBatchServiceModel) {

    int currCapacity = warehouseServiceModel.getCurrCapacity();

    if (currCapacity + flowersBatchServiceModel.getTrays() > warehouseServiceModel.getMaxCapacity()) {
      throw new IllegalArgumentException(
              String.format("No possible to register batch in %s warehouse", warehouseServiceModel.getName()));
    }
  }
}
