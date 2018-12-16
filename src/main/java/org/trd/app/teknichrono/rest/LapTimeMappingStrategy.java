package org.trd.app.teknichrono.rest;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.BeanField;
import com.opencsv.bean.MappingStrategy;
import com.opencsv.exceptions.CsvBadConverterException;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.trd.app.teknichrono.rest.dto.LapTimeDTO;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

public class LapTimeMappingStrategy implements MappingStrategy<LapTimeDTO> {


  /**
   * Gets the property descriptor for a given column position.
   *
   * @param col The column to find the description for
   * @return The property descriptor for the column position or null if one
   * could not be found.
   * @deprecated Introspection will be replaced with reflection in version 5.0
   */
  @Override
  public PropertyDescriptor findDescriptor(int col) {
    return null;
  }

  /**
   * Gets the field for a given column position.
   *
   * @param col The column to find the field for
   * @return BeanField containing the field for a given column position, or
   * null if one could not be found
   * @throws CsvBadConverterException If a custom converter for a field cannot
   *                                  be initialized
   * @deprecated Simply don't use this. It is meant for use internal to an
   * implementation of this interface
   */
  @Override
  public BeanField<LapTimeDTO> findField(int col) throws CsvBadConverterException {
    return null;
  }

  /**
   * Finds and returns the highest index in this mapping.
   * This is especially important for writing, since position-based mapping
   * can ignore some columns that must be included in the output anyway.
   * {@link #findField(int) } will return null for these columns, so we need
   * a way to know when to stop writing new columns.
   *
   * @return The highest index in the mapping. If there are no columns in the
   * mapping, returns -1.
   * @since 3.9
   * @deprecated Simply don't use this. It is meant for use internal to an
   * implementation of this interface
   */
  @Override
  public int findMaxFieldIndex() {
    return 0;
  }

  /**
   * Implementation will return a bean of the type of object you are mapping.
   *
   * @return A new instance of the class being mapped.
   * @throws InstantiationException Thrown on error creating object.
   * @throws IllegalAccessException Thrown on error creating object.
   * @deprecated Simply don't use this. It is meant for use internal to an
   * implementation of this interface
   */
  @Override
  public LapTimeDTO createBean() throws InstantiationException, IllegalAccessException {
    return null;
  }

  /**
   * Implementation of this method can grab the header line before parsing
   * begins to use to map columns to bean properties.
   *
   * @param reader The CSVReader to use for header parsing
   * @throws IOException                    If parsing fails
   * @throws CsvRequiredFieldEmptyException If a field is required, but the
   *                                        header or column position for the field is not present in the input
   */
  @Override
  public void captureHeader(CSVReader reader) throws IOException, CsvRequiredFieldEmptyException {

  }

  /**
   * Implementations of this method must return an array of column headers
   * based on the contents of the mapping strategy.
   * If no header can or should be generated, an array of zero length must
   * be returned, and not null.
   *
   * @param bean One fully populated bean from which the header can be derived.
   *             This is important in the face of joining and splitting. If we have a
   *             MultiValuedMap as a field that is the target for a join on reading, that
   *             same field must be split into multiple columns on writing. Since the
   *             joining is done via regular expressions, it is impossible for opencsv
   *             to know what the column names are supposed to be on writing unless this
   *             bean includes a fully populated map.
   * @return An array of column names for a header. This may be an empty array
   * if no header should be written, but it must not be {@code null}.
   * @throws CsvRequiredFieldEmptyException If a required header is missing
   *                                        while attempting to write. Since every other header is hard-wired
   *                                        through the bean fields and their associated annotations, this can only
   *                                        happen with multi-valued fields.
   * @since 3.9
   */
  @Override
  public String[] generateHeader(LapTimeDTO bean) throws CsvRequiredFieldEmptyException {
    return new String[0];
  }

  /**
   * Gets the column index that corresponds to a specific column name.
   * <p>If the CSV file doesn't have a header row, this method will always return
   * null. If the same column name is mapped to more than one column index,
   * this method returns one of the indices without any guarantee as to which
   * one.</p>
   * <p>Inside of opencsv itself this method is only used for testing.</p>
   *
   * @param name The column name
   * @return The column index, or null if the name doesn't exist
   * @deprecated There is no replacement for this method, since we are not
   * aware of a use for it. Let us know if you have a use case.
   */
  @Override
  public Integer getColumnIndex(String name) {
    return null;
  }

  /**
   * Determines whether the mapping strategy is driven by annotations.
   *
   * @return Whether the mapping strategy is driven by annotations
   */
  @Override
  public boolean isAnnotationDriven() {
    return false;
  }

  /**
   * Takes a line of input from a CSV file and creates a bean out of it.
   *
   * @param line A line of input returned from {@link CSVReader}
   * @return A bean containing the converted information from the input
   * @throws InstantiationException          Generally, if some part of the bean cannot
   *                                         be accessed and used as needed
   * @throws IllegalAccessException          Generally, if some part of the bean cannot
   *                                         be accessed and used as needed
   * @throws IntrospectionException          Generally, if some part of the bean cannot
   *                                         be accessed and used as needed
   * @throws InvocationTargetException       Generally, if some part of the bean cannot
   *                                         be accessed and used as needed
   * @throws CsvRequiredFieldEmptyException  If the input for a field defined
   *                                         as required is empty
   * @throws CsvDataTypeMismatchException    If conversion of the input to a
   *                                         field type fails
   * @throws CsvConstraintViolationException If the value provided for a field
   *                                         would in some way compromise the logical integrity of the data as a
   *                                         whole
   * @since 4.2
   */
  @Override
  public LapTimeDTO populateNewBean(String[] line) throws InstantiationException, IllegalAccessException, IntrospectionException, InvocationTargetException, CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, CsvConstraintViolationException {
    return null;
  }

  /**
   * Takes a line of input from a CSV file and creates a bean out of it.
   * This method does the same thing as
   * {@link #populateNewBean(String[])}, except that it uses
   * introspection instead of reflection to preserve backward compatibility
   * with non-annotated bean usage.
   *
   * @param line A line of input returned from {@link CSVReader}
   * @return A bean containing the converted information from the input
   * @throws InstantiationException         Generally, if some part of the bean cannot
   *                                        be accessed and used as needed
   * @throws IllegalAccessException         Generally, if some part of the bean cannot
   *                                        be accessed and used as needed
   * @throws IntrospectionException         Generally, if some part of the bean cannot
   *                                        be accessed and used as needed
   * @throws InvocationTargetException      Generally, if some part of the bean cannot
   *                                        be accessed and used as needed
   * @throws CsvRequiredFieldEmptyException If the input for a field defined
   *                                        as required is empty
   * @see #populateNewBean(String[])
   * @since 4.2
   * @deprecated Introspection is slated to be removed in version 5.0. This
   * method was added to preserve existing functionality until then.
   */
  @Override
  public LapTimeDTO populateNewBeanWithIntrospection(String[] line) throws InstantiationException, IllegalAccessException, IntrospectionException, InvocationTargetException, CsvRequiredFieldEmptyException {
    return null;
  }

  /**
   * Must be called once the length of input for a line/record is known to
   * verify that the line was complete.
   * Complete in this context means, no required fields are missing. The issue
   * here is, as long as a column is present but empty, we can check whether
   * the field is required and throw an exception if it is not, but if the data
   * end prematurely, we never have this chance without indication that no more
   * data are on the way.
   * Another validation is that the number of fields must match the number of
   * headers to prevent a data mismatch situation.
   *
   * @param numberOfFields The number of fields present in the line of input
   * @throws CsvRequiredFieldEmptyException If a required column is missing
   * @since 4.0
   */
  @Override
  public void verifyLineLength(int numberOfFields) throws CsvRequiredFieldEmptyException {

  }

  /**
   * Sets the locale for all error messages.
   *
   * @param errorLocale Locale for error messages. If null, the default locale
   *                    is used.
   * @since 4.0
   */
  @Override
  public void setErrorLocale(Locale errorLocale) {

  }

  /**
   * Sets the class type that is being mapped.
   * May perform additional initialization tasks.
   *
   * @param type Class type.
   * @throws CsvBadConverterException If a field in the bean is annotated
   *                                  with a custom converter that cannot be initialized. If you are not
   *                                  using custom converters that you have written yourself, it should be
   *                                  safe to catch this exception and ignore it.
   */
  @Override
  public void setType(Class<? extends LapTimeDTO> type) throws CsvBadConverterException {

  }

  /**
   * Transmutes a bean instance into an array of {@link String}s to be written
   * to a CSV file.
   *
   * @param bean The bean to be transmuted
   * @return The converted values of the bean fields in the correct order,
   * ready to be passed to a {@link CSVWriter}
   * @throws CsvDataTypeMismatchException   If expected to convert an
   *                                        unsupported data type
   * @throws CsvRequiredFieldEmptyException If the field is marked as required,
   *                                        but is currently empty
   */
  @Override
  public String[] transmuteBean(LapTimeDTO bean) throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
    return new String[0];
  }
}
