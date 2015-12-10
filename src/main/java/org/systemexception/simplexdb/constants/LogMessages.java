package org.systemexception.simplexdb.constants;

/**
 * @author leo
 * @date 06/12/15 17:15
 */
public enum LogMessages {

	CLOSE_DATABASE("Close database "),
	CREATE_DATABASE("Open database "),
	DELETE("Delete id: "),
	DELETED("Deleted id: "),
	EXPORT_START("Export started"),
	EXPORT_FINISH("Export finished"),
	FIND_ALL_IDS("Find all ids"),
	FIND_MATCH("Find matching: "),
	FIND_ID("Find id: "),
	FOUND_ID("Found id: "),
	FOUND_NOT_ID("Not found id: "),
	FOUND_MATCHING("Found matching: "),
	SAVE("Save: "),
	SAVED("Saved: "),
	SAVE_CONFLICT("Already exists: "),
	SCHEDULED_COMMIT("Committing"),
	STORAGE_FOLDER("Create folder: "),
	STORAGE_RENAME(" renamed to ");

	private String message;

	LogMessages(final String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return message;
	}
}
