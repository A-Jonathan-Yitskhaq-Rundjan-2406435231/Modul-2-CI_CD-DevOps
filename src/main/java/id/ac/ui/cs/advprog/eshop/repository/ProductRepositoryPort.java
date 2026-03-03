package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;

import java.util.Iterator;

public interface ProductRepositoryPort {
    Product create(Product product);
    void deleteById(String id);
    Iterator<Product> findAll();
    Product findById(String id);
    Product edit(Product updatedProduct);
}
