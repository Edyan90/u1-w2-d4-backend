package eddyTurpo;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Application {

    public static void main(String[] args) {
        Supplier<Long> randomNumberSupplier = () -> {
            Random rndm = new Random();
            return rndm.nextLong(1, 1000);
        };
        Supplier<Double> randomEuroSupplier = () -> {
            Random prezzo = new Random();
            return prezzo.nextDouble(1, 300);
        };
        Supplier<Product> prodottiSupplier = () -> {
            Faker f = new Faker(Locale.ITALY);
            return new Product(randomNumberSupplier.get(), f.book().title(), f.book().genre(), randomEuroSupplier.get());
        };
        Supplier<List<Product>> randomListProdotti = () -> {
            List<Product> prodotti = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                prodotti.add(prodottiSupplier.get());
            }
            return prodotti;
        };
        Supplier<Customer> customerSupplier = () -> {
            Faker f = new Faker(Locale.ITALY);
            return new Customer(randomNumberSupplier.get(), f.name().firstName(), f.number().numberBetween(1, 5));
        };
        Supplier<Order> orderSupplier = () -> {
            Faker f = new Faker(Locale.ITALY);
            Long id = randomNumberSupplier.get();
            String status = "In Consegna";
            LocalDate orderDate = LocalDate.now();
            LocalDate deliveryDate = orderDate.plusDays(1);
            List<Product> products = randomListProdotti.get();
            Customer customer = customerSupplier.get();
            return new Order(id, status, orderDate, deliveryDate, products, customer);
        };
        List<Order> ordini = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ordini.add(orderSupplier.get());
        }
        ordini.forEach(System.out::println);
        System.out.println("--------------------------ex1----------------------");


        Map<Customer, List<Order>> esercizio1 = ordini.stream()
                .collect(Collectors.groupingBy(Order::getCustomer));
        esercizio1.forEach((cliente, orders) -> System.out.println("Cliente: " + cliente.getName() + ", " + orders));


        System.out.println("--------------------------ex2----------------------");
        Map<Customer, Double> esercizio2 = ordini.stream().collect(Collectors.groupingBy(
                Order::getCustomer, Collectors.summingDouble(order -> order.getProducts().stream()
                        .mapToDouble(Product::getPrice)
                        .sum()
                )
        ));
        esercizio2.forEach((cliente, total) -> System.out.println("Cliente: " + cliente.getName() + ", Prezzo totale: " + total));
        System.out.println("--------------------------ex3----------------------");
        List<Product> prodottiEs = randomListProdotti.get();
        System.out.println(prodottiEs);
        System.out.println("filtro 3 prodotti più costosi: ");
        List<Product> top3ExpensiveProduct = prodottiEs.stream().
                sorted(Comparator.comparingDouble(Product::getPrice).
                        reversed())
                .limit(3)
                .toList();
        top3ExpensiveProduct.forEach(System.out::println);
        System.out.println("--------------------------ex4----------------------");
        OptionalDouble average = ordini.stream()
                .mapToDouble(order -> order.getProducts().stream().mapToDouble(Product::getPrice).sum())
                .average();
        average.ifPresent(avg -> System.out.println("La media degli importi degli ordini è: " + avg));
        System.out.println("--------------------------ex5----------------------");
        Map<String, Double> categoryGroup = prodottiEs.stream().
                collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.summingDouble(product -> product.getPrice())));
        categoryGroup.forEach((category, total) ->
                System.out.println("Categoria: " + category + ", Prezzo totale: " + total)
        );
        System.out.println("--------------------------ex6----------------------");
    }
}
