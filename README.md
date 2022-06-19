# scbEnrichTrade
Interview Coding Challenge

```bash
java -jar scbTrade.jar
```
to run compiled project

### API Request URL
- POST: http://localhost:9191/v1/product_update to initialize product Id & name. Needs product.csv as RequestBody
- POST: http://localhost:9191/v1/enrich to map trade orders to product names, returns 'enrich.csv' mapped file. Needs trade.csv as RequestBody

both http request needs product.csv & trade.csv files respectively

## Maximum Uploaded file size
maximum Upload file size limited to 5MB.
- a 100,000 records in a CSV file consumes 2.5MB.
