# inventory-service

The **inventory back office**. It manages product types, products (with min/max/available stock), price books, suppliers and purchase orders, with CSV import/export for products and suppliers.

> ⚠️ **Name clash:** product-service *also* registers as `inventory-service`. The gateway load-balances `/inventory/**` between the two apps, so a request can land on product-service, which doesn't have these endpoints, and get a 404. Until that's fixed, run only one of the two. See [docs/KNOWN_ISSUES.md](../docs/KNOWN_ISSUES.md#routing-and-service-discovery).

## At a glance

| | |
|---|---|
| Port | **8777** |
| Eureka name | `inventory-service` |
| Gateway prefix | `/inventory` |
| Database | MySQL `ms_mybiz_inventory` |
| Stack | Spring Boot 2.7.15 · Spring Cloud 2021.0.8 · Spring Data JPA · Log4j2 · OpenCSV / Commons CSV · springdoc |
| Base package | `com.clinch.crm.inventory` |
| Needs | MySQL schema, env var `GATEWAY_SECRET` |

## Run

There's no Maven wrapper in this module. Use a system `mvn`:

```bash
mvn spring-boot:run
```

Swagger UI: `http://localhost:8777/swagger-url.html` (needs `X-Gateway-Secret`).

## API

Through the gateway at `http://localhost:8556`. Every endpoint needs a JWT. `createdBy` / `modifiedBy` come from the `id` header set by the gateway. Paginated `list-*` endpoints take `?columnName=&sortOrder=&searchText=`.

### Product types

| Method | Path | Body / params |
|---|---|---|
| `GET` | `/inventory/check-duplicate-product-type` | `?productType=` |
| `POST` | `/inventory/create-product-type` | `ProductTypeBo` |
| `GET` | `/inventory/get-all-productType` | — |
| `GET` | `/inventory/get-productType/{productTypeId}` | — |
| `PUT` | `/inventory/update-productType` | `ProductTypeBo` |
| `DELETE` | `/inventory/delete-productType/{id}` | — |
| `GET` | `/inventory/list-product-type/{pageIndex}/{pageSize}` | list params |

### Products

| Method | Path | Body / params |
|---|---|---|
| `GET` | `/inventory/check-duplicate-product` | `?productName=` |
| `POST` | `/inventory/create-product` | `ProductBo` |
| `GET` | `/inventory/get-all-product` | — |
| `GET` | `/inventory/get-product/{productId}` | — |
| `PUT` | `/inventory/update-product` | `ProductBo` |
| `DELETE` | `/inventory/delete-product/{id}` | — |
| `GET` | `/inventory/list-product/{pageIndex}/{pageSize}` | list params |
| `GET` | `/inventory/export-product` | CSV download |
| `POST` | `/inventory/import-product` | multipart `file` |
| `POST` | `/inventory/get-duplicate-update` | `?productId=` |

### Price books

| Method | Path | Body / params |
|---|---|---|
| `GET` | `/inventory/check-duplicate-price-book` | `?priceBookName=` |
| `POST` | `/inventory/create-priceBook` | `PriceBookBO` |
| `GET` | `/inventory/get-priceBook` | — |
| `GET` | `/inventory/get-priceBookById/{priceBookId}` | — |
| `PUT` | `/inventory/update-priceBook` | `PriceBookBO` |
| `PUT` | `/inventory/change-pricebook-owner` | `PriceBookBO` |
| `DELETE` | `/inventory/delete-priceBook/{priceBookId}` | — |
| `GET` | `/inventory/list-pricebook/{pageIndex}/{pageSize}` | list params |

### Suppliers

| Method | Path | Body / params |
|---|---|---|
| `GET` | `/inventory/check-duplicate-supplier` | `?supplierName=` |
| `POST` | `/inventory/create-supplier` | `SupplierBO` |
| `GET` | `/inventory/get-all-supplier` | — |
| `GET` | `/inventory/get-supplier/{supplierId}` | — |
| `PUT` | `/inventory/update-supplier` | `SupplierBO` |
| `DELETE` | `/inventory/delete-supplier/{id}` | — |
| `GET` | `/inventory/list-supplier/{pageIndex}/{pageSize}` | list params |
| `GET` | `/inventory/export-supplier` | CSV download |
| `POST` | `/inventory/import-supplier` | multipart `file` |

### Purchase orders

| Method | Path | Body / params |
|---|---|---|
| `GET` | `/inventory/check-duplicate-subject` | `?subject=` |
| `POST` | `/inventory/create-purchaseOrder` | `PurchaseOrderBO` |
| `GET` | `/inventory/get-all-purchaseOrder` | — |
| `GET` | `/inventory/get-purchaseOrder/{purchaseOrderId}` | — |
| `PUT` | `/inventory/update-purchaseOrder` | `PurchaseOrderBO` |
| `DELETE` | `/inventory/delete-productOrder/{id}` | — (note `productOrder` in the path) |
| `GET` | `/inventory/list-purchase-order/{pageIndex}/{pageSize}` | list params |

Deletes are soft deletes (`isDelete = true`).

## Data model

| Entity → table | Fields |
|---|---|
| `ProductTypeVo` → `productType` | `productTypeId`, `productType`, `isActive`, `isDelete` |
| `ProductVo` → `product` | `productId`, `productName`, `productSpecification`, `productType`, `minStocks`, `maxStocks`, `avaliablesStocks` (sic), `startDate`, `endDate`, `isActive`, `isDelete` |
| `PriceBookVO` → `pricebook` | `priceBookId`, `priceBookName`, `priceBookOwner`, `assignEmpId`, `productId`, `productName`, `supplierName`, `specifyPrice`, `finalPrice`, `description`, `isActive`, `isDelete`, `createdBy`, `modifyiedBy` (sic), `CreatedTime`, `ModifiedTime` |
| `SupplierVO` → `supplier` | `supplierId`, `supplierName`, `emailId`, `mobileNo`, `address`, `city`, `state`, `country`, `location`, `website`, `techOriented`, `financialAmount`, `rating`, `isActive`, `isDelete`, `createdBy`, `modifyiedBy`, `CreatedTime`, `ModifiedTime` |
| `PurchaseOrderVO` → `purchase_order_table` | `purchaseOrderId`, `subject`, `salesOrderName`, `customerNo`, `accountName`, `assignedTo`, `status`, `purchaseOrderDate`, `dueDate`, `productName`, `quantity`, `unitPrice`, `listPrice`, `total`, `salesCommission`, `billingAddress`, `shippingAddress`, `termsAndConditions`, `description`, `isActive`, `isDelete`, `createdBy`, `modifiedBy`, `created`, `modified` |

product-service has its own, separate `ProductVo` table in a different database (`product_service`). The two product catalogues aren't synchronised.

## Calls and callers

No outgoing calls. It's called by clients through the gateway, and requests need `X-Gateway-Secret` (`gatewayauth/GatewayAuthFilter`).

## Configuration

| Property | Value |
|---|---|
| `server.port` | 8777 |
| `gateway.secret` | `${GATEWAY_SECRET}` |
| `spring.datasource.*` | `jdbc:mysql://localhost:3306/ms_mybiz_inventory`, `root` / `admin` |
| `management.endpoints.web.exposure.include` | `*` |
| logging | `log4j2-spring.xml` (Log4j2 starter + Splunk library) |

## Code layout

```
com.clinch.crm.inventory
├── controller/   InventoryController (all 41 endpoints)
├── service/  repository/
├── entity/       ProductTypeVo, ProductVo, PriceBookVO, SupplierVO, PurchaseOrderVO
├── model/        *Bo / *BO
├── csv/          import/export helpers
└── gatewayauth/  GatewayAuthFilter
```

## Tests

`InventoryServiceApplicationTests.contextLoads` only.

## Known issues

* 🔴 It shares its Eureka name with product-service.
* 🟡 A single ~870-line controller covers five sub-domains. Consider splitting it per sub-domain.
* 🟡 Inconsistent naming in paths and fields (`productType` / `productOrder`, `modifyiedBy`, `avaliablesStocks`).

See [docs/KNOWN_ISSUES.md](../docs/KNOWN_ISSUES.md).
