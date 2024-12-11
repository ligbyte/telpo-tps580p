package com.stkj.cashier.charting.interfaces.dataprovider;

import com.stkj.cashier.charting.components.YAxis.AxisDependency;
import com.stkj.cashier.charting.data.BarLineScatterCandleBubbleData;
import com.stkj.cashier.charting.utils.Transformer;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    Transformer getTransformer(AxisDependency axis);
    boolean isInverted(AxisDependency axis);
    
    float getLowestVisibleX();
    float getHighestVisibleX();

    BarLineScatterCandleBubbleData getData();
}
