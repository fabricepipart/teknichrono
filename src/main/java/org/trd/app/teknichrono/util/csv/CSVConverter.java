package org.trd.app.teknichrono.util.csv;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.jboss.logging.Logger;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class CSVConverter {

  private static final Logger LOGGER = Logger.getLogger(CSVConverter.class);


  public static String convertToCsv(List<LapTimeDTO> results) throws IOException {
    String csvResult;
    StringWriter writer = new StringWriter();
    // TODO StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder<LapTimeDTO>(writer).withMappingStrategy(new LapTimeMappingStrategy()).build();
    StatefulBeanToCsv<LapTimeDTO> beanToCsv = new StatefulBeanToCsvBuilder<LapTimeDTO>(writer).build();
    try {
      beanToCsv.write(results);
      csvResult = writer.toString();
      writer.close();
    } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
      LOGGER.error("unable to generate lap times CSV", e);
      throw new IOException(e);
    }
    return csvResult;
  }
}
