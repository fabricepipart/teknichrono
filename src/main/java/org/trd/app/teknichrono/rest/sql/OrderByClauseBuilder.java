package org.trd.app.teknichrono.rest.sql;

import java.util.ArrayList;
import java.util.List;

public class OrderByClauseBuilder {

  enum ORDER {
    DESC, ASC
  };

  List<String> orderByList = new ArrayList<String>();

  public void add(String orderBy) {
    orderByList.add(orderBy);
  }

  public void add(String orderBy, ORDER order) {
    add(orderBy + " " + order.toString());
  }

  public String build() {
    StringBuilder builder = new StringBuilder();
    boolean startedBuilding = false;
    for (String order : orderByList) {
      if (!startedBuilding) {
        builder.append(" ORDER BY ");
        startedBuilding = true;
      } else {
        builder.append(" , ");
      }
      builder.append(order);
    }
    return builder.toString();
  }

}
