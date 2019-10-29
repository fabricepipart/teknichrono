package org.trd.app.teknichrono.util.csv;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.trd.app.teknichrono.model.dto.LapTimeDTO;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;


class TestCSVConverter {

  LapTimeDTO dto;
  StatefulBeanToCsv beanToCsv;

  @BeforeEach
  public void setUp() {
    dto = mock(LapTimeDTO.class);
    beanToCsv = mock(StatefulBeanToCsv.class);
  }

  @Test
  public void throwsIoExceptionInPlaceOfCsvRequiredFieldEmptyException() throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
    List<LapTimeDTO> list = Arrays.asList(dto);
    CSVConverter converter = new CSVConverterStub();
    doThrow(new CsvRequiredFieldEmptyException()).when(beanToCsv).write(anyList());
    Assertions.assertThrows(IOException.class, () -> {
      converter.convertToCsv(list);
    });
  }

  @Test
  public void throwsIoExceptionInPlaceOfCsvDataTypeMismatchException() throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
    List<LapTimeDTO> list = Arrays.asList(dto);
    CSVConverter converter = new CSVConverterStub();
    doThrow(new CsvDataTypeMismatchException()).when(beanToCsv).write(anyList());
    Assertions.assertThrows(IOException.class, () -> {
      converter.convertToCsv(list);
    });
  }

  private class CSVConverterStub extends CSVConverter {

    StatefulBeanToCsv<LapTimeDTO> getBeanToCsv() {
      return beanToCsv;
    }
  }
}