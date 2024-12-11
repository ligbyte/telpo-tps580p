package com.stkj.cashier.charting.interfaces.dataprovider;

import com.stkj.cashier.charting.data.ScatterData;

public interface ScatterDataProvider extends BarLineScatterCandleBubbleDataProvider {

    ScatterData getScatterData();
}
