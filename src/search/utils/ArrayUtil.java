package search.utils;

import java.util.*;

/**
 * @author Yukio [GodWorld]
 * @version 13.10.2018 23:28
 */
public class ArrayUtil {
	public static <K, V> Map<K, V> sort(Map<K, V> map, Comparator<? super Map.Entry<K, V>> comparator) {
		List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
		list.sort(comparator);

		Map<K, V> result = new LinkedHashMap<>(map.size());
		for (Map.Entry<K, V> entry : list)
			result.put(entry.getKey(), entry.getValue());

		return result;
	}
}
