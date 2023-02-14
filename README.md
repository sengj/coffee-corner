# Coffee Corner

## Overview
Coffee corner is an application to order products with a bonus program and print the receipt.

### Constraints
* Only drinks can be ordered with extra ingredients
* Can't order the same extra on a drink
* Extra can't be ordered on an extra
* Extra can't be ordered without a drink
* Drink or Snack can't be ordered as extra

### Requirements
* Java version 17
* Apache Maven version 3.x

## Install

Compile project with Maven

``` bash
$ mvn clean package
```

## Usage
### Code Example 
``` java
	final int numberOfCurrentStamp = 0;
	final Product extraMilk = coffeeCorner.getProductService().findProductByName(EXTRA_MILK);
	final Product foamedMilk = coffeeCorner.getProductService().findProductByName(FOAMED_MILK);
	final Product smallCoffeeWithExtraMilkAndFoamedMilk = coffeeCorner.getProductService().findProductByName(SMALL_COFFEE);
	smallCoffeeWithExtraMilkAndFoamedMilk.addExtra(extraMilk);
	smallCoffeeWithExtraMilkAndFoamedMilk.addExtra(foamedMilk);
	final Product largeCoffee = coffeeCorner.getProductService().findProductByName(LARGE_COFFEE);
	final Product baconRoll = coffeeCorner.getProductService().findProductByName(BACON_ROLL);

	final List<Product> productsToOrder = new ArrayList<>();
	productsToOrder.add(smallCoffeeWithExtraMilkAndFoamedMilk);
	productsToOrder.add(largeCoffee);
	productsToOrder.add(baconRoll1);

	coffeeCorner.order(productsToOrder);
	coffeeCorner.applyDiscounts(numberOfCurrentStamp);
	coffeeCorner.printReceipt();
```

For more examples, check '/coffee-corner/src/test/java/com/jseng/coffee/corner/CoffeeCornerApplicationTest.java'

### Ouput Example 

``` bash

----------------------------------------------------
--------------Charlene's Coffee Corner--------------
----------------------------------------------------

----------------------Products----------------------
small coffee                                2.50 CHF
  - with extra milk                         0.30 CHF
  - with foamed milk                        0.50 CHF
small coffee                                2.50 CHF
large coffee                                3.50 CHF
large coffee                                3.50 CHF
bacon roll                                  4.50 CHF
bacon roll                                  4.50 CHF
freshly squeezed orange juice               3.95 CHF
freshly squeezed orange juice               3.95 CHF

---------------------Discounts----------------------
extra milk                                 -0.30 CHF
foamed milk                                -0.50 CHF
freshly squeezed orange juice              -3.95 CHF

----------------------------------------------------
Total                                      24.95 CHF
----------------------------------------------------

----------------------------------------------------
--------------Thank you for your visit--------------
----------------------------------------------------
```


