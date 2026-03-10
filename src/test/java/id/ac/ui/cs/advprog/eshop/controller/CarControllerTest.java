package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Car;
import id.ac.ui.cs.advprog.eshop.service.CarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(CarController.class)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CarService carService;

    @Test
    void createCarPageRendersForm() throws Exception {
        mockMvc.perform(get("/car/createCar"))
                .andExpect(status().isOk())
                .andExpect(view().name("CreateCar"))
                .andExpect(model().attributeExists("car"));
    }

    @Test
    void createCarPostRedirectsToList() throws Exception {
        mockMvc.perform(post("/car/createCar")
                        .param("carName", "Civic")
                        .param("carColor", "Red")
                        .param("carQuantity", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("listCar"));

        verify(carService).create(any(Car.class));
    }

    @Test
    void carListPageShowsCars() throws Exception {
        Car car = new Car();
        car.setCarId("car-1");
        when(carService.findAll()).thenReturn(List.of(car));

        mockMvc.perform(get("/car/listCar"))
                .andExpect(status().isOk())
                .andExpect(view().name("CarList"))
                .andExpect(model().attributeExists("cars"));

        verify(carService).findAll();
    }

    @Test
    void editCarPageRendersForm() throws Exception {
        Car car = new Car();
        car.setCarId("car-1");
        when(carService.findById("car-1")).thenReturn(car);

        mockMvc.perform(get("/car/editCar/car-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("EditCar"))
                .andExpect(model().attributeExists("car"));

        verify(carService).findById("car-1");
    }

    @Test
    void editCarPostRedirectsToList() throws Exception {
        mockMvc.perform(post("/car/editCar")
                        .param("carId", "car-1")
                        .param("carName", "Civic")
                        .param("carColor", "Red")
                        .param("carQuantity", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("listCar"));

        verify(carService).update(any(String.class), any(Car.class));
    }

    @Test
    void deleteCarRedirectsToList() throws Exception {
        mockMvc.perform(post("/car/deleteCar")
                        .param("carId", "car-1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("listCar"));

        verify(carService).deleteCarById("car-1");
    }
}
