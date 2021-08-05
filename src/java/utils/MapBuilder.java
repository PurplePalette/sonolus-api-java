package utils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Mapビルダー.<br>
 * 各メソッドはメソッドチェーンで呼び出すことを想定.<br>
 */
public class MapBuilder<K, V> {

    /** 生成したMap */
    private final Map<K, V> map;

    /** Mapビルダーを生成 */
    public MapBuilder() {
        map = new LinkedHashMap<>();
    }

    /** Mapに値を登録 */
    public MapBuilder<K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    /** 編集可能なMapを返す */
    public Map<K, V> toMap() {
        return map;
    }

    /** 編集不可なMapを返す */
    public Map<K, V> toConst() {
        return Collections.unmodifiableMap(map);
    }
}