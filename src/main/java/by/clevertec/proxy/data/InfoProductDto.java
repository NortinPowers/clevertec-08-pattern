package by.clevertec.proxy.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record InfoProductDto(
        UUID uuid,
        String name,
        String description,
        BigDecimal price,
        LocalDateTime created) {
    @Override
    public String toString() {
        return "Product"
                + "\n-uuid: " + uuid
                + "\n-name: " + name
                + "\n-description: " + description
                + "\n-price: " + price
                + "\n-created date: " + created.format(DateTimeFormatter.ISO_DATE);
    }
}
