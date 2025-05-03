# monkey-specification-search
Monkey API Query Search By Specification

This project was created to help everyone to create a searchable Rest API :)

## Operations:

<!-- table of operators -->
| Operator       | Description                     | Example                                                                |
|----------------|---------------------------------|------------------------------------------------------------------------|
| `:`            | Equal                           | `invoiceKey:ABC123`, `seller.id:10`                                    |
| `!`            | Not equal                       | `invoiceKey!ABC123`, `seller.id!10`                                    |
| `>`            | Greater than equal              | `paymentValue>200`, `seller.id>10`, `createdAt>2021-12-23`             |
| `<`            | Less than equal                 | `paymentValue<200`, `seller.id<10`, `createdAt<2021-12-23`             |
| `*`            | Starts with                     | `invoiceNumber:*23`, `seller.name:*pe`                                 |
| `*`            | Ends with                       | `invoiceNumber:AB*`, `seller.name:Feli*`                               |
| `*`            | Contains                        | `invoiceNumber:*C12*`, `seller.name:*eli*`                             |
| `OR`           | Logical OR                      | `invoiceNumber:ABC123 OR seller.name:Felipe`                           |
| `AND`          | Logical AND                     | `invoiceNumber:ABC123 AND seller.name:Felipe`                          |
| `IN`           | Value is in list                | `invoiceNumber IN ['123', '456']`, `seller.id IN [1, 2]`               |
| `()`           | Parenthesis                     | `seller.name:Felipe* OR (invoiceNumber:123 AND createdAt:>2021-12-23)` |


## How to use:

Add a search parameter on your URL like this:

```javascript
?search=color:white AND car.field:foo AND car.field2!bar OR car.field3:200 OR car.field4 IN ['foo','bar']
```

## CustomSpecificationSearch

With `CustomSpecificationSearch`, you can execute specifications using a request parameter. Follow the steps below to use it:

### 1. Create a Custom Specification

Define a custom specification for your model. For example, to create a specification that checks if a product is in one of two specific states:

```java
public static Specification<Product> readyToUse() {
    return (root, criteriaQuery, criteriaBuilder) -> {
        return criteriaBuilder.or(
            criteriaBuilder.equal(root.get(Product_.status), ENUM_STATUS_1),
            criteriaBuilder.equal(root.get(Product_.status), ENUM_STATUS_2)
        );
    };
}
```

### 2. Add a `@Configuration` Class to Your Project

Create a class annotated with `@Configuration` to register the custom specification in CustomSpecificationSearch:

```java
@Configuration
class MonkeySpecificationSearchConfiguration {

	@PostConstruct
	void customSpecification() {
		CustomSpecificationSearch.getInstance().add("productReadyToUse", ProductSpecification.readyToUse());
	}

}
```

### 3. Call the API Using the Custom Predicate

You can now call your API using the customPredicate as a search parameter. For example:

`GET: host/v2/products?search=customPredicate:readyToUse`