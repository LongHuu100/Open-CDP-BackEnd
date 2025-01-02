package vn.flast.controller.marketting;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.flast.entities.MyResponse;
import vn.flast.models.Data;
import vn.flast.models.DataMedia;
import vn.flast.repositories.DataMediaRepository;
import vn.flast.service.DataService;
import vn.flast.utils.DateUtils;

import java.util.Objects;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("/data-marketting")
public class DataMarkettingController {

    @Autowired
    private DataService dataService;

    @Autowired
    private DataMediaRepository dataMediaRepository;


    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @RequestMapping(value = "/create-lead", method = RequestMethod.POST)
    public MyResponse<?> create(@RequestParam(defaultValue = "0") Integer sessionId, @RequestBody Data data ) {
            data.setStatus(DataService.DATA_STATUS.CREATE_DATA.getStatusCode());
            data.setSource(DataService.DATA_SOURCE.WEB.getSource());
            var owner = dataService.findByPhone(data.getCustomerMobile());
            if(Objects.nonNull(owner)) {
                data.setSaleId(owner.getSaleId());
            }
            dataService.saveData(data);
            for (String item : data.getFileUrls()) {
                if(StringUtils.isEmpty(item)) {
                    continue;
                }
                var model = new DataMedia(Math.toIntExact(data.getId()), DateUtils.dateToInt(), item);
                dataMediaRepository.save(model);
            }
        return MyResponse.response(data);
        }
    }

