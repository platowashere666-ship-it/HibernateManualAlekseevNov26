package ru.academits.alekseev.hibernatemanualalekseevnov26;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.academits.alekseev.hibernatemanualalekseevnov26.entity.Category;
import ru.academits.alekseev.hibernatemanualalekseevnov26.entity.Product;

import java.util.List;

@SpringBootApplication
public class HibernateManualAlekseevNov26Application implements CommandLineRunner {
    @PersistenceContext
    private EntityManager entityManager;

    public static void main(String[] args) {
        SpringApplication.run(HibernateManualAlekseevNov26Application.class, args);
    }

    @Override
    @Transactional
    public void run(String @NonNull ... args) {
        System.out.println("Starting HibernateManualAlekseevNov26Application");

        Category food = new Category("Food");
        Category drinks = new Category("Drinks");

        Product meat = new Product("Meat", 499.0);
        Product bread = new Product("Bread", 49.0);
        Product milk = new Product("Milk", 99.0);
        Product cola = new Product("Cola", 199.0);

        food.addProduct(meat);
        food.addProduct(bread);

        drinks.addProduct(milk);
        drinks.addProduct(cola);

        entityManager.persist(food);
        entityManager.persist(drinks);

        entityManager.flush();
        entityManager.clear();

        System.out.println("Created categories and products");

        Product productToUpdate = entityManager.find(Product.class, meat.getId());

        if (productToUpdate != null) {
            System.out.println("Updating product " + productToUpdate);

            productToUpdate.setPrice(999.0);
            entityManager.merge(productToUpdate);

            System.out.println("Updated product " + productToUpdate);
        }

        Product productToDelete = entityManager.find(Product.class, cola.getId());

        if (productToDelete != null) {
            entityManager.remove(productToDelete);
            System.out.println("Deleted product " + productToDelete.getName());
        }

        entityManager.flush();
        entityManager.clear();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> criteriaQuery = criteriaBuilder.createQuery(Product.class);
        Root<Product> root = criteriaQuery.from(Product.class);

        root.fetch("category", JoinType.INNER);

        criteriaQuery.select(root);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("name")));

        List<Product> products = entityManager.createQuery(criteriaQuery).getResultList();

        for (Product product : products) {
            System.out.println("Product: " + product.getName() + ", price: "
                    + product.getPrice() + ", category: " + product.getCategory().getName());
        }

        System.out.println("Finishing HibernateManualAlekseevNov26Application");
    }
}
