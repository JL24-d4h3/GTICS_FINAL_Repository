package org.project.project.controller;

import org.project.project.model.entity.VersionAPI;
import org.project.project.repository.VersionAPIRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 🔧 CONTROLADOR TEMPORAL PARA FIX DE URLs
 * ⚠️ ELIMINAR DESPUÉS DE USAR
 * 
 * Propósito: Actualizar manualmente cloud_run_url cuando el deployment fue exitoso
 * pero la URL no se guardó en la base de datos.
 */
@RestController
@RequestMapping("/api/admin/fix")
public class TempFixController {

    @Autowired
    private VersionAPIRepository versionAPIRepository;

    /**
     * Endpoint para actualizar la cloud_run_url de una versión específica
     * 
     * POST /api/admin/fix/cloud-run-url
     * Body: {
     *   "versionId": 10,
     *   "cloudRunUrl": "https://user-api-1-10-1765674352702-4epq24vhwa-uc.a.run.app"
     * }
     */
    @PostMapping("/cloud-run-url")
    public ResponseEntity<Map<String, Object>> updateCloudRunUrl(
            @RequestBody Map<String, Object> request) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Long versionId = Long.valueOf(request.get("versionId").toString());
            String cloudRunUrl = request.get("cloudRunUrl").toString();
            
            Optional<VersionAPI> versionOpt = versionAPIRepository.findById(versionId);
            
            if (versionOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Version no encontrada con ID: " + versionId);
                return ResponseEntity.notFound().build();
            }
            
            VersionAPI version = versionOpt.get();
            
            // Guardar el valor anterior para el log
            String oldUrl = version.getCloudRunUrl();
            
            // Actualizar la URL
            version.setCloudRunUrl(cloudRunUrl);
            versionAPIRepository.save(version);
            
            response.put("success", true);
            response.put("message", "Cloud Run URL actualizada exitosamente");
            response.put("versionId", versionId);
            response.put("oldUrl", oldUrl);
            response.put("newUrl", cloudRunUrl);
            response.put("apiId", version.getApi().getApiId());
            response.put("numeroVersion", version.getNumeroVersion());
            
            System.out.println("✅ [TempFix] URL actualizada:");
            System.out.println("   Version ID: " + versionId);
            System.out.println("   Old URL: " + oldUrl);
            System.out.println("   New URL: " + cloudRunUrl);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Endpoint para listar versiones con problemas de URL
     * 
     * GET /api/admin/fix/missing-urls
     */
    @GetMapping("/missing-urls")
    public ResponseEntity<Map<String, Object>> findMissingUrls() {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Buscar versiones ACTIVE sin cloud_run_url
            var versionsWithMissingUrls = versionAPIRepository.findAll().stream()
                    .filter(v -> v.getDeploymentStatus() == VersionAPI.DeploymentStatus.ACTIVE)
                    .filter(v -> v.getCloudRunUrl() == null || v.getCloudRunUrl().trim().isEmpty())
                    .map(v -> {
                        Map<String, Object> info = new HashMap<>();
                        info.put("versionId", v.getVersionId());
                        info.put("apiId", v.getApi().getApiId());
                        info.put("nombreApi", v.getApi().getNombreApi());
                        info.put("numeroVersion", v.getNumeroVersion());
                        info.put("deploymentStatus", v.getDeploymentStatus());
                        info.put("cloudRunUrl", v.getCloudRunUrl());
                        info.put("fechaUltimoDeployment", v.getFechaUltimoDeployment());
                        return info;
                    })
                    .toList();
            
            response.put("success", true);
            response.put("count", versionsWithMissingUrls.size());
            response.put("versions", versionsWithMissingUrls);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al buscar versiones: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
