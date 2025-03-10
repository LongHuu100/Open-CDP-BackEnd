package vn.flast.searchs;

import lombok.With;
import vn.flast.utils.NumberUtils;

public record OrderFilter(
    @With Integer page,
    Integer limit,
    String code,

    @With String type,
    String customerName,
    String customerEmail,
    Integer customerId,
    Integer companyId,
    Integer saleId,
    String customerPhone
) {
    @Override
    public Integer page() {
        return NumberUtils.isNull(page) ? 0 : (page - 1);
    }

    @Override
    public Integer limit() {
        return NumberUtils.isNull(limit) ? 10 : limit;
    }
}
