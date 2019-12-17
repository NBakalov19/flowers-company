package org.nbakalov.flowerscompany.services.services;

import org.nbakalov.flowerscompany.services.models.WarehouseServiceModel;

import java.util.List;

public interface WarehouseService {

  WarehouseServiceModel createWarehouse(WarehouseServiceModel serviceModel,
                                        String currentUser);

  WarehouseServiceModel editWarehouse(String warehouseId,
                                      WarehouseServiceModel serviceModel,
                                      String currentUser);

  WarehouseServiceModel findWarehouseById(String id);

  WarehouseServiceModel updateCurrCapacity(WarehouseServiceModel serviceModel);

  List<WarehouseServiceModel> findAllWarehouses();

  Long getWarehousesCount();

  void deleteWarehouse(String warehouseId, String currentUser);

  void emptyWarehouse(String warehouseId, String currentUser);
}