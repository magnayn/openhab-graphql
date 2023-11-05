package org.openhab.io.graphql.datafetcher;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionWrapper<T> {
    public int total;
    public int skip;
    public int limit;

    List<T> items;
    public CollectionWrapper(Stream<T> items, int skip, int limit) {
        this.items = items.collect(Collectors.toList());
        this.skip = skip;
        this.limit = limit;
    }

    public Collection<T> getItems() {
        if( skip > items.size() )
            return Collections.EMPTY_LIST;

        int last = skip+limit;
        if( last > items.size() )
            last = items.size();

        return items.subList(skip, last);
    }

    public int getTotal() {
        return items.size();
    }


    public int getSkip() {
        return skip;
    }

    public int getLimit() {
        return limit;
    }

}
