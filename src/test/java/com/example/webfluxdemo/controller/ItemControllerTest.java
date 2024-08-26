package com.example.webfluxdemo.controller;

import com.example.webfluxdemo.entity.Item;
import com.example.webfluxdemo.exception.ResourceNotFoundException;
import com.example.webfluxdemo.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static org.assertj.core.api.Assertions.assertThat;


@WebFluxTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ItemService itemService;

    private Item sampleItem;

    @BeforeEach
    void setUp() {
        sampleItem = new Item(1L, "Sample Item");
    }

    @Test
    void testCreateItem() {
        Mockito.when(itemService.createItem(any(Item.class))).thenReturn(Mono.just(sampleItem));

        webTestClient.post()
                .uri("/api/items")
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(sampleItem))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Item.class)
                .value(item -> assertThat(item).usingRecursiveComparison().isEqualTo(sampleItem));
    }

    @Test
    void testGetItemById() {
        Mockito.when(itemService.getItemById(anyLong())).thenReturn(Mono.just(sampleItem));

        webTestClient.get()
                .uri("/api/items/{id}", sampleItem.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Item.class)
                .value(item -> assertThat(item).usingRecursiveComparison().isEqualTo(sampleItem));
    }

    @Test
    void testGetItemByIdNotFound() {
        Mockito.when(itemService.getItemById(anyLong())).thenReturn(Mono.error(new ResourceNotFoundException("Item not found")));

        webTestClient.get()
                .uri("/api/items/{id}", 999L)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testGetAllItems() {
        Mockito.when(itemService.getAllItems()).thenReturn(Flux.just(sampleItem));

        webTestClient.get()
                .uri("/api/items")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Item.class)
                .value(items -> assertThat(items).usingRecursiveFieldByFieldElementComparator().contains(sampleItem));
    }

    @Test
    void testUpdateItem() {
        Mockito.when(itemService.updateItem(eq(sampleItem.getId()), any(Item.class)))
                .thenReturn(Mono.just(sampleItem));

        webTestClient.put()
                .uri("/api/items/{id}", sampleItem.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(sampleItem))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Item.class)
                .value(item -> assertThat(item).usingRecursiveComparison().isEqualTo(sampleItem));
    }

    @Test
    void testUpdateItemNotFound() {
        Mockito.when(itemService.updateItem(eq(999L), any(Item.class)))
                .thenReturn(Mono.error(new ResourceNotFoundException("Item not found")));

        webTestClient.put()
                .uri("/api/items/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(sampleItem))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteItem() {
        Mockito.when(itemService.deleteItem(anyLong())).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/items/{id}", sampleItem.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testDeleteItemNotFound() {
        Mockito.when(itemService.deleteItem(anyLong())).thenReturn(Mono.error(new ResourceNotFoundException("Item not found")));

        webTestClient.delete()
                .uri("/api/items/{id}", 999L)
                .exchange()
                .expectStatus().isNotFound();
    }
}
