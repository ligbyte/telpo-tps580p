package com.stkj.cashier.charting.interfaces.dataprovider;

import com.stkj.cashier.charting.components.YAxis;
import com.stkj.cashier.charting.data.LineData;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

    LineData getLineData();

    YAxis getAxis(YAxis.AxisDependency dependency);
}
