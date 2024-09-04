package com.example.contactapi;

import com.example.contactapi.domain.Contact;
import com.example.contactapi.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.example.contactapi.constant.Constant.PHOTO_DIRECTORY;
import static org.springframework.util.MimeTypeUtils.IMAGE_JPEG_VALUE;
import static org.springframework.util.MimeTypeUtils.IMAGE_PNG_VALUE;

@RestController
@RequestMapping("/contacts")
@CrossOrigin(origins = "http://localhost:4200")
public class ContactController {
    @Autowired
    private ContactService service;
    @PostMapping
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
        //return ResponseEntity.ok().body(contactService.createContact(contact));
        Contact createdContact = service.createContact(contact);
        URI location = URI.create("/contacts/" + createdContact.getId());
        return ResponseEntity.created(location).body(createdContact);
    }


    @GetMapping //example http://localhost:8080/contacts?page=0&size=3
    public ResponseEntity<Page<Contact>> getContacts(@RequestParam(value = "page", defaultValue = "0") int page, // url/contacts?page=1&size=4
                                                     @RequestParam(value = "size", defaultValue = "20") int size) {
        try {
            // Add a delay
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Page<Contact> contacts = service.getAllContacts(page, size);
        return ResponseEntity.ok().body(contacts);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContact(@PathVariable(value = "id") String id) {
        try {
            Contact contact = service.getContact(id);
            return ResponseEntity.ok().body(contact); //if contact exist
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); //else return 404 with null body
        }
    }

    @DeleteMapping("/{id}")
    public String deleteContact(@PathVariable String id){
        service.deleteById(id);
        return "OK";
    }

    @GetMapping(path = "/image/{filename}", produces = { IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE })
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException {

        return Files.readAllBytes(Paths.get(PHOTO_DIRECTORY + filename));
    }

    //other way of saving images
//    @PutMapping("/photo")
//    public ResponseEntity<String> uploadPhoto(@RequestParam("id") String id, @RequestParam("file") MultipartFile file) {
//        return ResponseEntity.ok().body(service.uploadPhoto(id, file));
//    }

}
