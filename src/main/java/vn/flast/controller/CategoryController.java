package vn.flast.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.flast.entities.MyResponse;
import vn.flast.models.Attributed;
import vn.flast.models.Category;
import vn.flast.service.AttributedService;
import vn.flast.service.CategoryService;

@RestController
@RequestMapping("/category")
public class CategoryController {


    @Autowired
    private CategoryService categoryService;

    @PostMapping("/created")
    public MyResponse<?> created(@RequestBody Category input) {
        var data = categoryService.created(input);
        return MyResponse.response(data, "Nhập thành công .!");
    }

    @PostMapping("/updated")
    public MyResponse<?> updated(@RequestBody Category input) {
        var data = categoryService.updated(input);
        return MyResponse.response(data, "Cập nhật thành công .!");
    }

    @GetMapping("/fetch")
    public MyResponse<?> fetch(@RequestParam Integer page) {
        var data = categoryService.fetch(page);
        return MyResponse.response(data);
    }

    @PostMapping("/delete")
    public MyResponse<?> delete(@RequestParam Long id) {
        categoryService.delete(id);
        return MyResponse.response("Xáo bản ghi thành công .!");
    }
}
