# Spring AI MCP HTTP Server (Java)

This project is a minimal Spring Boot + Spring AI MCP server exposed over HTTP.

## What it provides

- MCP server transport: **Streamable HTTP**
- MCP endpoint: `POST/GET /mcp` (default)
- Example MCP tool: `add(a, b)` from [`MathTools`](/Users/gabriel/development/fooby-mcp/spring-ai-mcp-http/src/main/java/com/example/mcp/MathTools.java)

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

## Quick MCP smoke test

```bash
curl -X POST http://localhost:8080/mcp \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json, text/event-stream' \
  -d '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2025-03-26","capabilities":{},"clientInfo":{"name":"curl","version":"1.0.0"}}}'
```
