package com.velazco.velazco_back.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.velazco.velazco_back.model.Category;
import com.velazco.velazco_back.model.OrderDetail;
import com.velazco.velazco_back.model.OrderDetailId;
import com.velazco.velazco_back.model.Product;
import com.velazco.velazco_back.model.Sale;
import com.velazco.velazco_back.model.User;
import com.velazco.velazco_back.repositories.CategoryRepository;
import com.velazco.velazco_back.repositories.OrderRepository;
import com.velazco.velazco_back.repositories.ProductRepository;
import com.velazco.velazco_back.repositories.SaleRepository;
import com.velazco.velazco_back.repositories.UserRepository;

@Component
@Order(3)
public class MockDataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final SaleRepository saleRepository;
    private final UserRepository userRepository;

    public MockDataInitializer(ProductRepository productRepository, CategoryRepository categoryRepository,
                               OrderRepository orderRepository, SaleRepository saleRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
        this.saleRepository = saleRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Solo inicializar si hay muy pocos productos
        if (productRepository.count() > 15) {
            System.out.println("✅ Datos mockeados ya existen. Saltando inicialización.");
            return;
        }

        System.out.println("⏳ Generando datos mockeados (Productos, Historial de Ventas)...");

        // 1. Cargar Categorías
        Category panaderia = categoryRepository.findById(1L).orElse(null);
        Category pasteleria = categoryRepository.findById(2L).orElse(null);
        Category bebidas = categoryRepository.findById(3L).orElse(null);
        Category salados = categoryRepository.findById(4L).orElse(null);

        if (panaderia == null || pasteleria == null || bebidas == null || salados == null) {
            System.out.println("❌ Categorías no encontradas. Saltando inicialización.");
            return;
        }

        // 2. Crear Productos
        List<Product> products = new ArrayList<>();
        
        // Panadería
        products.add(createProduct("Ciabatta", 1.50, 60, panaderia, "ciabatta.jpg"));
        products.add(createProduct("Pan de Yema", 1.20, 80, panaderia, "pan_yema.jpg"));
        products.add(createProduct("Pan de Queso", 2.00, 40, panaderia, "pan_queso.jpg"));
        products.add(createProduct("Pan de Ajo", 2.50, 30, panaderia, "pan_ajo.jpg"));
        products.add(createProduct("Panetón Tradicional", 25.00, 10, panaderia, "paneton.jpg"));

        // Pastelería
        products.add(createProduct("Milhojas", 6.00, 15, pasteleria, "milhojas.jpg"));
        products.add(createProduct("Pionono", 5.50, 20, pasteleria, "pionono.jpg"));
        products.add(createProduct("Torta Tres Leches", 35.00, 5, pasteleria, "tres_leches.jpg"));
        products.add(createProduct("Alfajor de Maicena", 3.00, 40, pasteleria, "alfajor.jpg"));
        products.add(createProduct("Cheesecake de Fresa", 40.00, 3, pasteleria, "cheesecake.jpg"));
        products.add(createProduct("Brownie", 4.50, 25, pasteleria, "brownie.jpg"));

        // Bebidas
        products.add(createProduct("Jugo de Naranja", 6.00, 30, bebidas, "jugo_naranja.jpg"));
        products.add(createProduct("Chicha Morada", 5.00, 40, bebidas, "chicha.jpg"));
        products.add(createProduct("Limonada Frozen", 7.00, 25, bebidas, "limonada.jpg"));
        products.add(createProduct("Chocolate Caliente", 6.50, 20, bebidas, "chocolate_caliente.jpg"));
        products.add(createProduct("Mocachino", 8.00, 15, bebidas, "mocachino.jpg"));

        // Salados
        products.add(createProduct("Empanada de Pollo", 7.50, 20, salados, "empanada_pollo.jpg"));
        products.add(createProduct("Pastel de Acelga", 8.50, 15, salados, "pastel_acelga.jpg"));
        products.add(createProduct("Tequeños (12 un.)", 15.00, 10, salados, "tequenos.jpg"));
        products.add(createProduct("Triple de Pollo", 9.00, 12, salados, "triple_pollo.jpg"));
        products.add(createProduct("Quiche Loraine", 12.00, 8, salados, "quiche.jpg"));

        // Guardar todos los productos
        productRepository.saveAll(products);
        
        // Obtener productos ya existentes
        List<Product> allProducts = productRepository.findAll();

        // 3. Crear Historial de Órdenes y Ventas
        User cashier = userRepository.findById(2L).orElse(userRepository.findAll().get(0));
        Random rand = new Random();
        String[] paymentMethods = {"EFECTIVO", "TARJETA", "YAPE", "PLIN"};
        String[] clientNames = {"Juan Pérez", "María García", "Carlos López", "Ana Martínez", "Pedro Rodríguez", "Lucía Fernández", "Jorge Sánchez", "Marta Díaz"};

        // Generar 40 ventas en los últimos 30 días
        for (int i = 0; i < 40; i++) {
            LocalDateTime randomDate = LocalDateTime.now().minusDays(rand.nextInt(30)).minusHours(rand.nextInt(12)).minusMinutes(rand.nextInt(60));
            
            com.velazco.velazco_back.model.Order order = new com.velazco.velazco_back.model.Order();
            order.setClientName(clientNames[rand.nextInt(clientNames.length)]);
            order.setClientEmail("cliente" + i + "@test.com");
            order.setDate(randomDate);
            order.setStatus(com.velazco.velazco_back.model.Order.OrderStatus.PAGADO);
            order.setAttendedBy(cashier);
            com.velazco.velazco_back.model.Order savedOrder = orderRepository.save(order);

            int numProducts = rand.nextInt(4) + 1; // 1 a 4 productos
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<OrderDetail> details = new ArrayList<>();
            Set<Long> usedProductIds = new HashSet<>();

            for (int p = 0; p < numProducts; p++) {
                Product randomProd = allProducts.get(rand.nextInt(allProducts.size()));
                if (usedProductIds.contains(randomProd.getId())) {
                    continue;
                }
                usedProductIds.add(randomProd.getId());

                int qty = rand.nextInt(3) + 1; // 1 a 3
                BigDecimal subtotal = randomProd.getPrice().multiply(new BigDecimal(qty));
                totalAmount = totalAmount.add(subtotal);

                OrderDetail detail = new OrderDetail();
                detail.setId(new OrderDetailId(savedOrder.getId(), randomProd.getId()));
                detail.setOrder(savedOrder);
                detail.setProduct(randomProd);
                detail.setQuantity(qty);
                detail.setUnitPrice(randomProd.getPrice());
                
                details.add(detail);
            }
            savedOrder.setDetails(details);
            savedOrder = orderRepository.save(savedOrder);

            // Crear Venta
            Sale sale = new Sale();
            sale.setOrder(savedOrder);
            sale.setSaleDate(randomDate);
            sale.setPaymentMethod(paymentMethods[rand.nextInt(paymentMethods.length)]);
            sale.setTotalAmount(totalAmount);
            sale.setCashier(cashier);
            
            saleRepository.save(sale);
        }

        System.out.println("✅ Inicialización de datos completada con éxito!");
    }

    private Product createProduct(String name, double price, int stock, Category category, String image) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(new BigDecimal(price));
        p.setStock(stock);
        p.setCategory(category);
        p.setImage(image);
        p.setActive(true);
        return p;
    }
}
