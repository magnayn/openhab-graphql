package org.openhab.io.graphql.mapping.wrapper;

import org.openhab.io.graphql.mapping.MappingSession;

import com.google.gson.Gson;

public abstract class Wrapper<T> {
    protected final MappingSession session;
    protected final T item;

    protected Wrapper(MappingSession session, T item) {
        if( item == null )
            throw new IllegalArgumentException("Wrapped element cannot be null");
        this.session = session;
        this.item = item;
    }

    public String get__typename() {
        return item.getClass().getSimpleName();
    }

    protected String json(Object object) {
        return new Gson().toJson(object);
    }
}
