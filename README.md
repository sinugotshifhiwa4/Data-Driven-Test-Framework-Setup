# Data-Driven Test Framework Setup

A flexible test automation framework that supports both JSON and Excel data sources, featuring data validation, caching, and thread-safe operations.

## Features

- **Multiple Data Sources**: Support for both JSON and Excel test data
- **JSON Schema Validation**: Ensures data integrity through schema validation
- **Caching**: Implements thread-safe caching for improved performance
- **Flexible Data Providers**: Easy access to test data through custom data providers
- **Error Handling**: Comprehensive error handling and logging
- **Thread Safety**: Concurrent execution support
- **Data Combination**: Support for combining multiple data sources
- **Index-based Data Retrieval**: Ability to fetch specific data by index

## Prerequisites

- Java 11 or higher
- Maven 3.6.x or higher
- Git

## Dependencies

- Jackson for JSON processing
- Apache POI for Excel handling
- TestNG for test execution
- Log4j2 for logging

## Project Structure

```
src/
├── main/
│   └── java/
│       └── com/
│           └── data/
│               └── driven/
│                   ├── config/
│                   │   ├── excel/
│                   │   │   └── ExcelDataProviderConfig.java
│                   │   ├── jackson/
│                   │   │   ├── JsonDataConfig.java
│                   │   │   └── JsonDataReader.java
│                   │   └── dataProvider/
│                   │       ├── ExcelDataProviderConfig.java
│                   │       └── JsonDataProviderConfig.java
│                   └── utils/
│                       └── ErrorHandler.java
└── test/
    └── java/
        └── com/
            └── data/
                └── driven/
                    ├── base/
                    │   ├── TestBase.java
                    │   └── TestSetupManager.java
                    ├── config/
                    │   ├── constants/
                    │   ├── dataProvider/
                    │   ├── excel/
                    │   └── paths/
                    ├── dataProvider/
                    ├── tests/
                    └── resources/
```

## Usage Examples

### Excel Data Provider Examples

1. **Fetching Data by Index**:
```java
@DataProvider(name = "Location")
public Object[][] getLocationDataByIndex() {
    return ExcelDataProviderConfig.getValueByIndex(
            TestResourcePath.ADACTIN_HOTEL_EXCEL.getPath(),
            BOOKING_SHEET,
            "Location",
            5
    );
}
```

2. **Combining Multiple Data Sources**:
```java
@DataProvider(name = "BookingData")
public Iterator<Object[]> getCombinedData() {
    List<Iterator<Object[]>> dataIterators = Arrays.asList(
            getBookingData(),
            getPaymentData()
    );

    return ExcelDataCombiner.combineIteratorData(dataIterators).iterator();
}
```

### JSON Data Provider Examples

1. **Reading Integer Data List**:
```java
@DataProvider(name = "ExpiryYearDataList")
public Iterator<Object[]> ExpiryYearDataList() {
    try {
        return JsonDataProviderConfig.getIntegerDataList("ExpiryYear");
    } catch (Exception error) {
        ErrorHandler.logError(error, "ExpiryYearDataList",
            "Failed to retrieve expiry year data list");
        throw new RuntimeException(
            "Failed to retrieve expiry year data list",
            error
        );
    }
}
```

## Running Tests

### Running All Tests
```bash
mvn test -Denv=development
```

### Running JSON Data Tests
```bash
mvn clean test -Denv=json-loader
```

### Running Excel Data Tests
```bash
mvn test -Denv=excel-loader
```

### Running Specific Test Method
```bash
mvn clean test -Dtest=<className>#<methodName>
# Example
mvn clean test -Dtest=PaymentDataExcelTests#shouldRetrieveCardCvvNumber
```

### Running Tests by Groups

Run specific group:
```bash
mvn clean test -Dgroups="data-loader"
```

Exclude specific group:
```bash
mvn clean test -DexcludedGroups="json-loader"
```

## Configuration

### JSON Configuration

- Place JSON data files in `src/test/resources/testData`
- JSON schema files should be in `src/test/resources/schemas`
- Configure the file paths in `test.properties`

Example JSON data structure:
```json
{
  "Locations": [
    "Sydney",
    "Melbourne",
    "Brisbane",
    "Adelaide",
    "London",
    "New York",
    "Los Angeles",
    "Paris"
  ],
  "Hotels": [
    "Hotel Creek",
    "Hotel Sunshine",
    "Hotel River View",
    "Hotel Cornice"
  ],
  "RoomTypes": [
    "Standard",
    "Deluxe",
    "Executive",
    "Suite"
  ]
}
```

Example JSON Schema:
```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "type": "object",
  "properties": {
    "Locations": {
      "type": "array",
      "items": { "type": "string" },
      "minItems": 1
    },
    "Hotels": {
      "type": "array",
      "items": { "type": "string" },
      "minItems": 1
    },
    "RoomTypes": {
      "type": "array",
      "items": { "type": "string" },
      "minItems": 1
    }
  },
  "required": ["Locations", "Hotels", "RoomTypes"]
}
```

### Excel Configuration

- Place Excel files in `src/test/resources/testData/excel`
- Ensure column headers match the expected data keys

## Error Handling

The framework includes comprehensive error handling:
- Validation of input parameters
- Schema validation for JSON data
- Data type validation
- Thread-safe operations
- Detailed error logging through ErrorHandler utility
- Custom exception handling for data providers

## Best Practices

1. Always use try-with-resources for data readers
2. Validate data before usage
3. Use appropriate data providers for different data types
4. Include proper error handling in tests
5. Use meaningful test groups for organization
6. Implement proper base classes (TestBase) for common functionality
7. Maintain consistent path management through dedicated path classes
8. Use constants for repeated values

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request
