package yourstyle.com.shope.utils;

import java.util.Set;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;

@Configuration
public class ConverterConfig {
    // Đăng ký bộ chuyển đổi
    public FormattingConversionServiceFactoryBean conversionService(Set<Converter<?, ?>> converters) {
        FormattingConversionServiceFactoryBean factory = new FormattingConversionServiceFactoryBean();
        factory.setConverters(converters);
        return factory;
    }
}
