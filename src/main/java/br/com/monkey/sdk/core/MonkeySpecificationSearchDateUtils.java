package br.com.monkey.sdk.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import java.util.Optional;

import static java.time.Clock.systemDefaultZone;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class MonkeySpecificationSearchDateUtils {

	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public static ZonedDateTime from(long millis) {
		return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault());
	}

	public static Optional<Date> getEndOfDay(Date date) {
		if (nonNull(date)) {
			LocalDateTime localDateTime = dateToLocalDateTime(date);
			LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
			return Optional.of(localDateTimeToDate(endOfDay));
		}
		else {
			return Optional.empty();
		}
	}

	public static Optional<Date> getStartOfDay(Date date) {
		if (nonNull(date)) {
			LocalDateTime localDateTime = dateToLocalDateTime(date);
			LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
			return Optional.of(localDateTimeToDate(startOfDay));
		}
		else {
			return Optional.empty();
		}
	}

	public static Optional<LocalDate> toLocalDate(Date date) {
		if (isNull(date)) {
			return Optional.empty();
		}
		return Optional.of(LocalDate.from(date.toInstant().atZone(systemDefaultZone().getZone())));
	}

	public static Instant getEndOfDay(LocalDateTime date) {
		return date.atZone(ZoneId.systemDefault()).with(LocalTime.MAX).toInstant();
	}

	public static Instant getStartOfDay(LocalDateTime date) {
		return date.atZone(ZoneId.systemDefault()).with(LocalTime.MIN).toInstant();
	}

	public static String format(LocalDate value, String pattern) {
		return value.format(ofPattern(pattern));
	}

	public static String formatDate(Date date) {
		return simpleDateFormat.format(date);
	}

	public static Date formatDate(String date) {
		try {
			return simpleDateFormat.parse(date);
		}
		catch (ParseException e) {
			throw new IllegalStateException("Error on parse date " + date + "the correct format is " + "yyyy-MM-dd");
		}
	}

	private static Date localDateTimeToDate(LocalDateTime startOfDay) {
		return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
	}

	private static LocalDateTime dateToLocalDateTime(Date date) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
	}

}
