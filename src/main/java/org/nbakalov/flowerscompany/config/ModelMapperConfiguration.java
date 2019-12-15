package org.nbakalov.flowerscompany.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;
import org.nbakalov.flowerscompany.services.models.OrderServiceModel;
import org.nbakalov.flowerscompany.services.models.UserServiceModel;
import org.nbakalov.flowerscompany.services.models.WarehouseServiceModel;
import org.nbakalov.flowerscompany.web.models.view.order.OrderCancelViewModel;
import org.nbakalov.flowerscompany.web.models.view.order.OrderViewModel;
import org.nbakalov.flowerscompany.web.models.api.TodayFlowersBatchApiModel;
import org.nbakalov.flowerscompany.web.models.view.flowerBatch.FlowerBatchDeleteViewModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfiguration {

  private static ModelMapper modelMapper;

  static {
    modelMapper = new ModelMapper();
    initModelMapper(modelMapper);
  }

  private static void initModelMapper(ModelMapper modelMapper) {

    Converter<WarehouseServiceModel, String> warehouseServiceModelStringConverter =
            context -> String.valueOf(context.getSource().getName());

    Converter<UserServiceModel, String> userServiceModelStringConverter =
            context -> String.valueOf(context.getSource().getUsername());

    modelMapper.createTypeMap(
            FlowersBatchServiceModel.class, TodayFlowersBatchApiModel.class)
            .addMappings(map ->
                    map.using(warehouseServiceModelStringConverter)
                            .map(
                                    FlowersBatchServiceModel::getWarehouse,
                                    TodayFlowersBatchApiModel::setWarehouse));

    modelMapper
            .createTypeMap(FlowersBatchServiceModel.class, FlowerBatchDeleteViewModel.class)
            .addMappings(map ->
                    map.using(warehouseServiceModelStringConverter)
                            .map(
                                    FlowersBatchServiceModel::getWarehouse,
                                    FlowerBatchDeleteViewModel::setWarehouse));

    modelMapper
            .createTypeMap(OrderServiceModel.class, OrderViewModel.class)
            .addMappings(map ->
                    map.using(userServiceModelStringConverter)
                            .map(
                                    OrderServiceModel::getCustomer,
                                    OrderViewModel::setCustomer
                            ));

    modelMapper
            .createTypeMap(OrderServiceModel.class, OrderCancelViewModel.class)
            .addMappings(map ->
                    map.using(userServiceModelStringConverter)
                            .map(
                                    OrderServiceModel::getCustomer,
                                    OrderCancelViewModel::setCustomer
                            ));
  }

  @Bean
  public ModelMapper modelMapper() {
    return modelMapper;
  }
}
