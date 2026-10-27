package com.inventory.control.web.system.ports.out;

import com.inventory.control.web.system.domain.model.Product;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ProductReaderPort {
    List<Product> parseCsv(MultipartFile file);
}