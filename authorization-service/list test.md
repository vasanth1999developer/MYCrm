✅ Normal Cases
# Default (no params)
GET /list

# With page and size
GET /list?page=0&size=10
GET /list?page=1&size=10
GET /list?page=2&size=5

# With search
GET /list?searchText=admin
GET /list?searchText=john
GET /list?searchText=a

# With sort
GET /list?sortBy=name&direction=asc
GET /list?sortBy=name&direction=desc
GET /list?sortBy=id&direction=asc
GET /list?sortBy=id&direction=desc

# All combined
GET /list?page=0&size=10&searchText=admin&sortBy=name&direction=asc
GET /list?page=1&size=5&searchText=john&sortBy=id&direction=desc

❌ Edge Cases — Validation
# Size edge cases
GET /list?size=0          → expect 400 Bad Request
GET /list?size=-1         → expect 400 Bad Request
GET /list?size=-999       → expect 400 Bad Request
GET /list?size=51         → expect capped to 50
GET /list?size=99999      → expect capped to 50
GET /list?size=1          → expect 1 record

# Page edge cases
GET /list?page=-1         → expect fallback to 0
GET /list?page=-999       → expect fallback to 0
GET /list?page=999999     → expect empty data []

# Sort edge cases
GET /list?sortBy=password         → expect fallback to id
GET /list?sortBy=randomfield      → expect fallback to id
GET /list?sortBy=                 → expect fallback to id
GET /list?direction=random        → expect fallback to asc
GET /list?direction=DESC          → expect works (case insensitive)
GET /list?direction=ASC           → expect works (case insensitive)

# Search edge cases
GET /list?searchText=             → expect all records (empty = no filter)
GET /list?searchText=   spaces    → test trim handling
GET /list?searchText=@#$%^&       → expect no crash
GET /list?searchText=zzzzzzzzzzz  → expect empty data []

# Type edge cases
GET /list?page=abc        → expect 400 (wrong type)
GET /list?size=abc        → expect 400 (wrong type)
GET /list?page=1.5        → expect 400 (float not int)

🔁 Pagination Flow Test
# Test page navigation
GET /list?page=0&size=5   → check totalPages in response
GET /list?page=1&size=5   → check hasPrevious=true
GET /list?page=?&size=5   → last page → check isLast=true, hasNext=false

# Test response fields every time
{
  "data": [...],
  "currentPage": 0,       ← matches page param?
  "totalPages": ?,        ← makes sense?
  "totalItems": ?,        ← matches DB count?
  "isFirst": true/false,  ← correct?
  "isLast": true/false,   ← correct?
  "hasNext": true/false,  ← correct?
  "hasPrevious": true/false ← correct?
}

📋 Test Checklist
Normal Flow
□ Default call works
□ Pagination works (page 0, 1, 2)
□ Search returns correct results
□ Sort asc works
□ Sort desc works
□ All params combined works

Validation
□ size=0    → 400
□ size=-1   → 400
□ size=51   → capped to 50
□ page=-1   → fallback to 0
□ page=9999 → empty data, no crash
□ sortBy=invalid → fallback to id
□ direction=invalid → fallback to asc

Search
□ searchText empty → returns all
□ searchText no match → empty data []
□ searchText partial match → works

Response
□ isFirst true on page 0
□ isLast true on last page
□ hasNext false on last page
□ hasPrevious false on page 0
□ totalItems consistent across pages

Quick Postman Test Order
1. GET /list                          → baseline, see total records
2. GET /list?size=0                   → should fail 400
3. GET /list?size=5                   → small page
4. GET /list?page=0&size=5            → page 1
5. GET /list?page=1&size=5            → page 2
6. GET /list?searchText=a             → filter test
7. GET /list?sortBy=name&direction=asc   → sort test
8. GET /list?sortBy=invalid           → fallback test
9. GET /list?page=999                 → beyond last page