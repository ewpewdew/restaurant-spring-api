# 🍽️ План разработки Restaurant Backend

> Стек: Java 21 · Spring Boot 4.0 · PostgreSQL 17 · Flyway · JdbcClient · Docker

---

## Порядок действий (пошагово)

---

### Этап 1. 🔧 Доделать модуль User

Сейчас есть: создание пользователя и список всех. Нужно добить полноценный CRUD.

#### Шаг 1.1 — Валидация входных данных
**Файлы:**
- `CreateUserRequest` — добавить аннотации `@NotBlank`, `@Email`, `@Size`
- `UpdateUserRequest` — новый DTO (такой же как Create, но все поля необязательные)
- `GlobalExceptionHandler` — добавить обработчик `MethodArgumentNotValidException` (ловит ошибки валидации)

**Что сделать:**
- [ ] Добавить зависимость `spring-boot-starter-validation` в `pom.xml`
- [ ] Повесить аннотации на `CreateUserRequest`
- [ ] Создать `UpdateUserRequest`
- [ ] Добавить хендлер для `MethodArgumentNotValidException` в `GlobalExceptionHandler`

#### Шаг 1.2 — GET `/api/users/{id}` (получить по ID)
**Файлы:**
- `UserRepository` — добавить метод `Optional<User> findById(Long id)`
- `UserRepositoryImpl` — реализовать SQL-запрос
- `UserSql` — добавить константу `FIND_BY_ID`
- `UserService` / `UserServiceImpl` — добавить метод `getById(Long id)`
- `UserController` — добавить эндпоинт

**Логика:** если пользователь не найден → `NOT_FOUND`

#### Шаг 1.3 — PUT `/api/users/{id}` (обновить)
**Файлы:**
- `UserRepository` — добавить метод `void update(Long id, User user)`
- `UserRepositoryImpl` — реализовать `UPDATE` SQL (только те поля, которые переданы; `updated_at = NOW()`)
- `UserService` / `UserServiceImpl` — метод `update(Long id, UpdateUserRequest)`
- `UserController` — эндпоинт

**Логика:**
- Проверить, что пользователь существует (иначе `NOT_FOUND`)
- Если меняется email — проверить, что новый email не занят другим пользователем (`EMAIL_ALREADY_EXISTS`)
- Если меняется телефон — аналогично (`PHONE_ALREADY_EXISTS`)

#### Шаг 1.4 — DELETE `/api/users/{id}` (деактивировать)
**Файлы:**
- `UserRepository` — метод `void deactivate(Long id)` → `UPDATE users SET is_active = false`
- `UserService` / `UserServiceImpl` — метод `delete(Long id)`
- `UserController` — эндпоинт

**Логика:** soft-delete → `is_active = false`. Если уже неактивен → `NOT_FOUND`.

#### Шаг 1.5 — Адреса пользователя
**Файлы (новый модуль `address/` внутри `user/` или отдельно):**
- `Address` entity
- `AddressRepository` + `AddressRepositoryImpl`
- `AddressService` + `AddressServiceImpl`
- `AddressController`
- `AddressRowMapper`
- `CreateAddressRequest`, `AddressResponse`

**Эндпоинты:**
- `GET /api/users/{id}/addresses` — все адреса пользователя
- `POST /api/users/{id}/addresses` — добавить адрес
- `PUT /api/users/{id}/addresses/{addressId}` — обновить адрес
- `DELETE /api/users/{id}/addresses/{addressId}` — удалить адрес

#### Шаг 1.6 — Пагинация для списка
**Файлы:**
- `UserRepository` — метод `findAll(int page, int size)` возвращает `List<User>` + `int count()`
- `UserController` — принимать `?page=0&size=20`
- `UserResponse` — либо добавить поле `totalCount` в ответ, либо сделать обёртку `PagedResponse<T>`

---

### Этап 2. 📂 Категории (Category)

Создать модуль с нуля по шаблону `user/`.

#### Шаг 2.1 — Миграция
**Файл:** `V3__create_categories.sql`

```sql
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    display_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

#### Шаг 2.2 — Entity
**Файл:** `Category.java`
- Поля: `id`, `name`, `description`, `displayOrder`, `active`, `createdAt`, `updatedAt`
- Два конструктора (для создания и для чтения из БД) + пустой

#### Шаг 2.3 — RowMapper
**Файл:** `CategoryRowMapper.java`

#### Шаг 2.4 — Repository
**Файлы:** `CategoryRepository.java` + `CategoryRepositoryImpl.java`

Методы:
- `List<Category> findAll()`
- `Optional<Category> findById(Long id)`
- `Long save(Category category)` → возвращает сгенерированный ID
- `void update(Long id, Category category)`
- `void deactivate(Long id)`
- `boolean existsByName(String name)` — проверка уникальности

#### Шаг 2.5 — DTO
- `CreateCategoryRequest` (`name` обязательный, `description` и `displayOrder` опциональные)
- `UpdateCategoryRequest` (все поля опциональные)
- `CategoryResponse`

#### Шаг 2.6 — Service
**Файлы:** `CategoryService.java` + `CategoryServiceImpl.java`

Методы:
- `List<CategoryResponse> getAll()`
- `CategoryResponse getById(Long id)` → `NOT_FOUND`
- `CategoryResponse create(CreateCategoryRequest)` → проверить уникальность name
- `CategoryResponse update(Long id, UpdateCategoryRequest)` → проверить уникальность name
- `void delete(Long id)` → deactivate

#### Шаг 2.7 — Controller
**Файл:** `CategoryController.java`

| Метод | Путь | Действие |
|-------|------|----------|
| GET | `/api/categories` | Список всех |
| GET | `/api/categories/{id}` | По ID |
| POST | `/api/categories` | Создать |
| PUT | `/api/categories/{id}` | Обновить |
| DELETE | `/api/categories/{id}` | Деактивировать |

#### Шаг 2.8 — ErrorCode
Добавить `CATEGORY_NAME_ALREADY_EXISTS`, `CATEGORY_NOT_FOUND` в enum и хендлер.

---

### Этап 3. 🍕 Меню / Блюда (Menu)

#### Шаг 3.1 — Миграция
**Файл:** `V4__create_menu_items.sql`

```sql
CREATE TABLE menu_items (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    weight_grams INT,
    image_url VARCHAR(500),
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    is_vegan BOOLEAN NOT NULL DEFAULT FALSE,
    is_spicy BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_menu_items_category
        FOREIGN KEY (category_id) REFERENCES categories(id)
        ON DELETE RESTRICT
);
```

#### Шаг 3.2 — Entity
**Файл:** `MenuItem.java`
- Все поля из таблицы + конструкторы + геттеры/сеттеры

#### Шаг 3.3 — RowMapper
**Файл:** `MenuItemRowMapper.java`

#### Шаг 3.4 — Repository
**Файлы:** `MenuItemRepository.java` + `MenuItemRepositoryImpl.java`

Методы:
- `List<MenuItem> findAll(MenuFilter filter)` — с фильтрацией и пагинацией
- `Optional<MenuItem> findById(Long id)`
- `List<MenuItem> findByCategoryId(Long categoryId)`
- `Long save(MenuItem item)`
- `void update(Long id, MenuItem item)`
- `void setAvailable(Long id, boolean available)` — снять/вернуть в меню

**MenuFilter** — новый класс/record для фильтров:
- `categoryId`, `search`, `isVegan`, `isSpicy`, `minPrice`, `maxPrice`, `page`, `size`

#### Шаг 3.5 — DTO
- `CreateMenuItemRequest` (name, price, categoryId — обязательные)
- `UpdateMenuItemRequest`
- `MenuItemResponse` (включает название категории, а не только categoryId)
- `MenuFilterParams` — для принятия query-параметров в контроллере

#### Шаг 3.6 — Service
**Файлы:** `MenuItemService.java` + `MenuItemServiceImpl.java`

Методы:
- `PagedResponse<MenuItemResponse> getAll(MenuFilterParams filter)` — фильтрация + пагинация
- `MenuItemResponse getById(Long id)` → `MENU_ITEM_NOT_FOUND`
- `List<MenuItemResponse> getByCategory(Long categoryId)` — проверка, что категория существует
- `MenuItemResponse create(CreateMenuItemRequest)` — проверить, что category существует
- `MenuItemResponse update(Long id, UpdateMenuItemRequest)`
- `void delete(Long id)` → `is_available = false`

#### Шаг 3.7 — Controller
**Файл:** `MenuItemController.java`

| Метод | Путь | Действие |
|-------|------|----------|
| GET | `/api/menu` | Список с фильтрацией |
| GET | `/api/menu/{id}` | По ID |
| GET | `/api/menu/category/{categoryId}` | По категории |
| POST | `/api/menu` | Создать блюдо |
| PUT | `/api/menu/{id}` | Обновить |
| DELETE | `/api/menu/{id}` | Снять с меню |

#### Шаг 3.8 — ErrorCode
Добавить `MENU_ITEM_NOT_FOUND` в enum.

---

### Этап 4. 🛒 Заказы (Order)

Самый объёмный модуль. Две таблицы: `orders` и `order_items`.

#### Шаг 4.1 — Миграции
**Файл:** `V5__create_orders.sql`

```sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    address_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'NEW',
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    comment VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_orders_address FOREIGN KEY (address_id) REFERENCES user_addresses(id)
);
```

**Файл:** `V6__create_order_items.sql`

```sql
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    menu_item_id BIGINT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_menu FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
);
```

#### Шаг 4.2 — Enums
**Файл:** `OrderStatus.java` → `NEW, CONFIRMED, PREPARING, READY, DELIVERING, DELIVERED, CANCELLED`

#### Шаг 4.3 — Entities
- `Order.java` (id, userId, addressId, status, totalAmount, comment, createdAt, updatedAt)
- `OrderItem.java` (id, orderId, menuItemId, quantity, unitPrice, subtotal)

#### Шаг 4.4 — RowMappers
- `OrderRowMapper.java`
- `OrderItemRowMapper.java`

#### Шаг 4.5 — DTO
- `CreateOrderRequest`:
  ```java
  Long userId;
  Long addressId;
  String comment;        // опционально
  List<OrderItemDto> items;
  
  record OrderItemDto(Long menuItemId, int quantity) {}
  ```
- `OrderResponse` (с вложенным списком `List<OrderItemResponse> items`)
- `OrderItemResponse`
- `UpdateOrderStatusRequest` (`OrderStatus newStatus`)

#### Шаг 4.6 — Repository
**Файлы:** `OrderRepository.java` + `OrderRepositoryImpl.java`

Методы для `orders`:
- `List<Order> findAll(int page, int size)`
- `Optional<Order> findById(Long id)`
- `List<Order> findByUserId(Long userId)`
- `Long save(Order order)` — вставляет заказ и возвращает ID
- `void updateStatus(Long id, String status)`
- `int count()`

Методы для `order_items` (можно отдельный `OrderItemRepository`):
- `void saveAll(Long orderId, List<OrderItem> items)` — batch-insert
- `List<OrderItem> findByOrderId(Long orderId)`

#### Шаг 4.7 — Service
**Файлы:** `OrderService.java` + `OrderServiceImpl.java`

Методы:
- `PagedResponse<OrderResponse> getAll(int page, int size)`
- `OrderResponse getById(Long id)` → заказ + его items
- `List<OrderResponse> getByUser(Long userId)`
- `OrderResponse create(CreateOrderRequest request)` — **основная логика**:

```
1. Найти пользователя (userRepository.findById) → NOT_FOUND если нет
2. Найти адрес и проверить, что он принадлежит этому пользователю → NOT_FOUND
3. Для каждого item:
   - Найти MenuItem → MENU_ITEM_NOT_FOUND если нет
   - Проверить is_available == true → MENU_ITEM_NOT_AVAILABLE
   - Взять цену → unit_price, посчитать subtotal = price × quantity
4. Просуммировать все subtotal → total_amount
5. Сохранить Order со статусом NEW
6. Сохранить все OrderItem с order_id
7. Вернуть собранный OrderResponse
```

- `OrderResponse updateStatus(Long id, UpdateOrderStatusRequest)` — сменить статус
- `void cancel(Long id)` → статус CANCELLED (только если не DELIVERED)

**Валидация статусов (машина состояний):**
```
NEW → CONFIRMED, CANCELLED
CONFIRMED → PREPARING, CANCELLED
PREPARING → READY, CANCELLED
READY → DELIVERING, CANCELLED
DELIVERING → DELIVERED
DELIVERED → (конечный статус)
CANCELLED → (конечный статус)
```

#### Шаг 4.8 — Controller
**Файл:** `OrderController.java`

| Метод | Путь | Действие |
|-------|------|----------|
| GET | `/api/orders` | Список заказов |
| GET | `/api/orders/{id}` | Заказ + позиции |
| GET | `/api/orders/user/{userId}` | Заказы пользователя |
| POST | `/api/orders` | Создать заказ |
| PUT | `/api/orders/{id}/status` | Сменить статус |
| DELETE | `/api/orders/{id}` | Отменить |

#### Шаг 4.9 — ErrorCode
Добавить: `ORDER_NOT_FOUND`, `MENU_ITEM_NOT_AVAILABLE`, `INVALID_ORDER_STATUS_TRANSITION`, `ADDRESS_NOT_FOUND`, `ADDRESS_NOT_BELONG_TO_USER`

---

### Этап 5. 💳 Оплата (Payment)

#### Шаг 5.1 — Миграция
**Файл:** `V7__create_payments.sql`

```sql
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE,
    amount DECIMAL(10,2) NOT NULL CHECK (amount >= 0),
    method VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    paid_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(id)
);
```

#### Шаг 5.2 — Enums
- `PaymentMethod.java` → `CASH, CARD_ONLINE, CARD_COURIER`
- `PaymentStatus.java` → `PENDING, PAID, REFUNDED, FAILED`

#### Шаг 5.3 — Entity
**Файл:** `Payment.java`

#### Шаг 5.4 — RowMapper
**Файл:** `PaymentRowMapper.java`

#### Шаг 5.5 — DTO
- `CreatePaymentRequest` (`orderId`, `method`)
- `PaymentResponse`

#### Шаг 5.6 — Repository
**Файлы:** `PaymentRepository.java` + `PaymentRepositoryImpl.java`

Методы:
- `Optional<Payment> findById(Long id)`
- `Optional<Payment> findByOrderId(Long orderId)`
- `Long save(Payment payment)`
- `void updateStatus(Long id, String status, LocalDateTime paidAt)`
- `boolean existsByOrderId(Long orderId)` — проверка, что для заказа ещё нет платежа

#### Шаг 5.7 — Service
**Файлы:** `PaymentService.java` + `PaymentServiceImpl.java`

Методы:
- `PaymentResponse create(CreatePaymentRequest)`:

```
1. Найти заказ → ORDER_NOT_FOUND
2. Проверить, что для заказа ещё нет платежа → PAYMENT_ALREADY_EXISTS
3. Взять total_amount из заказа
4. Создать платёж со статусом PENDING
```

- `PaymentResponse getById(Long id)` → `PAYMENT_NOT_FOUND`
- `PaymentResponse getByOrderId(Long orderId)`
- `PaymentResponse pay(Long id)` → статус `PAID`, `paid_at = NOW()`
- `PaymentResponse refund(Long id)` → статус `REFUNDED` (только если был `PAID`)
- `PaymentResponse fail(Long id)` → статус `FAILED`

#### Шаг 5.8 — Controller
**Файл:** `PaymentController.java`

| Метод | Путь | Действие |
|-------|------|----------|
| POST | `/api/payments` | Создать платёж |
| GET | `/api/payments/{id}` | Платёж по ID |
| GET | `/api/payments/order/{orderId}` | Платёж по заказу |
| PUT | `/api/payments/{id}/pay` | Подтвердить оплату |
| PUT | `/api/payments/{id}/refund` | Возврат |
| PUT | `/api/payments/{id}/fail` | Пометить как ошибку |

#### Шаг 5.9 — ErrorCode
Добавить: `PAYMENT_NOT_FOUND`, `PAYMENT_ALREADY_EXISTS`, `INVALID_PAYMENT_STATUS_TRANSITION`

---

## 📋 Сводка всех файлов по этапам

| Этап | Новых файлов | Миграций |
|------|-------------|----------|
| 1. User (доделать) | ~6 (UpdateUserRequest, Address модуль, правки) | — |
| 2. Category | ~10 | V3 |
| 3. Menu | ~10 | V4 |
| 4. Order | ~14 | V5, V6 |
| 5. Payment | ~10 | V7 |
| **Итого** | **~50** | **5** |

---

## 🗄️ Финальный список миграций

```
V1__create_users.sql
V2__create_addresses.sql
V3__create_categories.sql
V4__create_menu_items.sql
V5__create_orders.sql
V6__create_order_items.sql
V7__create_payments.sql
```

---

## 🔐 Этап 6 — Аутентификация (на будущее, отдельным заходом)

- Spring Security + JWT
- Роли: `CUSTOMER`, `ADMIN`, `COURIER`
- Регистрация / логин / refresh-token
- Защита эндпоинтов по ролям
