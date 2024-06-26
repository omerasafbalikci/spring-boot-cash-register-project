# Market Checkout Project

This project is a Spring Boot based microservice project that provides market checkout functionality. Each microservice undertakes specific tasks and these services come together to form a complete system.

## Technologies used:

- Java 17
- Spring Boot 3.2.4
- Maven
- Spring Security
- JWT (JSON Web Token)
- Docker
- PostgreSQL
- Log4j2
- Spring Webflux
- ModelMapper
- jUnit5

## Microservices

1. **Discovery Server:** A centralized registration server that allows microservices to find and communicate with each other.


2. **API Gateway:** An intermediate server that allows clients to access all microservices through a single entry point, managing functions such as request routing and security.


3. **Authentication Authorization Service:** Responsible for authentication. It provides token authentication, token invalidation and token security.
   - Required Role: ADMIN
   - Endpoints:
       - Login: `POST /auth/login`
       - Logout: `POST /auth/logout`
       - Change Password: `PUT /auth/change-password`
       - Verify: `GET /auth/verify`


4. **User Management Service:** Performs operations such as listing, creating, updating, deleting users, adding or removing roles. It is related to the authentication and authorization service.
   - Required Role: ADMIN
   - Endpoints:
       - Get Users: `GET /api/user-management/get-all`
       - Add User: `POST /api/user-management/create`
       - Update User: `PUT /api/user-management/update`
       - Delete User: `PUT /api/user-management/delete`
       - Add Role: `PUT /api/user-management/role/add/{user_id}`
       - Remove Role: `PUT /api/user-management/role/remove/{user_id}`


4. **Product Service:** Products and categories; It performs operations such as listing, creating, updating and deleting. Checks whether the products are in stock and takes necessary actions.
    - Required Role: NONE
    - Endpoints for product categories:
        - Get Category: `GET /api/product-categories/get-all`
        - Search Category: `GET /api/product-categories/search`
        - Get Category By ID: `GET /api/product-categories/id`
        - Get Products By Category ID: `GET /api/product-categories/products`
        - Add Category: `POST /api/product-categories/add`
        - Update Category: `PUT /api/product-categories/update`
        - Delete Category: `DELETE /api/product-categories/delete`

    - Endpoints for products:
        - Get Products: `GET /api/products/get-all`
        - Search Product: `GET /api/products/search`
        - Add Product: `POST /api/products/add`
        - Update Product: `PUT /api/products/update`
        - Delete Product: `DELETE /api/products/delete`


4. **Sales Service:** It performs operations such as listing, creating, updating and deleting campaigns. It also carries out sales and returns transactions.
    - Required Role: CASHIER
    - Endpoints for campaigns:
        - Get Campaigns: `GET /api/campaigns/get-all`
        - Get Campaign By Campaign Number: `GET /api/campaigns/campaign-number`
        - Add Campaign: `POST /api/campaigns/create`
        - Update Campaign: `PUT /api/campaigns/update`
        - Delete Campaign: `DELETE /api/campaigns/delete`
        - Delete All Campaign: `DELETE /api/campaigns/delete-all`

    - Endpoints for sales:
        - Add Sales: `POST /api/sales/add`
        - Return Sale: `POST /api/sales/return`
        - Delete Sale: `DELETE /api/sales/delete`
        - Get Sales Statistics: `DELETE /api/sales/statistics`


4. **Report Service:** It performs functions such as listing sales and creating a PDF receipt for the desired sale.
    - Required Role: MANAGER
    - Endpoints for reports:
        - Get Sales: `GET /api/reports/get-all`
        - Generate PDF: `GET /api/reports/generate.pdf`


## Getting Started

To start the project without docker, follow these steps:

1. Clone the repository: `git clone https://github.com/omerasafbalikcii/springboot-microservices-market-checkout.git`
2. Build the project using maven:
   `mvn clean install`
3. Go to each microservice directory and
   `mvn spring-boot:run`
   Start it separately by typing the command.
4. The project worked successfully.

To stop:
- Press `CTRL+C` for each microservice started.

To start the project with docker, follow these steps:

1. Clone the repository: `git clone https://github.com/omerasafbalikcii/springboot-microservices-market-checkout.git`
2. Build the project using maven:
   `mvn clean install`
3. Go to the root directory of the project
   `docker-compose up --build`
   by typing the command
   run it.
4. The project worked successfully.

To stop:
- Go to the root directory of the project
`docker-compose down`
by typing the command
run it.

## User Guide
- By default, there is a user with username: 'admin' and password: 'admin'.
  You should use this until you open a new account.
- You must use the token obtained during login for all requests made to the services.

## Docker
- A special container has been created for each microservice in the project.
- The base image used for containers is `openjdk:17-jdk-slim`.
- The project includes a PostgreSQL database shared between all microservices.
- Data persistence is ensured using `postgres_data`.
- `safe` network is a virtual network that provides communication between containers. All services and database communicate over this network. The network provides isolation and secure communication of containers.


## API Documentation

### Product Service

### Endpoints for product categories

#### Add product category

Endpoint: `POST /api/product-categories/add`  
Description: Adds new category to the database. 'name' and 'createdBy' must not be null or blank.

Request Body:

```json
{
    "name": "Yiyecek",
    "description": "İndirimli fiyatlarla tüm yiyecekler burada!",
    "imageUrl": "www.images.com/yiyecek",
    "createdBy": "Ömer Asaf Balıkçı"
}
```

Response:

```json
{
    "id": 1,
    "categoryNumber": "c5348973",
    "name": "Yiyecek",
    "description": "İndirimli fiyatlarla tüm yiyecekler burada!",
    "imageUrl": "www.images.com/yiyecek",
    "createdBy": "Ömer Asaf Balıkçı",
    "updatedAt": "2024-06-24T04:12:03.9179019"
}
```

#### Update product category

Endpoint: `PUT /api/product-categories/update`  
Description: Updates a category in the database. 'id' and 'createdBy' must not be null or blank.

Request Body:

```json
{
    "id": 1,
    "name": "Yiyecek",
    "description": "Aradığın tüm yiyecekler burada!",
    "imageUrl": "www.images.com/yiyecek",
    "createdBy": "Ömer Asaf Balıkçı"
}
```

Response:

```json
{
    "id": 1,
    "categoryNumber": "c5348973",
    "name": "Yiyecek",
    "description": "Aradığın tüm yiyecekler burada!",
    "imageUrl": "www.images.com/yiyecek",
    "createdBy": "Ömer Asaf Balıkçı",
    "updatedAt": "2024-06-24T04:19:56.5311684"
}
```

#### Delete product category

Endpoint: `DELETE /api/product-categories/delete`  
Description: Deletes a category in the database.

Request Parameters:

| Key  | Value |
|------|-------|
| `id` | `3`   |

Response:

```json
{
    "id": 3,
    "categoryNumber": "dd656db3",
    "name": "Giyim",
    "description": "Bedenine uygun giysiler burada!",
    "imageUrl": "www.images.com/giyim",
    "createdBy": "Ömer Asaf Balıkçı",
    "updatedAt": "2024-06-24T19:02:28.270345"
}
```

#### Get all product categories

Endpoint: `GET /api/product-categories/get-all`  
Description: Lists all categories.

Response:

```json
[
    {
      "id": 1,
      "categoryNumber": "c5348973",
      "name": "Yiyecek",
      "description": "Aradığın tüm yiyecekler burada!",
      "imageUrl": "www.images.com/yiyecek",
      "createdBy": "Ömer Asaf Balıkçı",
      "updatedAt": "2024-06-24T04:19:56.531168"
    },
    {
      "id": 2,
      "categoryNumber": "f17b09f9",
      "name": "İçecek",
      "description": "Buz gibi ferahlatan içecekler!",
      "imageUrl": "www.images.com/içecek",
      "createdBy": "Ömer Asaf Balıkçı",
      "updatedAt": "2024-06-24T04:23:37.268661"
    }
]
```

#### Search product category

Endpoint: `GET /api/product-categories/search`  
Description: It searches within all categories.

Request Parameters:

| Key    | Value     |
|--------|-----------|
| `name` | `yiyecek` |

Response:

```json
[
    {
      "id": 1,
      "categoryNumber": "c5348973",
      "name": "Yiyecek",
      "description": "Aradığın tüm yiyecekler burada!",
      "imageUrl": "www.images.com/yiyecek",
      "createdBy": "Ömer Asaf Balıkçı",
      "updatedAt": "2024-06-24T04:19:56.531168"
    }
]
```

#### Get product category by id

Endpoint: `GET /api/product-categories/id`  
Description: Returns category by id.

Request Parameters:

| Key  | Value |
|------|-------|
| `id` | `1`   |

Response:

```json
{
    "id": 1,
    "categoryNumber": "c5348973",
    "name": "Yiyecek",
    "description": "Aradığın tüm yiyecekler burada!",
    "imageUrl": "www.images.com/yiyecek",
    "createdBy": "Ömer Asaf Balıkçı",
    "updatedAt": "2024-06-24T04:19:56.531168"
}
```

#### Get products by product category id

Endpoint: `GET /api/product-categories/products`  
Description: Lists products by category ID.

Request Parameters:

| Key          | Value |
|--------------|-------|
| `categoryId` | `1`   |

Response:

```json
[
    {
      "id": 1,
      "barcodeNumber": "c12d30cb",
      "name": "Ekmek",
      "description": "Karbonhidrat",
      "quantity": 100,
      "unitPrice": 10.0,
      "state": true,
      "imageUrl": "www.images.com/yiyecek/ekmek",
      "createdBy": "Ömer Asaf Balıkçı",
      "updatedAt": "2024-06-24T23:11:06.776624",
      "productCategoryName": "Yiyecek"
    },
    {
      "id": 2,
      "barcodeNumber": "43e1c32f",
      "name": "Et",
      "description": "Protein",
      "quantity": 150,
      "unitPrice": 500.0,
      "state": true,
      "imageUrl": "www.images.com/yiyecek/et",
      "createdBy": "Ömer Asaf Balıkçı",
      "updatedAt": "2024-06-24T23:25:59.84176",
      "productCategoryName": "Yiyecek"
    }
]
```

### Endpoints for products

#### Add product

Endpoint: `POST /api/products/add`  
Description: Adds new product to the database. 'name', 'quantity', 'unitPrice', 'state', 'createdBy', 'productCategoryId' must not be null or blank.

Request Body:

```json
{
    "name": "Ekmek",
    "description": "Karbonhidrat",
    "quantity": 100,
    "unitPrice": 10,
    "state": true,
    "imageUrl": "www.images.com/yiyecek/ekmek",
    "createdBy": "Ömer Asaf Balıkçı",
    "productCategoryId": 1
}
```

Response:

```json
{
    "id": 1,
    "barcodeNumber": "c12d30cb",
    "name": "Ekmek",
    "description": "Karbonhidrat",
    "quantity": 100,
    "unitPrice": 10.0,
    "state": true,
    "imageUrl": "www.images.com/yiyecek/ekmek",
    "createdBy": "Ömer Asaf Balıkçı",
    "updatedAt": "2024-06-24T23:11:06.7766242",
    "productCategoryName": "Yiyecek"
}
```

#### Update product

Endpoint: `PUT /api/products/update`  
Description: Updates a product in the database. 'id' and 'createdBy' must not be null or blank.

Request Body:

```json
{
    "id": 2,
    "name": "Et",
    "description": "Protein",
    "quantity": 150,
    "unitPrice": 500,
    "state": true,
    "imageUrl": "www.images.com/yiyecek/et",
    "createdBy": "Ömer Asaf Balıkçı",
    "productCategoryId": 1
}
```

Response:

```json
{
    "id": 2,
    "barcodeNumber": "43e1c32f",
    "name": "Et",
    "description": "Protein",
    "quantity": 150,
    "unitPrice": 500.0,
    "state": true,
    "imageUrl": "www.images.com/yiyecek/et",
    "createdBy": "Ömer Asaf Balıkçı",
    "updatedAt": "2024-06-24T23:21:18.7316078",
    "productCategoryName": "Yiyecek"
}
```

#### Delete product

Endpoint: `DELETE /api/products/delete`   
Description: Deletes a product in the database.

Request Parameters:

| Key  | Value |
|------|-------|
| `id` | `4`   |

Response:

```json
{
    "id": 4,
    "barcodeNumber": "894831ec",
    "name": "Kola",
    "description": "Sağlıksız",
    "quantity": 50,
    "unitPrice": 50.0,
    "state": true,
    "imageUrl": "www.images.com/içecek/kola",
    "createdBy": "Ömer Asaf Balıkçı",
    "updatedAt": "2024-06-24T23:30:34.073054",
    "productCategoryName": "İçecek"
}
```

#### Get all products

Endpoint: `GET /api/products/get-all`  
Description: Paging, filtering and sorting all products
brings with.

Request Parameters:

| Parameter                | Type      | Description               |
|--------------------------|-----------|---------------------------|
| `id`                     | `Long`    | Filter for id             |
| `barcodeNumber`          | `String`  | Filter for barcode number |
| `state`                  | `Boolean` | Filter for state          |
| `page`                   | `Integer` | Page number               |
| `size`                   | `Integer` | Page size                 |
| `sort`                   | `String`  | Sort (id,asc)             |

Response:

```json
{
    "currentPage": 0,
    "products": [
      {
        "id": 1,
        "barcodeNumber": "c12d30cb",
        "name": "Ekmek",
        "description": "Karbonhidrat",
        "quantity": 100,
        "unitPrice": 10.0,
        "state": true,
        "imageUrl": "www.images.com/yiyecek/ekmek",
        "createdBy": "Ömer Asaf Balıkçı",
        "updatedAt": "2024-06-24T23:11:06.776624",
        "productCategoryName": "Yiyecek"
      },
      {
        "id": 2,
        "barcodeNumber": "43e1c32f",
        "name": "Et",
        "description": "Protein",
        "quantity": 150,
        "unitPrice": 500.0,
        "state": true,
        "imageUrl": "www.images.com/yiyecek/et",
        "createdBy": "Ömer Asaf Balıkçı",
        "updatedAt": "2024-06-24T23:25:59.84176",
        "productCategoryName": "Yiyecek"
      },
      {
        "id": 3,
        "barcodeNumber": "f2396dab",
        "name": "Su",
        "description": "Damla",
        "quantity": 150,
        "unitPrice": 5.0,
        "state": true,
        "imageUrl": "www.images.com/içecek/su",
        "createdBy": "Ömer Asaf Balıkçı",
        "updatedAt": "2024-06-24T23:27:46.841515",
        "productCategoryName": "İçecek"
      }
    ],
    "totalItems": 3,
    "totalPages": 1
}
```

#### Search products

Endpoint: `GET /api/products/search`  
Description: Paging, filtering and sorting all products with search operation.

Request Parameters:

| Key    | Value    |
|--------|----------|
| `name` | `e`      |
| `page` | `0`      |
| `size` | `3`      |
| `sort` | `id,asc` |

Response:

```json
{
    "currentPage": 0,
    "products": [
      {
        "id": 1,
        "barcodeNumber": "c12d30cb",
        "name": "Ekmek",
        "description": "Karbonhidrat",
        "quantity": 100,
        "unitPrice": 10.0,
        "state": true,
        "imageUrl": "www.images.com/yiyecek/ekmek",
        "createdBy": "Ömer Asaf Balıkçı",
        "updatedAt": "2024-06-24T23:11:06.776624",
        "productCategoryName": "Yiyecek"
      },
      {
        "id": 2,
        "barcodeNumber": "43e1c32f",
        "name": "Et",
        "description": "Protein",
        "quantity": 150,
        "unitPrice": 500.0,
        "state": true,
        "imageUrl": "www.images.com/yiyecek/et",
        "createdBy": "Ömer Asaf Balıkçı",
        "updatedAt": "2024-06-24T23:25:59.84176",
        "productCategoryName": "Yiyecek"
      }
    ],
    "totalItems": 2,
    "totalPages": 1
}
```

### Sales Service

### Endpoints for campaigns

#### Add campaign

Endpoint: `POST /api/campaigns/add`  
Description: Adds new campaign to the database. 'name', (one of 'buyPay', 'percent', 'moneyDiscount'), 'state', 'createdBy' must not be null or blank.

Request Body:

```json
{
    "name": "3 AL 2 ÖDE!",
    "buyPay": "3,2",
    "state": true,
    "createdBy": "Ömer Asaf Balıkçı"
}
```

Response:

```json
{
    "id": 1,
    "campaignNumber": "998af4f5",
    "name": "3 AL 2 ÖDE!",
    "buyPay": "3,2",
    "percent": null,
    "moneyDiscount": null,
    "campaignType": 1,
    "state": true,
    "createdBy": "Ömer Asaf Balıkçı",
    "updatedAt": "2024-06-25T00:01:04.6899114"
}
```

#### Update campaign

Endpoint: `PUT /api/campaigns/update`  
Description: Updates a campaign in the database. 'id' and 'createdBy' must not be null or blank.

Request Body:

```json
{
    "id": 3,
    "name": "%50 İNDİRİM!",
    "percent": "50",
    "state": true,
    "createdBy": "Ömer Asaf Balıkçı"
}
```

Response:

```json
{
    "id": 3,
    "campaignNumber": "0a56d5a2",
    "name": "%50 İNDİRİM!",
    "buyPay": null,
    "percent": 50,
    "moneyDiscount": null,
    "campaignType": 2,
    "state": true,
    "createdBy": "Ömer Asaf Balıkçı",
    "updatedAt": "2024-06-25T00:06:58.6953634"
}
```

#### Delete campaign

Endpoint: `DELETE /api/campaigns/delete`  
Description: Deletes a campaign in the database.

Request Parameters:

| Key  | Value |
|------|-------|
| `id` | `5`   |

Response:

```json
{
    "id": 5,
    "campaignNumber": "511d6299",
    "name": "50 TL İNDİRİM!",
    "buyPay": null,
    "percent": null,
    "moneyDiscount": 50,
    "campaignType": 3,
    "state": true,
    "createdBy": "Ömer Asaf Balıkçı",
    "updatedAt": "2024-06-25T00:09:06.126004"
}
```

#### Delete all campaigns

Endpoint: `DELETE /api/campaigns/delete-all`  
Description: Deletes all campaigns in the database.

Response:

```json
"Deletion successful!"
```

#### Get all campaigns

Endpoint: `GET /campaigns/get-all`  
Description: Lists all campaigns.

Response:

```json
[
    {
      "id": 1,
      "campaignNumber": "998af4f5",
      "name": "3 AL 2 ÖDE!",
      "buyPay": "3,2",
      "percent": null,
      "moneyDiscount": null,
      "campaignType": 1,
      "state": true,
      "createdBy": "Ömer Asaf Balıkçı",
      "updatedAt": "2024-06-25T00:01:04.689911"
    },
    {
      "id": 2,
      "campaignNumber": "d3167c9b",
      "name": "5 AL 4 ÖDE!",
      "buyPay": "5,4",
      "percent": null,
      "moneyDiscount": null,
      "campaignType": 1,
      "state": false,
      "createdBy": "Ömer Asaf Balıkçı",
      "updatedAt": "2024-06-25T00:03:20.207655"
    },
    {
      "id": 4,
      "campaignNumber": "209af7c6",
      "name": "30 TL İNDİRİM!",
      "buyPay": null,
      "percent": null,
      "moneyDiscount": 30,
      "campaignType": 3,
      "state": true,
      "createdBy": "Ömer Asaf Balıkçı",
      "updatedAt": "2024-06-25T00:05:23.498168"
    },
    {
      "id": 3,
      "campaignNumber": "0a56d5a2",
      "name": "%50 İNDİRİM!",
      "buyPay": null,
      "percent": 50,
      "moneyDiscount": null,
      "campaignType": 2,
      "state": true,
      "createdBy": "Ömer Asaf Balıkçı",
      "updatedAt": "2024-06-25T00:06:58.695363"
    }
]
```

#### Get campaign by campaign number

Endpoint: `GET /api/campaigns/campaign-number`  
Description: Returns campaign by campaign number.

Request Parameters:

| Key              | Value      |
|------------------|------------|
| `campaignNumber` | `998af4f5` |

Response:

```json
{
    "id": 1,
    "campaignNumber": "998af4f5",
    "name": "3 AL 2 ÖDE!",
    "buyPay": "3,2",
    "percent": null,
    "moneyDiscount": null,
    "campaignType": 1,
    "state": true,
    "createdBy": "Ömer Asaf Balıkçı",
    "updatedAt": "2024-06-25T00:01:04.689911"
}
```

### Endpoints for sales

#### Add sales

Endpoint: `POST /api/sales/add`   
Description: Adds new sales to the database. 'createSalesItemsRequests' and 'createdBy' must not be null or blank.

Request Body:

```json
{
    "createdBy": "Ömer Asaf Balıkçı",
    "paymentType": "CASH",
    "money": 2100,
    "createSalesItemsRequests": [
      {
        "barcodeNumber": "c12d30cb",
        "quantity": 5
      },
      {
        "barcodeNumber": "43e1c32f",
        "quantity": 6,
        "campaignId": 1
      }
    ]
}
```

Response:

```json
{
    "id": 2,
    "salesNumber": "0fc31194",
    "salesDate": "2024-06-25T02:21:42.20881",
    "createdBy": "Ömer Asaf Balıkçı",
    "paymentType": "CASH",
    "totalPrice": 2050.0,
    "money": 2100.0,
    "change": 50.0,
    "salesItemsList": [
      {
        "id": 3,
        "barcodeNumber": "c12d30cb",
        "name": "Ekmek",
        "quantity": 5,
        "unitPrice": 10.0,
        "state": true,
        "totalPrice": 50.0,
        "paymentType": "CASH",
        "campaignName": null
      },
      {
        "id": 4,
        "barcodeNumber": "43e1c32f",
        "name": "Et",
        "quantity": 6,
        "unitPrice": 500.0,
        "state": true,
        "totalPrice": 2000.0,
        "paymentType": "CASH",
        "campaignName": "3 AL 2 ÖDE!"
      }
    ]
}
```

#### Return product

Endpoint: `POST /api/sales/return`   
Description: Returns the product. 'salesNumber', 'barcodeNumber', 'quantity' must not be null or blank.

Request Body:

```json
[
    {
      "salesNumber": "df779111",
      "barcodeNumber": "c12d30cb",
      "quantity": 1
    }
]
```

Response:

```json
[
    {
      "id": 5,
      "barcodeNumber": "c12d30cb",
      "name": "Ekmek",
      "quantity": 4,
      "unitPrice": 10.0,
      "state": true,
      "totalPrice": 40.0,
      "paymentType": "CARD",
      "campaignName": null
    }
]
```

#### Delete sales

Endpoint: `DELETE /api/sales/delete`   
Description: Deletes the sale from the database.

Request Parameters:

| Key           | Value      |
|---------------|------------|
| `salesNumber` | `0fc31194` |

Response:

```json
{
    "id": 2,
    "salesNumber": "0fc31194",
    "salesDate": "2024-06-25T02:21:42.20881",
    "createdBy": "Ömer Asaf Balıkçı",
    "paymentType": "CASH",
    "totalPrice": 2050.0,
    "money": 2100.0,
    "change": 50.0,
    "salesItemsList": [
      {
        "id": 3,
        "barcodeNumber": "c12d30cb",
        "name": "Ekmek",
        "quantity": 5,
        "unitPrice": 10.0,
        "state": true,
        "totalPrice": 50.0,
        "paymentType": "CASH",
        "campaignName": null
      },
      {
        "id": 4,
        "barcodeNumber": "43e1c32f",
        "name": "Et",
        "quantity": 6,
        "unitPrice": 500.0,
        "state": true,
        "totalPrice": 2000.0,
        "paymentType": "CASH",
        "campaignName": "3 AL 2 ÖDE!"
      }
    ]
}
```

#### Get sales statistics

Endpoint: `GET /api/sales/statistics`   
Description: Returns some sales statistics within the specified date range.

Request Parameters:

| Key         | Value                 |
|-------------|-----------------------|
| `startDate` | `2024-06-25 02:18:37` |
| `endDate`   | `2024-06-25 02:21:43` |

Response:

```json
{
    "averageSales": 1550.0,
    "totalSales": 3100.0,
    "totalSalesCount": 2
}
```

### Report Service

### Endpoints for reports

#### Get all sales

Endpoint: `GET /api/reports/get-all`  
Description: Paging, filtering and sorting all sales
brings with.

Request Parameters:

| Parameter     | Type      | Description             |
|---------------|-----------|-------------------------|
| `id`          | `Long`    | Filter for id           |
| `salesNumber` | `String`  | Filter for sales number |
| `salesDate`   | `String`  | Filter for sales date   |
| `createdBy`   | `String`  | Filter for created by   |
| `paymentType` | `String`  | Filter for payment type |
| `totalPrice`  | `Double`  | Filter for total price  |
| `money`       | `Double`  | Filter for money        |
| `change`      | `Double`  | Filter for change       |
| `page`        | `Integer` | Page number             |
| `size`        | `Integer` | Page size               |
| `sort`        | `String`  | Sort (id,asc)           |

Response:

```json
{
    "content": [
      {
        "id": 1,
        "salesNumber": "6d3a3c66",
        "salesDate": "2024-06-25T02:18:37.533878",
        "createdBy": "Ömer Asaf Balıkçı",
        "paymentType": "CASH",
        "totalPrice": 1050.0,
        "money": 1100.0,
        "change": 50.0,
        "salesItemsList": [
          {
            "id": 1,
            "barcodeNumber": "c12d30cb",
            "name": "Ekmek",
            "quantity": 5,
            "unitPrice": 10.0,
            "state": true,
            "totalPrice": 50.0,
            "paymentType": "CASH",
            "campaignName": null
          },
          {
            "id": 2,
            "barcodeNumber": "43e1c32f",
            "name": "Et",
            "quantity": 3,
            "unitPrice": 500.0,
            "state": true,
            "totalPrice": 1000.0,
            "paymentType": "CASH",
            "campaignName": "3 AL 2 ÖDE!"
          }
        ]
      },
      {
        "id": 2,
        "salesNumber": "0fc31194",
        "salesDate": "2024-06-25T02:21:42.20881",
        "createdBy": "Ömer Asaf Balıkçı",
        "paymentType": "CASH",
        "totalPrice": 2050.0,
        "money": 2100.0,
        "change": 50.0,
        "salesItemsList": [
          {
            "id": 3,
            "barcodeNumber": "c12d30cb",
            "name": "Ekmek",
            "quantity": 5,
            "unitPrice": 10.0,
            "state": true,
            "totalPrice": 50.0,
            "paymentType": "CASH",
            "campaignName": null
          },
          {
            "id": 4,
            "barcodeNumber": "43e1c32f",
            "name": "Et",
            "quantity": 6,
            "unitPrice": 500.0,
            "state": true,
            "totalPrice": 2000.0,
            "paymentType": "CASH",
            "campaignName": "3 AL 2 ÖDE!"
          }
        ]
      },
      {
        "id": 3,
        "salesNumber": "df779111",
        "salesDate": "2024-06-25T02:28:46.534322",
        "createdBy": "Ömer Asaf Balıkçı",
        "paymentType": null,
        "totalPrice": 1696.6666666666667,
        "money": 2050.0,
        "change": 353.333333333333,
        "salesItemsList": [
          {
            "id": 5,
            "barcodeNumber": "c12d30cb",
            "name": "Ekmek",
            "quantity": 3,
            "unitPrice": 10.0,
            "state": true,
            "totalPrice": 30.0,
            "paymentType": "CARD",
            "campaignName": null
          },
          {
            "id": 6,
            "barcodeNumber": "43e1c32f",
            "name": "Et",
            "quantity": 5,
            "unitPrice": 500.0,
            "state": true,
            "totalPrice": 1666.6666666666667,
            "paymentType": "CASH",
            "campaignName": "3 AL 2 ÖDE!"
          }
        ]
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 3,
      "totalElements": 3,
      "totalPages": 1
    }
}
```

#### Generate pdf report

Endpoint: `GET /api/reports/generate.pdf`  
Description: Generates pdf report of sales.

Request Parameters:

| Key           | Value      |
|---------------|------------|
| `salesNumber` | `6d3a3c66` |

Response:

```json
%PDF-1.6
%����
1 0 obj
<<
/Type /Catalog
/Version /1.6
/Pages 2 0 R
>>
endobj
10 0 obj
<<
/Length 9176
/Filter /FlateDecode
/Length1 13672
>>
stream
```

### User Management Service

#### Get All Users

Endpoint: `GET /user-management/get-all`  
Description: Lists user with paging, filtering and sorting.

Request Params:

| Parameter   | Type      | Description               |
|-------------|-----------|---------------------------|
| `id`        | `Long`    | Filter for id             |
| `firstName` | `String`  | Filter for user firstname |
| `lastName`  | `String`  | Filter for user lastname  |
| `email`     | `String`  | Filter for user email     |
| `username`  | `String`  | Filter for user username  |
| `gender`    | `String`  | Filter for user gender    |
| `page`      | `Integer` | Page number               |
| `size`      | `Integer` | Page size                 |
| `sort`      | `String`  | Sort (id,asc)             |

Response:

```json
{
    "content": [
      {
        "id": 2,
        "firstName": "Ömer Asaf",
        "lastName": "Balıkçı",
        "username": "omerasaf",
        "email": "omerasaf@gmail.com",
        "deleted": false,
        "roles": [
          "MANAGER"
        ],
        "gender": "MALE"
      },
      {
        "id": 3,
        "firstName": "Sevda",
        "lastName": "Aktaş",
        "username": "sevda",
        "email": "sevdaaktas@gmail.com",
        "deleted": false,
        "roles": [
          "CASHIER"
        ],
        "gender": "FEMALE"
      },
    {
      "id": 4,
      "firstName": "Kadir Can",
      "lastName": "Balıkçı",
      "username": "kadircan",
      "email": "kadircan@gmail.com",
      "deleted": false,
      "roles": [
        "CASHIER",
        "ADMIN"
      ],
      "gender": "MALE"
    }
    ],
    "pageable": {
      "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
      },
      "offset": 0,
      "pageNumber": 0,
      "pageSize": 5,
      "paged": true,
      "unpaged": false
    },
    "last": true,
    "totalElements": 3,
    "totalPages": 1,
    "size": 5,
    "number": 0,
    "sort": {
      "empty": true,
      "sorted": false,
      "unsorted": true
    },
    "first": true,
    "numberOfElements": 3,
    "empty": false
}
```

#### Create User

Endpoint: `POST /user-management/create`  
Description: Creates new user.

Request Body:

```json
{
  "firstName": "Asaf",
  "lastName": "Balıkçı",
  "username": "asa7",
  "email": "asa7@gmail.com",
  "password": "Fenerbahce1234",
  "roles": [
    "MANAGER"
  ],
  "gender": "MALE"
}
```

Response:

```json
{
  "id": 5,
  "firstName": "Asaf",
  "lastName": "Balıkçı",
  "username": "asa7",
  "email": "asa7@gmail.com",
  "deleted": false,
  "roles": [
    "MANAGER"
  ],
  "gender": "MALE"
}
```

#### Update User

Endpoint: `PUT /user-management/update`  
Description: Updates existing user.  

Request Body:

```json
{
  "id": 3,
  "firstName": "Ömer Asaf",
  "lastName": "Balıkçı",
  "username": "asaf",
  "email": "asaf@gmail.com",
  "gender": "FEMALE"
}
```

Response:

```json
{
  "id": 3,
  "firstName": "Ömer Asaf",
  "lastName": "Balıkçı",
  "username": "asaf",
  "email": "asaf@gmail.com",
  "deleted": true,
  "roles": [
    "MANAGER"
  ],
  "gender": "FEMALE"
}
```

#### Delete User

Endpoint: `PUT /user-management/delete`  
Description: Soft deletes user.

Request Parameters:

| Key  | Value |
|------|-------|
| `id` | `3`   |

Response:

```json
{
  "id": 3,
  "firstName": "Ömer Asaf",
  "lastName": "Balıkçı",
  "username": "asaf",
  "email": "asaf@gmail.com",
  "deleted": true,
  "roles": [
    "CASHIER"
  ],
  "gender": "FEMALE"
}
```

#### Add User Role

Endpoint: `PUT /user-management/role/add/{user_id}`  
Description: Adds role to user. Roles: ADMIN, CASHIER, MANAGER

Request Body:

```json
"CASHIER"
```

Response:

```json
{
  "id": 5,
  "firstName": "Asaf",
  "lastName": "Balıkçı",
  "username": "asaf",
  "email": "asaf@gmail.com",
  "deleted": false,
  "roles": [
    "CASHIER",
    "MANAGER"
  ],
  "gender": "MALE"
}
```

#### Remove User Role

Endpoint: `PUT /user-management/role/remove/{user_id}`
Description: Removes role from user. Roles: ADMIN, CASHIER, MANAGER  

Request Body:

```json
"MANAGER"
```

Response:

```json
{
  "id": 5,
  "firstName": "Asaf",
  "lastName": "Balıkçı",
  "username": "asaf",
  "email": "asaf@gmail.com",
  "deleted": false,
  "roles": [
    "CASHIER"
  ],
  "gender": "MALE"
}
```

## Authentication-Authorization Service

#### Login

Endpoint: `POST /auth/login`  
Description: Login for users.  

Request Body:

```json
{
  "username": "admin",
  "password": "admin"
}
```

Response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJhNjc1ZGEzYi0yOWEzLTRlMjAtYTFhYy0xODQxZjU2Y2FmZjkiLCJzdWIiOiJhZG1pbiIsImlhdCI6MTY4OTUzNzMxMSwiZXhwIjoxNjg5NTQwOTExfQ.FotzY4QeIIJls8DRFcbZCP5uJEeUS4idzgLUU2SCGjU"
}
```

#### Logout

Endpoint: `POST /auth/logout`  
Description: Logout for users.

Request: Uses token for logout.

Response:

#### Change Password

Endpoint: `PUT /auth/change-password`  
Description: Changes password.  

Request Body:

```json
{
  "oldPassword": "admin",
  "newPassword": "newPassword"
}
```

Response:
`Status: 200 OK`

## Contributing

We welcome contributions to this open-source project! Whether you want to add new features or fix existing issues, your efforts are greatly appreciated. To contribute, please follow these steps:

1. **Fork the Repository**: Fork this repository to your own GitHub account.
2. **Develop Your Feature or Fix**: Make the necessary changes in your forked repository.
3. **Create a Pull Request (PR)**: Submit a pull request with your changes to the main repository.

Thank you for helping us improve this project!

Happy coding!


