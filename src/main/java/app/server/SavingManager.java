package app.server;

import app.exceptions.FileNotFound;
import app.exceptions.UnknownException;
import app.product.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.List;
import java.util.TreeSet;

public class SavingManager {
    private String filePath;
    private static int numberOfSession = 1;

    public SavingManager(String envVar) {
        filePath = System.getenv(envVar);
        if (filePath == null) {
            filePath = "file.txt";
        }
    }

    /**
     * Читает сохранённую в файл коллекцию.
     *
     * @return сохранённая коллекция
     * @throws FileNotFoundException
     */
    public TreeSet<Product> readSaving() {
        try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(filePath))) {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Product> productList = objectMapper.readValue(inputStreamReader, new TypeReference<>() {
            });
            return new TreeSet<>(productList);
        } catch (FileNotFoundException f) {
            throw new FileNotFound("чтение сохранения", f.getMessage());
        } catch (Exception e) {
            filePath = filePath.replace(".", numberOfSession + ".");
            numberOfSession++;
            throw new UnknownException(e);
        }
    }

    public void save(TreeSet<Product> collection) throws FileNotFoundException {

        try (OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(filePath))) {
            ObjectMapper objectMapper = new ObjectMapper();

            List<Product> productList = collection.stream().toList();
            String jsonArray = objectMapper.writeValueAsString(productList);
            fileWriter.write(jsonArray);
        } catch (FileNotFoundException f) {
            throw f;
        } catch (Exception e) {
            throw new UnknownException(e);
        }
    }

    public String getPath() {return filePath;}
}