package com.eagletsoft.framework.plugin.workflow.data;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.task.Comment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentBo {
    private String id;
    private String taskId;
    private String userId;
    private String message;
    private Date timestamp;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


    public static CommentBo from(Comment comment) {
        if (null == comment) {
            return null;
        }

        CommentBo commentBo = new CommentBo();
        commentBo.setId(comment.getId());
        commentBo.setUserId(comment.getUserId());
        commentBo.setTaskId(comment.getTaskId());
        commentBo.setMessage(comment.getFullMessage());
        commentBo.setTimestamp(comment.getTime());

        return commentBo;
    }

    public static List<CommentBo> from(List<Comment> list) {
        List<CommentBo> ret = new ArrayList<>();
        for (Comment comment : list) {
            ret.add(CommentBo.from(comment));
        }
        return ret;
    }
}
