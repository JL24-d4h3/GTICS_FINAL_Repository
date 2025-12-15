package org.project.project.service;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Servicio para parsear contratos OpenAPI y extraer información de endpoints
 * 
 * @author Dev Portal Team
 * @since 2025-12-14
 */
@Slf4j
@Service
public class OpenApiParserService {

    /**
     * Parsea un contrato OpenAPI (YAML o JSON) y extrae todos los endpoints
     * 
     * @param contractYaml Contrato en formato YAML o JSON
     * @return Lista de endpoints con su metadata
     */
    public List<EndpointInfo> parseContract(String contractYaml) {
        List<EndpointInfo> endpoints = new ArrayList<>();
        
        try {
            // Parsear contrato con Swagger Parser
            OpenAPI openAPI = new OpenAPIV3Parser().readContents(contractYaml).getOpenAPI();
            
            if (openAPI == null || openAPI.getPaths() == null) {
                log.warn("Contrato OpenAPI vacío o sin paths");
                return endpoints;
            }
            
            // Iterar sobre cada path
            for (Map.Entry<String, PathItem> pathEntry : openAPI.getPaths().entrySet()) {
                String path = pathEntry.getKey();
                PathItem pathItem = pathEntry.getValue();
                
                // Extraer endpoints por cada método HTTP
                extractEndpoint(endpoints, path, "GET", pathItem.getGet());
                extractEndpoint(endpoints, path, "POST", pathItem.getPost());
                extractEndpoint(endpoints, path, "PUT", pathItem.getPut());
                extractEndpoint(endpoints, path, "DELETE", pathItem.getDelete());
                extractEndpoint(endpoints, path, "PATCH", pathItem.getPatch());
            }
            
            log.info("Se extrajeron {} endpoints del contrato OpenAPI", endpoints.size());
            
        } catch (Exception e) {
            log.error("Error al parsear contrato OpenAPI: {}", e.getMessage(), e);
        }
        
        return endpoints;
    }

    /**
     * Extrae información de un endpoint específico
     * 
     * IMPORTANTE: Cada combinación método+path genera un EndpointInfo separado
     */
    private void extractEndpoint(List<EndpointInfo> endpoints, String path, String method, Operation operation) {
        if (operation == null) {
            return; // Este método no existe para este path
        }
        
        EndpointInfo endpoint = new EndpointInfo();
        endpoint.setMethod(method);
        endpoint.setPath(path);
        endpoint.setSummary(operation.getSummary() != null ? operation.getSummary() : "");
        endpoint.setDescription(operation.getDescription());
        
        // Extraer parámetros (query, path, header)
        List<ParameterInfo> parameters = new ArrayList<>();
        if (operation.getParameters() != null) {
            for (Parameter param : operation.getParameters()) {
                ParameterInfo paramInfo = new ParameterInfo();
                paramInfo.setName(param.getName());
                paramInfo.setIn(param.getIn());
                paramInfo.setRequired(param.getRequired() != null ? param.getRequired() : false);
                paramInfo.setDescription(param.getDescription());
                paramInfo.setType(param.getSchema() != null ? param.getSchema().getType() : "string");
                
                // Extraer ejemplo si existe
                if (param.getExample() != null) {
                    paramInfo.setExample(param.getExample().toString());
                } else if (param.getSchema() != null && param.getSchema().getExample() != null) {
                    paramInfo.setExample(param.getSchema().getExample().toString());
                }
                
                parameters.add(paramInfo);
            }
        }
        endpoint.setParameters(parameters);
        
        // Extraer Request Body (para POST/PUT/PATCH)
        if (operation.getRequestBody() != null) {
            RequestBody requestBody = operation.getRequestBody();
            Content content = requestBody.getContent();
            
            if (content != null && content.get("application/json") != null) {
                MediaType mediaType = content.get("application/json");
                Schema schema = mediaType.getSchema();
                
                if (schema != null) {
                    String exampleJson = generateExampleFromSchema(schema);
                    endpoint.setRequestBodySchema(exampleJson);
                }
            }
        }
        
        // Extraer Response Example (primera respuesta exitosa: 200, 201, etc.)
        if (operation.getResponses() != null) {
            for (String responseCode : new String[]{"200", "201", "202", "204"}) {
                if (operation.getResponses().get(responseCode) != null) {
                    var response = operation.getResponses().get(responseCode);
                    if (response.getContent() != null && response.getContent().get("application/json") != null) {
                        MediaType mediaType = response.getContent().get("application/json");
                        Schema schema = mediaType.getSchema();
                        
                        if (schema != null) {
                            String exampleJson = generateExampleFromSchema(schema);
                            endpoint.setResponseExample(exampleJson);
                            break;
                        }
                    }
                }
            }
        }
        
        endpoints.add(endpoint);
        log.debug("Endpoint extraído: {} {}", method, path);
    }

    /**
     * Genera un ejemplo JSON a partir de un schema OpenAPI
     * 
     * @param schema Schema de OpenAPI
     * @return JSON de ejemplo como String
     */
    private String generateExampleFromSchema(Schema schema) {
        if (schema == null) {
            return "{}";
        }
        
        // Si ya tiene un ejemplo definido, usarlo
        if (schema.getExample() != null) {
            return formatJson(schema.getExample());
        }
        
        // Generar ejemplo basado en propiedades
        Map<String, Schema> properties = schema.getProperties();
        if (properties == null || properties.isEmpty()) {
            return "{}";
        }
        
        StringBuilder json = new StringBuilder("{\n");
        int count = 0;
        
        for (Map.Entry<String, Schema> entry : properties.entrySet()) {
            String propertyName = entry.getKey();
            Schema propertySchema = entry.getValue();
            
            if (count > 0) {
                json.append(",\n");
            }
            
            json.append("  \"").append(propertyName).append("\" : ");
            
            // Usar ejemplo si existe
            if (propertySchema.getExample() != null) {
                Object example = propertySchema.getExample();
                if (example instanceof String) {
                    json.append("\"").append(example).append("\"");
                } else {
                    json.append(example);
                }
            } else {
                // Generar valor por tipo
                String type = propertySchema.getType();
                switch (type != null ? type : "string") {
                    case "integer":
                    case "number":
                        json.append("0");
                        break;
                    case "boolean":
                        json.append("false");
                        break;
                    case "array":
                        json.append("[]");
                        break;
                    case "object":
                        json.append("{}");
                        break;
                    default:
                        json.append("\"\"");
                }
            }
            
            count++;
        }
        
        json.append("\n}");
        return json.toString();
    }

    /**
     * Formatea un objeto a JSON legible
     */
    private String formatJson(Object obj) {
        if (obj instanceof String) {
            return "\"" + obj + "\"";
        }
        return obj.toString();
    }

    /**
     * Información de un endpoint extraído del contrato OpenAPI
     */
    @Data
    public static class EndpointInfo {
        private String method;           // GET, POST, PUT, DELETE, PATCH
        private String path;              // /api/calculate, /health
        private String summary;           // Descripción corta
        private String description;       // Descripción detallada
        private List<ParameterInfo> parameters;  // Query params, path params, headers
        private String requestBodySchema; // JSON example del body (POST/PUT/PATCH)
        private String responseExample;   // JSON example de la respuesta exitosa
    }

    /**
     * Información de un parámetro (query, path, header)
     */
    @Data
    public static class ParameterInfo {
        private String name;
        private String in;          // query, path, header
        private boolean required;
        private String description;
        private String type;        // string, integer, boolean, etc.
        private String example;
    }
}
