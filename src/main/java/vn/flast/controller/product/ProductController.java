package vn.flast.controller.product;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.flast.entities.MyResponse;
import vn.flast.entities.SaleProduct;
import vn.flast.models.Product;
import vn.flast.repositories.ProductRepository;
import vn.flast.searchs.ProductFilter;
import vn.flast.service.ProductService;
import vn.flast.validator.ValidationErrorBuilder;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @PostMapping("/update-content-seo")
    public MyResponse<?> seoCreate(@Valid @RequestBody Product input, Errors errors) {
        if(errors.hasErrors()) {
            var newErrors = ValidationErrorBuilder.fromBindingErrors(errors);
            return MyResponse.response(newErrors, "Lỗi tham số đầu vào");
        }
        var data = productService.createdSeo(input);
        return MyResponse.response(data, "Cập nhật thành công .!");
    }

    @PostMapping("/save")
    public MyResponse<?> saveProduct(@Valid @RequestBody SaleProduct input, Errors errors) {
        if(errors.hasErrors()) {
            var newErrors = ValidationErrorBuilder.fromBindingErrors(errors);
            return MyResponse.response(newErrors, "Lỗi tham số đầu vào");
        }
        var data = productService.saveProduct(input);
        return MyResponse.response(data, "Cập nhật thành công .!");
    }

    @GetMapping("/fetch")
    public MyResponse<?> fetch(ProductFilter filter) {
        var data = productService.fetch(filter);
        return MyResponse.response(data);
    }

    @GetMapping("/find-by-name")
    public MyResponse<?> findName(@RequestParam("name") String name) {
        var product = productService.findName(name);
        return MyResponse.response(product);
    }

    @PostMapping("/delete")
    public MyResponse<?> delete(@RequestParam Long id) {
        productRepository.deleteById(id);
        return MyResponse.response("Xáo bản ghi thành công .!");
    }
}
