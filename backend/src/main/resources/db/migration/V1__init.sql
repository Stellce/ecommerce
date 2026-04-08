CREATE TABLE users (
    id uuid PRIMARY KEY,
    email varchar UNIQUE NOT NULL,
    password varchar NOT NULL,
    created_at timestamptz DEFAULT now() NOT NULL
);

CREATE TABLE roles (
    id int8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name varchar UNIQUE NOT NULL
);

CREATE TABLE user_roles (
    user_id uuid REFERENCES users(id) ON DELETE CASCADE,
    role_id int8 REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

INSERT INTO roles(name) VALUES ('USER'), ('ADMIN');

CREATE TABLE refresh_tokens (
    id int8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id uuid REFERENCES users(id) ON DELETE CASCADE NOT NULL,
    token varchar UNIQUE NOT NULL,
    created_at timestamptz DEFAULT now() NOT NULL,
    expires_at timestamptz NOT NULL,
    revoked boolean DEFAULT FALSE NOT NULL
);

CREATE TABLE products (
    id uuid PRIMARY KEY,
    name varchar NOT NULL,
    description varchar NOT NULL,
    price numeric(10,2) NOT NULL,
    stock int4 NOT NULL CHECK (stock >= 0)
);

CREATE TABLE orders (
    id uuid PRIMARY KEY,
    user_id uuid REFERENCES users(id) NOT NULL,
    status varchar NOT NULL,
    total_price numeric(10,2) NOT NULL,
    created_at timestamptz DEFAULT now() NOT NULL
);

CREATE TABLE order_items (
    id int8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id uuid REFERENCES orders(id) ON DELETE CASCADE NOT NULL,
    product_id uuid REFERENCES products(id) NOT NULL,
    quantity int4 NOT NULL,
    price_at_purchase numeric(10,2) NOT NULL,
    UNIQUE(order_id, product_id)
)