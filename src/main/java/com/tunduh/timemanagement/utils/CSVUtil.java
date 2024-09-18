package com.tunduh.timemanagement.utils;

import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.util.List;

public class CSVUtil {
    private static final Logger logger = LoggerFactory.getLogger(CSVUtil.class);

    public static String generateCSV(List<String[]> data) {
        try (StringWriter stringWriter = new StringWriter();
             CSVWriter csvWriter = new CSVWriter(stringWriter)) {
            csvWriter.writeAll(data);
            return stringWriter.toString();
        } catch (Exception e) {
            logger.error("Error generating CSV", e);
            throw new RuntimeException("Failed to generate CSV", e);
        }
    }
}