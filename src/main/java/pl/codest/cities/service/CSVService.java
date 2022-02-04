package pl.codest.cities.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import pl.codest.cities.model.City;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

class CSVService {

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
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT
                             .withFirstRecordAsHeader()
                             .withIgnoreHeaderCase()
                             .withTrim())) {
            return csvParser
                    .getRecords()
                    .stream()
                    .map(CSVService::mapCSVRecordToCity)
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
