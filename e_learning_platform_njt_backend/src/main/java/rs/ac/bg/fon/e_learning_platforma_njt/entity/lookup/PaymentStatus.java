package rs.ac.bg.fon.e_learning_platforma_njt.entity.lookup;

import jakarta.persistence.*;
import java.util.Objects;
import rs.ac.bg.fon.e_learning_platforma_njt.entity.MyEntity;

@Entity
@Table(name = "payment_status")
public class PaymentStatus implements MyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_status_id")
    private Long paymentStatusId;

    @Column(name = "payment_status_name", nullable = false, unique = true, length = 50)
    private String paymentStatusName;

    public PaymentStatus() {
    }

    public PaymentStatus(String paymentStatusName) {
        this.paymentStatusName = paymentStatusName;
    }

    public Long getPaymentStatusId() {
        return paymentStatusId;
    }

    public void setPaymentStatusId(Long paymentStatusId) {
        this.paymentStatusId = paymentStatusId;
    }

    public String getPaymentStatusName() {
        return paymentStatusName;
    }

    public void setPaymentStatusName(String paymentStatusName) {
        this.paymentStatusName = paymentStatusName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PaymentStatus other = (PaymentStatus) obj;
        if (!Objects.equals(this.paymentStatusName, other.paymentStatusName)) {
            return false;
        }
        return Objects.equals(this.paymentStatusId, other.paymentStatusId);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PaymentStatus: ").append(paymentStatusName);
        return sb.toString();
    }

}
