package J_N_Super_Pvt_Ltd.asset.goodReceivedNote.entity;


import J_N_Super_Pvt_Ltd.asset.PurchaseOrder.entity.PurchaseOrder;
import J_N_Super_Pvt_Ltd.asset.goodReceivedNote.entity.Enum.GoodReceivedNoteState;
import J_N_Super_Pvt_Ltd.asset.ledger.entity.Ledger;
import J_N_Super_Pvt_Ltd.asset.payment.entity.Payment;
import J_N_Super_Pvt_Ltd.util.audit.AuditEntity;
import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonFilter( "GoodReceivedNote" )
public class GoodReceivedNote extends AuditEntity {

    private String remarks;

    @Column( precision = 10, scale = 2 )
    private BigDecimal totalAmount;

    @Enumerated( EnumType.STRING )
    private GoodReceivedNoteState goodReceivedNoteState;

    @ManyToOne
    private PurchaseOrder purchaseOrder;

    @OneToMany( mappedBy = "goodReceivedNote" )
    private List< Payment > payments;

    @OneToMany( mappedBy = "goodReceivedNote" )
    private List< Ledger > ledgers;


}
