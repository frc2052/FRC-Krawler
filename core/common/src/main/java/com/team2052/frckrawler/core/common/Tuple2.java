package com.team2052.frckrawler.core.common;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

public class Tuple2<T1, T2> {
    public T1 t1;
    public T2 t2;

    public Tuple2(T1 t1, T2 t2) {
        this.t1 = t1;
        this.t2 = t2;
    }

    public static <D, T> List<T> yieldValues(Collection<Tuple2<D, T>> collection) {
        List<T> list = Lists.newArrayList();
        for (Tuple2<D, T> value : collection) {
            list.add(value.t2);
        }
        return list;
    }


    public Tuple2<T1, T2> setT1(T1 t1) {
        this.t1 = t1;
        return this;
    }

    public Tuple2<T1, T2> setT2(T2 t2) {
        this.t2 = t2;
        return this;
    }
}
