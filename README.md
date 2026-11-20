# Spring AI Fooby MCP HTTP Server (Java)

This Spring Boot application exposes the Fooby search API through Spring AI MCP over Streamable HTTP.

## What it provides

- MCP endpoint: `POST/GET /mcp` (default)
- MCP tool: `foobySearch`
- Wrapper implementation: [`FoobyTools.java`](/Users/gabriel/development/fooby-mcp/spring-ai-mcp-http/src/main/java/com/example/mcp/FoobyTools.java)
- Upstream API: `https://fooby.ch/hawaii_search.sri`

The tool exposes the API query parameters, including `query`, `lang`, `treffertyp`, pagination, repeated filter fields, author/video/step filters, months/seasons, and cooking times.

## Run

```bash
./mvnw spring-boot:run
```

By default the server starts on `http://localhost:8080`, and the MCP endpoint is:

- `http://localhost:8080/mcp`

## Key configuration

See [`application.properties`](/Users/gabriel/development/fooby-mcp/spring-ai-mcp-http/src/main/resources/application.properties):

```properties
spring.ai.mcp.server.protocol=STREAMABLE
spring.ai.mcp.server.annotation-scanner.enabled=false
```

This enables the modern HTTP MCP transport for the WebMVC starter.

## Test the Fooby tool

Initialize an MCP session and then call `foobySearch` with a pasta recipe search:

```bash
curl -X POST http://localhost:8080/mcp \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json, text/event-stream' \
  -d '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2025-03-26","capabilities":{},"clientInfo":{"name":"curl","version":"1.0.0"}}}'
```

Use the returned `Mcp-Session-Id` header to call the tool:

```bash
curl -X POST http://localhost:8080/mcp \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json, text/event-stream' \
  -H 'Mcp-Session-Id: <session-id>' \
  -d '{"jsonrpc":"2.0","id":2,"method":"tools/call","params":{"name":"foobySearch","arguments":{"query":"pasta","lang":"de","treffertyp":"rezepte","num":3}}}'
```
