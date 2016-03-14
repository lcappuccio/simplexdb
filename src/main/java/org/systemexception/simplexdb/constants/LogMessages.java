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
	COMMIT_MESSAGE("Committing"),
	STORAGE_FOLDER("Create folder: "),
	STORAGE_SAVE(" saved"),
	STORAGE_SAVE_FAILED(" failed to save, exception: "),
	STORAGE_RENAME(" renamed to "),
	STORAGE_RENAME_FAILED(" failed to rename"),
	STORAGE_RENAME_EXCEPTION(" failed to rename, exception: "),
	MEMORY_OCCUPATION_HIT("Memory occupation limit hit");

	private final String message;

	LogMessages(final String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return message;
	}
}
