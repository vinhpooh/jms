package fr.pham.vinh.jms.commons.dto;

/**
 * Squash Response DTO.
 * Created by Vinh PHAM on 15/03/2017.
 */
public class SquashResponse {

    private String status;

    private String comment;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
