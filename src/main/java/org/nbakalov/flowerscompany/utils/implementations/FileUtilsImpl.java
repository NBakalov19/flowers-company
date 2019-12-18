package org.nbakalov.flowerscompany.utils.implementations;

import org.nbakalov.flowerscompany.utils.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Component
public class FileUtilsImpl implements FileUtils {
  @Override
  public void writeFile(String content, String path) throws IOException {
    File file = new File(path);
    FileWriter writer = new FileWriter(file);
    writer.write(content);
    writer.close();
  }
}
