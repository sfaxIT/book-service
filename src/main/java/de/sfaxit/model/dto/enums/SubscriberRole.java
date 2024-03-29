package de.sfaxit.model.dto.enums;

import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Several kind of roles for book library users")
public enum SubscriberRole {
	
	AUTHOR("author"), ADMIN("admin"), READER("reader"), BANNED("banned");
	
	SubscriberRole(String name) {
		this.name = name;
	}
	
	private final String name;
	
	public String getName() {
		return this.name;
	}
	
	public static SubscriberRole of(final String name) {
		if (name != null) {
			return Stream.of(values())
			             .filter(u -> StringUtils.equalsIgnoreCase(u.getName(), name))
			             .findFirst()
			             .orElseThrow(IllegalArgumentException::new);
		}
		return null;
	}
	
}
