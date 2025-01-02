package vn.flast.domains.payments;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.StringUtils;
import vn.flast.models.CustomerOrderPayment;
import vn.flast.utils.NumberUtils;
import java.util.Date;

public record OrderPaymentInfo(
    Double amount,
    Long id,
    String method,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date datePay,
    String content
) {
    public void transformPayment(CustomerOrderPayment payment) {
        payment.setAmount(amount);
        if(StringUtils.isNotEmpty(method)) {
            payment.setMethod(method);
        }
        payment.setContent(content);
        payment.setConfirmTime(datePay);
    }

    public boolean validate() {
        if(NumberUtils.isNull(id) || StringUtils.isEmpty(method)) {
            return false;
        }
        return !NumberUtils.isNull(amount);
    }
}