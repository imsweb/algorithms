/*
 * Copyright (C) 2013 Information Management Services, Inc.
 */
package com.imsweb.algorithms.prcdauiho;

/**
 * The purpose of the <code>PrcdaUihoDataProvider</code> is to get PRCDA, UIHO, and UIHO facility for
 * the provided state of dx and county of dx either from the database or csv lookup based on the
 * implementation
 * <p/>
 * Created on Aug 12, 2019 by howew
 * @author howew
 */
public interface PrcdaUihoDataProvider {

    /**
     * Returns PRCDA for provided state of dx and county of dx.
     * <p/>
     * Created Aug 12, 2019 by howew
     * @param state state at DX
     * @param county county at DX
     * @return the corresponding PRCDA value
     */
    String getPRCDA(String state, String county);

    /**
     * Returns UIHO for provided state of dx and county of dx.
     * <p/>
     * Created Aug 12, 2019 by howew
     * @param state state at DX
     * @param county county at DX
     * @return the corresponding UIHO value
     */
    String getUIHO(String state, String county);
}
