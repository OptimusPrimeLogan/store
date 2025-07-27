const { faker}  = require('@faker-js/faker');

const N = 100; // Number of customers
const M = 10_000; // Number of orders
const P = 1000; // Number of products

/**
 * Escapes single quotes in a string for SQL by replacing them with two single quotes.
 * Eg., INSERT INTO customer (id, name) VALUES (92, 'Mrs. Jacquelyn D'Amore')]
 * @param {string} value The string to escape.
 * @returns {string} The escaped string.
 */
const escapeSqlString = (value) => {
    // In SQL, a single quote is escaped by doubling it.
    return value.replace(/'/g, "''");
};

// Generate customers
for (let i = 1; i <= N; i++) {
    const name = escapeSqlString(faker.name.fullName());
    console.log(`INSERT INTO customer (id, name) VALUES (${i}, '${name}');`);
}

console.log(`SELECT setval('customer_id_seq', COALESCE((SELECT MAX(id) FROM customer), 1), true);`);

// Generate orders
for (let i = 1; i <= M; i++) {
    const customerId = Math.ceil(Math.random() * N);
    console.log(`INSERT INTO "order" (id, order_number, customer_id) VALUES (${i}, gen_random_uuid(), ${customerId});`);
}

console.log(`SELECT setval('order_id_seq', COALESCE((SELECT MAX(id) FROM "order"), 1), true);`);

// Generate products
for (let i = 1; i <= P; i++) {
    // Product descriptions can also contain single quotes (e.g., "Men's Shoes")
    const description = escapeSqlString(faker.commerce.productName());
    console.log(`INSERT INTO product (id, description) VALUES (${i}, '${description}');`);
}

console.log(`SELECT setval('product_id_seq', COALESCE((SELECT MAX(id) FROM product), 1), true);`);

// Generate order items
for (let i = 1; i <= M; i++) {
    const orderId = i;
    const productCount = Math.ceil(Math.random() * 5); // Random number of products per order
    const productIdsForOrder = new Set();

    // Ensure we have a unique set of products for this order
    while (productIdsForOrder.size < productCount) {
        const productId = Math.ceil(Math.random() * P);
        productIdsForOrder.add(productId);
    }

    for (const productId of productIdsForOrder) {
        console.log(`INSERT INTO order_product (order_id, product_id) VALUES (${orderId}, ${productId});`);
    }
}

console.log(`SELECT setval('order_product_id_seq', COALESCE((SELECT MAX(id) FROM order_product), 1), true);`);