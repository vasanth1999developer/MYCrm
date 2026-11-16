package com.microservice.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.company.bean.CompanyBO;
import com.microservice.company.entity.CompanyVO;

/** Company read/create/update responses must never contain the password. */
class CompanyPasswordJsonTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void boAcceptsPasswordInRequestButNeverReturnsIt() throws Exception {
        CompanyBO bo = mapper.readValue("{\"companyName\":\"Acme\",\"password\":\"s3cret\"}", CompanyBO.class);
        assertEquals("s3cret", bo.getPassword(), "create/update must still receive the password");

        String json = mapper.writeValueAsString(bo);
        assertFalse(json.contains("password"), json);
        assertFalse(json.contains("s3cret"), json);
    }

    @Test
    void entityNeverSerializesPassword() throws Exception {
        CompanyVO vo = new CompanyVO();
        vo.setCompanyName("Acme");
        vo.setPassword("s3cret");

        String json = mapper.writeValueAsString(vo);
        assertFalse(json.contains("password"), json);
        assertFalse(json.contains("s3cret"), json);
    }

    @Test
    void passwordStillCopiedFromRequestToEntityForSaving() {
        CompanyBO bo = new CompanyBO();
        bo.setPassword("s3cret");
        CompanyVO vo = new CompanyVO();
        BeanUtils.copyProperties(bo, vo);
        assertEquals("s3cret", vo.getPassword());
    }
}
