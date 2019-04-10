package app.shiva.ajna.Model;

public class SimpleCoupon {
    private String couponId;
    private String couponCreaterId;
    private String quantity;
    private String discountPercentage;
    private String deadline;
    private String comment;


    public SimpleCoupon() {
    }

    public SimpleCoupon(String couponId, String couponCreaterId, String quantity, String discountPercentage, String deadline, String comment) {
        this.couponId = couponId;
        this.couponCreaterId = couponCreaterId;
        this.quantity = quantity;
        this.discountPercentage = discountPercentage;
        this.deadline = deadline;
        this.comment = comment;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getCouponCreaterId() {
        return couponCreaterId;
    }

    public void setCouponCreaterId(String couponCreaterId) {
        this.couponCreaterId = couponCreaterId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(String discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
