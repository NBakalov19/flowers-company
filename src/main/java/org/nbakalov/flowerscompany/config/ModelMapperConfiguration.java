package org.nbakalov.flowerscompany.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.models.FlowersBatchUpdateModel;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;
import org.nbakalov.flowerscompany.services.models.WarehouseServiceModel;
import org.nbakalov.flowerscompany.web.models.api.TodayFlowersBatchApiModel;
import org.nbakalov.flowerscompany.web.models.view.FlowerBatchDeleteViewModel;
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

    modelMapper.createTypeMap(
            FlowersBatchServiceModel.class, TodayFlowersBatchApiModel.class)
            .addMappings(map ->
                    map.using(warehouseServiceModelStringConverter)
                            .map(
                                    FlowersBatchServiceModel::getWarehouse,
                                    TodayFlowersBatchApiModel::setWarehouse));

    modelMapper.createTypeMap(
            FlowersBatchServiceModel.class, FlowersBatchUpdateModel.class)
            .addMappings(map ->
                    map.using(warehouseServiceModelStringConverter)
                            .map(
                                    FlowersBatchServiceModel::getWarehouse,
                                    FlowersBatchUpdateModel::setWarehouse));

    modelMapper
            .createTypeMap(FlowersBatchServiceModel.class, FlowerBatchDeleteViewModel.class)
            .addMappings(map ->
                    map.using(warehouseServiceModelStringConverter)
                            .map(
                                    FlowersBatchServiceModel::getWarehouse,
                                    FlowerBatchDeleteViewModel::setWarehouse));
  }

  @Bean
  public ModelMapper modelMapper() {
    return modelMapper;
  }
}
