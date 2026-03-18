package dev.muthukumar.ai_crm.controller;

import dev.muthukumar.ai_crm.service.CertificateService;
import dev.muthukumar.ai_crm.model.Certificate;
import dev.muthukumar.ai_crm.util.PdfGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;
    private final PdfGeneratorUtil pdfGenerator;

    @GetMapping("/preview/{allocationId}")
    public ResponseEntity<?> preview(@PathVariable Long allocationId) {
        return ResponseEntity.ok(Map.of("success", true, "data", certificateService.preview(allocationId)));
    }

    @PostMapping
    public ResponseEntity<?> issue(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("success", true, "message", "Certificate issued", "data", certificateService.issue(body)));
    }

    @GetMapping("/allocation/{allocationId}")
    public ResponseEntity<?> getByAllocation(@PathVariable Long allocationId) {
        return ResponseEntity.ok(Map.of("success", true, "data", certificateService.getByAllocation(allocationId)));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(Map.of("success", true, "data", certificateService.getAll()));
    }

    @GetMapping("/download/{allocationId}")
    public ResponseEntity<byte[]> download(@PathVariable Long allocationId) {
        Certificate cert = certificateService.getByAllocation(allocationId);
        try {
            byte[] pdf = pdfGenerator.generateCertificatePdf(cert);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"certificate_" + cert.getCertificateNumber() + ".pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed: " + e.getMessage());
        }
    }
}
