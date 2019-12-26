package org.nbakalov.flowerscompany.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.nbakalov.flowerscompany.data.models.entities.Order;
import org.nbakalov.flowerscompany.data.models.entities.Role;
import org.nbakalov.flowerscompany.data.models.entities.User;
import org.nbakalov.flowerscompany.data.models.enums.Status;
import org.nbakalov.flowerscompany.data.models.enums.Variety;
import org.nbakalov.flowerscompany.data.repositories.OrderRepository;
import org.nbakalov.flowerscompany.errors.illegalservicemodels.IllegalOrderServiceModelException;
import org.nbakalov.flowerscompany.errors.notfound.OrderNotFoundException;
import org.nbakalov.flowerscompany.services.models.OrderServiceModel;
import org.nbakalov.flowerscompany.services.models.RoleServiceModel;
import org.nbakalov.flowerscompany.services.models.UserServiceModel;
import org.nbakalov.flowerscompany.services.services.FlowersBatchService;
import org.nbakalov.flowerscompany.services.services.LogService;
import org.nbakalov.flowerscompany.services.services.UserService;
import org.nbakalov.flowerscompany.services.services.implementations.OrderServiceImpl;
import org.nbakalov.flowerscompany.services.validators.OrderServiceModelValidatorService;
import org.nbakalov.flowerscompany.services.validators.implementation.OrderServiceModelValidatorServiceImpl;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class OrderServiceTests {

  private static final String VALID_USERNAME = "Niko";
  private static final String VALID_PASSWORD = "123456";
  private static final String VALID_EMAIL = "dj_as@abv.bg";
  private static final String VALID_ID = "id";
  private static final String IMAGE_URL = "[random_url]";

  private static final Variety VALID_VARIETY = Variety.APOTHEOSE;
  private static final Integer VALID_QUANTITY = 100;
  private static final Integer VALID_BUNCHES_PER_TRAY = 16;
  private static final LocalDateTime VALID_ORDER_DATE_TIME = LocalDateTime.now();


  @InjectMocks
  private OrderServiceImpl orderService;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private UserService userService;

  @Mock
  private FlowersBatchService flowersBatchService;

  @Mock
  private OrderServiceModelValidatorService validatorService;

  @Mock
  private ModelMapper modelMapper;

  @Mock
  private LogService logService;

  private Order order;
  private OrderServiceModel orderModel;

  @Before
  public void init() {
    OrderServiceModelValidatorServiceImpl actualValidator =
            new OrderServiceModelValidatorServiceImpl();

    ModelMapper actualMapper = new ModelMapper();

    when(modelMapper.map(any(OrderServiceModel.class), eq(Order.class)))
            .thenAnswer(invocationOnMock ->
                    actualMapper.map(invocationOnMock.getArguments()[0], Order.class));

    when(modelMapper.map(any(Order.class), eq(OrderServiceModel.class)))
            .thenAnswer(invocationOnMock ->
                    actualMapper.map(invocationOnMock.getArguments()[0], OrderServiceModel.class));

//    when(modelMapper.map(any(UserServiceModel.class), eq(User.class)))
//            .thenAnswer(invocationOnMock ->
//                    actualMapper.map(invocationOnMock.getArguments()[0], User.class));

//    when(modelMapper.map(any(User.class), eq(UserServiceModel.class)))
//            .thenAnswer(invocationOnMock ->
//                    actualMapper.map(invocationOnMock.getArguments()[0], UserServiceModel.class));

    when(validatorService.isValid(any()))
            .thenAnswer(invocationOnMock ->
                    actualValidator.isValid((OrderServiceModel) invocationOnMock.getArguments()[0]));

    when(logService.saveLog(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

    when(logService.saveLog(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

    order = initOrder();
    orderModel = initOrderServiceModel();
  }

  private Order initOrder() {
    return new Order() {{
      setId(VALID_ID);
      setCustomer(new User() {{
        setId(VALID_ID);
        setUsername(VALID_USERNAME);
        setPassword(VALID_PASSWORD);
        setEmail(VALID_EMAIL);
        setProfilePictureUrl(IMAGE_URL);
        setAuthorities(Set.of(new Role()));
      }});
      setVariety(VALID_VARIETY);
      setBunchesPerTray(VALID_BUNCHES_PER_TRAY);
      setQuantity(VALID_QUANTITY);
      setOrderDateTime(VALID_ORDER_DATE_TIME);
      setStatus(Status.INPROGRESS);
    }};
  }

  private OrderServiceModel initOrderServiceModel() {
    return new OrderServiceModel() {{
      setId(VALID_ID);
      setCustomer(new UserServiceModel() {{
        setId(VALID_ID);
        setUsername(VALID_USERNAME);
        setEmail(VALID_EMAIL);
        setProfilePictureUrl(IMAGE_URL);
        setAuthorities(Set.of(new RoleServiceModel()));
      }});
      setVariety(VALID_VARIETY);
      setBunchesPerTray(VALID_BUNCHES_PER_TRAY);
      setQuantity(VALID_QUANTITY);
      setOrderDateTime(VALID_ORDER_DATE_TIME);
      setStatus(Status.INPROGRESS);
    }};
  }

  @Test(expected = IllegalOrderServiceModelException.class)
  public void makeOrder_ShouldThrow_WhenInputIsNotValid() {

    when(validatorService.isValid(orderModel)).thenReturn(false);

    orderService.makeOrder(orderModel, VALID_USERNAME);
  }

  @Test
  public void makeOrder_ShouldWorkCorrect_WhenInputIsValid() {

    when(validatorService.isValid(orderModel)).thenReturn(true);
    when(userService.findByUsername(VALID_USERNAME))
            .thenReturn(new UserServiceModel() {{
              setUsername(VALID_USERNAME);
            }});
    when(orderRepository.saveAndFlush(any(Order.class))).thenReturn(order);

    OrderServiceModel result = orderService.makeOrder(orderModel, VALID_USERNAME);

    assertNotNull(result);
    assertEquals(VALID_VARIETY, result.getVariety());
    assertEquals(VALID_BUNCHES_PER_TRAY, result.getBunchesPerTray());
    assertEquals(VALID_QUANTITY, result.getQuantity());
    assertEquals(LocalDate.now(), result.getOrderDateTime().toLocalDate());
    assertEquals(Status.INPROGRESS, result.getStatus());
  }

  @Test(expected = OrderNotFoundException.class)
  public void findOrderById_ShouldThrow_IfOrderNotExist() {

    orderService.findOrderById(VALID_ID);
  }

  @Test
  public void findOrderById_ShouldWorkCorrect_IfOrderExist() {

    when(orderRepository.findById(VALID_ID)).thenReturn(Optional.of(order));

    OrderServiceModel result = orderService.findOrderById(VALID_ID);

    assertEquals(VALID_USERNAME, result.getCustomer().getUsername());
    assertEquals(VALID_VARIETY, result.getVariety());
    assertEquals(VALID_QUANTITY, result.getQuantity());
    assertEquals(VALID_BUNCHES_PER_TRAY, result.getBunchesPerTray());
  }

  @Test
  public void findAllOrders_ShouldReturnNothing_IfNoOrdersAreMade() {

    when(orderRepository.findAllByOrderByOrderDateTimeDescQuantityDesc())
            .thenReturn(new ArrayList<>());

    List<OrderServiceModel> orders = orderService.findAllOrders();

    assertEquals(0, orders.size());
  }

  @Test
  public void findAllOrders_ShouldReturnAllOrders_IfHaveOrders() {

    List<Order> orders = List.of(order);

    when(orderRepository.findAllByOrderByOrderDateTimeDescQuantityDesc())
            .thenReturn(orders);

    List<OrderServiceModel> result = orderService.findAllOrders();

    assertEquals(1, result.size());
  }

  @Test
  public void findAllMyOrders_ShouldReturnNothing_IfNoOrdersAreMade() {

    List<OrderServiceModel> orders = orderService.findAllMyOrders(VALID_USERNAME);

    assertEquals(0, orders.size());
  }

//  @Test
//  public void findAllMyOrders_ShouldReturnAllOrders_IfHaveOrders() {
//
//    List<OrderServiceModel> orders = List.of(orderModel);
//    User user = new User() {{
//      setId(VALID_ID);
//      setUsername(VALID_USERNAME);
//      setPassword(VALID_PASSWORD);
//      setEmail(VALID_EMAIL);
//      setProfilePictureUrl(IMAGE_URL);
//      setAuthorities(Set.of(new Role()));
//    }};
//
//    List<Order> orderList = List.of(order);
//
//    UserServiceModel userModel = new UserServiceModel() {{
//      setId(VALID_ID);
//      setUsername(VALID_USERNAME);
//      setPassword(VALID_PASSWORD);
//      setEmail(VALID_EMAIL);
//      setProfilePictureUrl(IMAGE_URL);
//      setAuthorities(Set.of(new RoleServiceModel()));
//    }};
//
//    when(orderRepository.findAllByCustomerOrderByOrderDateTimeDescQuantityDesc(user))
//            .thenReturn(orderList);
//    when(orderService.findAllMyOrders(VALID_USERNAME)).thenReturn(orders);
//    when(userService.findByUsername(VALID_USERNAME)).thenReturn(userModel);
//
//    List<OrderServiceModel> result = orderService.findAllMyOrders(user.getUsername());
//
//    assertEquals(1, result.size());
//    assertEquals(VALID_USERNAME, result.get(0).getCustomer().getUsername());
//  }
}
