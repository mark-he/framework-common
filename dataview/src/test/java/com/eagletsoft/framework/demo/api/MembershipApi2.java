package com.eagletsoft.framework.demo.api;

import com.eagletsoft.framework.demo.repo.Membership;
import com.eagletsoft.framework.demo.view.MembershipCreation;
import com.eagletsoft.framework.demo.view.MembershipUpdate;
import com.eagletsoft.framework.plugin.dataview.crud.api.BaseViewApi;
import com.eagletsoft.framework.plugin.dataview.crud.service.BaseViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;

@Controller
@RequestMapping(value = "/memberships2")
public class MembershipApi2 extends BaseViewApi<MembershipCreation, MembershipUpdate, HashMap, Membership> {
    @Autowired MembershipService2 membershipService;

    @Override
    protected BaseViewService<Membership> getService() {
        return membershipService;
    }
}
