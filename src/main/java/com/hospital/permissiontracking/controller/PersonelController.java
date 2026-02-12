package com.hospital.permissiontracking.controller;

import com.hospital.permissiontracking.entity.Personel;
import com.hospital.permissiontracking.repository.PersonelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/personels")
@RequiredArgsConstructor
public class PersonelController {

    private final PersonelRepository personelRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Personel create(@RequestBody Personel personel) {
        // Faz-1: basic create
        return personelRepository.save(personel);
    }

    @GetMapping
    public List<Personel> list() {
        return personelRepository.findAll();
    }

    @GetMapping("/{id}")
    public Personel get(@PathVariable Long id) {
        return personelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Personel bulunamadı"));
    }
}
