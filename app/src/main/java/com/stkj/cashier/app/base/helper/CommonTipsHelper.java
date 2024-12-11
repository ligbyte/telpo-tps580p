package com.stkj.cashier.app.base.helper;

import com.stkj.cashier.app.weigh.commontips.CommonTipsView;

public enum CommonTipsHelper {
    INSTANCE;

    private CommonTipsView mainTipsView;
    private CommonTipsView consumerTipsView;

    public void setMainTipsView(CommonTipsView mainTipsView) {
        this.mainTipsView = mainTipsView;
    }

    public void setConsumerTipsView(CommonTipsView consumerTipsView) {
        this.consumerTipsView = consumerTipsView;
    }

    public void setTips(String tips) {
        if (mainTipsView != null) {
            mainTipsView.setTips(tips);
        }
        if (consumerTipsView != null) {
            consumerTipsView.setTips(tips);
        }
    }

    public void setTipsDelayHide(String tips) {
        if (mainTipsView != null) {
            mainTipsView.setTips(tips);
            mainTipsView.delayHideTipsView();
        }
        if (consumerTipsView != null) {
            consumerTipsView.setTips(tips);
            consumerTipsView.delayHideTipsView();
        }
    }

    public void setLoading(String loading) {
        if (mainTipsView != null) {
            mainTipsView.setLoading(loading);
        }
        if (consumerTipsView != null) {
            consumerTipsView.setLoading(loading);
        }
    }

    public void delayHideTipsView() {
        if (mainTipsView != null) {
            mainTipsView.delayHideTipsView();
        }
        if (consumerTipsView != null) {
            consumerTipsView.delayHideTipsView();
        }
    }

    public void hideTipsView() {
        if (mainTipsView != null) {
            mainTipsView.hideTipsView();
        }
        if (consumerTipsView != null) {
            consumerTipsView.hideTipsView();
        }
    }
}