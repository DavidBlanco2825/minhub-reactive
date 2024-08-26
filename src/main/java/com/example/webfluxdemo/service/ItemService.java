package com.example.webfluxdemo.service;

import com.example.webfluxdemo.entity.Item;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemService {

    Mono<Item> createItem(Item item);

    Mono<Item> getItemById(Long id);

    Flux<Item> getAllItems();

    Mono<Item> updateItem(Long id, Item item);

    Mono<Void> deleteItem(Long id);
}
