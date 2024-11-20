package yourstyle.com.shope.validation.admin;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import yourstyle.com.shope.model.Address;

@Component
public class AddressToAddressDtoConverter implements Converter<Address, AddressDto> {
    // tạo bộ chuyển đổi
    @Override
    public AddressDto convert(Address source) {
        AddressDto addressDto = new AddressDto();
        addressDto.setAddressId(source.getAddressId());
        addressDto.setStreet(source.getStreet());
        addressDto.setWard(source.getWard());
        addressDto.setDistrict(source.getDistrict());
        addressDto.setCity(source.getCity());
        addressDto.setIsDefault(source.getIsDefault());
        addressDto.setCustomerId(source.getCustomer().getCustomerId());
        addressDto.setCustomer(source.getCustomer());
        return addressDto;
    }

}
