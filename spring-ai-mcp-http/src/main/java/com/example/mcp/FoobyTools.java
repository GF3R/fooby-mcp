package com.example.mcp;

import java.util.List;

import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;

@Service
class FoobyTools {

	private final RestClient foobyClient;

	FoobyTools(RestClient.Builder restClientBuilder) {
		this.foobyClient = restClientBuilder
			.baseUrl("https://fooby.ch/hawaii_search.sri")
			.build();
	}

	@Tool(description = "Search recipes, menus, and other results on Fooby")
	public String foobySearch(
		@ToolParam(description = "Search query", required = false) String query,
		@ToolParam(description = "Language code, such as de, fr, or it", required = false) String lang,
		@ToolParam(description = "Result type, for example rezepte, menus, or produkte", required = false) String treffertyp,
		@ToolParam(description = "Pagination offset", required = false) Integer start,
		@ToolParam(description = "Maximum number of results, from 1 to 100", required = false) Integer num,
		@ToolParam(description = "Client interface value; normally leave unset", required = false) String interfaceName,
		@ToolParam(description = "Whether to interpret the query as a user query", required = false) Boolean userquery,
		@ToolParam(description = "Content type filters", required = false) List<String> inhaltsart,
		@ToolParam(description = "Menu type filters", required = false) List<String> menuart,
		@ToolParam(description = "Region filters", required = false) List<String> region,
		@ToolParam(description = "Occasion filters", required = false) List<String> anlass,
		@ToolParam(description = "Diet/nutrition filters", required = false) List<String> ernaehrungsweise,
		@ToolParam(description = "Author ID", required = false) Integer authorid,
		@ToolParam(description = "Filter for results with videos", required = false) Boolean hasvideo,
		@ToolParam(description = "Filter for results with instructions", required = false) Boolean hassteps,
		@ToolParam(description = "Month filters", required = false) List<String> monat,
		@ToolParam(description = "Season filters", required = false) List<String> season,
		@ToolParam(description = "Maximum total cooking time in minutes", required = false) Integer zeitaufwandMax,
		@ToolParam(description = "Exact active cooking time in minutes", required = false) Integer zeitaufwandAktiv
	) {
		String searchLang = lang == null || lang.isBlank() ? "de" : lang;
		String resultType = treffertyp == null || treffertyp.isBlank() ? "rezepte" : treffertyp;
		int offset = start == null ? 0 : Math.max(0, start);
		int count = num == null ? 20 : Math.max(1, Math.min(100, num));

		UriComponentsBuilder uri = UriComponentsBuilder.fromUriString("https://fooby.ch/hawaii_search.sri")
			.queryParam("lang", searchLang)
			.queryParam("treffertyp", resultType)
			.queryParam("start", offset)
			.queryParam("num", count);

		if (query != null && !query.isBlank()) uri.queryParam("query", query);
		if (interfaceName != null && !interfaceName.isBlank()) uri.queryParam("interface", interfaceName);
		if (userquery != null) uri.queryParam("userquery", userquery);
		addListQueryParams(uri, "inhaltsart", inhaltsart);
		addListQueryParams(uri, "menuart", menuart);
		addListQueryParams(uri, "region", region);
		addListQueryParams(uri, "anlass", anlass);
		addListQueryParams(uri, "ernaehrungsweise", ernaehrungsweise);
		if (authorid != null) uri.queryParam("authorid", authorid);
		if (hasvideo != null) uri.queryParam("hasvideo", hasvideo);
		if (hassteps != null) uri.queryParam("hassteps", hassteps);
		addListQueryParams(uri, "monat", monat);
		addListQueryParams(uri, "season", season);
		if (zeitaufwandMax != null) uri.queryParam("zeitaufwand_max", zeitaufwandMax);
		if (zeitaufwandAktiv != null) uri.queryParam("zeitaufwand_aktiv", zeitaufwandAktiv);

		return foobyClient.get()
			.uri(uri.build().toUri())
			.retrieve()
			.body(String.class);
	}

	private void addListQueryParams(UriComponentsBuilder uri, String name, List<String> values) {
		if (values == null) return;
		for (String value : values) {
			if (value != null && !value.isBlank()) uri.queryParam(name, value);
		}
	}

}

@Configuration
class McpToolConfiguration {

	@Bean
	RestClient.Builder restClientBuilder() {
		StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
		stringConverter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN));
		return RestClient.builder()
			.configureMessageConverters(converters -> converters
				.withStringConverter(stringConverter)
				.registerDefaults());
	}

	@Bean
	ToolCallbackProvider toolCallbackProvider(FoobyTools foobyTools) {
		return MethodToolCallbackProvider.builder().toolObjects(foobyTools).build();
	}

}
