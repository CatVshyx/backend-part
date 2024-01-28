package com.example.backend.service;


import com.example.backend.dao.Category;
import com.example.backend.dao.Form;
import com.example.backend.dao.LinkedTable;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.FormRepository;
import com.example.backend.repository.LinkedTableRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.*;

@Service
public class MediaService {
    @Autowired
    private FormRepository formRepository;
    @Value("${root.path}")
    private String ROOT;
    @Autowired
    private LinkedTableRepository linkedTableRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private final static ObjectMapper mapper = new ObjectMapper();

    public Map<String,Object> createForm(String name, List<Integer> categories) throws EntityNotFoundException, EntityExistsException{
        if (formRepository.existsByName(name))
            throw new EntityExistsException("This name is already defined");
        if(!isCategoryNotFound(categories))
            throw new EntityNotFoundException("Some category IDs were not found");

        Form form = formRepository.save(new Form(name));
        for (Integer id : categories){
            Category category = categoryRepository.findById(id).get();
            category.setLastNumber(category.getLastNumber() + 1);
            linkedTableRepository.save(new LinkedTable(category,form,category.getLastNumber()));
            categoryRepository.save(category);
        }

        Map<String,Object> map = mapper.convertValue(form,Map.class);
        map.put("categories",getFormCategories(form.getId()));

        return map;
    }
    public String setFile(Form form, String filename,int subdirectory) throws IllegalArgumentException{
        String path = ROOT  + (subdirectory == 0 ? "BLANK" : "SAMPLE")  + File.separator;

        if (subdirectory == 0){
            deleteFile(path + form.getBlankLink());
            form.setBlankLink(filename);
        }else{
            if (!filename.contains(".pdf"))
                throw new IllegalArgumentException("Only pdf format is used in samples");
            deleteFile(path + form.getSampleLink());
            form.setSampleLink(filename);
        }

        formRepository.save(form);
        return path;
    }
    public Form getFormById(long id) throws EntityNotFoundException{
        return formRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
    public List<Form> getForms(){
        List<Form> list = formRepository.findAll();
        list.forEach(this::setFilePath);

        return list;
    }
    public List<Map<String,Object>> getFormsByCategory(int categoryId) throws EntityNotFoundException{
        if (categoryId > categoryRepository.count())
            throw new EntityNotFoundException("Category not found");

        List<LinkedTable> list = linkedTableRepository.findAllByCategory(categoryId);
        List<Map<String,Object>> out = new ArrayList<>();

        list.forEach(el -> {
            setFilePath(el.getForm());
            Map<String, Object> map = mapper.convertValue(el.getForm(),Map.class);
            map.put("position",el.getListNumber());
            out.add(map);
        });

        return out;
    }
    private void deleteFile(String filepath){
        new File(filepath).delete();
    }
    public void deleteForm(long id) throws EntityNotFoundException {
        Form form = formRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        linkedTableRepository.deleteByFormId(id);

        deleteFile(ROOT +  "BLANK" + File.separator + form.getBlankLink());
        deleteFile(ROOT +  "SAMPLE" + File.separator + form.getSampleLink());

        formRepository.delete(form);
    }

    private void setFilePath(Form form){
        if (form.getBlankLink() != null)
            form.setBlankLink("/api/media/BLANK" +  '/' + form.getBlankLink());
        if (form.getSampleLink() != null)
            form.setSampleLink("/api/media/SAMPLE"  + '/' + form.getSampleLink());
    }
    public void deleteFileById(long id, String subdirectory) throws EntityNotFoundException,IllegalArgumentException {
        Form form = formRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        if (subdirectory.equals("BLANK") && form.getBlankLink() != null){
            deleteFile(ROOT + subdirectory + File.separator + form.getBlankLink());
            form.setBlankLink(null);
        }else if(subdirectory.equals("SAMPLE") && form.getSampleLink() != null){
            deleteFile(ROOT + subdirectory +  File.separator + form.getSampleLink());
            form.setSampleLink(null);
        }

        formRepository.save(form);
    }
    public List<Category> getCategories(){
        return categoryRepository.findAll();
    }
    public List<Map<String,Integer>> getFormCategories(long id) throws EntityNotFoundException {
        List<Map<String,Integer>> list = new ArrayList<>();

        linkedTableRepository.findCategoriesAndPositionsByFormId(id)
                .forEach(obj -> list.add(Map.of("categoryId",(Integer) obj[0], "position", (Integer) obj[1] )));
        if (list.size() == 0)
            throw new EntityNotFoundException();
        return list;
    }
    public List<Map<String,Integer>> editFormCategories(Form form, List<Integer> categories) throws IllegalArgumentException {
        if (!isCategoryNotFound(categories))
            throw new EntityNotFoundException("Some category IDs were not found");
        List<Integer> prevList = linkedTableRepository.findCategoriesAndPositionsByFormId(form.getId()).stream()
                .mapToInt(obj -> (Integer) obj[0]).boxed().toList();

        linkedTableRepository.deleteCategoriesById(form.getId(),categories);
        categories.stream().filter(unsorted -> !prevList.contains(unsorted)).forEach(sorted -> {
            Category category = categoryRepository.findById(sorted).get();
            category.setLastNumber(category.getLastNumber() + 1);

            linkedTableRepository.save(new LinkedTable(category,form, category.getLastNumber()));
            categoryRepository.save(category);
        });

        return getFormCategories(form.getId());
    }

    public void editFormName(Form form, String name) throws EntityExistsException{
        if (form.getName().equals(name))
            return;
        if(formRepository.existsByName(name))
            throw new EntityExistsException("Such name is already occupied");

        form.setName(name);
        formRepository.save(form);
    }
    private boolean isCategoryNotFound(Collection<Integer> collection){
        int counted = (int) categoryRepository.count();
        for (Integer category : collection){
            if (category <= 0 || category > counted)
                return false;
        }
        return true;
    }
}
