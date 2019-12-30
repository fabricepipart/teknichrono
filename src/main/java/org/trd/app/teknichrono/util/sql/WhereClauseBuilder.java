package org.trd.app.teknichrono.util.sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class WhereClauseBuilder {

  Map<String, String> whereEqualMap = new HashMap<>();
  Map<String, String> whereInMap = new HashMap<>();
  Map<String, Object> parameters = new HashMap<>();
  Map<String, List> parametersList = new HashMap<>();

  public String getSqlDescription() {
    return "";
  }

  public void addEqualsClause(String toMatch, String parameterName, Object parameterValue) {
    if (parameterValue != null) {
      this.whereEqualMap.put(parameterName, toMatch);
      this.parameters.put(parameterName, parameterValue);
    }
  }

  public void addInClause(String toMatch, String parameterName, List<Long> parameterValue) {
    if (parameterValue != null && !parameterValue.isEmpty()) {
      this.whereInMap.put(parameterName, toMatch);
      this.parametersList.put(parameterName, parameterValue);
    }
  }

  public Map<String, Object> getParametersMap() {
    Map<String, Object> toReturn = new HashMap<>();
    for (Entry<String, Object> entry : this.parameters.entrySet()) {
      toReturn.put(entry.getKey(), entry.getValue());
    }
    for (Entry<String, List> entry : this.parametersList.entrySet()) {
      toReturn.put(entry.getKey(), entry.getValue());
    }
    return toReturn;
  }

  public String build() {
    StringBuilder builder = new StringBuilder();
    boolean startedBuilding = false;
    for (Entry<String, String> entry : this.whereEqualMap.entrySet()) {
      if (!startedBuilding) {
        builder.append(" WHERE ");
        startedBuilding = true;
      } else {
        builder.append(" AND ");
      }
      builder.append(entry.getValue()).append(" = :").append(entry.getKey());
    }
    for (Entry<String, String> entry : this.whereInMap.entrySet()) {
      if (!startedBuilding) {
        builder.append(" WHERE ");
        startedBuilding = true;
      } else {
        builder.append(" AND ");
      }
      String value = entry.getKey();
      builder.append(entry.getValue()).append(" IN ( :").append(value).append(" )");
    }
    return builder.toString();
  }

}
