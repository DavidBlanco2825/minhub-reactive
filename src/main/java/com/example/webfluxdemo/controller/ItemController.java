package com.example.webfluxdemo.controller;

import com.example.webfluxdemo.entity.Item;
import com.example.webfluxdemo.exception.ResourceNotFoundException;
import com.example.webfluxdemo.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Create a new item", description = "Creates a new item and returns the created item.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Item.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string", example = "Invalid item data."))),
            @ApiResponse(responseCode = "500", description = "Internal server error.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string", example = "An error occurred while processing your request.")))
    })
    public Mono<Item> createItem(
            @RequestBody
            @Parameter(description = "Item data for the new item", required = true) Item item) {
        return itemService.createItem(item);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get item by ID", description = "Retrieves an item by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Item.class))),
            @ApiResponse(responseCode = "404", description = "Item not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string", example = "Item not found."))),
            @ApiResponse(responseCode = "500", description = "Internal server error.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string", example = "An error occurred while processing your request.")))
    })
    public Mono<Item> getItemById(
            @PathVariable("id")
            @Parameter(description = "ID of the item to be retrieved", required = true, example = "1") Long id) {
        return itemService.getItemById(id);
    }

    @GetMapping
    @Operation(summary = "Get all items", description = "Retrieves all items.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Items retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Item.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string", example = "An error occurred while processing your request.")))
    })
    public Flux<Item> getAllItems() {
        return itemService.getAllItems();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an item by ID", description = "Updates an item by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Item.class))),
            @ApiResponse(responseCode = "404", description = "Item not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string", example = "Item not found."))),
            @ApiResponse(responseCode = "500", description = "Internal server error.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string", example = "An error occurred while processing your request.")))
    })
    public Mono<Item> updateItem(
            @PathVariable("id")
            @Parameter(description = "ID of the item to be updated", required = true, example = "1") Long id,
            @RequestBody
            @Parameter(description = "Updated item data", required = true) Item item) {
        return itemService.updateItem(id, item);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an item by ID", description = "Deletes an item by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Item not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string", example = "Item not found."))),
            @ApiResponse(responseCode = "500", description = "Internal server error.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "string", example = "An error occurred while processing your request.")))
    })
    public Mono<ResponseEntity<Object>> deleteItem(
            @PathVariable("id")
            @Parameter(description = "ID of the item to be deleted", required = true, example = "1") Long id) {
        return itemService.deleteItem(id)
                .then(Mono.just(ResponseEntity.noContent().build()))
                .onErrorResume(ResourceNotFoundException.class,
                        ex -> Mono.just(ResponseEntity.notFound().build()));
    }
}
