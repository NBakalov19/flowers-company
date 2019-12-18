package org.nbakalov.flowerscompany.services.services.implementations;

import lombok.AllArgsConstructor;
import org.nbakalov.flowerscompany.services.models.FlowersBatchServiceModel;
import org.nbakalov.flowerscompany.services.models.LogServiceModel;
import org.nbakalov.flowerscompany.services.services.FlowersBatchService;
import org.nbakalov.flowerscompany.services.services.LogService;
import org.nbakalov.flowerscompany.services.services.ScheduleService;
import org.nbakalov.flowerscompany.utils.FileUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.nbakalov.flowerscompany.constants.GlobalConstants.*;

@Service
@AllArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

  private static final String FOLDER_PATH = "C:\\Users\\Nikolay\\Desktop\\Batches\\";
  private static final String BATCHES_EXTENSION = "batches.txt";
  private static final String LOGS_EXTENSION = "logs.txt";

  private final LogService logService;
  private final FlowersBatchService flowersBatchService;
  private final FileUtils fileUtils;

  @Override
  @Scheduled(cron = "0 58 23 * * *")
  public void writeTodayLogs() throws IOException {
    List<LogServiceModel> allLogsFromToday =
            logService.findAllLogsFromToday();

    String logFromToday = getLogsFromToday(allLogsFromToday);

    String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

    String path = FOLDER_PATH + today + LOGS_EXTENSION;

    fileUtils.writeFile(logFromToday, path);
  }

  @Override
  @Scheduled(cron = "0 59 23 * * *")
  public void writeTodaysBatches() throws IOException {

    List<FlowersBatchServiceModel> allBatchesRegisteredToday =
            flowersBatchService.findAllBatchesRegisteredToday();

    String batchesFromToday = getTodaysBatchesAsString(allBatchesRegisteredToday);

    String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

    String path = FOLDER_PATH + today + BATCHES_EXTENSION;

    fileUtils.writeFile(batchesFromToday, path);

  }

  private String getTodaysBatchesAsString(List<FlowersBatchServiceModel> todaysBatches) {
    StringBuilder output = new StringBuilder();

    String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

    output.append(String.format("Flowers Batches from %s", today)).append(SEPARATOR);

    int index = 1;
    for (FlowersBatchServiceModel batch : todaysBatches) {

      String batchRegisteredTime = batch.getDatePicked().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm"));


      output.append(
              String.format(TODAY_FLOWERS_BATCHES_INFO_PATTERN,
                      index++,
                      batchRegisteredTime,
                      batch.getTeamSupervisor(),
                      batch.getFieldName(),
                      batch.getVariety().getVarietyName(),
                      batch.getTrays(),
                      batch.getBunchesPerTray(),
                      batch.getWarehouse().getName()))
              .append(SEPARATOR);
    }


    return output.toString().trim();
  }

  private String getLogsFromToday(List<LogServiceModel> todaysLogs) {
    StringBuilder output = new StringBuilder();

    String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

    output.append(String.format("Logs from %s", today)).append(SEPARATOR);

    int index = 1;
    for (LogServiceModel log : todaysLogs) {

      String logRegisteredTime = log.getCreatedOn().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm"));

      output.append(
              String.format(TODAY_LOG_INFO_PATTERN,
                      index++,
                      log,
                      log.getUsername(),
                      log.getDescription()))
              .append(SEPARATOR);
    }

    return output.toString().trim();
  }

}
