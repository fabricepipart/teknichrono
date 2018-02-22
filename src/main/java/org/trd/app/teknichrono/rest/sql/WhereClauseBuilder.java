package org.trd.app.teknichrono.rest.sql;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.TypedQuery;

public class WhereClauseBuilder {

  Map<String, Object> whereEqualMap = new HashMap<String, Object>();
  Map<String, Object> parameters = new HashMap<String, Object>();

  public String getSqlDescription() {
    return "";
  }

  public void addEqualsClause(String left, String parameterName, Object parameter) {
    if (parameter != null) {
      whereEqualMap.put(left, parameterName);
      parameters.put(parameterName, parameter);
    }
  }

  public void applyClauses(TypedQuery query) {
    for (Entry<String, Object> entry : parameters.entrySet()) {
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
    return builder.toString();
  }

}
