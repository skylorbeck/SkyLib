package website.skylorbeck.minecraft.skylorlib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ConfigFileHandler {
    /**
     * Attempts to load a config file from the given path, inside the /config/ folder. If the file does not exist, it will be created with the default values from obj.
     * @param path The path to the config file, inside the /config/ folder.
     * @param obj The structure of the config file, with default/fallback values.
     * @return A copy of the config file, with the values loaded from the config. If no config file exists, a copy of the default values will be returned.
     */
    public static <T> T initConfigFile(String path,Object obj) {
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
        Logger.getGlobal().log(Level.SEVERE,"Failed to read or write log, returning default values");
        return (T) obj;
    }
}
