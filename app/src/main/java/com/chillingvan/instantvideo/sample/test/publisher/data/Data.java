package com.chillingvan.instantvideo.sample.test.publisher.data;


public abstract class Data {
    private int position;

    public Data(int position) {
        this.position = position;
    }

    public final int getPosition() {
        return position;
    }

    public abstract String getType();
}
