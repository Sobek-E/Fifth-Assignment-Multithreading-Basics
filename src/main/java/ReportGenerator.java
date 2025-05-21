import java.io.*;
import java.util.*;

public class ReportGenerator {
    static class TaskRunnable implements Runnable {
        private final String path;
        private double totalCost;
        private int totalAmount;
        private int totalDiscountSum;
        private int totalLines;
        private Product mostExpensiveProduct;
        private double highestCostAfterDiscount;

        public TaskRunnable(String path) {
            this.path = path;
            this.totalCost = 0;
            this.totalAmount = 0;
            this.totalDiscountSum = 0;
            this.totalLines = 0;
            this.highestCostAfterDiscount = 0;
            this.mostExpensiveProduct = null;
        }

        @Override
        public void run() {
            try (InputStream is = ReportGenerator.class.getClassLoader().getResourceAsStream(path)) {
                if (is == null) {
                    System.out.println("File not found: " + path);
                    return;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    totalLines++;
                    String[] parts = line.split(",");
                    int productId = Integer.parseInt(parts[0]);
                    int amount = Integer.parseInt(parts[1]);
                    int discount = Integer.parseInt(parts[2]);

                    Product product = findProductById(productId);
                    if (product == null) continue;

                    double totalPrice = product.getPrice() * amount;
                    double discountedPrice = totalPrice - discount;

                    totalCost += discountedPrice;
                    totalAmount += amount;
                    totalDiscountSum += discount;

                    if (discountedPrice > highestCostAfterDiscount) {
                        highestCostAfterDiscount = discountedPrice;
                        mostExpensiveProduct = product;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private Product findProductById(int id) {
            for (Product p : productCatalog) {
                if (p != null && p.getProductID() == id) {
                    return p;
                }
            }
            return null;
        }

        public void makeReport() {
            System.out.println("File: " + path);
            System.out.println("Total cost: " + totalCost);
            System.out.println("Total items bought: " + totalAmount);
            double averageDiscount = totalLines > 0 ? (double) totalDiscountSum / totalLines : 0;
            System.out.println("Average discount: " + averageDiscount);
            if (mostExpensiveProduct != null) {
                System.out.println("Most expensive purchase after discount: " +
                        mostExpensiveProduct.getProductName() + " for $" + highestCostAfterDiscount);
            }
            System.out.println();
        }
    }

    static class Product {
        private int productID;
        private String productName;
        private double price;

        public Product(int productID, String productName, double price) {
            this.productID = productID;
            this.productName = productName;
            this.price = price;
        }

        public int getProductID() {
            return productID;
        }

        public String getProductName() {
            return productName;
        }

        public double getPrice() {
            return price;
        }
    }

    private static final String[] ORDER_FILES = {
            "2021_order_details.txt",
            "2022_order_details.txt",
            "2023_order_details.txt",
            "2024_order_details.txt"
    };

    static Product[] productCatalog = new Product[10];

    public static void loadProducts() throws IOException {
        InputStream is = ReportGenerator.class.getClassLoader().getResourceAsStream("Products.txt");
        if (is == null) {
            throw new FileNotFoundException("Products.txt not found in resources.");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                double price = Double.parseDouble(parts[2]);
                productCatalog[index++] = new Product(id, name, price);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        try {
            loadProducts();

            List<TaskRunnable> tasks = new ArrayList<>();
            List<Thread> threads = new ArrayList<>();

            for (String file : ORDER_FILES) {
                TaskRunnable task = new TaskRunnable(file);
                Thread thread = new Thread(task);
                tasks.add(task);
                threads.add(thread);
                thread.start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            for (TaskRunnable task : tasks) {
                task.makeReport();
            }
        } catch (IOException e) {
            System.err.println("Error loading product catalog: " + e.getMessage());
        }
    }
}
