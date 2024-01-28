package com.example.backend.service;

import com.example.backend.dao.Document;
import com.example.backend.dao.Type;
import com.example.backend.repository.DocumentRepository;
import com.example.backend.repository.TypeRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentService {
    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private DocumentRepository documentRepository;
    public List<Type> getTypes() {
        return typeRepository.findAll();
    }

    record DocumentDTO(long id, String name, String link, String description){}
    public List<DocumentDTO> getDocumentsByType(int id) throws EntityNotFoundException{
        if(!typeRepository.existsById(id))
            throw new EntityNotFoundException();
        List<DocumentDTO> list = new ArrayList<>();
        documentRepository.findAllByTypeId(id).forEach(el -> list.add(new DocumentDTO(el.getId(),el.getName(),el.getLink(), el.getDescription())));
        return list;
    }

    public Document createDocument(String name, String link, int typeId,String description) throws EntityExistsException, EntityNotFoundException {
        Type type = typeRepository.findById(typeId).orElseThrow(EntityNotFoundException::new);
        if(documentRepository.existsByName(name))
            throw new EntityExistsException("Such document name is already defined");

        Document document = new Document(name,type,link,description);
        return documentRepository.save(document);
    }

    public void deleteDocument(long id) {
        documentRepository.deleteById(id);
    }

    public void editDocument(long id, String name, String link, int typeId, String description) throws EntityNotFoundException{
        Document document = documentRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        Type type = typeRepository.findById(typeId).orElseThrow(() -> {throw new EntityNotFoundException("Type not found");});

        if(name != null && !document.getName().equals(name)){
            if (documentRepository.existsByName(name))
                throw new EntityExistsException("Such name is already defined");
            document.setName(name);
        }
        if (link != null && !document.getLink().equals(link))
            document.setLink(link);
        if (!document.getType().equals(type))
            document.setType(type);
        if (description != null )
            document.setDescription(description);

        documentRepository.save(document);
    }
}
