package org.nbakalov.flowerscompany.services.services;

import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;

import java.util.List;

public interface FlowersBatchService {

  FlowersBatchServiceModel registerBatch(FlowersBatchServiceModel flowersBatchServiceModel);

  List<FlowersBatchServiceModel> findAllBatchesRegisteredToday();

  FlowersBatchServiceModel findBatchById(String id);

  FlowersBatchServiceModel editFlowerBatch(String id, FlowersBatchServiceModel updateModel);
}
