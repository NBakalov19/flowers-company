package org.nbakalov.flowerscompany.constants;

import org.nbakalov.flowerscompany.data.models.enums.Variety;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public final class GlobalConstants {

  public final static LocalDateTime TODAY = LocalDateTime.now();

  public static final LocalDateTime BEGIN_OF_DAY = LocalDate.now().atStartOfDay();

  public static final LocalDateTime END_OF_DAY = LocalDate.now().atTime(23, 59, 59);

  public static final String TODAY_FLOWERS_BATCHES_INFO_PATTERN = "%d %s %s %s %s %d %d %s";

  public static final String TODAY_LOG_INFO_PATTERN = "%d %s %s %s";

  public static final String SEPARATOR = System.lineSeparator();

  public static final List<Integer> VALID_BUNCHES_PER_TRAY_COUNT = List.of(10, 15, 16, 17, 18, 20, 25);

  public static final List<Variety> VALID_VARIETIES = Variety.stream().collect(Collectors.toList());
}
