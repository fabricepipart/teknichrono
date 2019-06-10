package org.trd.app.teknichrono.rest;

import io.quarkus.panache.common.Page;

public class Paging {

    private static final int DEFAULT_PAGE_SIZE = Integer.MAX_VALUE;

    /**
     * Returns a Page with appropriates defaults. When using a Page,
     * up to <code>pageSize</code> results starting from <code>pageIndex * pageSize</code> will be returned.
     *
     * @param pageIndex the index (0-based) of the result page you want to get.
     *                  If <code>null</code>, then the first page (index 0) is assumed.
     * @param pageSize  the size of each page.
     *                  If <code>null</code>, <code>Integer.MAX_VALUE</code> is assumed.
     * @return a Page with appropriates defaults.
     */
    public static Page from(Integer pageIndex, Integer pageSize) {
        if (pageIndex == null) {
            pageIndex = 0;
        }
        if (pageSize == null) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        return Page.of(pageIndex, pageSize);
    }
}
