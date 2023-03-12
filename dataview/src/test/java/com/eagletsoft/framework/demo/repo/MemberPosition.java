package com.eagletsoft.framework.demo.repo;

import com.eagletsoft.boot.framework.data.entity.guid.GuidEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "member_position")
public class MemberPosition extends GuidEntity {
   private String memberId;
   private String positionId;
   private String type;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
