package alararestaurant.service;

import alararestaurant.domain.dtos.CategoryImportDto;
import alararestaurant.domain.dtos.ItemImportDto;
import alararestaurant.domain.entities.Category;
import alararestaurant.domain.entities.Item;
import alararestaurant.repository.CategoryRepository;
import alararestaurant.repository.ItemRepository;
import alararestaurant.util.FileUtil;
import alararestaurant.util.ValidationUtil;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ItemServiceImpl implements ItemService {

    private final static String ITEM_JSON_FILE_PATH = "C:\\Users\\lin\\Documents\\Programming\\6.Hibernate\\11.EXAM PREPARATION\\AlaraRestaurantNEW\\src\\main\\resources\\files\\items.json";

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper mapper;
    private final Gson gson;
    private final ValidationUtil validator;
    private final FileUtil fileUtil;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, CategoryRepository categoryRepository, ModelMapper mapper, Gson gson, ValidationUtil validator, FileUtil fileUtil) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
        this.gson = gson;
        this.validator = validator;
        this.fileUtil = fileUtil;
    }


    @Override
    public Boolean itemsAreImported() {
       return this.itemRepository.count() > 0;
    }

    @Override
    public String readItemsJsonFile() throws IOException {
        return fileUtil.readFile(ITEM_JSON_FILE_PATH);
    }

    @Override
    public String importItems(String items) throws IOException {
        items = readItemsJsonFile();
        ItemImportDto[] itemImportDtos = gson.fromJson(items, ItemImportDto[].class);
        StringBuilder sb = new StringBuilder();
        for (ItemImportDto itemImportDto : itemImportDtos) {
            Category category = this.categoryRepository.findByName(itemImportDto.getCategory()).orElse(null);
            if (category==null){
                CategoryImportDto categoryImportDto = new CategoryImportDto(itemImportDto.getCategory());
                category = mapper.map(categoryImportDto, Category.class);
                if (!validator.isValid(category)){
                    sb.append(validator.violations(category)).append(System.lineSeparator());
                    continue;
                }
            }
            Item item = mapper.map(itemImportDto, Item.class);
            if (itemRepository.findByName(item.getName()).isPresent()){
                continue;
            }
            item.setCategory(category);
            if (!validator.isValid(item)){
                sb.append(validator.violations(item)).append(System.lineSeparator());
                continue;
            }
            categoryRepository.saveAndFlush(category);
            itemRepository.saveAndFlush(item);
            sb.append(String.format("Record %s successfully imported", item.getName()))
                    .append(System.lineSeparator());
        }
        return sb.toString();
    }
}
