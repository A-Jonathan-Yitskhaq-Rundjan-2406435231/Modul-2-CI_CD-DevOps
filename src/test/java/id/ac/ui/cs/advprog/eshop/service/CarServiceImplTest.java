package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Car;
import id.ac.ui.cs.advprog.eshop.repository.CarRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    private CarRepositoryPort carRepository;

    @InjectMocks
    private CarServiceImpl carService;

    @Test
    void createDelegatesToRepository() {
        Car car = new Car();
        car.setCarId("car-1");
        when(carRepository.create(car)).thenReturn(car);

        Car result = carService.create(car);

        assertEquals("car-1", result.getCarId());
        verify(carRepository).create(car);
    }

    @Test
    void findAllReturnsList() {
        Car car = new Car();
        car.setCarId("car-1");
        Iterator<Car> iterator = List.of(car).iterator();
        when(carRepository.findAll()).thenReturn(iterator);

        List<Car> result = carService.findAll();

        assertEquals(1, result.size());
        assertEquals("car-1", result.get(0).getCarId());
    }

    @Test
    void findByIdDelegates() {
        Car car = new Car();
        car.setCarId("car-1");
        when(carRepository.findById("car-1")).thenReturn(car);

        Car result = carService.findById("car-1");

        assertEquals("car-1", result.getCarId());
        verify(carRepository).findById("car-1");
    }

    @Test
    void updateDelegates() {
        Car car = new Car();
        car.setCarId("car-1");

        carService.update("car-1", car);

        verify(carRepository).update("car-1", car);
    }

    @Test
    void deleteDelegates() {
        carService.deleteCarById("car-1");
        verify(carRepository).delete("car-1");
    }
}
