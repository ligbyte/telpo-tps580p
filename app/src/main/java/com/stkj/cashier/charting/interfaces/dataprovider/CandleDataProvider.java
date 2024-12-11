package com.stkj.cashier.charting.interfaces.dataprovider;

import com.stkj.cashier.charting.data.CandleData;

public interface CandleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    CandleData getCandleData();
}
