package com.team2052.frckrawler.database.subscribers;

import com.team2052.frckrawler.database.consumer.DataComsumer;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

public abstract class BaseDataSubscriber<T, V> implements Observer<T> {
    DataComsumer<V> mConsumer;
    T data;
    V dataToBind;

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        AndroidSchedulers.mainThread().createWorker().schedule(() -> {
            if (mConsumer != null) {
                mConsumer.onError(e);
            }
        });
    }

    @Override
    public void onNext(T t) {
        setData(t);
        parseData();
        bindData();
    }

    public void bindData() {
        AndroidSchedulers.mainThread().createWorker().schedule(() -> {
            if (mConsumer != null) {
                mConsumer.updateData(dataToBind);
            }
        });
    }

    public void setConsumer(DataComsumer<V> mConsumer) {
        this.mConsumer = mConsumer;
    }

    public abstract void parseData();

    public void setData(T data) {
        this.data = data;
    }
}
