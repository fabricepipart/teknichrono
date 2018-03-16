package org.trd.app.teknichrono.rest.sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.TypedQuery;

public class WhereClauseBuilder {

  Map<String, Object> whereEqualMap = new HashMap<String, Object>();
  Map<String, Object> whereInMap = new HashMap<String, Object>();
  Map<String, Object> parameters = new HashMap<String, Object>();
  Map<String, List> parametersList = new HashMap<String, List>();

  public String getSqlDescription() {
    return "";
  }

  public void addEqualsClause(String left, String parameterName, Object parameterValue) {
    if (parameterValue != null) {
      whereEqualMap.put(left, parameterName);
      parameters.put(parameterName, parameterValue);
    }
  }

  public void addInClause(String left, String parameterName, List<Integer> parameterValue) {
    if (parameterValue != null && !parameterValue.isEmpty()) {
      whereInMap.put(left, parameterName);
      parametersList.put(parameterName, parameterValue);
    }
  }

  public void applyClauses(TypedQuery query) {
    for (Entry<String, Object> entry : parameters.entrySet()) {
      query.setParameter(entry.getKey(), entry.getValue());
    }
    for (Entry<String, List> entry : parametersList.entrySet()) {
      query.setParameter(entry.getKey(), entry.getValue());
    }
  }

  public String build() {
    StringBuilder builder = new StringBuilder();
    boolean startedBuilding = false;
    for (Entry<String, Object> entry : whereEqualMap.entrySet()) {
      if (!startedBuilding) {
        builder.append(" WHERE ");
        startedBuilding = true;
      } else {
        builder.append(" AND ");
      }
      builder.append(entry.getKey()).append(" = :").append(entry.getValue());
    }
    for (Entry<String, Object> entry : whereInMap.entrySet()) {
      if (!startedBuilding) {
        builder.append(" WHERE ");
        startedBuilding = true;
      } else {
        builder.append(" AND ");
      }
      Object value = entry.getValue();
      builder.append(entry.getKey()).append(" IN ( :").append(value).append(" )");
    }
    return builder.toString();
  }

}
