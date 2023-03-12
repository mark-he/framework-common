package com.eagletsoft.framework.demo.api;

import com.eagletsoft.boot.framework.api.BaseApi;
import com.eagletsoft.boot.framework.data.filter.PageSearch;
import com.eagletsoft.boot.framework.data.json.ExtViewHelper;
import com.eagletsoft.boot.framework.data.service.ICRUDService;
import com.eagletsoft.framework.demo.repo.Membership;
import com.eagletsoft.framework.demo.view.MembershipCreation;
import com.eagletsoft.framework.demo.view.MembershipUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/memberships")
public class MembershipApi extends BaseApi<Membership, MembershipCreation, MembershipUpdate> {
    @Autowired MembershipService membershipService;

    @Override
    protected ICRUDService<Membership> getService() {
        return membershipService;
    }

    @Override
    public Object search(@Valid PageSearch pageRequest) {
        ExtViewHelper.setGroup("test1", 1);
        return super.search(pageRequest);
    }
}
