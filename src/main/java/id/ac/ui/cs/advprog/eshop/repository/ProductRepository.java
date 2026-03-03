package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Repository
public class ProductRepository implements ProductRepositoryPort {
    private final List<Product> productData = new ArrayList<>();

    @Override
    public Product create(Product product) {
        productData.add(product);
        return product;
    }

    @Override
    public void deleteById(String id){
        productData.removeIf(product -> product.getProductId().equals(id));
    }

    @Override
    public Iterator<Product> findAll() {
        return productData.iterator();
    }

    @Override
    public Product findById(String id){
        for(Product product : productData){
            if(product.getProductId().equals(id)){
                return product;
            }
        }
        return null;
    }

    @Override
    public Product edit(Product updatedProduct) {
        for (Product product : productData) {
            if (product.getProductId().equals(updatedProduct.getProductId())) {
                product.setProductName(updatedProduct.getProductName());
                product.setProductQuantity(updatedProduct.getProductQuantity());
                return product;
            }
        }
        return null;
    }

}
