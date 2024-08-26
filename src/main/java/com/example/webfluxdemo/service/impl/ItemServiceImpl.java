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
