package de.sfaxit.model.dto;

import static java.util.Collections.emptyList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Iterator;
import java.util.List;

import lombok.Getter;

@Getter
public class CollectionResponseDTO<T> implements Iterable<T> {
	private final List<T> collection;
	
	@JsonCreator
	public CollectionResponseDTO(@JsonProperty(value = "collection", required = true) final List<T> collection) {
		this.collection = collection != null ? collection : emptyList();
	}
	
	public static <S> CollectionResponseDTO<S> of(final List<S> collection) {
		return new CollectionResponseDTO<S>(collection);
	}
	
	@Override
	public Iterator<T> iterator() {
		return this.collection.iterator();
	}
	
}
