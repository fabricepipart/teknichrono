package org.trd.app.teknichrono.rest;

import io.quarkus.panache.common.Page;

public class Paging {

    private static final int DEFAULT_PAGE_SIZE = Integer.MAX_VALUE;

    public static Page from(Integer startPosition, Integer maxResults) {
        int pageIndex;
        int pageSize;
        if (maxResults == null) {
            pageSize = DEFAULT_PAGE_SIZE;
        } else {
            pageSize = maxResults;
        }
        if (startPosition == null) {
            pageIndex = 0;
        } else {
            pageIndex = startPosition / pageSize;
        }
        return Page.of(pageIndex, pageSize);
    }
}
