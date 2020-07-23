package J_N_Super_Pvt_Ltd.asset.itemBatch.entity;



import J_N_Super_Pvt_Ltd.asset.invoice.entity.InvoiceItemQuantity;
import J_N_Super_Pvt_Ltd.asset.item.entity.Enum.ItemStatus;
import J_N_Super_Pvt_Ltd.asset.item.entity.Item;
import J_N_Super_Pvt_Ltd.asset.ledger.entity.Ledger;
import J_N_Super_Pvt_Ltd.asset.supplier.entity.Supplier;
import J_N_Super_Pvt_Ltd.util.audit.AuditEntity;
import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonFilter("ItemBatch")
public class ItemBatch extends AuditEntity {

    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;

    @Column(unique = true)
    private String code;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate mDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate eDate;

    @ManyToOne
    private Item item;

    @ManyToOne
    private Supplier supplier;

    @OneToMany(mappedBy = "itemBatch")
    private List<Ledger> ledgers;

    @OneToMany(mappedBy = "itemBatch")
    private List<InvoiceItemQuantity> invoiceItemQuantities;
}