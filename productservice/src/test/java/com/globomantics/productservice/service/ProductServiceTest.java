package com.globomantics.productservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import com.globomantics.productservice.model.Product;
import com.globomantics.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.naming.directory.InvalidAttributesException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Tests the ProductService.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ProductServiceTest {

    /**
     * The service that we want to test.
     */
    @Autowired
    private ProductService service;

    /**
     * A mock version of the ProductRepository for use in our tests.
     */
    @MockBean
    private ProductRepository repository;

    @Test
    @DisplayName("Test findById Success")
    void testFindByIdSuccess() {
        // Setup our mock
        Product mockProduct = new Product(1, "Product Name", 10, 1);
        doReturn(Optional.of(mockProduct)).when(repository).findById(1);

        // Execute the service call
        Optional<Product> returnedProduct = service.findById(1);

        // Assert the response
        Assertions.assertTrue(returnedProduct.isPresent(), "Product was not found");
        Assertions.assertSame(returnedProduct.get(), mockProduct, "Products should be the same");
    }

    @Test
    @DisplayName("Test findById Not Found")
    void testFindByIdNotFound() {
        // Setup our mock
        // Product mockProduct = new Product(1, "Product Name", 10, 1);
        doReturn(Optional.empty()).when(repository).findById(1);

        // Execute the service call
        Optional<Product> returnedProduct = service.findById(1);

        // Assert the response
        Assertions.assertFalse(returnedProduct.isPresent(), "Product was found, when it shouldn't be");
    }

    @Test
    @DisplayName("Test findAll")
    void testFindAll() {
        // Setup our mock
        Product mockProduct = new Product(1, "Product Name", 10, 1);
        Product mockProduct2 = new Product(2, "Product Name 2", 15, 3);
        doReturn(Arrays.asList(mockProduct, mockProduct2)).when(repository).findAll();

        // Execute the service call
        List<Product> products = service.findAll();

        Assertions.assertEquals(2, products.size(), "findAll should return 2 products");
    }

    @Test
    @DisplayName("Test save product")
    void testSave() throws InvalidAttributesException {
        Product mockProduct = new Product(1, "Product Name", 10);
        doReturn(mockProduct).when(repository).save(any());

        Product returnedProduct = service.save(mockProduct);

        Assertions.assertNotNull(returnedProduct, "The saved product should not be null");
        Assertions.assertEquals(1, returnedProduct.getVersion().intValue(),
                "The version for a new product should be 1");
    }

    @Test()
    @DisplayName("Test save product- dup name should throw exception")
    void givenDuplicateName_should_throwException() throws InvalidAttributesException{
        // Assumption
        Product p1 = new Product(1, "Product Name", 10, 1);
        List<Product> products = new ArrayList<>();
        products.add(p1);
        doReturn(products).when(repository).findAll();

        Assertions.assertThrows(InvalidAttributesException.class,
                () -> {
                    service.save(new Product("Product Name", 5));
                } ,"重复名称的Product应该抛出异常");
    }

    @Test()
    @DisplayName("Test save product- no dup name should save successfully")
    void testNotEmptyList_should_PASS() throws InvalidAttributesException{
        // Assumption
        Product newProduct = new Product("Product Name", 10);
        Product mockProduct = new Product(2, "Product Name", 10, 1);

        Product p1 = new Product(1, "Product Name 01", 10, 1);
        List<Product> products = new ArrayList<>();
        products.add(p1);
        doReturn(products).when(repository).findAll();
        doReturn(mockProduct).when(repository).save(newProduct);

        Product actual = service.save(newProduct);

        Assertions.assertEquals(mockProduct, actual);
    }

    @Test()
    @DisplayName("Test save product- empty list should pass")
    void givenAnyName_withEmptyList_should_PASS() throws InvalidAttributesException{
        Product newProduct = new Product("Product Name", 20);

        Product mockProduct = new Product(1, "Product Name", 20, 1);
        List<Product> mockProducts = new ArrayList<>();
        doReturn(mockProducts).when(repository).findAll();
        doReturn(mockProduct).when(repository).save(any());

        Product returnedProduct = service.save(newProduct);

        Assertions.assertNotNull(returnedProduct, "The saved product should not be null");
        Assertions.assertEquals(1, returnedProduct.getVersion().intValue(),
                "The version for a new product should be 1");
        Assertions.assertEquals(20, returnedProduct.getQuantity().intValue(),
                "The quantity for a new product should be 20");
    }
}
