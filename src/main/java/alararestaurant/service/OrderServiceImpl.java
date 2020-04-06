package alararestaurant.service;

import alararestaurant.domain.dtos.ItemOrderDto;
import alararestaurant.domain.dtos.OrderImportDto;
import alararestaurant.domain.dtos.OrderRootDto;
import alararestaurant.domain.entities.Employee;
import alararestaurant.domain.entities.Item;
import alararestaurant.domain.entities.Order;
import alararestaurant.domain.entities.OrderItem;
import alararestaurant.repository.EmployeeRepository;
import alararestaurant.repository.ItemRepository;
import alararestaurant.repository.OrderItemRepository;
import alararestaurant.repository.OrderRepository;
import alararestaurant.util.FileUtil;
import alararestaurant.util.ValidationUtil;
import alararestaurant.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final static String ORDER_XML_FILE_PATH = "C:\\Users\\lin\\Documents\\Programming\\6.Hibernate\\11.EXAM PREPARATION\\AlaraRestaurantNEW\\src\\main\\resources\\files\\orders.xml";

    private final OrderRepository orderRepository;
    private final EmployeeRepository employeeRepository;
    private final ItemRepository itemRepository;
    private final OrderItemRepository orderItemRepository;
    private final ModelMapper mapper;
    private final FileUtil fileUtil;
    private final XmlParser xmlParser;
    private final ValidationUtil validator;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, EmployeeRepository employeeRepository, ItemRepository itemRepository, OrderItemRepository orderItemRepository, ModelMapper mapper, FileUtil fileUtil, XmlParser xmlParser, ValidationUtil validator) {
        this.orderRepository = orderRepository;
        this.employeeRepository = employeeRepository;
        this.itemRepository = itemRepository;
        this.orderItemRepository = orderItemRepository;
        this.mapper = mapper;
        this.fileUtil = fileUtil;
        this.xmlParser = xmlParser;
        this.validator = validator;
    }

    @Override
    public Boolean ordersAreImported() {
       return this.orderRepository.count() > 0;
    }

    @Override
    public String readOrdersXmlFile() throws IOException {
        return this.fileUtil.readFile(ORDER_XML_FILE_PATH);
    }

    @Override
    public String importOrders() throws JAXBException {

        OrderRootDto orderRootDto = this.xmlParser.importXml(OrderRootDto.class, ORDER_XML_FILE_PATH);
        StringBuilder sb = new StringBuilder();
        for (OrderImportDto orderDto : orderRootDto.getOrders()) {
            Employee employee = this.employeeRepository.findByName(orderDto.getEmployee()).orElse(null);
            if (employee==null){
                sb.append("Invalid data!").append(System.lineSeparator());
                continue;
            }
            LocalDateTime dateTime = LocalDateTime.parse(orderDto.getDateTime(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            List<OrderItem> orderItems = new ArrayList<>();
            boolean isItemPresent = true;
            for (ItemOrderDto itemDto : orderDto.getItemRootDto().getItems()) {
                Item item = itemRepository.findByName(itemDto.getName()).orElse(null);
                if (item==null){
                    isItemPresent = false;
                    break;
                }
                OrderItem orderItem = new OrderItem(item, itemDto.getQuantity());
                if (!validator.isValid(orderItem)){
                    continue;
                }
                orderItems.add(orderItem);
            }
            Order order = mapper.map(orderDto, Order.class);
            order.setEmployee(employee);
            order.setDateTime(dateTime);
            order.setOrderItems(orderItems);
            if (!validator.isValid(order)||!isItemPresent){
                sb.append("Invalid data!").append(System.lineSeparator());
                continue;
            }
            orderRepository.saveAndFlush(order);
            for (OrderItem orderItem : orderItems) {
                orderItem.setOrder(order);
            }
            orderItemRepository.saveAll(order.getOrderItems());
            sb.append(String.format("Order for %s on %s added", order.getCustomer(), order.getDateTime()))
                    .append(System.lineSeparator());
        }
        return sb.toString();
    }

    @Override
    public String exportOrdersFinishedByTheBurgerFlippers() {
        StringBuilder sb = new StringBuilder();
        this.orderRepository.findAllByBurgerFlippersOrderByEmployeeNameAndOrderId()
                .forEach(o -> {
                    sb.append(String.format(
                            "Name: %s\n" +
                                    "Orders:\n" +
                                    "   Customer: %s\n" +
                                    "   Items:\n", o.getEmployee().getName(),
                            o.getCustomer()));
                    o.getOrderItems().stream().forEach(i->{
                        sb.append(String.format("Name: %s\n" +
                                "   Price: %.2f\n" +
                                "   Quantity: %d\n", i.getItem().getName(),
                                i.getItem().getPrice(), i.getQuantity()))
                                .append(System.lineSeparator());
                    });
                });
        return sb.toString();
    }
}
