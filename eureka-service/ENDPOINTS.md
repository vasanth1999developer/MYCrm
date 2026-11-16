# eureka-service: endpoints and methods (learning guide)

This guide explains **every HTTP endpoint** the Eureka server exposes and **every method** in this module's code, with real requests and responses captured from a local run (port 8555). For the short reference (port, config, Docker), see [README.md](README.md). For *why* service discovery exists at all, see [Purpose.md](Purpose.md).

---

## 1. The big picture

Eureka is a **phone book for services**. Nothing in this module was hand-written except one class. All endpoints below come from the `spring-cloud-starter-netflix-eureka-server` dependency, switched on by one annotation.

```
             (1) register: POST /eureka/apps/EMPLOYEE-SERVICE
employee-service ───────────────────────────────────────────►┐
             (2) heartbeat every 30 s: PUT /eureka/apps/...   │
                                                              ▼
                                                    ┌──────────────────┐
                                                    │  eureka-service  │
                                                    │  :8555           │
                                                    │  in-memory map:  │
                                                    │  APP → instances │
                                                    └──────────────────┘
                                                              ▲
             (3) fetch registry every 5 s: GET /eureka/apps/delta
gateway-service ─────────────────────────────────────────────┘
             (4) "lb://employee-service" → picks 192.168.1.12:8562 from its local copy
```

Key ideas:

* **The registry lives in memory.** There is no database. Restart Eureka and the map is empty until every service re-registers (they do so automatically on their next heartbeat, which gets a 404 and triggers re-registration).
* **Clients do the work.** Each service registers itself, sends heartbeats, and downloads the registry. Eureka itself never calls a service.
* **Clients cache.** The gateway doesn't ask Eureka on every request. It keeps a local copy and refreshes it every 5 s (`eureka.client.registry-fetch-interval-seconds=5` in the gateway). So if Eureka goes down, routing keeps working with the last known copy.
* **Leases.** A registration is a 90-second lease (`durationInSecs`). Each heartbeat (every 30 s, `renewalIntervalInSecs`) renews it. If no heartbeat arrives for 90 s, the eviction task (runs every 5 s here) removes the instance.

---

## 2. The code: every class and method

The module has exactly **one** class.

### `EurekaServiceApplication` (`src/main/java/com/clinch/crm/eureka/EurekaServiceApplication.java`)

```java
@EnableEurekaServer
@SpringBootApplication
public class EurekaServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServiceApplication.class, args);
        System.out.println("Eureka Server");
    }
}
```

| Element | What it does |
|---|---|
| `@SpringBootApplication` | Standard Spring Boot entry point: turns on auto-configuration, component scanning of `com.clinch.crm.eureka`, and lets the class hold `@Bean`s. |
| `@EnableEurekaServer` | The important one. It adds a marker bean that activates `EurekaServerAutoConfiguration`, which creates the in-memory registry (`PeerAwareInstanceRegistry`), the Jersey REST API under `/eureka/*`, the HTML dashboard (`EurekaController` at `/`), and the eviction timer. Remove it and the app starts as a plain web app with none of the endpoints below. |
| `main(String[] args)` | Boots the embedded Tomcat on port 8555 and starts the registry. `SpringApplication.run` blocks only until startup is done, so `"Eureka Server"` is printed once the app is up. (A logger would be better than `System.out`, but it's harmless.) |

### Test: `EurekaServiceApplicationTests.contextLoads()`

An empty `@SpringBootTest`. It passes if the whole Spring context starts, which proves the config is valid. It doesn't call any endpoint.

### How configuration shapes the endpoints (`application.yml`)

| Setting | Effect you can see on the endpoints |
|---|---|
| `server.port: 8555` | All URLs below start with `http://localhost:8555`. |
| `eureka.client.register-with-eureka: false` | Eureka doesn't list itself in `GET /eureka/apps`. |
| `eureka.client.fetch-registry: false` | The server doesn't try to download a registry from a peer. |
| `eureka.client.service-url.defaultZone: http://localhost:8555/eureka/` | Points at *itself*. It's used as the "peer" list for replication. Because the only peer is itself, `GET /eureka/status` reports it under `unavailable-replicas`. That looks alarming but is normal for a single-node setup. |
| `eureka.server.enable-self-preservation: false` | Expired instances are always evicted. (With it on, Eureka stops evicting when too many heartbeats are missing at once, assuming a network problem. That's the right default in production.) |
| `eureka.server.eviction-interval-timer-in-ms: 5000` | The eviction task runs every 5 s (default 60 s). |
| `eureka.server.wait-time-in-ms-when-sync-empty: 0` | Serves registry requests immediately after startup instead of waiting (default 5 min) to sync from peers. |
| `eureka.instance.prefer-ip-address: true` | Eureka's own instance info uses its IP rather than hostname. |

---

## 3. Endpoint overview

| # | Method | Path | Who calls it | Purpose |
|---|---|---|---|---|
| 1 | GET | `/` | You (browser) | HTML dashboard |
| 2 | GET | `/lastn` | You (browser) | Last registered/cancelled instances |
| 3 | POST | `/eureka/apps/{appId}` | Each service at startup | **Register** an instance |
| 4 | PUT | `/eureka/apps/{appId}/{instanceId}` | Each service every 30 s | **Heartbeat** (renew lease) |
| 5 | DELETE | `/eureka/apps/{appId}/{instanceId}` | Each service at shutdown | **Deregister** |
| 6 | GET | `/eureka/apps` | Clients (first fetch) | Full registry |
| 7 | GET | `/eureka/apps/delta` | Clients (every fetch after the first) | Only recent changes |
| 8 | GET | `/eureka/apps/{appId}` | You / tools | One application's instances |
| 9 | GET | `/eureka/apps/{appId}/{instanceId}` | You / tools | One instance |
| 10 | GET | `/eureka/instances/{instanceId}` | You / tools | One instance, without knowing its app |
| 11 | PUT | `/eureka/apps/{appId}/{instanceId}/status?value=…` | Ops | Force a status (e.g. take out of rotation) |
| 12 | DELETE | `/eureka/apps/{appId}/{instanceId}/status` | Ops | Remove the forced status |
| 13 | PUT | `/eureka/apps/{appId}/{instanceId}/metadata?k=v` | Ops / clients | Add/replace metadata |
| 14 | GET | `/eureka/vips/{vipAddress}` | Clients | Instances by virtual host name |
| 15 | GET | `/eureka/svips/{svipAddress}` | Clients | Same, for the secure VIP |
| 16 | GET | `/eureka/status` | You / monitoring | Server and replica health |
| 17 | POST | `/eureka/peerreplication/batch` | Other Eureka nodes | Replication between peers |
| 18 | GET | `/actuator`, `/actuator/health` | Monitoring | Spring Boot health |

**Common rules for the `/eureka/**` REST API:**

* **Format:** responses are **XML by default**. Send `Accept: application/json` to get JSON. Requests may send either (`Content-Type: application/json` or `application/xml`).
* **`{appId}`** is the service's `spring.application.name`. Eureka stores it **upper-cased** (`employee-service` → `EMPLOYEE-SERVICE`), and lookups are case-insensitive.
* **`{instanceId}`** is what the client set in `eureka.instance.instance-id`. In this project that's usually `${spring.application.name}:${random.value}`; without it, Spring uses `host:app-name:port`.
* **No security.** Anyone who can reach port 8555 can register, deregister or override any instance. That's why the port must stay internal (see [README.md](README.md#security)).
* **The gateway does not route here.** There's no `/eureka/**` route in the gateway, so call port 8555 directly.

---

## 4. Endpoints in detail

The examples were run with `curl` against a local server. A fake service `DEMO-SERVICE` with instance id `demo:1` was used so nothing real was affected.

### 4.1 `GET /` — dashboard

Open http://localhost:8555 in a browser. It's an HTML page (Freemarker template inside the dependency) that shows:

* **System Status:** environment, data center, uptime, current time, whether lease expiration is enabled, renews threshold vs. renews in the last minute.
* **DS Replicas:** peer Eureka nodes (here: itself).
* **Instances currently registered with Eureka:** one row per application, with its instances and status (`UP`, `DOWN`, `OUT_OF_SERVICE`, `STARTING`, `UNKNOWN`). This is the quickest way to check "did my service register?".
* **General Info / Instance Info:** memory, CPUs, replicas, and Eureka's own IP and status.

The red banner *"RENEWALS ARE LESSER THAN THE THRESHOLD…"* or *"THE SELF PRESERVATION MODE IS TURNED OFF…"* is expected here, because self-preservation is disabled on purpose.

### 4.2 `GET /lastn` — recent events

HTML page listing the last 1000 **registered** and **cancelled** instances with timestamps. Useful to see if a service is flapping (registering and dropping repeatedly).

### 4.3 `POST /eureka/apps/{appId}` — register

Called by the Eureka client inside each service when it starts (the first registration happens roughly 30–40 s after startup unless the client is configured otherwise).

**Request body:** an `InstanceInfo` wrapped in `"instance"`. Minimum fields:

```bash
curl -i -X POST http://localhost:8555/eureka/apps/DEMO-SERVICE \
  -H "Content-Type: application/json" \
  -d '{"instance":{
        "instanceId":"demo:1",
        "hostName":"localhost",
        "app":"DEMO-SERVICE",
        "ipAddr":"127.0.0.1",
        "status":"UP",
        "port":{"$":9999,"@enabled":"true"},
        "dataCenterInfo":{"@class":"com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo","name":"MyOwn"}
      }}'
```

**Response:** `204 No Content` on success. `400 Bad Request` if a required field (such as `hostName`, `app`, `ipAddr`, `dataCenterInfo`) is missing.

Notes:
* The odd `"$"` / `"@enabled"` keys come from Eureka's XML-first design (XML text and attributes mapped to JSON).
* Fields you don't send get defaults: `securePort` 7002 (disabled), `leaseInfo` 30 s / 90 s, empty `metadata`.
* Registering the same `instanceId` again just replaces it.

### 4.4 `PUT /eureka/apps/{appId}/{instanceId}` — heartbeat (renew)

```bash
curl -i -X PUT http://localhost:8555/eureka/apps/DEMO-SERVICE/demo:1
```

| Response | Meaning |
|---|---|
| `200 OK` | Lease renewed for another 90 s. |
| `404 Not Found` | Eureka doesn't know this instance (e.g. Eureka restarted or evicted it). The client reacts by **registering again**. That's how services recover automatically after a Eureka restart. |

Optional query params the client sends: `status`, `lastDirtyTimestamp`, `overriddenstatus`. They let the server detect that its copy is out of date.

### 4.5 `DELETE /eureka/apps/{appId}/{instanceId}` — deregister (cancel)

```bash
curl -i -X DELETE http://localhost:8555/eureka/apps/DEMO-SERVICE/demo:1
```

`200 OK` if removed, `404` if it wasn't registered. A service sends this on graceful shutdown (Ctrl+C / `SIGTERM`). If it's killed hard, nothing is sent, and the instance stays listed until its lease expires (up to 90 s plus the 5 s eviction tick). During that window the gateway may still route to the dead instance, which is one reason the gateway has `Retry=3`.

### 4.6 `GET /eureka/apps` — full registry

```bash
curl http://localhost:8555/eureka/apps -H "Accept: application/json"
```

```json
{"applications":{
  "versions__delta":"1",
  "apps__hashcode":"UP_1_",
  "application":[
    {"name":"DEMO-SERVICE",
     "instance":[{
       "instanceId":"demo:1","hostName":"localhost","app":"DEMO-SERVICE",
       "ipAddr":"127.0.0.1","status":"UP","overriddenStatus":"UNKNOWN",
       "port":{"$":9999,"@enabled":"true"},
       "securePort":{"$":7002,"@enabled":"false"},
       "leaseInfo":{"renewalIntervalInSecs":30,"durationInSecs":90,
                    "registrationTimestamp":1794800928692,"lastRenewalTimestamp":1794800928692,
                    "evictionTimestamp":0,"serviceUpTimestamp":1794800928692},
       "metadata":{},
       "vipAddress":"…","secureVipAddress":"…",
       "lastUpdatedTimestamp":…,"lastDirtyTimestamp":…,"actionType":"ADDED"
     }]}
  ]}}
```

Field guide:
* `apps__hashcode`: a summary like `UP_3_DOWN_1_` (count per status). Clients compare it after applying a delta; if it doesn't match, they fetch the full registry again.
* `status` vs `overriddenStatus`: `status` is what the instance reports; `overriddenStatus` is set by endpoint 4.11 and wins when present.
* `actionType`: `ADDED`, `MODIFIED` or `DELETED` (used mainly in deltas).
* Supports `Accept-Encoding: gzip`, which real clients use.

Without `Accept: application/json` you get XML:

```xml
<applications>
  <versions__delta>1</versions__delta>
  <apps__hashcode></apps__hashcode>
</applications>
```

### 4.7 `GET /eureka/apps/delta` — changes only

Same shape as 4.6, but contains only instances added, modified or deleted in the last ~3 minutes. Clients fetch the full list once, then call this every fetch interval to keep their copy in sync cheaply.

### 4.8 `GET /eureka/apps/{appId}` — one application

```bash
curl http://localhost:8555/eureka/apps/EMPLOYEE-SERVICE -H "Accept: application/json"
```

Returns `{"application":{"name":"…","instance":[…]}}`, or `404` if no instance of that app is registered. Handy for checking whether a service the gateway returns 503 for is actually registered.

### 4.9 `GET /eureka/apps/{appId}/{instanceId}` — one instance

Returns `{"instance":{…}}` or `404`.

### 4.10 `GET /eureka/instances/{instanceId}` — one instance by id only

Same response as 4.9, but you don't need to know the app name.

### 4.11 `PUT /eureka/apps/{appId}/{instanceId}/status?value={STATUS}` — override status

```bash
curl -i -X PUT "http://localhost:8555/eureka/apps/DEMO-SERVICE/demo:1/status?value=OUT_OF_SERVICE"
```

`200 OK`. Values: `UP`, `DOWN`, `STARTING`, `OUT_OF_SERVICE`, `UNKNOWN`. The override sticks even when the instance's own heartbeats say `UP`. Clients only load-balance to `UP` instances by default, so `OUT_OF_SERVICE` is the standard way to **drain an instance before a deploy** without stopping it.

### 4.12 `DELETE /eureka/apps/{appId}/{instanceId}/status` — remove override

```bash
curl -i -X DELETE "http://localhost:8555/eureka/apps/DEMO-SERVICE/demo:1/status?value=UP"
```

`200 OK`. Removes the override; the optional `value` sets the status to use right away. After this, the instance's own reported status counts again.

### 4.13 `PUT /eureka/apps/{appId}/{instanceId}/metadata?{key}={value}` — metadata

```bash
curl -i -X PUT "http://localhost:8555/eureka/apps/DEMO-SERVICE/demo:1/metadata?zone=dev"
```

`200 OK`. Adds or replaces key/value pairs in the instance's `metadata` map. Spring clients normally set metadata at startup with `eureka.instance.metadata-map.<key>=<value>`; load balancers can use it (e.g. zone-aware routing).

### 4.14 / 4.15 `GET /eureka/vips/{vipAddress}` and `/eureka/svips/{svipAddress}`

Return all instances whose `vipAddress` (or `secureVipAddress`) matches. Spring clients set `vipAddress` to the lower-case application name, so `GET /eureka/vips/employee-service` returns the employee instances. (In the test above it returned an empty list because the fake instance had no `vipAddress`.)

### 4.16 `GET /eureka/status` — server status

```bash
curl http://localhost:8555/eureka/status -H "Accept: application/json"
```

```json
{"generalStats":{"num-of-cpus":"8","total-avail-memory":"114mb",
                 "current-memory-usage":"78mb (68%)","server-uptime":"00:00"},
 "applicationStats":{"registered-replicas":"http://localhost:8555/eureka/",
                     "available-replicas":"",
                     "unavailable-replicas":"http://localhost:8555/eureka/,"},
 "instanceInfo":{"instanceId":"192.168.1.12:eureka-service:8555","app":"EUREKA-SERVICE","status":"UP", …}}
```

This is the data behind the dashboard's "General Info" panel. The "unavailable replica" is Eureka itself (see `defaultZone` in section 2).

### 4.17 `POST /eureka/peerreplication/batch` — peer replication

Used only when you run several Eureka servers that list each other in `defaultZone`. Each node forwards register/renew/cancel/status events to its peers in batches through this endpoint. With one node it's unused, but it still answers (`200`).

### 4.18 `GET /actuator` and `GET /actuator/health`

The Eureka server starter brings in Spring Boot Actuator. With default settings only `health` is exposed over HTTP:

```bash
curl http://localhost:8555/actuator/health
# {"status":"UP"}
```

Use it for container health checks. To expose more (`info`, `metrics`…), set `management.endpoints.web.exposure.include`.

---

## 5. Try it yourself

```bash
# 1. Start Eureka
cd eureka-service && mvn spring-boot:run

# 2. Register a fake instance (see 4.3 for the body), then watch it appear at http://localhost:8555
# 3. Don't send heartbeats: about 90-95 s later it disappears (lease expired + eviction tick)
# 4. Start gateway-service and any business service, then:
curl http://localhost:8555/eureka/apps -H "Accept: application/json"
# 5. Take a service out of rotation and watch the gateway return 503 for it
curl -X PUT "http://localhost:8555/eureka/apps/EMPLOYEE-SERVICE/<instanceId>/status?value=OUT_OF_SERVICE"
# (the gateway notices within ~5 s, its registry fetch interval)
```

## 6. Troubleshooting with these endpoints

| Symptom | Check | Typical cause |
|---|---|---|
| Gateway returns **503** for `/xyz/**` | `GET /eureka/apps/<NAME>` | Service not registered: missing `defaultZone` (it tries `localhost:8761`), wrong `spring.application.name`, or it just started (first registration takes ~30-40 s). |
| Requests go to a dead instance | `GET /lastn`, instance `lastRenewalTimestamp` | Killed without deregistering; it stays until the lease expires (~95 s). |
| Two different apps answer the same route | `GET /eureka/apps/INVENTORY-SERVICE` shows 2 instances from different apps | Name clash (product-service registers as `inventory-service`, see [docs/KNOWN_ISSUES.md](../docs/KNOWN_ISSUES.md)). |
| Everything vanished | Eureka restarted | Normal; services re-register on their next heartbeat (≤ 30 s). |
