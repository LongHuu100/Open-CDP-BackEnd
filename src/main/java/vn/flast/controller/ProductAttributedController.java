package vn.flast.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.flast.entities.MyResponse;
import vn.flast.models.Category;
import vn.flast.models.ProductAttributed;
import vn.flast.service.CategoryService;
import vn.flast.service.ProductAttributedService;


@RestController
@RequestMapping("/product-attributed")
public class ProductAttributedController {

    @Autowired
    private ProductAttributedService productAttributedService;

    @PostMapping("/created")
    public MyResponse<?> created(@RequestBody ProductAttributed input) {
        var data = productAttributedService.created(input);
        return MyResponse.response(data, "Nhập thành công .!");
    }

    @PostMapping("/updated")
    public MyResponse<?> updated(@RequestBody ProductAttributed input) {
        var data = productAttributedService.updated(input);
        return MyResponse.response(data, "Cập nhật thành công .!");
    }

    @GetMapping("/fetch")
    public MyResponse<?> fetch(@RequestParam Integer page) {
        var data = productAttributedService.fetch(page);
        return MyResponse.response(data);
    }

    @PostMapping("/delete")
    public MyResponse<?> delete(@RequestParam Integer id) {
        productAttributedService.delete(id);
        return MyResponse.response("Xáo bản ghi thành công .!");
    }
}
