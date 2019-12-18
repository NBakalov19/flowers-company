package org.nbakalov.flowerscompany.constants;

public final class WarehouseConstants {

  public static final int INITIAL_CURRENT_CAPACITY = 0;

  public static final double MIN_WAREHOUSE_TEMPERATURE = 0.1;

  public static final double MAX_WAREHOUSE_TEMPERATURE = 4.0;

  public static final String WAREHOUSE_NOT_FOUND = "Warehouse not found.";

  public static final String NOT_POSSIBLE_TO_EMPTY = "Not Possible To Empty Warehouse";

  public static final String WAREHOUSE_BAD_CREDENTIALS = "Warehouse not created. Bad credentials.";

  public static final String WAREHOUSE_ALLREADY_EXIST = "Warehouse allready exist";

  public static final String WAREHOUSE_CAN_NOT_BE_EMPTY = "Name can`t be empty.";

  public static final String WAREHOUSE_NAME_LENGTH_NOT_CORRECT =
          "Warehouse name must be between 4 and 20 symbols";

  public static final String WAREHOUSE_MAX_CAPACITY_NOT_CORRECT =
          "Warehouse max capacity must be between 2500 and 50000 trays";

  public static final Integer WAREHOUSE_NAME_MIN_LENGTH = 4;
  public static final Integer WAREHOUSE_NAME_MAX_LENGTH = 20;

  public static final Integer WAREHOUSE_MAX_CAPACITY_MIN_VALUE = 2500;
  public static final Integer WAREHOUSE_MAX_CAPACITY_MAX_VALUE = 50000;
}
