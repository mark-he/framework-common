package com.eagletsoft.framework.demo.api;

import com.eagletsoft.framework.demo.repo.Membership;
import com.eagletsoft.framework.demo.repo.MembershipRepo2;
import com.eagletsoft.framework.plugin.dataview.crud.service.BaseViewService;
import com.eagletsoft.framework.plugin.dataview.spi.jpa.IJpaDataViewRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MembershipService2 extends BaseViewService<Membership> {
    @Autowired
    private MembershipRepo2 membershipRepo;

    @Override
    protected IJpaDataViewRepo<Membership> getRepo() {
        return membershipRepo;
    }
}
