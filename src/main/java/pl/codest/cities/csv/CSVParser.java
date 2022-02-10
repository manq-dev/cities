package pl.codest.cities.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import pl.codest.cities.model.City;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CSVParser {

    public static List<City> csvToCities(String pathName) {
        try {
            File testedFile = new File(pathName);
            InputStream targetStream = new FileInputStream(testedFile);
            return readStreamToCities(targetStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        }
    }

    private static List<City> readStreamToCities(InputStream is) throws IOException {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             org.apache.commons.csv.CSVParser csvParser = new org.apache.commons.csv.CSVParser(fileReader,
                     CSVFormat.DEFAULT
                             .withFirstRecordAsHeader()
                             .withIgnoreHeaderCase()
                             .withTrim())) {
            return csvParser
                    .getRecords()
                    .stream()
                    .map(CSVParser::mapCSVRecordToCity)
                    .toList();
        }
    }

    private static City mapCSVRecordToCity(CSVRecord record) {
        return City
                .builder()
                .id(Integer.parseInt(record.get("id")))
                .name(record.get("name"))
                .imageUrl(record.get("photo"))
                .build();
    }
}
