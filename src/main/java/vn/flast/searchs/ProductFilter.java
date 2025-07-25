package vn.flast.searchs;

import vn.flast.utils.NumberUtils;
import java.util.List;

public record ProductFilter(
    String name,
    Integer page,
    Integer limit,
    Integer status,
    String code,
    Integer providerId,
    Integer serviceId,
    List<String> ids
) {
    @Override
    public Integer page() {
        return NumberUtils.isNull(page) ? 0 : (page - 1);
    }

    @Override
    public Integer limit() {
        return NumberUtils.isNull(limit) ? 20 : limit;
    }
}
