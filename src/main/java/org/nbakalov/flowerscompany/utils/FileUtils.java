package org.nbakalov.flowerscompany.utils;

import java.io.IOException;

public interface FileUtils {

  void writeFile(String content, String path) throws IOException;
}
