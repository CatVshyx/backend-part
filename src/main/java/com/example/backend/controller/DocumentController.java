package com.example.backend.controller;

import com.example.backend.service.DocumentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/government-link")
public class DocumentController {
    @Autowired
    private DocumentService service;
    @GetMapping("/get-types")
    public ResponseEntity<Object> getTypes(){
        return ResponseEntity.ok(service.getTypes());
    }
    @GetMapping("/get-by-type")
    public ResponseEntity<Object> getDocumentsByType(@RequestParam(name = "id") int id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.getDocumentsByType(id));
    }
    @PreAuthorize("hasAuthority(true)")
    @PostMapping("/create")
    public ResponseEntity<Object> createDocument(@RequestBody HashMap<String,String> map) throws JsonProcessingException {
        String name = map.get("name");
        String link = map.get("link");
        String description = map.get("description");
        int type = Integer.parseInt(map.get("typeId"));
        if (name == null || link == null)
            throw new NullPointerException();
        try{
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(serializeObjectWithFilter(service.createDocument(name,link,type,description),null));
        }catch (DataIntegrityViolationException e){
            return ResponseEntity.badRequest().body(Map.of("error","Data sent for 'description' is too large, max size is 255 characters"));
        }
    }
    @PreAuthorize("hasAuthority(true)")
    @PatchMapping("/edit")
    public ResponseEntity<Object> editDocument(@RequestBody Map<String,String> map){
        long id = Long.parseLong(map.get("id"));
        String name = map.get("name");
        String link = map.get("link");
        int typeId = Integer.parseInt(map.get("typeId"));
        String description = map.get("description");

        service.editDocument(id,name,link,typeId,description);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority(true)")
    @DeleteMapping("/delete")
    public ResponseEntity<Object> deleteDocument(@RequestParam(name = "id") int id){
        service.deleteDocument(id);
        return ResponseEntity.ok().build();
    }
    private String serializeObjectWithFilter(Object object, Set<String> excludedProperties) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        SimpleBeanPropertyFilter theFilter = SimpleBeanPropertyFilter.serializeAllExcept(excludedProperties);
        FilterProvider filters = new SimpleFilterProvider().addFilter("myFilter", theFilter);

        return mapper.writer(filters).writeValueAsString(object);
    }
}
