package org.nbakalov.flowerscompany.services.services;

import org.nbakalov.flowerscompany.data.models.enums.Variety;
import org.nbakalov.flowerscompany.data.models.models.flowers.MoveBatchModel;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;

import java.util.List;

public interface FlowersBatchService {

  FlowersBatchServiceModel registerBatch(FlowersBatchServiceModel serviceModel,
                                         String currentUser);

  FlowersBatchServiceModel editFlowerBatch(String id,
                                           FlowersBatchServiceModel updateModel,
                                           String currentUser);

  FlowersBatchServiceModel findBatchById(String id);

  List<FlowersBatchServiceModel> findAllBatchesRegisteredToday();

  List<FlowersBatchServiceModel> findAllBatchesByVarietyAndBunchesPerTray(Variety variety, Integer bunchesPerTray);

  List<FlowersBatchServiceModel> findAllBatchesByBunchesPerTray(Integer bunchesPerTray);

  void moveBatch(String id, MoveBatchModel model, String currentUser);

  void deleteBatch(String id, String currentUser);

}
