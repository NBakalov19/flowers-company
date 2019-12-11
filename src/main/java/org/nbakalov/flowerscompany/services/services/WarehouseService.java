package org.nbakalov.flowerscompany.services.services;

import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;
import org.nbakalov.flowerscompany.services.models.WarehouseServiceModel;

import java.util.List;

public interface WarehouseService {

  WarehouseServiceModel createWarehouse(WarehouseServiceModel warehouseServiceModel);

  List<WarehouseServiceModel> findAllWarehouses();

  WarehouseServiceModel findWarehouseById(String id);

  WarehouseServiceModel editWarehouse(String id, WarehouseServiceModel warehouseServiceModel);

  void deleteWarehouse(String id, WarehouseServiceModel warehouseServiceModel);

  void emptyWarehouse(String id);

  WarehouseServiceModel findWarehouseByName(String name);

  WarehouseServiceModel updateCurrCapacity(WarehouseServiceModel warehouseServiceModel);
}

