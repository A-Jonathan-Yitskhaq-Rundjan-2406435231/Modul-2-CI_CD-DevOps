package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void createGeneratesIdWhenMissing() {
        Product product = new Product();
        product.setProductName("Keyboard");
        product.setProductQuantity(2);

        productService.create(product);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).create(captor.capture());
        Product saved = captor.getValue();
        assertNotNull(saved.getProductId());
        assertFalse(saved.getProductId().isBlank());
    }

    @Test
    void createKeepsExistingId() {
        Product product = new Product();
        product.setProductId("fixed-id");
        product.setProductName("Mouse");
        product.setProductQuantity(1);

        productService.create(product);

        verify(productRepository).create(product);
        assertEquals("fixed-id", product.getProductId());
    }

    @Test
    void findAllReturnsListFromRepository() {
        Product product1 = new Product();
        product1.setProductId("id-1");
        Product product2 = new Product();
        product2.setProductId("id-2");

        List<Product> data = new ArrayList<>();
        data.add(product1);
        data.add(product2);
        Iterator<Product> iterator = data.iterator();

        when(productRepository.findAll()).thenReturn(iterator);

        List<Product> result = productService.findAll();

        assertEquals(2, result.size());
        assertEquals("id-1", result.get(0).getProductId());
        assertEquals("id-2", result.get(1).getProductId());
    }

    @Test
    void deleteDelegatesToRepository() {
        productService.delete("id-1");
        verify(productRepository).deleteById("id-1");
    }

    @Test
    void findByIdDelegatesToRepository() {
        Product product = new Product();
        product.setProductId("id-1");
        when(productRepository.findById("id-1")).thenReturn(product);

        Product result = productService.findById("id-1");

        assertEquals("id-1", result.getProductId());
        verify(productRepository).findById("id-1");
    }

    @Test
    void editDelegatesToRepository() {
        Product product = new Product();
        product.setProductId("id-1");
        when(productRepository.edit(product)).thenReturn(product);

        Product result = productService.edit(product);

        assertEquals("id-1", result.getProductId());
        verify(productRepository).edit(product);
    }
}
