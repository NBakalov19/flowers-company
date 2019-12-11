package org.nbakalov.flowerscompany.services.services.implementations;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.FlowersBatch;
import org.nbakalov.flowerscompany.data.models.entities.Variety;
import org.nbakalov.flowerscompany.data.repositories.FlowersBatchRepository;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;
import org.nbakalov.flowerscompany.services.models.WarehouseServiceModel;
import org.nbakalov.flowerscompany.services.services.FlowersBatchService;
import org.nbakalov.flowerscompany.services.services.WarehouseService;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.util.List;
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

    flowersBatchServiceModel.setDatePicked(LocalDate.now());

    WarehouseServiceModel warehouseServiceModel = flowersBatchServiceModel.getWarehouse();

    hasRoomInWarehouse(warehouseServiceModel, flowersBatchServiceModel);

    FlowersBatch flowersBatch = flowersBatchRepository.saveAndFlush(
            modelMapper.map(flowersBatchServiceModel, FlowersBatch.class));

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


    serviceModel.setTeamSupervisor(
            updateSupervisior(updateModel.getTeamSupervisor(), serviceModel.getTeamSupervisor()));

    serviceModel.setFieldName(
            updateField(updateModel.getFieldName(), serviceModel.getFieldName()));

    serviceModel.setVariety(
            updateVariety(updateModel.getVariety(), serviceModel.getVariety()));

    serviceModel.setBunchesPerTray(
            updateBunches(updateModel.getBunchesPerTray(), serviceModel.getBunchesPerTray()));


    if (updateModel.getTrays() == 0) {
      throw new IllegalArgumentException("Trays count can`t be zero");
    }

    serviceModel.setTrays(updateModel.getTrays());

    hasRoomInWarehouse(serviceModel.getWarehouse(), serviceModel);

    FlowersBatch flowersBatch = flowersBatchRepository.saveAndFlush(
            modelMapper.map(serviceModel, FlowersBatch.class));

    return modelMapper.map(flowersBatch, FlowersBatchServiceModel.class);
  }

  private void hasRoomInWarehouse(WarehouseServiceModel warehouseServiceModel,
                                  FlowersBatchServiceModel flowersBatchServiceModel) {

    int currCapacity = warehouseServiceModel.getCurrCapacity();

    if (currCapacity + flowersBatchServiceModel.getTrays() > warehouseServiceModel.getMaxCapacity()) {
      throw new IllegalArgumentException(
              String.format("No possible to register batch in %s warehouse", warehouseServiceModel.getName()));
    }
  }

  private Integer updateBunches(Integer newBunchesPerTray, Integer currBunchesPerTray) {

    return currBunchesPerTray.equals(newBunchesPerTray)
            ? currBunchesPerTray
            : newBunchesPerTray;
  }

  private Variety updateVariety(Variety newVariety, Variety currVariety) {

    return currVariety.getVarietyName().equals(newVariety.getVarietyName())
            ? currVariety
            : newVariety;
  }

  private String updateSupervisior(String newSupervisor, String currSupervisor) {

    return currSupervisor.equals(newSupervisor)
            ? currSupervisor
            : newSupervisor;
  }

  private String updateField(String newFieldName, String currFieldName) {

    return currFieldName.equals(newFieldName)
            ? currFieldName
            : newFieldName;
  }
}
