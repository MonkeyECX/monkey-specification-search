# monkey-specification-search
Monkey API Query Search By Specification

This project was created to help everyone to create a searchable Rest API :)

### How to use:

Add a search parameter on your URL like this:

```javascript
?search=color:white AND car.field:foo AND car.field2!bar OR car.field3:200
```

#### Aggregators: 
AND

OR

#### Operations:

EQUAL: car.field:foo

NOT: car.field!foo

GREATER_THAN_EQUAL: car.field>200

LESS_THAN_EQUAL: car.field>2021-12-23

CONTAINS: car.field:FO*, car.field:*OO

NOT_CONTAINS: car.field!FO*, car.field!*OO
