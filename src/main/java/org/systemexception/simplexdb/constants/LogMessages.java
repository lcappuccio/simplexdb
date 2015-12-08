package org.systemexception.simplexdb.constants;

/**
 * @author leo
 * @date 06/12/15 17:15
 */
public enum LogMessages {

	CREATE_DATABASE("Open database "),
	CLOSE_DATABASE("Close database "),
	FIND_ID("Find id: "),
	FOUND_ID("Found id: "),
	FOUND_NOT_ID("Not found id: "),
	FIND_ALL_IDS("Find all ids"),
	FIND_MATCH("Find matching: "),
	FOUND_MATCHING("Found matching: "),
	SAVE("Save: "),
	SAVED("Saved: "),
	SAVE_CONFLICT("Already exists: "),
	DELETE("Delete id: "),
	DELETED("Deleted id: "),
	STORAGE_FOLDER("Create folder: ");

	private String message;

	LogMessages(final String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return message;
	}
}
