package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class CarRepositoryTest {
    private CarRepository carRepository;

    @BeforeEach
    void setUp() {
        carRepository = new CarRepository();
    }

    @Test
    void createAssignsIdWhenMissing() {
        Car car = new Car();
        car.setCarName("Civic");
        car.setCarColor("Red");
        car.setCarQuantity(1);

        Car created = carRepository.create(car);

        assertNotNull(created.getCarId());
        assertFalse(created.getCarId().isBlank());
    }

    @Test
    void createKeepsExistingId() {
        Car car = new Car();
        car.setCarId("car-1");
        car.setCarName("Civic");
        car.setCarColor("Red");
        car.setCarQuantity(1);

        Car created = carRepository.create(car);

        assertEquals("car-1", created.getCarId());
    }

    @Test
    void findAllReturnsCars() {
        Car car = new Car();
        car.setCarId("car-1");
        carRepository.create(car);

        Iterator<Car> iterator = carRepository.findAll();

        assertTrue(iterator.hasNext());
        assertEquals("car-1", iterator.next().getCarId());
    }

    @Test
    void findByIdReturnsNullWhenMissing() {
        assertNull(carRepository.findById("missing"));
    }

    @Test
    void findByIdFindsSecondItem() {
        Car car1 = new Car();
        car1.setCarId("car-1");
        carRepository.create(car1);

        Car car2 = new Car();
        car2.setCarId("car-2");
        carRepository.create(car2);

        Car found = carRepository.findById("car-2");
        assertNotNull(found);
        assertEquals("car-2", found.getCarId());
    }

    @Test
    void updateReturnsUpdatedCar() {
        Car car = new Car();
        car.setCarId("car-1");
        car.setCarName("Civic");
        car.setCarColor("Red");
        car.setCarQuantity(1);
        carRepository.create(car);

        Car updated = new Car();
        updated.setCarName("City");
        updated.setCarColor("Blue");
        updated.setCarQuantity(2);

        Car result = carRepository.update("car-1", updated);

        assertNotNull(result);
        assertEquals("City", result.getCarName());
        assertEquals("Blue", result.getCarColor());
        assertEquals(2, result.getCarQuantity());
    }

    @Test
    void updateReturnsNullWhenMissing() {
        Car updated = new Car();
        updated.setCarName("City");
        assertNull(carRepository.update("missing", updated));
    }

    @Test
    void updateSecondItem() {
        Car car1 = new Car();
        car1.setCarId("car-1");
        carRepository.create(car1);

        Car car2 = new Car();
        car2.setCarId("car-2");
        car2.setCarName("Old");
        carRepository.create(car2);

        Car updated = new Car();
        updated.setCarName("New");
        updated.setCarColor("Black");
        updated.setCarQuantity(2);

        Car result = carRepository.update("car-2", updated);
        assertNotNull(result);
        assertEquals("New", result.getCarName());
    }

    @Test
    void deleteRemovesCar() {
        Car car = new Car();
        car.setCarId("car-1");
        carRepository.create(car);

        carRepository.delete("car-1");

        assertNull(carRepository.findById("car-1"));
    }
}
