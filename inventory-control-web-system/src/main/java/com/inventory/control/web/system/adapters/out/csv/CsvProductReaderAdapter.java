package com.inventory.control.web.system.adapters.out.csv;

import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.ports.out.ProductReaderPort;
import com.inventory.control.web.system.adapters.out.mapper.CsvProductMapper;
import com.inventory.control.web.system.adapters.out.csv.dto.CsvProductDto;
import com.inventory.control.web.system.adapters.exception.CsvValidationException;
import com.inventory.control.web.system.infrastructure.properties.CsvProductProperties;
import com.inventory.control.web.system.adapters.exception.InvalidCsvHeaderException;
import com.inventory.control.web.system.adapters.exception.InvalidCsvRowContentException;
import com.inventory.control.web.system.adapters.exception.InvalidFileTypeException;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvProductReaderAdapter implements ProductReaderPort {

    private final CsvProductMapper csvProductMapper;
    private final CsvProductProperties csvProductProperties;

    public CsvProductReaderAdapter(CsvProductMapper csvProductMapper, CsvProductProperties csvProductProperties) {
        this.csvProductMapper = csvProductMapper;
        this.csvProductProperties = csvProductProperties;
    }

@Override
    public List<Product> parseCsv(MultipartFile file) {
        validateFileType(file);

        List<Product> products = new ArrayList<>();

        CSVFormat csvFormat = CSVFormat.DEFAULT
                .builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .build();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = csvFormat.parse(reader)) {

            validateHeaders(csvParser.getHeaderMap());

            for (CSVRecord record : csvParser) {
                long lineNumber = record.getRecordNumber() + 1;

                validateRowContent(record, lineNumber);

                CsvProductDto dto = new CsvProductDto(
                        record.get("sku"),
                        record.get("name"),
                        record.get("description"),
                        record.get("price"),
                        record.get("quantity"),
                        record.get("category_id"),
                        record.get("category_name"),
                        record.get("category_description")
                );

                products.add(csvProductMapper.toDomain(dto));
            }
        } catch (InvalidCsvHeaderException | InvalidCsvRowContentException e) {
            throw e;
        } catch (Exception e) {
            throw new CsvValidationException("Falha ao processar e ler o arquivo CSV de produtos", e);
        }

        return products;
    }

    private void validateFileType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileTypeException("O arquivo enviado está vazio ou nulo.");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
            throw new InvalidFileTypeException("Formato de arquivo inválido. Apenas arquivos com extensão .csv são permitidos.");
        }

        String contentType = file.getContentType();
        if (contentType != null && !contentType.equals("text/csv") && !contentType.equals("application/vnd.ms-excel")) {
            throw new InvalidFileTypeException("MIME type inválido. O arquivo deve ser do tipo text/csv.");
        }
    }

    private void validateHeaders(Map<String, Integer> headerMap) {
        if (headerMap == null || headerMap.isEmpty()) {
            throw new InvalidCsvHeaderException("O arquivo CSV está sem o cabeçalho.");
        }

        for (String expectedHeader : csvProductProperties.requiredHeaders()) {
            boolean headerExists = headerMap.keySet().stream()
                    .anyMatch(h -> h.equalsIgnoreCase(expectedHeader));

            if (!headerExists) {
                throw new InvalidCsvHeaderException("O cabeçalho do CSV está incompleto. Coluna ausente: '" + expectedHeader + "'");
            }
        }
    }

    private void validateRowContent(CSVRecord record, long lineNumber) {
        String sku = record.get("sku");
        String name = record.get("name");
        String priceStr = record.get("price");
        String quantityStr = record.get("quantity");
        String categoryId = record.get("category_id");
        String categoryName = record.get("category_name");

        validateBlankFields(sku, name, priceStr, quantityStr, categoryId, categoryName, lineNumber);

        try {
            BigDecimal price = new BigDecimal(priceStr);
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidCsvRowContentException(lineNumber, "O campo 'price' deve ser um valor maior que zero.");
            }
        } catch (NumberFormatException e) {
            throw new InvalidCsvRowContentException(lineNumber, "O valor '" + priceStr + "' no campo 'price' é um número decimal inválido.");
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) {
                throw new InvalidCsvRowContentException(lineNumber, "O campo 'quantity' não pode ser um número negativo.");
            }
        } catch (NumberFormatException e) {
            throw new InvalidCsvRowContentException(lineNumber, "O valor '" + quantityStr + "' no campo 'quantity' é um número inteiro inválido.");
        }
    }

    private void validateBlankFields(String sku, 
                                        String name, 
                                        String price, 
                                        String quantity,
                                        String categoryId, 
                                        String categoryName, 
                                        long lineNumber) {

        if (StringUtils.isBlank(sku)) {
            throw new InvalidCsvRowContentException(lineNumber, "O campo 'sku' é obrigatório.");
        }
        if (StringUtils.isBlank(name)) {
            throw new InvalidCsvRowContentException(lineNumber, "O campo 'name' é obrigatório.");
        }
        if (StringUtils.isBlank(price)) {
            throw new InvalidCsvRowContentException(lineNumber, "O campo 'price' é obrigatório.");
        }
        if (StringUtils.isBlank(quantity)) {
            throw new InvalidCsvRowContentException(lineNumber, "O campo 'quantity' é obrigatório.");
        }
        if (StringUtils.isBlank(categoryId)) {
            throw new InvalidCsvRowContentException(lineNumber, "O campo 'category_id' é obrigatório.");
        }
        if (StringUtils.isBlank(categoryName)) {
            throw new InvalidCsvRowContentException(lineNumber, "O campo 'category_name' é obrigatório.");
        }
    }
}