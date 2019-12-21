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

  private StringWriter writer = new StringWriter();

  public String convertToCsv(List<LapTimeDTO> results) throws IOException {
    String csvResult;
    StatefulBeanToCsv<LapTimeDTO> beanToCsv = getBeanToCsv();
    try {
      beanToCsv.write(results);
      csvResult = this.writer.toString();
      this.writer.close();
    } catch (CsvRequiredFieldEmptyException e) {
      LOGGER.error("Unable to generate lap times CSV (required field error)", e);
      throw new IOException(e);
    } catch (CsvDataTypeMismatchException e) {
      LOGGER.error("Unable to generate lap times CSV (data type error)", e);
      throw new IOException(e);
    }
    return csvResult;
  }

  StatefulBeanToCsv<LapTimeDTO> getBeanToCsv() {
    // StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder<LapTimeDTO>(writer).withMappingStrategy(new LapTimeMappingStrategy()).build();
    return new StatefulBeanToCsvBuilder<LapTimeDTO>(this.writer).build();
  }
}
