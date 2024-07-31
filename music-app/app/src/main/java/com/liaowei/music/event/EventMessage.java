package com.liaowei.music.event;

import androidx.annotation.NonNull;

public class EventMessage<T> {
    private int type;
    private T message;
    public EventMessage(int type, T message) {
        this.type = type;
        this.message = message;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public T getMessage() {
        return message;
    }
    public void setMessage(T message) {
        this.message = message;
    }


    @NonNull
    @Override
    public String toString() {
        return "type="+type+"--message= "+message;
    }
}