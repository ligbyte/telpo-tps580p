package com.stkj.cashier.charting.interfaces.dataprovider;

import com.stkj.cashier.charting.data.BubbleData;

public interface BubbleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BubbleData getBubbleData();
}
