package net.shadowmage.ancientwarfare.core.util;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class StreamUtils {
	private StreamUtils() {
	}

	public static <T> Collector<T, ?, List<T>> toList() {
		return Collectors.toList();
	}
}
