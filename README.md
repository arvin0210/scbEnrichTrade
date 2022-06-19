# scbEnrichTrade
Interview Coding Challenge

# Software Architecture
Built software based on SOLID Principles
- SRP Single Responsibility
- Open Closed
- Liskov Substitution
- Interface Segregation
- Dependency Inverse of Control


```bash
java -jar scbTrade.jar
```
You may run the compiled package using above to run

### API Request URL
- POST: http://localhost:9191/v1/product_update to initialize product Id & name. Needs product.csv as RequestBody
- POST: http://localhost:9191/v1/enrich to map trade orders to product names, returns 'enrich.csv' mapped file. Needs trade.csv as RequestBody

both http request needs product.csv & trade.csv files respectively

## Maximum Uploaded file size
maximum Upload file size limited to 5MB.
- a 100,000 records in a CSV file consumes 2.5MB.
