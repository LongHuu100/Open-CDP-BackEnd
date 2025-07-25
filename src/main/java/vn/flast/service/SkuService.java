package vn.flast.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.flast.models.ProductSkus;
import vn.flast.models.ProductSkusDetails;
import vn.flast.models.ProductSkusPrice;
import vn.flast.repositories.ProductSkusDetailsRepository;
import vn.flast.repositories.ProductSkusPriceRepository;
import vn.flast.repositories.ProductSkusRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SkuService {

    @Autowired
    private ProductSkusRepository productSkusRepository;

    @Autowired
    private ProductSkusDetailsRepository productSkusDetailsRepository;

    @Autowired
    private ProductSkusPriceRepository skusPriceRepository;

    List<ProductSkus> listProductSkuAndDetail(Long productId) {
        List<ProductSkus> skusList = productSkusRepository.findByProductId(productId);
        if (skusList.isEmpty()) {
            return skusList;
        }

        List<Long> skuIds = skusList.stream()
            .map(ProductSkus::getId)
            .collect(Collectors.toList());

        Map<Long, List<ProductSkusDetails>> detailMap = productSkusDetailsRepository.findBySkusId(skuIds).stream()
            .collect(Collectors.groupingBy(ProductSkusDetails::getSkuId));

        Map<Long, List<ProductSkusPrice>> priceRangeMap = skusPriceRepository.findByListSkuId(skuIds).stream()
            .collect(Collectors.groupingBy(ProductSkusPrice::getSkuId));

        for (ProductSkus sku : skusList) {
            sku.setSkuDetail(detailMap.getOrDefault(sku.getId(), Collections.emptyList()));
            sku.setListPriceRange(priceRangeMap.getOrDefault(sku.getId(), Collections.emptyList()));
        }
        return skusList;
    }
}
