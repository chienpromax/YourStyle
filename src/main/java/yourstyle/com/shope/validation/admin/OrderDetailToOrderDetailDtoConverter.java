package yourstyle.com.shope.validation.admin;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import yourstyle.com.shope.model.OrderDetail;

@Component
public class OrderDetailToOrderDetailDtoConverter implements Converter<OrderDetail, OrderDetailDto> {

    @Override
    public OrderDetailDto convert(OrderDetail source) {
        OrderDetailDto orderDetailDto = new OrderDetailDto();
        orderDetailDto.setOrderDetailId(source.getOrderDetailId());
        orderDetailDto.setOrderId(source.getOrder().getOrderId());
        orderDetailDto.setProductVariantId(source.getProductVariant().getProductVariantId());
        orderDetailDto.setDiscountPrice(source.getPrice());
        orderDetailDto.setQuantity(source.getQuantity());
        return orderDetailDto;
    }

}
