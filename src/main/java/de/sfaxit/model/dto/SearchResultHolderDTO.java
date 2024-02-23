package de.sfaxit.model.dto;

import de.sfaxit.model.entity.Book;

import io.quarkus.runtime.annotations.RegisterForReflection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@RegisterForReflection(ignoreNested = false)
public class SearchResultHolderDTO {

    private List<Book> books;
    private int currentPage;
    private int pageSize;
    private int pageCount;
    private long totalCount;

}
