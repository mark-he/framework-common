package com.eagletsoft.framework.plugin.workflow.data;

import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class TaskDetailBo {
    private String id;
    private String name;
    private String assignee;
    private Map<String, Object> variables;
    private Date createTime;
    private Date claimTime;
    private Date dueDate;
    private Date endTime;
    private List<CommentBo> comments;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getClaimTime() {
        return claimTime;
    }

    public void setClaimTime(Date claimTime) {
        this.claimTime = claimTime;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public List<CommentBo> getComments() {
        return comments;
    }

    public void setComments(List<CommentBo> comments) {
        this.comments = comments;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public static TaskDetailBo from(Task ti, List<CommentBo> comments) {
        TaskDetailBo task = new TaskDetailBo();
        task.id = ti.getId();
        task.name = ti.getName();
        task.assignee = ti.getAssignee();
        task.variables = ti.getTaskLocalVariables();
        task.createTime = ti.getCreateTime();
        task.claimTime = ti.getClaimTime();
        task.dueDate = ti.getDueDate();
        task.comments = comments;
        return task;
    }

    public static TaskDetailBo from(HistoricTaskInstance ti, List<CommentBo> comments) {
        TaskDetailBo task = new TaskDetailBo();
        task.id = ti.getId();
        task.name = ti.getName();
        task.assignee = ti.getAssignee();
        task.variables = ti.getTaskLocalVariables();
        task.createTime = ti.getCreateTime();
        task.claimTime = ti.getClaimTime();
        task.dueDate = ti.getDueDate();
        task.endTime = ti.getEndTime();
        task.comments = comments;
        return task;
    }
}
