package com.inventory.control.web.system.adapters.out.csv;

import com.inventory.control.web.system.domain.model.Category;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.ports.out.ProductReaderPort;
import com.inventory.control.web.system.adapters.out.mapper.CsvProductMapper;
import com.inventory.control.web.system.adapters.out.csv.dto.CsvProductDto;
import com.inventory.control.web.system.adapters.exception.CsvErrorException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvProductReaderAdapter implements ProductReaderPort {

    private final CsvProductMapper csvProductMapper;

    public CsvProductReaderAdapter(CsvProductMapper csvProductMapper) {
        this.csvProductMapper = csvProductMapper;
    }

    @Override
    public List<Product> parseCsv(MultipartFile file) {
        List<Product> products = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim()
                    .parse(reader);

            for (CSVRecord record : records) {
                CsvProductDto dto = new CsvProductDto(
                        record.get("sku"),
                        record.get("name"),
                        record.get("description"),
                        record.get("price"),
                        record.get("quantity"),
                        record.get("category_name"),
                        record.get("category_description")
                );

                products.add(csvProductMapper.toDomain(dto));
            }
        } catch (Exception e) {
            throw new CsvErrorException("Falha ao processar e ler o arquivo CSV de produtos", e);
        }

        return products;
    }
}