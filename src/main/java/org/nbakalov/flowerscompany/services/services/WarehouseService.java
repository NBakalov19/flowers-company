package org.nbakalov.flowerscompany.services.services;

import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;
import org.nbakalov.flowerscompany.services.models.WarehouseServiceModel;

import java.util.List;

public interface WarehouseService {

  WarehouseServiceModel createWarehouse(WarehouseServiceModel warehouseServiceModel);

  WarehouseServiceModel editWarehouse(String id, WarehouseServiceModel warehouseServiceModel);

  WarehouseServiceModel findWarehouseById(String id);

  WarehouseServiceModel findWarehouseByName(String name);

  WarehouseServiceModel updateCurrCapacity(WarehouseServiceModel warehouseServiceModel);

  List<WarehouseServiceModel> findAllWarehouses();

  void deleteWarehouse(String id);

  void emptyWarehouse(String id);
}

