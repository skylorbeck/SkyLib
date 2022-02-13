package website.skylorbeck.minecraft.skylorlib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.Primitives;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigFileHandler {

    public static <T> T initConfigFile(String path,Object obj) throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        if (!Files.exists(Paths.get("config/"+path))) {
            try {
                Files.write(Paths.get("config/"+path), gson.toJson(obj).getBytes());
                return (T) obj;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                return (T) gson.fromJson(Files.readString(Paths.get("config/"+path)), obj.getClass());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        throw new IOException("Config file not valid");
    }
}
