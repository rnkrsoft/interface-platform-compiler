package demo.service;

import demo.domains.AddRequest;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by rnkrsoft.com on 2019/2/23.
 */
public class DemoServiceTest {

    @Test
    public void testAdd() throws Exception {
        DemoService demoService = null;
        AddRequest request = new AddRequest();
        AddRequest.RecordVO recordVO = new AddRequest.RecordVO();
        AddRequest.RecordVO.Record1VO record1VO = new AddRequest.RecordVO.Record1VO();
        record1VO.setSex("xsc");
        record1VO.setSize(5);
        AddRequest.RecordVO.Record1VO.Record2VO record2VO = new AddRequest.RecordVO.Record1VO.Record2VO();
        record2VO.setSize(21);
        request.setRecordV11(recordVO);
        request.getList1().add(recordVO);
        recordVO.setRecord1VO(record1VO);
        record1VO.setRecord2VO(record2VO);
        System.out.println(request);
//        demoService.add(request);
    }
}