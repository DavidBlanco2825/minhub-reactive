
I need to add Swagger annotation that allow for the documentation of the following controller endpoints, please keep in my that we are using Spring WebFlux
I added the definition of the ItemService so you can see the return cases for each one of the endpoints

```java
package com.example.webfluxdemo.controller;

import com.example.webfluxdemo.entity.Item;
import com.example.webfluxdemo.exception.ResourceNotFoundException;
import com.example.webfluxdemo.service.ItemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/items")
@Tag(name = "ItemController", description = "Operations related to item management")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public Mono<Item> createItem(@RequestBody Item item) {
        return itemService.createItem(item);
    }

    @GetMapping("/{id}")
    public Mono<Item> getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    @GetMapping
    public Flux<Item> getAllItems() {
        return itemService.getAllItems();
    }

    @PutMapping("/{id}")
    public Mono<Item> updateItem(@PathVariable Long id, @RequestBody Item item) {
        return itemService.updateItem(id, item);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> deleteItem(@PathVariable Long id) {
        return itemService.deleteItem(id)
                .then(Mono.just(ResponseEntity.noContent().build()))
                .onErrorResume(ResourceNotFoundException.class,
                        ex -> Mono.just(ResponseEntity.notFound().build()));
    }
}
```

```java
package com.example.webfluxdemo.service.impl;

import com.example.webfluxdemo.entity.Item;
import com.example.webfluxdemo.exception.ResourceNotFoundException;
import com.example.webfluxdemo.repository.ItemRepository;
import com.example.webfluxdemo.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.example.webfluxdemo.config.Constants.ITEM_NOT_FOUND_ID;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Mono<Item> createItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    public Mono<Item> getItemById(Long id) {
        return itemRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ITEM_NOT_FOUND_ID + id)));
    }

    @Override
    public Flux<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public Mono<Item> updateItem(Long id, Item item) {
        return itemRepository.findById(id)
                .flatMap(existingItem -> updateExistingItem(existingItem, item))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ITEM_NOT_FOUND_ID + id)));
    }

    private Mono<Item> updateExistingItem(Item existingItem, Item newItemData) {
        existingItem.setName(newItemData.getName());
        return itemRepository.save(existingItem);
    }

    @Override
    public Mono<Void> deleteItem(Long id) {
        return itemRepository.findById(id)
                .flatMap(itemRepository::delete)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ITEM_NOT_FOUND_ID + id)));
    }
}
```