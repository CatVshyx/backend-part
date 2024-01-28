package com.example.backend.controller;

import com.example.backend.dao.Category;
import com.example.backend.dao.Form;
import com.example.backend.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    @Autowired
    private MediaService formService;
    @Value("${root.path}")
    private String ROOT;

    @GetMapping(path = {"/",""})
    public List<Form> getForms(){
        return formService.getForms();
    }

    @GetMapping("/get-by-category")
    public ResponseEntity<Object> getFormsByCategory(@RequestParam(name = "id") int category){
        return ResponseEntity.ok(formService.getFormsByCategory(category));
    }
    @GetMapping("/get-form-categories")
    public ResponseEntity<Object> getCategoriesById(@RequestParam(name = "id") int id){
           return ResponseEntity.ok(formService.getFormCategories(id));
    }
    @GetMapping("/categories")
    public List<Category> getCategories(){
        return formService.getCategories();
    }
    @GetMapping(value = "/{sub}/{name}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String sub,
                                            @PathVariable String name, @RequestParam String fileName) throws IOException{

            String path = ROOT + '/' + sub + '/' + name;
            Resource resource = new UrlResource(Paths.get(path).toUri());

            if (resource.exists()) {
                String temp = resource.getURI().toString();
                String responseName = fileName + (temp.substring(temp.indexOf('.')));
                System.out.println("Sending file");
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + UriUtils.encode(responseName, StandardCharsets.UTF_8) + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }


    }
    @GetMapping("/get-production-sample")
    public ResponseEntity<Resource> getProductionSample() throws IOException {
        return downloadFile("SAMPLE","criminal-production-sample.pdf","Зразок кримінального провадження");
    }
    @GetMapping("/get-photorobot")
    public ResponseEntity<Resource> getPhotorobot() throws IOException{
        return downloadFile("SAMPLE","photorobot.rar","Фоторобот");
    }
    @PreAuthorize("hasAuthority(true)")
    @PostMapping("/upload")
    public ResponseEntity<Object> uploadFile(@RequestParam(value = "blank_file",required = false) MultipartFile blank,
                                             @RequestParam(value = "sample_file", required = false) MultipartFile sample,
                                             @RequestParam("id") int formId) throws IOException {
        // true - Blank false - sample
        Form form = formService.getFormById(formId);
        MultipartFile[] files = {blank,sample};
        for(int i = 0; i < files.length; i++){
            if (files[i] == null)
                continue;
            String filename = files[i].getOriginalFilename();
            String uuid = UUID.randomUUID() + filename.substring(filename.lastIndexOf('.'));
            String directoryPath = formService.setFile(form,uuid,i);

            Path path = Files.createDirectories(Paths.get(directoryPath));
            files[i].transferTo(Path.of(path + File.separator + uuid));
        }

        return ResponseEntity.ok().build();

    }
    @PreAuthorize("hasAuthority(true)")
    @DeleteMapping("/delete-file")
    public ResponseEntity<Object> deleteFile(@RequestBody HashMap<String, String> map){
        long id = Long.parseLong(map.get("id"));
        String type = map.get("type");
        if (!type.equals("BLANK") && !type.equals("SAMPLE"))
            throw new IllegalArgumentException("Only BLANK or SAMPLE are available");

        formService.deleteFileById(id,type);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasAuthority(true)")
    @DeleteMapping("/delete-form")
    public ResponseEntity<Object> deleteForm(@RequestParam(name = "id") int id){
        formService.deleteForm(id);
        return ResponseEntity.ok().build();
    }
    @PreAuthorize("hasAuthority(true)")
    @PostMapping("/create-form")
    public ResponseEntity<Object> createForm(@RequestBody Map<String,Object> map){
        String name = (String) map.get("name");
        if (name == null)
            throw new  NullPointerException("Name of form is not defined");
        String wrapCategories = map.get("categories").toString().substring(1,map.get("categories").toString().length() - 1).replaceAll(" ","");
        List<Integer> categories = Arrays.stream(wrapCategories.split(",")).map(Integer::valueOf).toList();

        return ResponseEntity.ok().body(formService.createForm(name,categories));
    }
    @PreAuthorize("hasAuthority(true)")
    @PostMapping("/edit-form")
    public ResponseEntity<Object> editForm(@RequestBody HashMap<String, Object> map){
        long id = Long.parseLong(map.get("id").toString());
        Form form = formService.getFormById(id);
        Object value = null;

        if (map.get("name") != null)
            formService.editFormName(form,map.get("name").toString());
        if (map.get("categories") != null){
            String values = map.get("categories").toString().substring(1, map.get("categories").toString().length() - 1).replaceAll(" ","");
            List<Integer> arr = Arrays.stream(values.split(",")).map(Integer::valueOf).toList();
            value = formService.editFormCategories(form,arr);
        }

        return ResponseEntity.ok(value);
    }

}
