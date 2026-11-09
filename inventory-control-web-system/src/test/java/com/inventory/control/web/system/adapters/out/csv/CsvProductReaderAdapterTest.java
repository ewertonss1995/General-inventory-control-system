package com.inventory.control.web.system.adapters.out.csv;

import com.inventory.control.web.system.adapters.exception.CsvValidationException;
import com.inventory.control.web.system.adapters.exception.InvalidCsvHeaderException;
import com.inventory.control.web.system.adapters.exception.InvalidCsvRowContentException;
import com.inventory.control.web.system.adapters.exception.InvalidFileTypeException;
import com.inventory.control.web.system.adapters.out.csv.dto.CsvProductDto;
import com.inventory.control.web.system.adapters.out.mapper.CsvProductMapper;
import com.inventory.control.web.system.domain.model.Product;
import com.inventory.control.web.system.infrastructure.properties.CsvProductProperties;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.inventory.control.web.system.mocks.domain.service.product.ProductDomainMockFactory.createValidProduct;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CsvProductReaderAdapterTest {

    @Mock
    private CsvProductMapper csvProductMapper;

    @Mock
    private CsvProductProperties csvProductProperties;

    private MeterRegistry meterRegistry;

    @InjectMocks
    private CsvProductReaderAdapter csvProductReaderAdapter;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        csvProductReaderAdapter = new CsvProductReaderAdapter(csvProductMapper, csvProductProperties, meterRegistry);

        lenient().when(csvProductProperties.requiredHeaders()).thenReturn(Set.of(
                "sku", "name", "description", "price", "quantity", "category_id", "category_name", "category_description"
        ));
    }

    @Test
    void shouldParseCsvSuccessfullyAndReturnProducts() {
        String csvContent = "sku,name,description,price,quantity,category_id,category_name,category_description\n" +
                "SKU-PROD-001,Monitor Gamer 27,Monitor IPS 144Hz 1ms,1200.00,15,cat-789,Monitores,Monitores e Displays";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "products.csv",
                "text/csv",
                csvContent.getBytes()
        );

        Product expectedProduct = createValidProduct();
        when(csvProductMapper.toDomain(any(CsvProductDto.class))).thenReturn(expectedProduct);

        List<Product> products = csvProductReaderAdapter.parseCsv(file);

        assertNotNull(products);
        assertEquals(1, products.size());
        assertEquals("SKU-PROD-001", products.get(0).getSku());

        verify(csvProductMapper).toDomain(any(CsvProductDto.class));
        assertEquals(1, meterRegistry.counter("csv.reader.success", "layer", "adapter_out", "target", "csv_parser").count());
    }

    @Test
    void shouldParseCsvSuccessfullyWithExcelContentType() {
        String csvContent = "sku,name,description,price,quantity,category_id,category_name,category_description\n" +
                "SKU-PROD-001,Monitor Gamer 27,Monitor IPS 144Hz 1ms,1200.00,15,cat-789,Monitores,Monitores e Displays";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "products.csv",
                "application/vnd.ms-excel",
                csvContent.getBytes()
        );

        Product expectedProduct = createValidProduct();
        when(csvProductMapper.toDomain(any(CsvProductDto.class))).thenReturn(expectedProduct);

        List<Product> products = csvProductReaderAdapter.parseCsv(file);

        assertNotNull(products);
        assertEquals(1, products.size());
    }

    @Test
    void shouldParseCsvSuccessfullyWithNullContentType() {
        String csvContent = "sku,name,description,price,quantity,category_id,category_name,category_description\n" +
                "SKU-PROD-001,Monitor Gamer 27,Monitor IPS 144Hz 1ms,1200.00,15,cat-789,Monitores,Monitores e Displays";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "products.csv",
                null,
                csvContent.getBytes()
        );

        Product expectedProduct = createValidProduct();
        when(csvProductMapper.toDomain(any(CsvProductDto.class))).thenReturn(expectedProduct);

        List<Product> products = csvProductReaderAdapter.parseCsv(file);

        assertNotNull(products);
        assertEquals(1, products.size());
    }

    @Test
    void shouldThrowExceptionWhenFileIsNull() {
        assertThrows(InvalidFileTypeException.class, () -> csvProductReaderAdapter.parseCsv(null));
    }

    @Test
    void shouldThrowExceptionWhenFileIsEmpty() {
        MockMultipartFile file = new MockMultipartFile("file", "empty.csv", "text/csv", new byte[0]);
        assertThrows(InvalidFileTypeException.class, () -> csvProductReaderAdapter.parseCsv(file));
    }

    @Test
    void shouldThrowExceptionWhenFileExtensionIsNotCsv() {
        MockMultipartFile file = new MockMultipartFile("file", "document.txt", "text/plain", "data".getBytes());
        assertThrows(InvalidFileTypeException.class, () -> csvProductReaderAdapter.parseCsv(file));
    }

    @Test
    void shouldThrowExceptionWhenContentTypeIsInvalid() {
        MockMultipartFile file = new MockMultipartFile("file", "products.csv", "application/pdf", "data".getBytes());
        assertThrows(InvalidFileTypeException.class, () -> csvProductReaderAdapter.parseCsv(file));
    }

    @Test
    void shouldThrowExceptionWhenCsvHeaderIsMissing() {
        String csvContent = "invalid_header_1,invalid_header_2\nval1,val2";
        MockMultipartFile file = new MockMultipartFile("file", "products.csv", "text/csv", csvContent.getBytes());

        assertThrows(InvalidCsvHeaderException.class, () -> csvProductReaderAdapter.parseCsv(file));
        assertEquals(1.0, meterRegistry.counter("csv.reader.failures", "layer", "adapter_out", "target", "csv_parser", "error_type", "InvalidCsvHeaderException").count());
    }

    @Test
    void shouldThrowExceptionWhenRequiredFieldIsBlank() {
        String csvContent = "sku,name,description,price,quantity,category_id,category_name,category_description\n" +
                ",Monitor Gamer 27,Monitor IPS 144Hz 1ms,1200.00,15,cat-789,Monitores,Monitores e Displays";

        MockMultipartFile file = new MockMultipartFile("file", "products.csv", "text/csv", csvContent.getBytes());

        assertThrows(InvalidCsvRowContentException.class, () -> csvProductReaderAdapter.parseCsv(file));
        assertEquals(1.0, meterRegistry.counter("csv.reader.failures", "layer", "adapter_out", "target", "csv_parser", "error_type", "InvalidCsvRowContentException").count());
    }

    @Test
    void shouldThrowExceptionWhenPriceIsInvalidNumber() {
        String csvContent = "sku,name,description,price,quantity,category_id,category_name,category_description\n" +
                "SKU-PROD-001,Monitor Gamer 27,Monitor IPS 144Hz 1ms,invalid_price,15,cat-789,Monitores,Monitores e Displays";

        MockMultipartFile file = new MockMultipartFile("file", "products.csv", "text/csv", csvContent.getBytes());

        assertThrows(InvalidCsvRowContentException.class, () -> csvProductReaderAdapter.parseCsv(file));
    }

    @Test
    void shouldThrowExceptionWhenPriceIsZeroOrNegative() {
        String csvContent = "sku,name,description,price,quantity,category_id,category_name,category_description\n" +
                "SKU-PROD-001,Monitor Gamer 27,Monitor IPS 144Hz 1ms,0.00,15,cat-789,Monitores,Monitores e Displays";

        MockMultipartFile file = new MockMultipartFile("file", "products.csv", "text/csv", csvContent.getBytes());

        assertThrows(InvalidCsvRowContentException.class, () -> csvProductReaderAdapter.parseCsv(file));
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsNegative() {
        String csvContent = "sku,name,description,price,quantity,category_id,category_name,category_description\n" +
                "SKU-PROD-001,Monitor Gamer 27,Monitor IPS 144Hz 1ms,1200.00,-5,cat-789,Monitores,Monitores e Displays";

        MockMultipartFile file = new MockMultipartFile("file", "products.csv", "text/csv", csvContent.getBytes());

        assertThrows(InvalidCsvRowContentException.class, () -> csvProductReaderAdapter.parseCsv(file));
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsInvalidNumber() {
        String csvContent = "sku,name,description,price,quantity,category_id,category_name,category_description\n" +
                "SKU-PROD-001,Monitor Gamer 27,Monitor IPS 144Hz 1ms,1200.00,invalid_qty,cat-789,Monitores,Monitores e Displays";

        MockMultipartFile file = new MockMultipartFile("file", "products.csv", "text/csv", csvContent.getBytes());

        assertThrows(InvalidCsvRowContentException.class, () -> csvProductReaderAdapter.parseCsv(file));
    }

    @Test
    void shouldWrapIOExceptionAsCsvValidationException() throws Exception {
        MultipartFile fileMock = mock(MultipartFile.class);
        when(fileMock.getOriginalFilename()).thenReturn("products.csv");
        when(fileMock.getContentType()).thenReturn("text/csv");
        when(fileMock.isEmpty()).thenReturn(false);
        when(fileMock.getInputStream()).thenThrow(new IOException("Stream error"));

        assertThrows(CsvValidationException.class, () -> csvProductReaderAdapter.parseCsv(fileMock));
        assertEquals(1.0, meterRegistry.counter("csv.reader.failures", "layer", "adapter_out", "target", "csv_parser", "error_type", "IOException").count());
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        String csvContent = "sku,name,description,price,quantity,category_id,category_name,category_description\n" +
                "SKU-PROD-001,,Monitor IPS 144Hz 1ms,1200.00,15,cat-789,Monitores,Monitores e Displays";

        MockMultipartFile file = new MockMultipartFile("file", "products.csv", "text/csv", csvContent.getBytes());

        assertThrows(InvalidCsvRowContentException.class, () -> csvProductReaderAdapter.parseCsv(file));
    }

    @Test
    void shouldThrowExceptionWhenPriceIsBlank() {
        String csvContent = "sku,name,description,price,quantity,category_id,category_name,category_description\n" +
                "SKU-PROD-001,Monitor Gamer 27,Monitor IPS 144Hz 1ms,,15,cat-789,Monitores,Monitores e Displays";

        MockMultipartFile file = new MockMultipartFile("file", "products.csv", "text/csv", csvContent.getBytes());

        assertThrows(InvalidCsvRowContentException.class, () -> csvProductReaderAdapter.parseCsv(file));
    }

    @Test
    void shouldThrowExceptionWhenQuantityIsBlank() {
        String csvContent = "sku,name,description,price,quantity,category_id,category_name,category_description\n" +
                "SKU-PROD-001,Monitor Gamer 27,Monitor IPS 144Hz 1ms,1200.00,,cat-789,Monitores,Monitores e Displays";

        MockMultipartFile file = new MockMultipartFile("file", "products.csv", "text/csv", csvContent.getBytes());

        assertThrows(InvalidCsvRowContentException.class, () -> csvProductReaderAdapter.parseCsv(file));
    }

    @Test
    void shouldThrowExceptionWhenCategoryIdIsBlank() {
        String csvContent = "sku,name,description,price,quantity,category_id,category_name,category_description\n" +
                "SKU-PROD-001,Monitor Gamer 27,Monitor IPS 144Hz 1ms,1200.00,15,,Monitores,Monitores e Displays";

        MockMultipartFile file = new MockMultipartFile("file", "products.csv", "text/csv", csvContent.getBytes());

        assertThrows(InvalidCsvRowContentException.class, () -> csvProductReaderAdapter.parseCsv(file));
    }

    @Test
    void shouldThrowExceptionWhenCategoryNameIsBlank() {
        String csvContent = "sku,name,description,price,quantity,category_id,category_name,category_description\n" +
                "SKU-PROD-001,Monitor Gamer 27,Monitor IPS 144Hz 1ms,1200.00,15,cat-789,,Monitores e Displays";

        MockMultipartFile file = new MockMultipartFile("file", "products.csv", "text/csv", csvContent.getBytes());

        assertThrows(InvalidCsvRowContentException.class, () -> csvProductReaderAdapter.parseCsv(file));
    }
}