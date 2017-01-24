package com.soaringnova.novascenic.base;

import java.util.List;

/**
 * Created by Be on 2017/1/24.
 */
public interface Variable<T> {
    void refresh(List<T> data);

    void append(T data);

    void append(int position, T data);

    void append(List<T> data);

    T remove(T item);

    T remove(int position);
}
