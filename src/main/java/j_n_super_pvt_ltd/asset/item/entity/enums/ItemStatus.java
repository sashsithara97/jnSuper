package j_n_super_pvt_ltd.asset.item.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ItemStatus {

    AVAILABLE("Available"),
    NOT_AVAILABLE("Not Available"),
    ORDERED("Ordered");

    private final String itemStatus;
}