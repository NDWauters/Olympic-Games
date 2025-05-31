package utils;

import java.time.format.DateTimeFormatter;

public interface InitFormatter {
	DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	DateTimeFormatter TIMEFORMATTER = DateTimeFormatter.ofPattern("HH:mm");
}
