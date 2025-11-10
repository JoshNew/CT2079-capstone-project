package com.capstone.ticketing_system;

import com.capstone.ticketing_system.models.Advertisement;
import com.capstone.ticketing_system.models.repositories.AdvertisementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/api/advertisements")
public class AdvertisementController {

    @Autowired
    private AdvertisementRepository advertisementRepository;

    // GET all advertisements (ordered by displayOrder)
    @GetMapping
    public List<Advertisement> getAllAdvertisements() {
        return advertisementRepository.findAllByOrderByDisplayOrderAsc();
    }

    // GET only active advertisements (for public display)
    @GetMapping("/active")
    public List<Advertisement> getActiveAdvertisements() {
        return advertisementRepository.findByActiveOrderByDisplayOrderAsc(true);
    }

    // GET advertisement by ID
    @GetMapping("/{id}")
    public ResponseEntity<Advertisement> getAdvertisementById(@PathVariable String id) {
        Optional<Advertisement> advertisement = advertisementRepository.findById(id);
        return advertisement.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST create new advertisement
    @PostMapping
    public ResponseEntity<Advertisement> createAdvertisement(@RequestBody Advertisement advertisement) {
        try {
            // Set timestamps (using Singapore timezone)
            LocalDateTime nowSingapore = ZonedDateTime.now(ZoneId.of("Asia/Singapore")).toLocalDateTime();
            advertisement.setCreatedAt(nowSingapore);
            advertisement.setUpdatedAt(nowSingapore);

            // If displayOrder not set, put it at the end
            if (advertisement.getDisplayOrder() == 0) {
                long count = advertisementRepository.count();
                advertisement.setDisplayOrder((int) count);
            }

            Advertisement savedAdvertisement = advertisementRepository.save(advertisement);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAdvertisement);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST create multiple advertisements at once
    @PostMapping("/batch")
    public ResponseEntity<List<Advertisement>> createAdvertisements(@RequestBody List<Advertisement> advertisements) {
        try {
            // Set displayOrder for each ad (using Singapore timezone)
            LocalDateTime nowSingapore = ZonedDateTime.now(ZoneId.of("Asia/Singapore")).toLocalDateTime();
            int startOrder = (int) advertisementRepository.count();
            for (int i = 0; i < advertisements.size(); i++) {
                Advertisement ad = advertisements.get(i);
                ad.setDisplayOrder(startOrder + i);
                ad.setCreatedAt(nowSingapore);
                ad.setUpdatedAt(nowSingapore);
            }

            List<Advertisement> savedAdvertisements = advertisementRepository.saveAll(advertisements);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAdvertisements);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT update advertisement
    @PutMapping("/{id}")
    public ResponseEntity<Advertisement> updateAdvertisement(@PathVariable String id, @RequestBody Advertisement advertisementDetails) {
        try {
            Optional<Advertisement> optionalAdvertisement = advertisementRepository.findById(id);

            if (optionalAdvertisement.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Advertisement advertisement = optionalAdvertisement.get();

            // Update fields
            if (advertisementDetails.getName() != null) {
                advertisement.setName(advertisementDetails.getName());
            }
            if (advertisementDetails.getImageData() != null) {
                advertisement.setImageData(advertisementDetails.getImageData());
            }
            if (advertisementDetails.getType() != null) {
                advertisement.setType(advertisementDetails.getType());
            }
            if (advertisementDetails.getSize() != null) {
                advertisement.setSize(advertisementDetails.getSize());
            }
            advertisement.setDisplayOrder(advertisementDetails.getDisplayOrder());
            advertisement.setActive(advertisementDetails.isActive());
            // Update timestamp (using Singapore timezone)
            advertisement.setUpdatedAt(ZonedDateTime.now(ZoneId.of("Asia/Singapore")).toLocalDateTime());

            Advertisement updatedAdvertisement = advertisementRepository.save(advertisement);
            return ResponseEntity.ok(updatedAdvertisement);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE advertisement by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdvertisement(@PathVariable String id) {
        try {
            if (!advertisementRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            advertisementRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE all advertisements
    @DeleteMapping
    public ResponseEntity<Void> deleteAllAdvertisements() {
        try {
            advertisementRepository.deleteAll();
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // PUT reorder advertisements
    @PutMapping("/reorder")
    public ResponseEntity<List<Advertisement>> reorderAdvertisements(@RequestBody List<String> orderedIds) {
        try {
            // Use Singapore timezone for all updates
            LocalDateTime nowSingapore = ZonedDateTime.now(ZoneId.of("Asia/Singapore")).toLocalDateTime();
            for (int i = 0; i < orderedIds.size(); i++) {
                String id = orderedIds.get(i);
                Optional<Advertisement> optionalAd = advertisementRepository.findById(id);

                if (optionalAd.isPresent()) {
                    Advertisement ad = optionalAd.get();
                    ad.setDisplayOrder(i);
                    ad.setUpdatedAt(nowSingapore);
                    advertisementRepository.save(ad);
                }
            }

            return ResponseEntity.ok(advertisementRepository.findAllByOrderByDisplayOrderAsc());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}