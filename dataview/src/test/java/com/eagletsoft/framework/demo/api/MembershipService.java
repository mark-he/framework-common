package com.eagletsoft.framework.demo.api;

import com.eagletsoft.boot.framework.data.repo.IRepo;
import com.eagletsoft.boot.framework.data.service.BaseCRUDService;
import com.eagletsoft.framework.demo.repo.Membership;
import com.eagletsoft.framework.demo.repo.MembershipRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MembershipService extends BaseCRUDService<Membership> {
    @Autowired
    private MembershipRepo membershipRepo;

    @Override
    protected IRepo<Membership> getRepo() {
        return membershipRepo;
    }
}
