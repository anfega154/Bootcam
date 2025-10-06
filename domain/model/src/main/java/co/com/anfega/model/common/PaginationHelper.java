package co.com.anfega.model.common;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class PaginationHelper {
    private PaginationHelper() {
    }

    public static <T, V extends Comparable<? super V>> PageResponse<T> paginateAndSort(
            List<T> items,
            int page,
            int size,
            String direction,
            Function<T, V> sortKeyExtractor
    ) {
        Comparator<T> comparator = Comparator.comparing(sortKeyExtractor, Comparator.nullsLast(Comparator.naturalOrder()));

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        items.sort(comparator);

        return PageResponse.of(items, page, size);
    }
}
