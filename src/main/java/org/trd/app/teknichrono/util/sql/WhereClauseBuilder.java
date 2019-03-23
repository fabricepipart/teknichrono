package org.trd.app.teknichrono.util.sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.TypedQuery;

public class WhereClauseBuilder {

  Map<String, String> whereEqualMap = new HashMap<String, String>();
  Map<String, String> whereInMap = new HashMap<String, String>();
  Map<String, Object> parameters = new HashMap<String, Object>();
  Map<String, List> parametersList = new HashMap<String, List>();

  public String getSqlDescription() {
    return "";
  }

  public void addEqualsClause(String toMatch, String parameterName, Object parameterValue) {
    if (parameterValue != null) {
      whereEqualMap.put(parameterName, toMatch);
      parameters.put(parameterName, parameterValue);
    }
  }

  public void addInClause(String toMatch, String parameterName, List<Integer> parameterValue) {
    if (parameterValue != null && !parameterValue.isEmpty()) {
      whereInMap.put(parameterName, toMatch);
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
    for (Entry<String, String> entry : whereEqualMap.entrySet()) {
      if (!startedBuilding) {
        builder.append(" WHERE ");
        startedBuilding = true;
      } else {
        builder.append(" AND ");
      }
      builder.append(entry.getValue()).append(" = :").append(entry.getKey());
    }
    for (Entry<String, String> entry : whereInMap.entrySet()) {
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
