package org.nbakalov.flowerscompany.constants;

import java.time.LocalDateTime;

public final class LogConstants {

  public static final LocalDateTime NOW = LocalDateTime.now();

  // User
  public static final String REGISTERED_USER = "was successfully created.";
  public static final String EDITED_USER = "was successfully edited.";
  public static final String PROMOTED_OPERATOR = "was successfully promoted to operator by root user.";
  public static final String PROMOTED_ADMIN = "was successfully promoted to admin by root user.";

  //Warehouse
  public static final String CREATED_WAREHOUSE = "successfully create warehouse with name %s.";
  public static final String EDITED_WAREHOUSE = "successfully edit %s warehouse.";
  public static final String DELETED_WAREHOUSE = "successfully delete %s warehouse.";
  public static final String MOVE_ALL_BATCHES = "successfully move all batches from %s warehouse to %s warehouse.";

  //Flowers Batch
  public static final String CREATED_FLOWERS_BATCH = "successfully create flowers batch.";
  public static final String EDITED_FLOWERS_BATCH = "successfully edit flowers batch.";
  public static final String MOVED_FLOWERS_BATCH = "successfully move flowers batch from %s warehouse to %s warehouse.";
  public static final String DELETED_FLOWERS_BATCH = "successfully delete flowers batch.";

  //Order
  public static final String MADE_ORDER = "made order.";
  public static final String EDITED_ORDER = "edit order.";
  public static final String DELETE_ORDER = "delete order.";
  public static final String REVIEW_ORDER = "process order.";
}
