package vn.flast.domains.customer;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import vn.flast.entities.MyResponse;
import vn.flast.entities.customer.TagsRequest;
import vn.flast.models.CustomerContract;
import vn.flast.models.CustomerEnterprise;
import vn.flast.models.CustomerPersonal;
import vn.flast.repositories.CustomerContractRepository;
import vn.flast.repositories.CustomerEnterpriseRepository;
import vn.flast.repositories.CustomerOrderRepository;
import vn.flast.repositories.CustomerPersonalRepository;
import vn.flast.searchs.CustomerFilter;
import vn.flast.service.customer.CustomerServiceGlobal;
import vn.flast.utils.Common;
import vn.flast.utils.UploadsUtils;
import vn.flast.validator.ValidationErrorBuilder;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerPersonalRepository customerRepository;

    @Autowired
    private CustomerPersonalService customerPersonalService;

    @Autowired
    private CustomerContractRepository contractRepository;

    @Autowired
    private CustomerEnterpriseRepository enterpriseRepository;

    @Autowired
    private CustomerOrderRepository orderRepository;

    @Autowired
    private CustomerServiceGlobal customerServiceGlobal;

    @PostMapping("/save")
    public MyResponse<?> save(@Valid @RequestBody CustomerPersonal entity, Errors errors) {
        if(errors.hasErrors()) {
            var newErrors = ValidationErrorBuilder.fromBindingErrors(errors);
            return MyResponse.response(newErrors, "Input invalid .!");
        }
        var customer = customerPersonalService.save(entity);
        return MyResponse.response(customer);
    }

    @GetMapping("/find")
    public MyResponse<?> find(CustomerFilter filter) {
        var customers = customerPersonalService.find(filter);
        return MyResponse.response(customers);
    }

    @GetMapping("/find-id")
    public MyResponse<?> findById(@RequestParam Long customerId) {
        var customer = customerRepository.findById(customerId);
        return MyResponse.response(customer);
    }

    @GetMapping("/report-by-id/{customerId}")
    public MyResponse<?> findReportId(@PathVariable Long customerId) {
        var report = customerServiceGlobal.report(customerId);
        return MyResponse.response(report);
    }

    @GetMapping("/tags/fetch")
    public MyResponse<?> fetchTags(@RequestParam List<Long> ids){
        var data = customerPersonalService.fetchTags(ids);
        return MyResponse.response(data);
    }

    @PostMapping("/tags/save/{customerId}")
    public MyResponse<?> saveCustomerTags(@RequestBody TagsRequest tags, @PathVariable Long customerId) {
        customerPersonalService.saveTags(customerId, tags.getTags());
        return MyResponse.response("Okie");
    }

    @GetMapping("/fetch-customer-personal")
    public MyResponse<?> fetchCustomerPersonal(vn.flast.entities.customer.CustomerFilter filter){
        var data = customerServiceGlobal.fetchCustomerPersonal(filter);
        return MyResponse.response(data);
    }

    @GetMapping("/fetch-customer-enterprise")
    public MyResponse<?> fetchCustomerEnterPrise(vn.flast.entities.customer.CustomerFilter filter){
        var data = customerServiceGlobal.fetchCustomerEnterprise(filter);
        return MyResponse.response(data);
    }

    @PostMapping(value = "/create-enterprise", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public MyResponse<?> createEnterprise(
        @RequestParam(name = "orderId") Long orderId,
        @ModelAttribute CustomerEnterprise customerEnterprise
    ) throws Exception {
        var order = orderRepository.findById(orderId).orElseThrow(
            () -> new RuntimeException("Không tìm thấy thông tin đơn hàng")
        );
        var data = enterpriseRepository.save(customerEnterprise);
        order.setEnterpriseId(data.getId());
        order.setEnterpriseName(data.getCompanyName());
        orderRepository.save(order);

        List<CustomerContract> contracts = new ArrayList<>();
        for(var item : customerEnterprise.getContracts()) {
            CustomerContract contract = new CustomerContract();
            String file = UploadsUtils.upload(contract, item);
            contract.setFileName(file);
            contract.setEnterpriseId(customerEnterprise.getId());
            contract.setOrderCode(order.getCode());
            contracts.add(contract);
        }
        if(!Common.CollectionIsEmpty(contracts)) {
            contractRepository.saveAll(contracts);
        }
        return MyResponse.response(data, "Cập nhật thông tin công ty thành công !");
    }
}
