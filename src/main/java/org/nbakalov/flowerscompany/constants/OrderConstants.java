package org.nbakalov.flowerscompany.constants;

import java.time.LocalDateTime;

public final class OrderConstants {

  private OrderConstants() {
  }

  public static final Integer ORDER_MIN_QUANTITY = 1;

  public static final LocalDateTime TODAY = LocalDateTime.now();

  public static final String ORDER_NOT_FOUND = "Order not found.";

  public static final String ORDER_BAD_CREDENTIALS = "Order not created. Bad credentials.";

  public static final String ORDER_QUANTITY_CAN_NOT_BE_ZERO = "Order can`t be zero or negative";
}
