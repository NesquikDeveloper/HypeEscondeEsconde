package nesquikdeveloper.escondeesconde.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    
    /**
     * Serializa uma localização para string
     * 
     * @param location Localização a ser serializada
     * @return String serializada da localização
     */
    public static String serializeLocation(Location location) {
        return location.getWorld().getName() + ";" +
                location.getX() + ";" +
                location.getY() + ";" +
                location.getZ() + ";" +
                location.getYaw() + ";" +
                location.getPitch();
    }
    
    /**
     * Deserializa uma string para uma localização
     * 
     * @param serialized String serializada
     * @return Localização ou null se inválida
     */
    public static Location deserializeLocation(String serialized) {
        if (serialized == null) return null;
        
        String[] parts = serialized.split(";");
        if (parts.length < 6) return null;
        
        World world = Bukkit.getWorld(parts[0]);
        if (world == null) return null;
        
        try {
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            float yaw = Float.parseFloat(parts[4]);
            float pitch = Float.parseFloat(parts[5]);
            
            return new Location(world, x, y, z, yaw, pitch);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Copia um arquivo
     * 
     * @param source Arquivo de origem
     * @param destination Arquivo de destino
     */
    public static void copyFile(File source, File destination) throws IOException {
        if (!destination.exists()) {
            destination.createNewFile();
        }
        
        try (FileChannel sourceChannel = new FileInputStream(source).getChannel();
             FileChannel destChannel = new FileOutputStream(destination).getChannel()) {
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }
    }
    
    /**
     * Copia uma pasta
     * 
     * @param source Pasta de origem
     * @param destination Pasta de destino
     */
    public static void copyFolder(File source, File destination) {
        if (!source.exists() || !source.isDirectory()) {
            return;
        }
        
        if (!destination.exists()) {
            destination.mkdirs();
        }
        
        File[] files = source.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                copyFolder(file, new File(destination, file.getName()));
            } else {
                try {
                    copyFile(file, new File(destination, file.getName()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Exclui uma pasta e seu conteúdo
     * 
     * @param folder Pasta a ser excluída
     */
    public static void deleteFolder(File folder) {
        if (!folder.exists() || !folder.isDirectory()) {
            return;
        }
        
        File[] files = folder.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                deleteFolder(file);
            } else {
                file.delete();
            }
        }
        
        folder.delete();
    }
    
    /**
     * Lista todos os arquivos em uma pasta recursivamente
     * 
     * @param folder Pasta a listar
     * @return Lista de arquivos
     */
    public static List<File> listFiles(File folder) {
        List<File> result = new ArrayList<>();
        
        if (!folder.exists() || !folder.isDirectory()) {
            return result;
        }
        
        File[] files = folder.listFiles();
        if (files == null) return result;
        
        for (File file : files) {
            if (file.isDirectory()) {
                result.addAll(listFiles(file));
            } else {
                result.add(file);
            }
        }
        
        return result;
    }
}