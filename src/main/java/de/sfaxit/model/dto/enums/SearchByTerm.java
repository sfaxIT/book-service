package de.sfaxit.model.dto.enums;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "acceptable search terms for books in the library")
public enum SearchByTerm {
	TITLE, DESCRIPTION, PRICE
}
