package com.example.EShop.services;

import com.example.EShop.dtos.CategoryDto;
import com.example.EShop.dtos.CommentDto;
import com.example.EShop.dtos.ProductDto;
import com.example.EShop.models.Comment;
import com.example.EShop.models.Image;
import com.example.EShop.models.Product;
import com.example.EShop.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final BasketRepository basketRepository;
    private final ImageRepository imageRepository;


    public ProductDto findAllProducts(Long id) {
        return productRepository.findById(id)
                .map(this::convertProductToDto)
                .orElse(null);  // Вернуть null, если продукт не найден
    }

    public List<ProductDto> findAllProducts() {

        return productRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(this::convertProductToDto)
                .collect(Collectors.toList());
    }

    private ProductDto convertProductToDto(Product product) {
        Image image = imageRepository.findById(product.getPreviewImageId()).orElse(null);
        String base64Image = image != null ? Base64.getEncoder().encodeToString(image.getBytes()) : null;

        List<Comment> commentsRaw = product.getComments();
        List<CommentDto> comments = commentsRaw.stream()
                .map(this::convertCommentToDto)
                .collect(Collectors.toList());

        List<CategoryDto> categories = new ArrayList<>();
        String[] categoryStrings = product.getCategory().split(",");
        String[] categoryOrders = product.getCategoryOrder().split("/");

        for (int i = 0; i < categoryStrings.length; i++) {
            String[] categoryParts = categoryStrings[i].split("/");
            String name = categoryParts.length > 0 ? categoryParts[0] : null;
            String subcategory = categoryParts.length > 1 ? categoryParts[1] : null;
            String subsubcategory = categoryParts.length > 2 ? categoryParts[2] : null;

            String[] orderStrings = categoryOrders[i].split(",");
            Integer[] orderInt = new Integer[orderStrings.length];
            for (int k = 0; k < orderStrings.length; k++) {
                orderInt[k] = Integer.parseInt(orderStrings[k]);
            }

            Integer order = orderInt[0];
            Integer subcategoryOrder = null;
            Integer subsubcategoryOrder = null;

            if (categoryParts.length > 1) {
                subcategoryOrder = (orderInt.length > 1 && i < orderInt.length) ? orderInt[1] : null;
            }
            if (categoryParts.length > 2) {
                subsubcategoryOrder = (orderInt.length > 2 && i < orderInt.length) ? orderInt[2] : null;
            }

            categories.add(new CategoryDto(name, subcategory, subsubcategory, order, subcategoryOrder, subsubcategoryOrder));
        }
        return new ProductDto(
                product.getId(),
                product.getTitle(),
                product.getDescription(),
                product.getCount(),
                product.getPrice(),
                product.getPreviewImageId(),
                product.getDiscountPrice(),
                categories,
                comments,
                base64Image
        );
    }

    public void generateCategoryOrderForProduct(Product product) {
        // Создаем Map для категорий, подкатегорий и подподкатегорий
        Map<String, Map<String, Integer>> categoryMap = new HashMap<>();
        Map<String, Map<String, Integer>> subcategoryMap = new HashMap<>();
        Map<String, Integer> subsubCategoryMap = new HashMap<>();

        List<Product> allProducts = productRepository.findAll();

        // Проходим по всем существующим продуктам и заполняем карты
        for (Product existingProduct : allProducts) {
            String[] existingCategories = existingProduct.getCategory().split(",");
            String[] existingCategoryOrders = existingProduct.getCategoryOrder().split("/");

            for (int i = 0; i < existingCategories.length; i++) {
                String[] categoryParts = existingCategories[i].split("/");
                String[] orderParts = existingCategoryOrders[i].split(",");

                // Работа с главной категорией
                if (categoryParts.length > 0) {
                    String mainCategory = categoryParts[0];
                    int mainOrder = Integer.parseInt(orderParts[0]);

                    categoryMap.putIfAbsent(mainCategory, new HashMap<>());

                    // Работа с подкатегорией
                    if (categoryParts.length > 1) {
                        String subCategory = categoryParts[1];
                        int subOrder = Integer.parseInt(orderParts[1]);

                        categoryMap.get(mainCategory).putIfAbsent(subCategory, subOrder);

                        // Работа с подподкатегорией
                        if (categoryParts.length > 2) {
                            String subSubCategory = categoryParts[2];
                            int subSubOrder = Integer.parseInt(orderParts[2]);

                            subcategoryMap.putIfAbsent(subCategory, new HashMap<>());
                            subcategoryMap.get(subCategory).putIfAbsent(subSubCategory, subSubOrder);

                            subsubCategoryMap.putIfAbsent(subSubCategory, subSubOrder);
                        }
                    }
                }
            }
        }

        // Теперь генерируем порядковый номер для нового продукта
        String[] newCategories = product.getCategory().split("/");
        StringBuilder categoryOrderBuilder = new StringBuilder();

        // Обработка главной категории
        String mainCategory = newCategories[0];
        int mainOrder;

        // Если главная категория уже существует, используем её порядковый номер
        if (categoryMap.containsKey(mainCategory) && !categoryMap.get(mainCategory).isEmpty()) {
            // Здесь вместо size() используем конкретный порядковый номер для главной категории
            mainOrder = categoryMap.get(mainCategory).values().iterator().next();
        } else {
            // Если категория новая, присваиваем первый свободный порядковый номер
            mainOrder = categoryMap.size() + 1;
            categoryMap.put(mainCategory, new HashMap<>());
        }
        categoryOrderBuilder.append(mainOrder);

        // Обработка подкатегорий и подподкатегорий
        for (int i = 1; i < newCategories.length; i++) {
            String currentCategory = newCategories[i];
            int currentOrder = 0;

            if (i == 1) { // Подкатегория
                if (categoryMap.get(mainCategory).containsKey(currentCategory)) {
                    currentOrder = categoryMap.get(mainCategory).get(currentCategory);
                } else {
                    currentOrder = categoryMap.get(mainCategory).size() + 1;
                    categoryMap.get(mainCategory).put(currentCategory, currentOrder);
                }
            } else if (i == 2) { // Подподкатегория
                String subCategory = newCategories[1];
                if (subcategoryMap.containsKey(subCategory) && subcategoryMap.get(subCategory).containsKey(currentCategory)) {
                    currentOrder = subcategoryMap.get(subCategory).get(currentCategory);
                } else {
                    currentOrder = subcategoryMap.get(subCategory).size() + 1;
                    subcategoryMap.get(subCategory).put(currentCategory, currentOrder);
                }
            }

            categoryOrderBuilder.append(",").append(currentOrder);
        }

        // Устанавливаем новый порядковый номер для продукта
        product.setCategoryOrder(categoryOrderBuilder.toString());
    }

    @Transactional
    public void saveProduct(Product product, String category, String subCategory, String subSubCategory, MultipartFile file1) throws IOException {
        Image image1;

        if (file1.getSize() != 0 && file1.getSize() > 0) {
            image1 = toImageEntity(file1);

            product.addImageToProduct(image1);
        }
        String cat = category;
        if (subCategory != null)
            cat += "/" + subCategory;
        product.setCategory(cat);
        if (subSubCategory != null)
            cat += "/" + subSubCategory;

        generateCategoryOrderForProduct(product);
        log.info("Saving new Product. Title: {}", product.getTitle());
        Product productFromDB = productRepository.save(product);
        if (!productFromDB.getImages().isEmpty()) {
            productFromDB.setPreviewImageId(productFromDB.getImages().get(0).getId());
        }
        productRepository.save(product);
    }

    public Image toImageEntity(MultipartFile file) throws IOException {
        Image image = new Image();
        image.setName(file.getName());
        image.setOriginalFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setSize(file.getSize());
        image.setBytes(file.getBytes());
        return image;
    }

    public void deleteProduct(Long id) {
        basketRepository.deleteProductFromBasketProducts(id);
        productRepository.deleteById(id);
    }

    private CommentDto convertCommentToDto(Comment comment) {
        CommentDto dto = new CommentDto();

        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setImages(comment.getImages());
        dto.setScore(comment.getScore());
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(comment.getUser().getUsername());
        dto.setProductId(comment.getProduct().getId());
        dto.setProductTitle(comment.getProduct().getTitle());

        return dto;
    }

    public void reorderCategories(Long productId, List<CategoryDto> newOrder) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        StringBuilder categoryBuilder = new StringBuilder();
        StringBuilder categoryOrderBuilder = new StringBuilder();

        for (CategoryDto categoryDto : newOrder) {
            String category = categoryDto.getName();
            if (categoryDto.getSubcategory() != null) {
                category += "/" + categoryDto.getSubcategory();
            }
            if (categoryDto.getSubsubcategory() != null) {
                category += "/" + categoryDto.getSubsubcategory();
            }
            if (categoryBuilder.length() > 0) {
                categoryBuilder.append(",");
            }
            categoryBuilder.append(category);

            String order = categoryDto.getOrder().toString();
            if (categoryDto.getSubcategoryOrder() != null) {
                order += "," + categoryDto.getSubcategoryOrder();
            }
            if (categoryDto.getSubsubcategoryOrder() != null) {
                order += "," + categoryDto.getSubsubcategoryOrder();
            }
            if (categoryOrderBuilder.length() > 0) {
                categoryOrderBuilder.append("/");
            }
            categoryOrderBuilder.append(order);
        }

        product.setCategory(categoryBuilder.toString());
        product.setCategoryOrder(categoryOrderBuilder.toString());

        productRepository.save(product);
    }

    public void changeProduct(Long productId, String newTitle, String newDescription, Integer newCount, Long newPrice, Long newDiscountPrice, String newCategory, String newSubCategory, String newSubSubCategory, MultipartFile images) throws IOException {
        Product oldProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (newTitle != null) oldProduct.setTitle(newTitle);
        if (newDescription != null) oldProduct.setDescription(newDescription);
        if (newCount != null) oldProduct.setCount(newCount);
        if (newPrice != null) oldProduct.setPrice(newPrice);
        if (newDiscountPrice != null) oldProduct.setDiscountPrice(newDiscountPrice);

        if (newCategory != null) {
            String cat = newCategory;
            if (newSubCategory != null)
                cat += "/" + newSubCategory;
            oldProduct.setCategory(cat);
            if (newSubSubCategory != null)
                cat += "/" + newSubSubCategory;
            generateCategoryOrderForProduct(oldProduct);
        }
        if (images != null) {
            Image image1;
            oldProduct.getImages().get(0).setPreviewImage(false);
            image1 = toImageEntity(images);
            image1.setPreviewImage(true);
            image1 = imageRepository.save(image1);

            oldProduct.addImageToProduct(image1);
            oldProduct.setPreviewImageId(image1.getId());
        }

        Product savedProduct = productRepository.save(oldProduct);
    }

}

