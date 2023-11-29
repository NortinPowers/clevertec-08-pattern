package by.clevertec.proxy.data;

import java.math.BigDecimal;

public record ProductDto(

        /*
          {@link by.clevertec.proxy.entity.Product}
         */
        String name,

        /*
         * {@link by.clevertec.proxy.entity.Product}
         */
        String description,

        /*
         * {@link by.clevertec.proxy.entity.Product}
         */
        BigDecimal price) {
}
