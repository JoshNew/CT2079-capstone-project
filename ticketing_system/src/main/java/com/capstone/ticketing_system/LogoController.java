package com.capstone.ticketing_system;

import com.capstone.ticketing_system.models.Logo;
import com.capstone.ticketing_system.models.repositories.LogoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.List;

@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/api/logo")
public class LogoController {

    @Autowired
    private LogoRepository logoRepository;

    // GET the logo (there should only be one)
    @GetMapping
    public ResponseEntity<Logo> getLogo() {
        try {
            List<Logo> logos = logoRepository.findAll();
            if (logos.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            // Return the first (and should be only) logo
            return ResponseEntity.ok(logos.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST save/update the logo
    @PostMapping
    public ResponseEntity<Logo> saveLogo(@RequestBody Logo logo) {
        try {
            // Delete all existing logos first (there should only be one)
            logoRepository.deleteAll();

            // Set timestamps (using Singapore timezone)
            LocalDateTime nowSingapore = ZonedDateTime.now(ZoneId.of("Asia/Singapore")).toLocalDateTime();
            logo.setCreatedAt(nowSingapore);
            logo.setUpdatedAt(nowSingapore);

            Logo savedLogo = logoRepository.save(logo);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedLogo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE the logo
    @DeleteMapping
    public ResponseEntity<Void> deleteLogo() {
        try {
            logoRepository.deleteAll();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
