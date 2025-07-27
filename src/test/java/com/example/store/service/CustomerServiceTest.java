package com.example.store.service;

import com.example.store.dto.request.CreateCustomerRequest;
import com.example.store.dto.response.CustomerDTO;
import com.example.store.dto.response.CustomerOrderDTO;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    private Customer customer;
    private CustomerDTO customerDTOWithOrders;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");

        Order order = new Order();
        order.setId(101L);
        order.setOrderNumber(UUID.randomUUID());
        order.setCustomer(customer);

        customer.setOrders(Set.of(order));

        CustomerOrderDTO customerOrderDTO = new CustomerOrderDTO(order.getId(), order.getOrderNumber());
        customerDTOWithOrders = new CustomerDTO(customer.getId(), customer.getName(), Set.of(customerOrderDTO));
    }

    @Test
    void findCustomers_shouldReturnAllCustomers_whenQueryIsBlank() {
        List<Customer> customers = List.of(customer);
        when(customerRepository.findAll()).thenReturn(customers);
        when(customerMapper.customersToCustomerDTOs(customers)).thenReturn(List.of(customerDTOWithOrders));

        List<CustomerDTO> result = customerService.findCustomers(null);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("John Doe");
        assertThat(result.get(0).orders()).hasSize(1);

        verify(customerRepository, times(1)).findAll();
        verify(customerRepository, never()).findByNameContainingIgnoreCase(anyString());
    }

    @Test
    void findCustomers_shouldReturnFilteredCustomers_whenQueryIsProvided() {
        String query = "John";
        List<Customer> customers = List.of(customer);
        when(customerRepository.findByNameContainingIgnoreCase(query)).thenReturn(customers);
        when(customerMapper.customersToCustomerDTOs(customers)).thenReturn(List.of(customerDTOWithOrders));

        List<CustomerDTO> result = customerService.findCustomers(query);

        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("John Doe");
        assertThat(result.get(0).orders()).hasSize(1);

        verify(customerRepository, times(1)).findByNameContainingIgnoreCase(query);
        verify(customerRepository, never()).findAll();
    }

    @Test
    void createCustomer_shouldSaveAndReturnCustomerDTO() {
        CreateCustomerRequest request = new CreateCustomerRequest("Jane Doe");
        Customer newCustomer = new Customer();
        newCustomer.setName(request.name());

        Customer savedCustomer = new Customer();
        savedCustomer.setId(2L);
        savedCustomer.setName(request.name());

        CustomerDTO expectedDTO = new CustomerDTO(2L, request.name(), Collections.emptySet());

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);

        when(customerRepository.save(customerCaptor.capture())).thenReturn(savedCustomer);
        when(customerMapper.customerToCustomerDTO(savedCustomer)).thenReturn(expectedDTO);

        CustomerDTO createdCustomer = customerService.createCustomer(request);

        assertThat(createdCustomer).isNotNull();
        assertThat(createdCustomer.name()).isEqualTo("Jane Doe");
        assertThat(createdCustomer.orders()).isNotNull().isEmpty();

        Customer capturedCustomer = customerCaptor.getValue();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getId()).isNull();

        verify(customerRepository, times(1)).save(any(Customer.class));
        verify(customerMapper, times(1)).customerToCustomerDTO(any(Customer.class));
    }
}
