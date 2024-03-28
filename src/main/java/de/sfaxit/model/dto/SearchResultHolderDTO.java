package de.sfaxit.model.dto;

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
	
	private List<BookDTO> books;
	private List<UserDTO> users;
	private int currentPage;
	private int pageSize;
	private int pageCount;
	private long totalCount;
	
}
