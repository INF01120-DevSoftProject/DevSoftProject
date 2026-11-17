package input;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class JsonInputReader<T, V> implements InputReader<LinkedHashMap<T, V>> {

    private final Gson gson = new Gson();
    private final Type type;

    public JsonInputReader(Class<T> keyType, Class<V> valueType) {
        this.type = TypeToken.getParameterized(
            LinkedHashMap.class,
            keyType,
            valueType
        ).getType();
    }

    @Override
    public LinkedHashMap<T, V> readFile(Path path) throws IOException {

        if (path == null) {
            throw new IllegalArgumentException("O caminho do arquivo não pode ser nulo.");
        }

        String json = Files.readString(path, StandardCharsets.UTF_8);

        return gson.fromJson(json, type);
    }
}
