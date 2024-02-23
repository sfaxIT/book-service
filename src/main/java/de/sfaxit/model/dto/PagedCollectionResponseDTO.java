package de.sfaxit.model.dto;

import static java.util.Collections.emptyList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

import lombok.Getter;

@Getter
public class PagedCollectionResponseDTO<T> implements Iterable<T> {

    private final List<T> collection;
    private Integer pages;
    private Integer currentPage;
    private Long records;

    @JsonCreator
    public PagedCollectionResponseDTO(@JsonProperty(value = "collection", required = true) final List<T> collection) {
        this.collection = collection != null ? collection : emptyList();
        this.records = (long) this.collection.size();
    }

    public static <T> PagedCollectionResponseDTO<T> of(final List<T> collection, final int pages, final int currentPage, final long records) {
        final PagedCollectionResponseDTO<T> result = new PagedCollectionResponseDTO<>(collection);

        result.currentPage = currentPage;
        result.pages = pages;
        result.records = records;

        return result;
    }

    public static <S> PagedCollectionResponseDTO<S> of(final List<S> collection) {
        return new PagedCollectionResponseDTO<>(collection);
    }

    @Override
    public Iterator<T> iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return Iterable.super.spliterator();
    }

}
