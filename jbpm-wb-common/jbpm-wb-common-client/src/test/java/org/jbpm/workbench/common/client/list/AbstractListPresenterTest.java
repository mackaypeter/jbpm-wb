/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.workbench.common.client.list;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Timer;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractListPresenterTest {

    @Mock
    ExtendedPagedTable extendedPagedTable;

    @Mock
    HasData next;

    @Mock
    AsyncDataProvider dataProviderMock;

    @Mock
    private Timer timer;

    @Mock
    private ListView viewMock;

    private AbstractListPresenter testListPresenter;

    @Before
    public void setupMocks() {
        testListPresenter = spy(AbstractScreenListPresenter.class);
        when(testListPresenter.getListView()).thenReturn(viewMock);
        when(next.getVisibleRange()).thenReturn(new Range(1,
                                                          1));
        testListPresenter.initDataProvider();
        testListPresenter.getDataProvider().addDataDisplay(next);
    }

    @Test
    public void autoRefreshDisabledByDefaultTest() {
        testListPresenter.setRefreshTimer(null);
        testListPresenter.updateRefreshTimer();

        assertNotNull(testListPresenter.getRefreshTimer());
        assertFalse(testListPresenter.isAutoRefreshEnabled());

        testListPresenter.setRefreshTimer(timer);
        testListPresenter.setAutoRefreshSeconds(60);
        testListPresenter.updateRefreshTimer();

        assertFalse(testListPresenter.isAutoRefreshEnabled());
        verify(timer).cancel();
    }

    @Test
    public void autoRefreshEnabledScheduleTimerTest() {
        testListPresenter.setAutoRefreshEnabled(true);
        testListPresenter.setAutoRefreshSeconds(60);
        testListPresenter.setRefreshTimer(timer);
        testListPresenter.updateRefreshTimer();

        assertNotNull(testListPresenter.getRefreshTimer());
        verify(timer).cancel();
        verify(timer).schedule(60000);
    }

    @Test
    public void restoreTabsTest() {
        testListPresenter.onRestoreDefaultFilters();

        verify(viewMock).showRestoreDefaultFilterConfirmationPopup();
    }

    @Test
    public void testUpDateDataOnCallBackFirstPage() {
        List instanceSummaries = new ArrayList<>();
        instanceSummaries.add("item1");
        instanceSummaries.add("item2");

        int startRange = 0;
        testListPresenter.setDataProvider(dataProviderMock);
        testListPresenter.updateDataOnCallback(instanceSummaries,
                                               startRange,
                                               startRange + instanceSummaries.size(),
                                               false);

        verify(dataProviderMock).updateRowCount(2,
                                                false);
        verify(dataProviderMock).updateRowData(0,
                                               instanceSummaries);
    }

    @Test
    public void testUpDateDataOnCallBackMiddlePage() {
        List instanceSummaries = new ArrayList<>();
        instanceSummaries.add("item1");
        instanceSummaries.add("item2");

        int startRange = 10;
        testListPresenter.setDataProvider(dataProviderMock);
        testListPresenter.updateDataOnCallback(instanceSummaries,
                                               startRange,
                                               startRange + instanceSummaries.size(),
                                               true);

        verify(dataProviderMock).updateRowCount(startRange + instanceSummaries.size(),
                                                true);
        verify(dataProviderMock).updateRowData(startRange,
                                               instanceSummaries);
    }

    @Test
    public void testRefreshGrid() {

        Range range = new Range(0,
                                5);

        when(viewMock.getListGrid()).thenReturn(extendedPagedTable);
        when(extendedPagedTable.getVisibleRange()).thenReturn(range);

        testListPresenter.refreshGrid();

        verify(extendedPagedTable).setVisibleRangeAndClearData(range,
                                                               true);
    }
}