package org.nbakalov.flowerscompany.services.services;

import org.nbakalov.flowerscompany.data.models.entities.Variety;
import org.nbakalov.flowerscompany.data.models.models.flowers.MoveBatchModel;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;

import java.util.List;

public interface FlowersBatchService {

  FlowersBatchServiceModel findBatchById(String id);

  FlowersBatchServiceModel registerBatch(FlowersBatchServiceModel flowersBatchServiceModel);

  FlowersBatchServiceModel editFlowerBatch(String id, FlowersBatchServiceModel updateModel);

  List<FlowersBatchServiceModel> findAllBatchesRegisteredToday();

  List<FlowersBatchServiceModel> findAllBatchesByVarietyAndBunchesPerTray(Variety variety, Integer bunchesPerTray);

  List<FlowersBatchServiceModel> findAllBatchesByBunchesPerTray(Integer bunchesPerTray);

  void moveBatch(String id, MoveBatchModel model);

  void deleteBatch(String id);

//  void deleteEmptyBatches();
}
